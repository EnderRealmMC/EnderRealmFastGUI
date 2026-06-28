package cn.enderrealm.fastgui.inventory.event.click;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.base.InventoryGUIEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

/**
 * 容器 UI 点击事件
 * 当玩家点击容器中的格子时触发
 */
public class InventoryClickEvent extends InventoryGUIEvent {

    private final int slot;
    private final ClickType clickType;
    private final ItemStack currentItem;
    private final String renameText;
    private final AnvilView anvilView;

    /**
     * 创建点击事件（完整版本，包含 AnvilView）
     *
     * @param gui         触发事件的 GUI
     * @param player      触发事件的玩家
     * @param slot        点击的格子索引
     * @param clickType   点击类型
     * @param currentItem 当前格子中的物品
     * @param renameText  铁砧重命名文本（仅铁砧容器有效）
     * @param anvilView   铁砧视图（仅铁砧容器有效）
     */
    public InventoryClickEvent(InventoryGUI gui, Player player, int slot, ClickType clickType, ItemStack currentItem, String renameText, AnvilView anvilView) {
        super(gui, player);
        this.slot = slot;
        this.clickType = clickType;
        this.currentItem = currentItem;
        this.renameText = renameText;
        this.anvilView = anvilView;
    }

    /**
     * 创建点击事件（兼容旧版本，包含 renameText）
     *
     * @param gui         触发事件的 GUI
     * @param player      触发事件的玩家
     * @param slot        点击的格子索引
     * @param clickType   点击类型
     * @param currentItem 当前格子中的物品
     * @param renameText  铁砧重命名文本（仅铁砧容器有效）
     */
    public InventoryClickEvent(InventoryGUI gui, Player player, int slot, ClickType clickType, ItemStack currentItem, String renameText) {
        this(gui, player, slot, clickType, currentItem, renameText, null);
    }

    /**
     * 创建点击事件（兼容旧版本）
     *
     * @param gui         触发事件的 GUI
     * @param player      触发事件的玩家
     * @param slot        点击的格子索引
     * @param clickType   点击类型
     * @param currentItem 当前格子中的物品
     */
    public InventoryClickEvent(InventoryGUI gui, Player player, int slot, ClickType clickType, ItemStack currentItem) {
        this(gui, player, slot, clickType, currentItem, null, null);
    }

    /**
     * 获取点击的格子索引
     *
     * @return 格子索引
     */
    public int getSlot() {
        return slot;
    }

    /**
     * 获取点击类型
     *
     * @return ClickType 枚举
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * 获取当前格子中的物品
     *
     * @return ItemStack，可能为 null
     */
    public ItemStack getCurrentItem() {
        return currentItem;
    }

    /**
     * 获取铁砧重命名文本
     * 仅当容器类型为铁砧时有效
     *
     * @return 重命名文本，如果不是铁砧容器则返回 null
     */
    public String getRenameText() {
        return renameText;
    }

    /**
     * 获取铁砧视图
     * 仅当容器类型为铁砧时有效，可用于设置经验成本等
     *
     * @return AnvilView 实例，如果不是铁砧容器则返回 null
     */
    public AnvilView getAnvilView() {
        return anvilView;
    }

    /**
     * 是否是左键点击
     *
     * @return true 表示左键
     */
    public boolean isLeftClick() {
        return clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT;
    }

    /**
     * 是否是右键点击
     *
     * @return true 表示右键
     */
    public boolean isRightClick() {
        return clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT;
    }

    /**
     * 是否是 Shift 点击
     *
     * @return true 表示 Shift 点击
     */
    public boolean isShiftClick() {
        return clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT;
    }
}
