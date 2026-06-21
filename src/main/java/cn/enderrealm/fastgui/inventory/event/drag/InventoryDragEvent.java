package cn.enderrealm.fastgui.inventory.event.drag;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.base.InventoryGUIEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

/**
 * 容器 UI 拖拽事件
 * 当玩家在容器中拖拽物品时触发
 */
public class InventoryDragEvent extends InventoryGUIEvent {

    private final Map<Integer, ItemStack> newItems;
    private final Set<Integer> rawSlots;

    /**
     * 创建拖拽事件
     *
     * @param gui      触发事件的 GUI
     * @param player   触发事件的玩家
     * @param newItems 拖拽后的新物品映射（slot -> item）
     * @param rawSlots 涉及的原始槽位
     */
    public InventoryDragEvent(InventoryGUI gui, Player player, Map<Integer, ItemStack> newItems, Set<Integer> rawSlots) {
        super(gui, player);
        this.newItems = newItems;
        this.rawSlots = rawSlots;
    }

    /**
     * 获取拖拽后的新物品映射
     *
     * @return slot -> ItemStack 映射
     */
    public Map<Integer, ItemStack> getNewItems() {
        return newItems;
    }

    /**
     * 获取涉及的所有槽位
     *
     * @return 槽位集合
     */
    public Set<Integer> getRawSlots() {
        return rawSlots;
    }
}
