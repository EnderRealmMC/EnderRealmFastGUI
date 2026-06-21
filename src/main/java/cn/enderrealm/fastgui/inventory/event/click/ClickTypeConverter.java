package cn.enderrealm.fastgui.inventory.event.click;

/**
 * 点击类型转换器
 * 用于在 Bukkit 点击类型和 GUI 点击类型之间转换
 */
public class ClickTypeConverter {

    /**
     * 从 Bukkit 点击类型转换为 GUI 点击类型
     *
     * @param bukkitClickType Bukkit 的点击类型
     * @return 对应的 GUI ClickType
     */
    public static ClickType fromBukkitClickType(org.bukkit.event.inventory.ClickType bukkitClickType) {
        switch (bukkitClickType) {
            case LEFT:
                return ClickType.LEFT;
            case RIGHT:
                return ClickType.RIGHT;
            case SHIFT_LEFT:
                return ClickType.SHIFT_LEFT;
            case SHIFT_RIGHT:
                return ClickType.SHIFT_RIGHT;
            case NUMBER_KEY:
                return ClickType.NUMBER_KEY;
            case MIDDLE:
                return ClickType.MIDDLE;
            case DOUBLE_CLICK:
                return ClickType.DOUBLE_CLICK;
            default:
                return ClickType.LEFT;
        }
    }
}
