package cn.enderrealm.fastgui.inventory.event.click;

/**
 * 点击类型枚举
 * 定义容器 UI 中支持的点击类型
 */
public enum ClickType {

    /**
     * 左键点击
     */
    LEFT,

    /**
     * 右键点击
     */
    RIGHT,

    /**
     * Shift + 左键
     */
    SHIFT_LEFT,

    /**
     * Shift + 右键
     */
    SHIFT_RIGHT,

    /**
     * 数字键（1-9）
     */
    NUMBER_KEY,

    /**
     * 中键（滚轮点击）
     */
    MIDDLE,

    /**
     * 双击
     */
    DOUBLE_CLICK;
}
