package cn.enderrealm.fastgui.inventory.handler;

import cn.enderrealm.fastgui.inventory.event.close.InventoryCloseEvent;

/**
 * 关闭事件处理器
 * 用于处理容器 UI 的关闭事件
 */
@FunctionalInterface
public interface CloseHandler {

    /**
     * 处理关闭事件
     *
     * @param event 关闭事件
     */
    void onClose(InventoryCloseEvent event);
}
