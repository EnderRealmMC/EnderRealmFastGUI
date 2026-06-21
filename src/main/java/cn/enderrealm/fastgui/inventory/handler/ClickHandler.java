package cn.enderrealm.fastgui.inventory.handler;

import cn.enderrealm.fastgui.inventory.event.click.InventoryClickEvent;

/**
 * 点击事件处理器
 * 用于处理容器 UI 中的点击事件
 */
@FunctionalInterface
public interface ClickHandler {

    /**
     * 处理点击事件
     *
     * @param event 点击事件
     */
    void onClick(InventoryClickEvent event);
}
