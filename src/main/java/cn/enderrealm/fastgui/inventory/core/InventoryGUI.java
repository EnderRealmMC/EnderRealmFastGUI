package cn.enderrealm.fastgui.inventory.core;

import cn.enderrealm.fastgui.inventory.config.ContainerConfig;
import cn.enderrealm.fastgui.inventory.config.ContainerFactory;
import cn.enderrealm.fastgui.inventory.config.SlotPermission;
import cn.enderrealm.fastgui.inventory.event.click.InventoryClickEvent;
import cn.enderrealm.fastgui.inventory.event.close.InventoryCloseEvent;
import cn.enderrealm.fastgui.inventory.event.drag.InventoryDragEvent;
import cn.enderrealm.fastgui.inventory.event.open.InventoryOpenEvent;
import cn.enderrealm.fastgui.inventory.handler.ClickHandler;
import cn.enderrealm.fastgui.inventory.handler.CloseHandler;
import cn.enderrealm.fastgui.inventory.handler.DragHandler;
import cn.enderrealm.fastgui.inventory.handler.OpenHandler;
import cn.enderrealm.fastgui.inventory.holder.GUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 容器 UI 核心类
 * 管理容器的创建、配置和事件处理
 */
public class InventoryGUI {

    private final ContainerConfig config;
    private final GUIHolder holder;
    private final Inventory inventory;
    private final Map<Integer, ClickHandler> slotClickHandlers;
    private final Map<Integer, ItemStack> slotItems;
    private final Map<Integer, SlotPermission> slotPermissions;

    private SlotPermission containerDefaultPermission = SlotPermission.DEFAULT;
    private SlotPermission playerInventoryDefaultPermission = SlotPermission.FULL_ACCESS;

    private ClickHandler globalClickHandler;
    private CloseHandler closeHandler;
    private DragHandler dragHandler;
    private OpenHandler openHandler;

    /**
     * 创建容器 UI
     *
     * @param config 容器配置
     */
    public InventoryGUI(ContainerConfig config) {
        this.config = config;
        this.holder = new GUIHolder(this);
        this.inventory = ContainerFactory.create(config, holder);
        this.slotClickHandlers = new HashMap<>();
        this.slotItems = new HashMap<>();
        this.slotPermissions = new HashMap<>();
        holder.setInventory(inventory);
    }

    /**
     * 打开容器 UI 给玩家
     *
     * @param player 要打开的玩家
     */
    public void open(Player player) {
        player.openInventory(inventory);
    }

    /**
     * 关闭容器 UI
     *
     * @param player 要关闭的玩家
     */
    public void close(Player player) {
        player.closeInventory();
    }

    /**
     * 获取容器配置
     *
     * @return ContainerConfig 实例
     */
    public ContainerConfig getConfig() {
        return config;
    }

    /**
     * 获取 Bukkit Inventory
     *
     * @return Inventory 实例
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 设置容器区域默认权限
     *
     * @param permission 默认权限
     */
    public void setContainerDefaultPermission(SlotPermission permission) {
        this.containerDefaultPermission = permission;
    }

    /**
     * 设置背包区域默认权限
     *
     * @param permission 默认权限
     */
    public void setPlayerInventoryDefaultPermission(SlotPermission permission) {
        this.playerInventoryDefaultPermission = permission;
    }

    /**
     * 设置指定格子的物品
     *
     * @param slot       格子索引
     * @param item       物品
     * @param handler    点击处理器
     * @param permission 权限
     */
    public void setSlot(int slot, ItemStack item, ClickHandler handler, SlotPermission permission) {
        if (slot < 0 || slot >= config.getSize()) {
            throw new IllegalArgumentException("Slot 索引超出范围: " + slot);
        }
        slotItems.put(slot, item);
        inventory.setItem(slot, item);
        if (handler != null) {
            slotClickHandlers.put(slot, handler);
        }
        if (permission != null) {
            slotPermissions.put(slot, permission);
        }
    }

    /**
     * 设置指定格子的物品（使用默认权限）
     *
     * @param slot    格子索引
     * @param item    物品
     * @param handler 点击处理器
     */
    public void setSlot(int slot, ItemStack item, ClickHandler handler) {
        setSlot(slot, item, handler, null);
    }

    /**
     * 设置指定格子的物品（无点击处理器）
     *
     * @param slot 格子索引
     * @param item 物品
     */
    public void setSlot(int slot, ItemStack item) {
        setSlot(slot, item, null, null);
    }

    /**
     * 获取指定格子的物品
     *
     * @param slot 格子索引
     * @return 物品，可能为 null
     */
    public ItemStack getSlot(int slot) {
        return slotItems.get(slot);
    }

    /**
     * 获取指定格子的权限
     *
     * @param slot 格子索引
     * @return 权限，如果没有单独设置则返回区域默认权限
     */
    public SlotPermission getSlotPermission(int slot) {
        // 先检查是否有单独设置的权限
        SlotPermission permission = slotPermissions.get(slot);
        if (permission != null) {
            return permission;
        }

        // 根据区域返回默认权限
        if (slot < config.getSize()) {
            return containerDefaultPermission;
        } else {
            return playerInventoryDefaultPermission;
        }
    }

    /**
     * 移除指定格子的物品和处理器
     *
     * @param slot 格子索引
     */
    public void removeSlot(int slot) {
        slotItems.remove(slot);
        slotClickHandlers.remove(slot);
        slotPermissions.remove(slot);
        inventory.setItem(slot, null);
    }

    /**
     * 清空所有格子
     */
    public void clear() {
        slotItems.clear();
        slotClickHandlers.clear();
        slotPermissions.clear();
        inventory.clear();
    }

    /**
     * 设置全局点击处理器
     *
     * @param handler 点击处理器
     */
    public void setGlobalClickHandler(ClickHandler handler) {
        this.globalClickHandler = handler;
    }

    /**
     * 设置关闭处理器
     *
     * @param handler 关闭处理器
     */
    public void setCloseHandler(CloseHandler handler) {
        this.closeHandler = handler;
    }

    /**
     * 设置拖拽处理器
     *
     * @param handler 拖拽处理器
     */
    public void setDragHandler(DragHandler handler) {
        this.dragHandler = handler;
    }

    /**
     * 设置打开处理器
     *
     * @param handler 打开处理器
     */
    public void setOpenHandler(OpenHandler handler) {
        this.openHandler = handler;
    }

    /**
     * 处理点击事件（内部调用）
     *
     * @param event 点击事件
     */
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        SlotPermission permission = getSlotPermission(slot);

        // 默认取消事件
        event.setCancelled(true);

        // 检查是否可点击
        if (!permission.isClickable() && !permission.isTakeable() && !permission.isPlaceable()) {
            return;
        }

        // 检查是否可拿走（当前格子有物品时）
        if (!permission.isTakeable() && event.getCurrentItem() != null) {
            // 不允许拿走，但可能允许点击
            if (permission.isClickable()) {
                // 只触发回调，不改变取消状态
                ClickHandler slotHandler = slotClickHandlers.get(slot);
                if (slotHandler != null) {
                    slotHandler.onClick(event);
                }
                if (globalClickHandler != null) {
                    globalClickHandler.onClick(event);
                }
            }
            return;
        }

        // 检查是否可放入（Shift+点击时可能放入物品）
        if (!permission.isPlaceable() && event.isShiftClick()) {
            return;
        }

        // 调用格子特定处理器
        ClickHandler slotHandler = slotClickHandlers.get(slot);
        if (slotHandler != null) {
            slotHandler.onClick(event);
        }

        // 调用全局处理器
        if (globalClickHandler != null) {
            globalClickHandler.onClick(event);
        }
    }

    /**
     * 处理拖拽事件（内部调用）
     *
     * @param event 拖拽事件
     */
    public void handleDrag(InventoryDragEvent event) {
        // 默认取消事件
        event.setCancelled(true);

        // 检查所有涉及的 slot 是否可移动
        for (int slot : event.getRawSlots()) {
            SlotPermission permission = getSlotPermission(slot);
            if (!permission.isMovable()) {
                return;
            }
        }

        // 所有 slot 都允许移动，触发回调
        if (dragHandler != null) {
            dragHandler.onDrag(event);
        }
    }

    /**
     * 处理关闭事件（内部调用）
     *
     * @param event 关闭事件
     */
    public void handleClose(InventoryCloseEvent event) {
        if (closeHandler != null) {
            closeHandler.onClose(event);
        }
    }

    /**
     * 处理打开事件（内部调用）
     *
     * @param event 打开事件
     */
    public void handleOpen(InventoryOpenEvent event) {
        if (openHandler != null) {
            openHandler.onOpen(event);
        }
    }

    /**
     * 创建 Builder
     *
     * @return InventoryGUIBuilder 实例
     */
    public static InventoryGUIBuilder builder() {
        return new InventoryGUIBuilder();
    }
}
