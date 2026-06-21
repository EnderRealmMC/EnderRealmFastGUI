package cn.enderrealm.fastgui.inventory.event.click;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.base.InventoryGUIEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 容器 UI 点击事件
 * 当玩家点击容器中的格子时触发
 */
public class InventoryClickEvent extends InventoryGUIEvent {

    private final int slot;
    private final ClickType clickType;
    private final ItemStack currentItem;

    /**
     * 创建点击事件
     *
     * @param gui         触发事件的 GUI
     * @param player      触发事件的玩家
     * @param slot        点击的格子索引
     * @param clickType   点击类型
     * @param currentItem 当前格子中的物品
     */
    public InventoryClickEvent(InventoryGUI gui, Player player, int slot, ClickType clickType, ItemStack currentItem) {
        super(gui, player);
        this.slot = slot;
        this.clickType = clickType;
        this.currentItem = currentItem;
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
