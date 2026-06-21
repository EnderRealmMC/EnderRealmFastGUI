package cn.enderrealm.fastgui.inventory.config;

import cn.enderrealm.fastgui.inventory.holder.GUIHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/**
 * 容器工厂
 * 根据配置创建 Bukkit Inventory 实例
 */
public class ContainerFactory {

    /**
     * 根据配置创建 Inventory
     *
     * @param config 容器配置
     * @param holder GUI 持有者
     * @return 创建的 Inventory 实例
     */
    public static Inventory create(ContainerConfig config, GUIHolder holder) {
        ContainerType type = config.getType();
        String title = config.getTitle();

        if (type.isResizable()) {
            // 可调整大小的容器（如箱子）
            return Bukkit.createInventory(holder, config.getSize(), title);
        } else {
            // 固定大小的容器
            return Bukkit.createInventory(holder, type.getBukkitType(), title);
        }
    }
}
