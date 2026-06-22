package cn.enderrealm.fastgui.inventory.core;

import cn.enderrealm.fastgui.inventory.config.ContainerConfig;
import cn.enderrealm.fastgui.inventory.config.ContainerType;
import cn.enderrealm.fastgui.inventory.config.SlotPermission;
import cn.enderrealm.fastgui.inventory.handler.ClickHandler;
import cn.enderrealm.fastgui.inventory.handler.CloseHandler;
import cn.enderrealm.fastgui.inventory.handler.DragHandler;
import cn.enderrealm.fastgui.inventory.handler.OpenHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 容器 UI Builder
 * 用于构建 InventoryGUI 实例
 */
public class InventoryGUIBuilder {

    private ContainerType type;
    private String title;
    private int size = -1;
    private final List<SlotEntry> slots = new ArrayList<>();
    private SlotPermission containerDefaultPermission = SlotPermission.DEFAULT;
    private SlotPermission playerInventoryDefaultPermission = SlotPermission.FULL_ACCESS;
    private SlotPermission playerHotbarPermission = null;
    private SlotPermission playerMainPermission = null;
    private ClickHandler globalClickHandler;
    private CloseHandler closeHandler;
    private DragHandler dragHandler;
    private OpenHandler openHandler;

    /**
     * 设置容器类型
     *
     * @param type 容器类型
     * @return Builder 实例
     */
    public InventoryGUIBuilder type(ContainerType type) {
        this.type = type;
        return this;
    }

    /**
     * 设置容器标题
     *
     * @param title 标题字符串
     * @return Builder 实例
     */
    public InventoryGUIBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置容器大小
     *
     * @param size 格子数量
     * @return Builder 实例
     */
    public InventoryGUIBuilder size(int size) {
        this.size = size;
        return this;
    }

    /**
     * 设置容器行数（仅对箱子类型有效）
     *
     * @param rows 行数（1-6）
     * @return Builder 实例
     */
    public InventoryGUIBuilder rows(int rows) {
        this.size = rows * 9;
        return this;
    }

    /**
     * 设置容器区域默认权限
     *
     * @param permission 默认权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder containerPermission(SlotPermission permission) {
        this.containerDefaultPermission = permission;
        return this;
    }

    /**
     * 设置背包区域默认权限
     *
     * @param permission 默认权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerInventoryPermission(SlotPermission permission) {
        this.playerInventoryDefaultPermission = permission;
        return this;
    }

    /**
     * 设置背包快捷栏默认权限
     *
     * @param permission 默认权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerHotbarPermission(SlotPermission permission) {
        this.playerHotbarPermission = permission;
        return this;
    }

    /**
     * 设置背包主区域默认权限
     *
     * @param permission 默认权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerMainPermission(SlotPermission permission) {
        this.playerMainPermission = permission;
        return this;
    }

    /**
     * 添加背包中的格子（带权限）
     * 快捷栏索引：0-8
     * 主背包索引：9-35
     *
     * @param index      背包中的索引（0-35）
     * @param item       物品
     * @param handler    点击处理器
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerSlot(int index, ItemStack item, ClickHandler handler, SlotPermission permission) {
        // 转换为 rawSlot 索引（需要加上容器大小，但容器大小在 build 时才知道）
        // 使用负数标记，build 时再转换
        slots.add(new SlotEntry(-(index + 1), item, handler, permission));
        return this;
    }

    /**
     * 添加背包中的格子
     *
     * @param index   背包中的索引（0-35）
     * @param item    物品
     * @param handler 点击处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerSlot(int index, ItemStack item, ClickHandler handler) {
        return playerSlot(index, item, handler, null);
    }

    /**
     * 添加背包中的格子（带权限，无回调）
     *
     * @param index      背包中的索引（0-35）
     * @param item       物品
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerSlot(int index, ItemStack item, SlotPermission permission) {
        return playerSlot(index, item, null, permission);
    }

    /**
     * 添加背包中的格子（无点击处理器）
     *
     * @param index 背包中的索引（0-35）
     * @param item  物品
     * @return Builder 实例
     */
    public InventoryGUIBuilder playerSlot(int index, ItemStack item) {
        return playerSlot(index, item, null, null);
    }

    /**
     * 添加一个格子（带权限）
     *
     * @param slot       格子索引
     * @param item       物品
     * @param handler    点击处理器
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder slot(int slot, ItemStack item, ClickHandler handler, SlotPermission permission) {
        slots.add(new SlotEntry(slot, item, handler, permission));
        return this;
    }

    /**
     * 添加一个格子
     *
     * @param slot    格子索引
     * @param item    物品
     * @param handler 点击处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder slot(int slot, ItemStack item, ClickHandler handler) {
        return slot(slot, item, handler, null);
    }

    /**
     * 添加一个格子（带权限，无回调）
     *
     * @param slot       格子索引
     * @param item       物品
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder slot(int slot, ItemStack item, SlotPermission permission) {
        return slot(slot, item, null, permission);
    }

    /**
     * 添加一个格子（无点击处理器）
     *
     * @param slot 格子索引
     * @param item 物品
     * @return Builder 实例
     */
    public InventoryGUIBuilder slot(int slot, ItemStack item) {
        return slot(slot, item, null, null);
    }

    /**
     * 填充所有空位
     *
     * @param item 物品
     * @return Builder 实例
     */
    public InventoryGUIBuilder fill(ItemStack item) {
        slots.add(new SlotEntry(-1, item, null, null)); // -1 表示填充
        return this;
    }

    /**
     * 填充所有空位（带权限）
     *
     * @param item       物品
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder fill(ItemStack item, SlotPermission permission) {
        slots.add(new SlotEntry(-1, item, null, permission));
        return this;
    }

    /**
     * 填充指定范围
     *
     * @param start 起始索引
     * @param end   结束索引
     * @param item  物品
     * @return Builder 实例
     */
    public InventoryGUIBuilder fillRange(int start, int end, ItemStack item) {
        for (int i = start; i < end; i++) {
            slots.add(new SlotEntry(i, item, null, null));
        }
        return this;
    }

    /**
     * 填充指定范围（带权限）
     *
     * @param start      起始索引
     * @param end        结束索引
     * @param item       物品
     * @param permission 权限
     * @return Builder 实例
     */
    public InventoryGUIBuilder fillRange(int start, int end, ItemStack item, SlotPermission permission) {
        for (int i = start; i < end; i++) {
            slots.add(new SlotEntry(i, item, null, permission));
        }
        return this;
    }

    /**
     * 设置全局点击处理器
     *
     * @param handler 点击处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder onClick(ClickHandler handler) {
        this.globalClickHandler = handler;
        return this;
    }

    /**
     * 设置关闭处理器
     *
     * @param handler 关闭处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder onClose(CloseHandler handler) {
        this.closeHandler = handler;
        return this;
    }

    /**
     * 设置拖拽处理器
     *
     * @param handler 拖拽处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder onDrag(DragHandler handler) {
        this.dragHandler = handler;
        return this;
    }

    /**
     * 设置打开处理器
     *
     * @param handler 打开处理器
     * @return Builder 实例
     */
    public InventoryGUIBuilder onOpen(OpenHandler handler) {
        this.openHandler = handler;
        return this;
    }

    /**
     * 构建 InventoryGUI
     *
     * @return InventoryGUI 实例
     */
    public InventoryGUI build() {
        // 验证参数
        if (type == null) {
            throw new IllegalStateException("容器类型不能为空");
        }
        if (title == null) {
            title = "";
        }

        // 确定大小
        int finalSize;
        if (size != -1) {
            finalSize = size;
        } else {
            finalSize = type.getDefaultSize();
        }

        // 验证大小
        if (!type.isValidSize(finalSize)) {
            throw new IllegalStateException("无效的容器大小: " + finalSize + "，类型: " + type);
        }

        // 创建配置
        ContainerConfig config = new ContainerConfig(type, title, finalSize);

        // 创建 GUI
        InventoryGUI gui = new InventoryGUI(config);

        // 设置区域默认权限
        gui.setContainerDefaultPermission(containerDefaultPermission);
        gui.setPlayerInventoryDefaultPermission(playerInventoryDefaultPermission);

        // 设置背包快捷栏和主区域的默认权限
        if (playerHotbarPermission != null) {
            gui.setPlayerHotbarPermission(playerHotbarPermission);
        }
        if (playerMainPermission != null) {
            gui.setPlayerMainPermission(playerMainPermission);
        }

        // 设置格子
        for (SlotEntry entry : slots) {
            if (entry.slot == -1) {
                // 填充所有空位
                for (int i = 0; i < finalSize; i++) {
                    if (gui.getSlot(i) == null) {
                        gui.setSlot(i, entry.item, entry.handler, entry.permission);
                    }
                }
            } else if (entry.slot < 0) {
                // 背包格子（负数标记）
                int playerIndex = -(entry.slot + 1);
                int rawSlot = finalSize + playerIndex;
                gui.setSlot(rawSlot, entry.item, entry.handler, entry.permission);
            } else {
                gui.setSlot(entry.slot, entry.item, entry.handler, entry.permission);
            }
        }

        // 设置处理器
        if (globalClickHandler != null) {
            gui.setGlobalClickHandler(globalClickHandler);
        }
        if (closeHandler != null) {
            gui.setCloseHandler(closeHandler);
        }
        if (dragHandler != null) {
            gui.setDragHandler(dragHandler);
        }
        if (openHandler != null) {
            gui.setOpenHandler(openHandler);
        }

        return gui;
    }

    /**
     * 格子条目（内部类）
     */
    private static class SlotEntry {
        final int slot;
        final ItemStack item;
        final ClickHandler handler;
        final SlotPermission permission;

        SlotEntry(int slot, ItemStack item, ClickHandler handler, SlotPermission permission) {
            this.slot = slot;
            this.item = item;
            this.handler = handler;
            this.permission = permission;
        }
    }
}
