package cn.enderrealm.fastgui.inventory.event.open;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.event.base.InventoryGUIEvent;
import org.bukkit.entity.Player;

/**
 * 容器 UI 打开事件
 * 当玩家打开容器时触发
 */
public class InventoryOpenEvent extends InventoryGUIEvent {

    /**
     * 创建打开事件
     *
     * @param gui    触发事件的 GUI
     * @param player 触发事件的玩家
     */
    public InventoryOpenEvent(InventoryGUI gui, Player player) {
        super(gui, player);
    }
}
