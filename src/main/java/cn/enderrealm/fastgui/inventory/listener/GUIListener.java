package cn.enderrealm.fastgui.inventory.listener;

import cn.enderrealm.fastgui.inventory.config.SlotPermission;
import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.click.ClickType;
import cn.enderrealm.fastgui.inventory.event.click.ClickTypeConverter;
import cn.enderrealm.fastgui.inventory.event.click.InventoryClickEvent;
import cn.enderrealm.fastgui.inventory.event.close.InventoryCloseEvent;
import cn.enderrealm.fastgui.inventory.event.drag.InventoryDragEvent;
import cn.enderrealm.fastgui.inventory.event.open.InventoryOpenEvent;
import cn.enderrealm.fastgui.inventory.holder.GUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * GUI 监听器
 * 监听所有容器事件，分发到对应的 GUI 处理器
 * 实现权限检查逻辑，支持细粒度的 slot 权限控制
 */
public class GUIListener implements Listener {

    /**
     * 处理点击事件
     * 根据权限系统决定是否允许操作
     *
     * @param event Bukkit 原生点击事件
     */
    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        InventoryGUI gui = getGUI(event.getInventory());
        if (gui == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();
        int topInventorySize = gui.getConfig().getSize();
        ClickType clickType = ClickTypeConverter.fromBukkitClickType(event.getClick());
        InventoryAction action = event.getAction();

        // 判断点击的是容器区域还是背包区域
        boolean isContainerSlot = rawSlot >= 0 && rawSlot < topInventorySize;
        boolean isPlayerInventorySlot = rawSlot >= topInventorySize && rawSlot < topInventorySize + 36;

        // 获取源 slot 的权限
        SlotPermission sourcePermission;
        if (isContainerSlot) {
            sourcePermission = gui.getSlotPermission(rawSlot);
        } else if (isPlayerInventorySlot) {
            sourcePermission = gui.getSlotPermission(rawSlot);
        } else {
            // 点击的是外部区域
            event.setCancelled(true);
            return;
        }

        // 创建 GUI 事件
        InventoryClickEvent guiEvent = new InventoryClickEvent(
                gui, player, rawSlot, clickType, event.getCurrentItem()
        );

        // 根据操作类型检查权限
        if (isContainerSlot) {
            handleContainerClick(event, guiEvent, gui, sourcePermission, rawSlot, action);
        } else {
            handlePlayerInventoryClick(event, guiEvent, gui, sourcePermission, rawSlot, action);
        }
    }

    /**
     * 处理容器区域的点击
     *
     * @param event      Bukkit 原生事件
     * @param guiEvent   GUI 事件
     * @param gui        GUI 实例
     * @param permission slot 权限
     * @param slot       slot 索引
     * @param action     操作类型
     */
    private void handleContainerClick(
            org.bukkit.event.inventory.InventoryClickEvent event,
            InventoryClickEvent guiEvent,
            InventoryGUI gui,
            SlotPermission permission,
            int slot,
            InventoryAction action
    ) {
        // 检查是否是移动物品的操作
        boolean isMoveAction = isMoveAction(action);

        if (isMoveAction) {
            // 移动物品操作
            if (!permission.isTakeable()) {
                // 不允许拿走，取消事件
                event.setCancelled(true);
                // 但仍然可以触发点击回调（如果允许）
                if (permission.isClickable()) {
                    gui.handleClick(guiEvent);
                }
                return;
            }
        } else {
            // 点击操作（不移动物品）
            if (!permission.isClickable() && !permission.isTakeable()) {
                // 既不能点击也不能拿走
                event.setCancelled(true);
                return;
            }
        }

        // 允许操作，触发回调
        gui.handleClick(guiEvent);

        if (guiEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    /**
     * 处理背包区域的点击
     *
     * @param event      Bukkit 原生事件
     * @param guiEvent   GUI 事件
     * @param gui        GUI 实例
     * @param permission slot 权限
     * @param slot       slot 索引
     * @param action     操作类型
     */
    private void handlePlayerInventoryClick(
            org.bukkit.event.inventory.InventoryClickEvent event,
            InventoryClickEvent guiEvent,
            InventoryGUI gui,
            SlotPermission permission,
            int slot,
            InventoryAction action
    ) {
        boolean isMoveAction = isMoveAction(action);

        if (isMoveAction) {
            // 检查背包 slot 是否可拿走
            if (!permission.isTakeable()) {
                event.setCancelled(true);
                return;
            }

            // 检查是否是移动到容器区域
            if (isMoveToContainer(action)) {
                // 检查容器区域是否有任何 slot 允许放入
                if (!canPlaceInContainer(gui)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // 触发回调
        gui.handleClick(guiEvent);

        if (guiEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    /**
     * 检查容器区域是否有任何 slot 允许放入
     *
     * @param gui GUI 实例
     * @return true 表示有 slot 允许放入
     */
    private boolean canPlaceInContainer(InventoryGUI gui) {
        int containerSize = gui.getConfig().getSize();
        
        for (int i = 0; i < containerSize; i++) {
            SlotPermission permission = gui.getSlotPermission(i);
            if (permission.isPlaceable()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 判断是否是移动物品的操作
     *
     * @param action Bukkit InventoryAction
     * @return true 表示是移动操作
     */
    private boolean isMoveAction(InventoryAction action) {
        return switch (action) {
            case MOVE_TO_OTHER_INVENTORY,
                 HOTBAR_MOVE_AND_READD,
                 HOTBAR_SWAP,
                 COLLECT_TO_CURSOR,
                 PICKUP_ALL,
                 PICKUP_HALF,
                 PICKUP_ONE,
                 PICKUP_SOME,
                 PLACE_ALL,
                 PLACE_ONE,
                 PLACE_SOME,
                 SWAP_WITH_CURSOR -> true;
            default -> false;
        };
    }

    /**
     * 判断是否是移动到容器的操作
     *
     * @param action Bukkit InventoryAction
     * @return true 表示是移动到容器
     */
    private boolean isMoveToContainer(InventoryAction action) {
        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY;
    }

    /**
     * 处理关闭事件
     *
     * @param event Bukkit 原生关闭事件
     */
    @EventHandler
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        InventoryGUI gui = getGUI(event.getInventory());
        if (gui == null) {
            return;
        }

        Player player = (Player) event.getPlayer();
        InventoryCloseEvent guiEvent = new InventoryCloseEvent(gui, player);

        gui.handleClose(guiEvent);
    }

    /**
     * 处理拖拽事件
     * 检查所有涉及的 slot 的权限
     *
     * @param event Bukkit 原生拖拽事件
     */
    @EventHandler
    public void onInventoryDrag(org.bukkit.event.inventory.InventoryDragEvent event) {
        InventoryGUI gui = getGUI(event.getInventory());
        if (gui == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Map<Integer, org.bukkit.inventory.ItemStack> newItems = new HashMap<>(event.getNewItems());
        Set<Integer> rawSlots = new HashSet<>(event.getRawSlots());

        InventoryDragEvent guiEvent = new InventoryDragEvent(gui, player, newItems, rawSlots);

        gui.handleDrag(guiEvent);

        if (guiEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    /**
     * 处理打开事件
     *
     * @param event Bukkit 原生打开事件
     */
    @EventHandler
    public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        InventoryGUI gui = getGUI(event.getInventory());
        if (gui == null) {
            return;
        }

        Player player = (Player) event.getPlayer();
        InventoryOpenEvent guiEvent = new InventoryOpenEvent(gui, player);

        gui.handleOpen(guiEvent);
    }

    /**
     * 从 Inventory 中获取关联的 GUI
     *
     * @param inventory Bukkit Inventory
     * @return InventoryGUI 实例，如果不是 GUI 则返回 null
     */
    private InventoryGUI getGUI(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GUIHolder guiHolder) {
            return guiHolder.getGui();
        }
        return null;
    }
}
