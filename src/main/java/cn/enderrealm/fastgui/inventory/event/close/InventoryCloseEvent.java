package cn.enderrealm.fastgui.inventory.event.close;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.base.InventoryGUIEvent;
import org.bukkit.entity.Player;

/**
 * 容器 UI 关闭事件
 * 当玩家关闭容器时触发
 */
public class InventoryCloseEvent extends InventoryGUIEvent {

    /**
     * 创建关闭事件
     *
     * @param gui    触发事件的 GUI
     * @param player 触发事件的玩家
     */
    public InventoryCloseEvent(InventoryGUI gui, Player player) {
        super(gui, player);
    }
}
