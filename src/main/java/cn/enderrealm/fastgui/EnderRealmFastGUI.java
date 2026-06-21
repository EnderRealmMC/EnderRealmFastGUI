package cn.enderrealm.fastgui;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import cn.enderrealm.fastgui.inventory.listener.GUIListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EnderRealmFastGUI - 快速 GUI 库
 * 提供简单的 GUI 创建功能
 */
public class EnderRealmFastGUI {

    private static JavaPlugin plugin;
    private static boolean initialized = false;

    /**
     * 初始化 FastGUI 库
     * 在插件的 onEnable 中调用此方法
     *
     * @param plugin 宿主插件
     */
    public static void init(JavaPlugin plugin) {
        if (initialized) {
            throw new IllegalStateException("EnderRealmFastGUI 已经初始化");
        }
        EnderRealmFastGUI.plugin = plugin;

        // 注册监听器
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);

        initialized = true;
    }

    /**
     * 获取宿主插件
     *
     * @return 宿主插件实例
     */
    public static JavaPlugin getPlugin() {
        if (!initialized) {
            throw new IllegalStateException("EnderRealmFastGUI 尚未初始化，请先调用 init()");
        }
        return plugin;
    }

    /**
     * 检查是否已初始化
     *
     * @return true 表示已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
