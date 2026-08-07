package cn.enderrealm.fastgui.inventory.template;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 单槽下拉选择模板
 * 在单个格子中提供下拉选项：lore 列出所有选项，当前选项以彩色前缀高亮；
 * 左键切换到下一项、右键切换到上一项（循环切换）。
 * 变更后通过 {@link BiConsumer} 回调通知调用方，并自动刷新格子显示。
 *
 * @param <T> 选项值类型
 */
public class DropdownSlotTemplate<T> {

    /** 关联的 GUI */
    private final InventoryGUI gui;
    /** 所在格子索引 */
    private final int slot;
    /** 展示用基础物品（会被克隆并追加选项 lore） */
    private final ItemStack displayItem;
    /** 可选选项列表 */
    private final List<T> options;
    /** 选项文本格式化（将选项值转为显示文本） */
    private final Function<T, String> labelFormatter;
    /** 变更回调（参数为触发玩家与新选项值） */
    private final BiConsumer<Player, T> onChange;
    /** 当前选中项索引 */
    private int currentIndex = 0;

    /**
     * 创建单槽下拉选择模板
     *
     * @param gui            关联的 GUI
     * @param slot            所在格子索引
     * @param displayItem     展示用基础物品
     * @param options         可选选项列表
     * @param labelFormatter  选项值转显示文本
     * @param onChange        变更回调
     */
    public DropdownSlotTemplate(InventoryGUI gui, int slot, ItemStack displayItem,
                                List<T> options, Function<T, String> labelFormatter,
                                BiConsumer<Player, T> onChange) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("下拉选项不能为空");
        }
        this.gui = gui;
        this.slot = slot;
        this.displayItem = displayItem;
        this.options = options;
        this.labelFormatter = labelFormatter;
        this.onChange = onChange;
        render();
        // 注册点击处理：左键下一项、右键上一项，循环切换
        gui.setSlot(slot, buildDisplayItem(), event -> {
            if (event.isLeftClick()) {
                select(event.getPlayer(), currentIndex + 1);
            } else if (event.isRightClick()) {
                select(event.getPlayer(), currentIndex - 1);
            }
        });
    }

    /**
     * 设置当前选中项
     *
     * @param value 选项值（需存在于选项列表中）
     */
    public void setSelected(T value) {
        int index = options.indexOf(value);
        if (index >= 0 && index != currentIndex) {
            currentIndex = index;
            render();
        }
    }

    /**
     * 获取当前选中项
     *
     * @return 当前选项值
     */
    public T getSelected() {
        return options.get(currentIndex);
    }

    /**
     * 根据方向切换到指定索引（循环环绕）
     *
     * @param player      触发切换的玩家
     * @param targetIndex 目标索引（未处理环绕）
     */
    private void select(Player player, int targetIndex) {
        int size = options.size();
        currentIndex = ((targetIndex % size) + size) % size;
        render();
        if (onChange != null) {
            onChange.accept(player, options.get(currentIndex));
        }
    }

    /**
     * 渲染格子物品：lore 列出所有选项，当前选项高亮
     */
    private void render() {
        gui.updateSlot(slot, buildDisplayItem());
    }

    /**
     * 构建带选项 lore 的展示物品
     *
     * @return 展示物品
     */
    private ItemStack buildDisplayItem() {
        ItemStack item = displayItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                String text = labelFormatter.apply(options.get(i));
                lore.add(i == currentIndex
                        ? Component.text("§a→ " + text)
                        : Component.text("§7  " + text));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}