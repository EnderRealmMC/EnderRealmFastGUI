package cn.enderrealm.fastgui.inventory.config;

/**
 * Slot 权限配置
 * 定义容器中每个格子的操作权限
 */
public class SlotPermission {

    private final boolean clickable;
    private final boolean takeable;
    private final boolean placeable;
    private final boolean movable;

    /**
     * 创建 Slot 权限
     *
     * @param clickable 是否可点击触发回调
     * @param takeable  是否可拿走
     * @param placeable 是否可放入
     * @param movable   是否可移动（容器内交换/拖拽）
     */
    public SlotPermission(boolean clickable, boolean takeable, boolean placeable, boolean movable) {
        this.clickable = clickable;
        this.takeable = takeable;
        this.placeable = placeable;
        this.movable = movable;
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isTakeable() {
        return takeable;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public boolean isMovable() {
        return movable;
    }

    // ==================== 预设权限 ====================

    /**
     * 只读 - 不能点击、不能拿走、不能放入、不能移动
     * 适用于装饰性物品、标题展示
     */
    public static final SlotPermission READ_ONLY = new SlotPermission(false, false, false, false);

    /**
     * 仅交互 - 可点击触发回调，不能拿走、不能放入、不能移动
     * 适用于商店商品展示、按钮
     */
    public static final SlotPermission INTERACT_ONLY = new SlotPermission(true, false, false, false);

    /**
     * 仅拿走 - 可拿走，不能点击、不能放入、不能移动
     * 适用于取货区、结果槽
     */
    public static final SlotPermission TAKE_ONLY = new SlotPermission(false, true, false, false);

    /**
     * 仅放入 - 可放入，不能点击、不能拿走、不能移动
     * 适用于材料输入区
     */
    public static final SlotPermission PLACE_ONLY = new SlotPermission(false, false, true, false);

    /**
     * 可拿走且可放入 - 不能点击、不能移动
     * 适用于交换槽
     */
    public static final SlotPermission TAKE_AND_PLACE = new SlotPermission(false, true, true, false);

    /**
     * 完全访问 - 所有操作都允许
     * 适用于背包区域、自由区域
     */
    public static final SlotPermission FULL_ACCESS = new SlotPermission(true, true, true, true);

    /**
     * 默认权限 - 仅交互
     * 适用于大多数容器 slot
     */
    public static final SlotPermission DEFAULT = INTERACT_ONLY;

    // ==================== 构建器 ====================

    /**
     * 创建自定义权限构建器
     *
     * @return SlotPermissionBuilder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 权限构建器
     */
    public static class Builder {
        private boolean clickable = false;
        private boolean takeable = false;
        private boolean placeable = false;
        private boolean movable = false;

        public Builder clickable(boolean clickable) {
            this.clickable = clickable;
            return this;
        }

        public Builder takeable(boolean takeable) {
            this.takeable = takeable;
            return this;
        }

        public Builder placeable(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        public Builder movable(boolean movable) {
            this.movable = movable;
            return this;
        }

        public SlotPermission build() {
            return new SlotPermission(clickable, takeable, placeable, movable);
        }
    }
}
