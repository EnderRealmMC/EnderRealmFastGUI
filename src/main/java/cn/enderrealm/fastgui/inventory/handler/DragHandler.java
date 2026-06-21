package cn.enderrealm.fastgui.inventory.handler;

import cn.enderrealm.fastgui.inventory.event.drag.InventoryDragEvent;

/**
 * 拖拽事件处理器
 * 用于处理容器 UI 中的拖拽事件
 */
@FunctionalInterface
public interface DragHandler {

    /**
     * 处理拖拽事件
     *
     * @param event 拖拽事件
     */
    void onDrag(InventoryDragEvent event);
}
