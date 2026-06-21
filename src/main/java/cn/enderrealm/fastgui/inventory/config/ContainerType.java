package cn.enderrealm.fastgui.inventory.config;

import org.bukkit.event.inventory.InventoryType;

/**
 * 容器类型枚举
 * 定义支持的容器类型及其属性
 */
public enum ContainerType {

    /**
     * 箱子 - 支持自定义行数（1-6行，每行9格）
     */
    CHEST(InventoryType.CHEST, 27, true),

    /**
     * 漏斗 - 固定5格
     */
    HOPPER(InventoryType.HOPPER, 5, false),

    /**
     * 熔炉 - 固定3格
     */
    FURNACE(InventoryType.FURNACE, 3, false),

    /**
     * 投掷器 - 固定9格
     */
    DROPPER(InventoryType.DROPPER, 9, false),

    /**
     * 发射器 - 固定9格
     */
    DISPENSER(InventoryType.DISPENSER, 9, false),

    /**
     * 酿造台 - 固定5格
     */
    BREWING_STAND(InventoryType.BREWING, 5, false),

    /**
     * 铁砧 - 固定3格
     */
    ANVIL(InventoryType.ANVIL, 3, false),

    /**
     * 潜影盒 - 固定27格
     */
    SHULKER_BOX(InventoryType.SHULKER_BOX, 27, false),

    /**
     * 木桶 - 固定27格
     */
    BARREL(InventoryType.BARREL, 27, false),

    /**
     * 切石机 - 固定4格
     */
    STONECUTTER(InventoryType.STONECUTTER, 4, false),

    /**
     * 织布机 - 固定4格
     */
    LOOM(InventoryType.LOOM, 4, false),

    /**
     * 砂轮 - 固定3格
     */
    GRINDSTONE(InventoryType.GRINDSTONE, 3, false),

    /**
     * 制图台 - 固定4格
     */
    CARTOGRAPHY(InventoryType.CARTOGRAPHY, 4, false),

    /**
     * 工作台 - 固定10格（3x3 + 输出）
     */
    CRAFTING(InventoryType.WORKBENCH, 10, false);

    private final InventoryType bukkitType;
    private final int defaultSize;
    private final boolean resizable;

    ContainerType(InventoryType bukkitType, int defaultSize, boolean resizable) {
        this.bukkitType = bukkitType;
        this.defaultSize = defaultSize;
        this.resizable = resizable;
    }

    /**
     * 获取 Bukkit 原生容器类型
     *
     * @return Bukkit InventoryType
     */
    public InventoryType getBukkitType() {
        return bukkitType;
    }

    /**
     * 获取默认大小
     *
     * @return 默认格子数
     */
    public int getDefaultSize() {
        return defaultSize;
    }

    /**
     * 是否支持自定义大小
     *
     * @return true 表示可自定义大小
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * 验证大小是否合法
     *
     * @param size 要验证的大小
     * @return true 表示合法
     */
    public boolean isValidSize(int size) {
        if (!resizable) {
            return size == defaultSize;
        }
        // 箱子：1-6行，每行9格
        return size >= 9 && size <= 54 && size % 9 == 0;
    }
}
