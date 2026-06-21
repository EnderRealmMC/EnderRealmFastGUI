package cn.enderrealm.fastgui.inventory.holder;

import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * GUI 持有者
 * 实现 InventoryHolder 接口，用于标识容器 UI
 */
public class GUIHolder implements InventoryHolder {

    private final InventoryGUI gui;
    private Inventory inventory;

    /**
     * 创建 GUI 持有者
     *
     * @param gui 关联的 GUI 实例
     */
    public GUIHolder(InventoryGUI gui) {
        this.gui = gui;
    }

    /**
     * 获取关联的 GUI 实例
     *
     * @return InventoryGUI 实例
     */
    public InventoryGUI getGui() {
        return gui;
    }

    /**
     * 设置关联的 Inventory
     *
     * @param inventory Bukkit Inventory 实例
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
