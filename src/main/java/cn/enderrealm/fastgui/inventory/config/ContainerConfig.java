package cn.enderrealm.fastgui.inventory.config;

/**
 * 容器配置类
 * 存储容器的所有配置信息
 */
public class ContainerConfig {

    private final ContainerType type;
    private final String title;
    private final int size;

    /**
     * 创建容器配置
     *
     * @param type  容器类型
     * @param title 容器标题
     * @param size  容器大小
     */
    public ContainerConfig(ContainerType type, String title, int size) {
        this.type = type;
        this.title = title;
        this.size = size;
    }

    /**
     * 获取容器类型
     *
     * @return 容器类型
     */
    public ContainerType getType() {
        return type;
    }

    /**
     * 获取容器标题
     *
     * @return 标题字符串
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取容器大小
     *
     * @return 格子数量
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取行数（仅对箱子类型有效）
     *
     * @return 行数
     */
    public int getRows() {
        return size / 9;
    }

    /**
     * 验证配置是否合法
     *
     * @return true 表示合法
     */
    public boolean isValid() {
        return type != null && title != null && type.isValidSize(size);
    }
}
