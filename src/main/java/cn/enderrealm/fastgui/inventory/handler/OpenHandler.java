package cn.enderrealm.fastgui.inventory.handler;

import cn.enderrealm.fastgui.inventory.event.open.InventoryOpenEvent;

/**
 * 打开事件处理器
 * 用于处理容器 UI 的打开事件
 */
@FunctionalInterface
public interface OpenHandler {

    /**
     * 处理打开事件
     *
     * @param event 打开事件
     */
    void onOpen(InventoryOpenEvent event);
}
