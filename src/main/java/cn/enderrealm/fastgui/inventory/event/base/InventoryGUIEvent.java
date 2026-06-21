package cn.enderrealm.fastgui.inventory.event.base;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import org.bukkit.entity.Player;

/**
 * 容器 UI 事件基类
 * 所有容器 UI 事件都继承此类
 */
public abstract class InventoryGUIEvent {

    protected final InventoryGUI gui;
    protected final Player player;
    protected boolean cancelled;

    /**
     * 创建容器 UI 事件
     *
     * @param gui    触发事件的 GUI
     * @param player 触发事件的玩家
     */
    public InventoryGUIEvent(InventoryGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
        this.cancelled = false;
    }

    /**
     * 获取触发事件的 GUI
     *
     * @return InventoryGUI 实例
     */
    public InventoryGUI getGui() {
        return gui;
    }

    /**
     * 获取触发事件的玩家
     *
     * @return Player 实例
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 设置事件是否取消
     *
     * @param cancelled true 表示取消
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * 获取事件是否已取消
     *
     * @return true 表示已取消
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
