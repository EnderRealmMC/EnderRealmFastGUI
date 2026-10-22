package cn.enderrealm.fastgui.inventory.template;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * 布尔开关模板
 * 在单个格子中提供开关切换：开启显示绿色羊毛、关闭显示红色羊毛；
 * 点击切换状态并通过 {@link BiConsumer} 回调通知调用方。
 */
public class BooleanSwitchTemplate {

    /** 关联的 GUI */
    private final InventoryGUI gui;

    /** 所在格子索引 */
    private final int slot;

    /** 开启状态展示物品（默认绿色羊毛） */
    private final ItemStack onItem;

    /** 关闭状态展示物品（默认红色羊毛） */
    private final ItemStack offItem;

    /** 当前状态 */
    private boolean value;

    /** 状态变更回调 */
    private final BiConsumer<Player, Boolean> onChange;

    /**
     * 创建布尔开关模板（默认绿色/红色羊毛展示）
     *
     * @param gui      关联的 GUI
     * @param slot     所在格子索引
     * @param value    初始状态
     * @param onChange 状态变更回调
     */
    public BooleanSwitchTemplate(InventoryGUI gui, int slot, boolean value, BiConsumer<Player, Boolean> onChange) {
        this(gui, slot, value, onChange, new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.RED_WOOL));
    }

    /**
     * 创建布尔开关模板（自定义展示物品）
     *
     * @param gui      关联的 GUI
     * @param slot     所在格子索引
     * @param value    初始状态
     * @param onChange 状态变更回调
     * @param onItem   开启状态展示物品
     * @param offItem  关闭状态展示物品
     */
    public BooleanSwitchTemplate(InventoryGUI gui, int slot, boolean value, BiConsumer<Player, Boolean> onChange,
                                 ItemStack onItem, ItemStack offItem) {
        this.gui = gui;
        this.slot = slot;
        this.value = value;
        this.onChange = onChange;
        this.onItem = onItem;
        this.offItem = offItem;
        render();
        gui.setSlot(slot, buildItem(), event -> {
            // 必须读取字段而非构造参数 value（参数遮蔽字段，lambda 捕获后永远得到初始值）
            boolean newValue = !this.value;
            setValue(event.getPlayer(), newValue);
        });
    }

    /**
     * 设置当前状态并触发回调
     *
     * @param player 触发切换的玩家
     * @param newValue 新状态
     */
    public void setValue(Player player, boolean newValue) {
        this.value = newValue;
        render();
        if (onChange != null) {
            onChange.accept(player, newValue);
        }
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public boolean getValue() {
        return value;
    }

    /**
     * 渲染格子显示
     */
    private void render() {
        gui.updateSlot(slot, buildItem());
    }

    /**
     * 构建当前状态的展示物品
     *
     * @return 展示物品
     */
    private ItemStack buildItem() {
        return value ? onItem.clone() : offItem.clone();
    }
}