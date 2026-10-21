# EnderRealmFastGUI

> [English documentation](README.md) · 中文文档

**把容器界面写成描述，而不是接线拼装。** 一条链写清楚界面长什么样、点下去会发生什么、玩家允许动哪些格子；至于界面会不会被玩家"用坏"，由库负责。

## 目录

- [项目简介](#项目简介)
- [它给你什么](#它给你什么)
- [改造前后](#改造前后)
- [运行环境](#运行环境)
- [安装](#安装)
- [第一个界面](#第一个界面)
- [每个格子要回答的四个问题](#每个格子要回答的四个问题)
- [一次点击是怎么走到你的代码的](#一次点击是怎么走到你的代码的)
- [重绘已经打开的界面](#重绘已经打开的界面)
- [两个不必自己写的控件](#两个不必自己写的控件)
- [箱子之外的容器](#箱子之外的容器)
- [动手前要知道的事](#动手前要知道的事)
- [贡献代码](#贡献代码)
- [许可证](#许可证)

## 项目简介

EnderRealmFastGUI 是一个用来做**可交互容器界面**的 Paper 库——界面的全部载体就是那个背包窗口。它把这件事从头管到尾：创建容器、把物品摆进格子、在服务端所有背包事件里认出哪个窗口归你的代码管、判断玩家对某个格子的这次操作是否被允许、并在允许时把操作交给你的代码。它不是一个装进服务器 `plugins` 目录的插件，而是编译进你自己的插件里；窗口到底代表什么含义，由你的插件决定。

之所以需要这么一层东西，是因为背包窗口的用途本身是错位的。Minecraft 的背包界面是一块**操作物品的表面**：里面的任何东西都能被拿起、挪动、拆分、堆叠、用快捷键换进快捷栏，或者一次双击被一扫而空。而菜单要的恰恰相反：里面的钻石是按钮，不是商品；玻璃板是边框，不是可以被玩家顺走的装饰；下半屏是玩家真正的背包，在他使用界面的这段时间里不该来搅局。让背包界面称职的每一条特性，放进菜单里都成了负担，而且都得逐条明确拒绝。

库的解法是把**玩家对这个格子能做什么**写进声明里。无论是一次点击、shift 点击、快捷键交换还是一次拖拽，在真正动手之前，格子的权限——可点击、可拿走、可放入、可借道移动——先判定这次操作到底算"操作界面"还是"操作物品"。算操作界面，就作为物品移动被拒绝，并在格子可点击时把事件交给你的回调；算操作物品，就交给游戏原生处理，但同样会通知你的回调，让你还能在最后一步把它拦下来。就是这一套机制，让同一个箱子窗口可以是一块只读公告板、一个带投放槽的商店、或者一个玩家可以自由整理的储物箱，而三者用的是同一组概念。库里其余的一切——Builder、回调、原地刷新、现成的开关和下拉——都只是让这套模型写起来不那么难受。

范围是刻意收窄的。EnderRealmFastGUI 只负责画窗口、管交互。它不会帮你对一大堆物品分页，不会把界面状态绑定到你的数据模型上，也不会替你组装物品：布局、价格、冷却、物品标签这些仍然留在你的插件里，也只有留在你的插件里才好测。所以它的 API 面很窄——一个 Builder、一个界面对象、一种权限类型；这份克制换来什么、又意味着什么，写在[动手前要知道的事](#动手前要知道的事)。

## 它给你什么

- **一条表达式写完布局。** 容器类型、标题、行数、逐格物品、整体填充、区间填充串成一条链，菜单的形状一眼读完。
- **用回调，不用监听器。** 每个格子可以挂一个 lambda，整个界面另有一个全局 `onClick`，再加 `onOpen`、`onClose`、`onDrag`。你的插件里不用再出现任何注册代码。
- **每格四个维度的权限。** 能不能点、能不能拿走、能不能放入、能不能借道移动。常用组合有现成预设，容器区域和玩家背包区域各有默认值，单个格子又能把两者都盖掉。
- **看得懂意图的移动防护。** 库判断的是 Bukkit 的具体 action，而不只是格子编号，所以横扫、快捷键交换、shift 想把你的按钮塞进背包这类操作会被拒绝，而点击回调照常执行。
- **不关窗刷新。** 玩家正盯着界面的时候改单个格子或一批格子；只有 `refresh()` 会重开，而那一下玩家是看得见的。
- **两个成品控件。** 布尔开关和单格下拉选择器，各自负责原地重绘自己的格子。
- **14 种容器形状。** 1 到 6 行的箱子，加上漏斗、熔炉、铁砧、酿造台等其余原生形状，各自保持原本的排布。

## 改造前后

先看朴素 Bukkit 写法里，一个商店按钮会长成什么样：

```java
public class ShopListener implements Listener {

    @EventHandler
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        // is this even our menu?
        if (!(event.getInventory().getHolder() instanceof ShopHolder)) {
            return;
        }
        // top region only
        int topSize = event.getInventory().getSize();
        if (event.getRawSlot() < 0 || event.getRawSlot() >= topSize) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (event.getRawSlot() == 11) {
            buyDiamond(player);
        }
        // ...and still open: shift-click, double-click collect, number keys,
        // offhand swap, drag across slots, close without cleanup
    }
}
```

同一个菜单交给 EnderRealmFastGUI：

```java
InventoryGUI shop = InventoryGUI.builder()
        .type(ContainerType.CHEST)
        .title("Shop")
        .rows(3)
        .containerPermission(SlotPermission.READ_ONLY)
        .playerInventoryPermission(SlotPermission.READ_ONLY)
        .slot(11, new ItemStack(Material.DIAMOND),
                click -> buyDiamond(click.getPlayer()), SlotPermission.INTERACT_ONLY)
        .build();

shop.open(player);
```

监听器没有了，注册没有了，raw slot 的边界算术没有了，你自己那个 `InventoryHolder` 标记接口也没有了。留在你文件里的，是真正属于你插件的那部分：哪个格子放哪个物品，点下去要做什么。

## 运行环境

- Java 21。
- Paper，或者保留 Paper API 的分支端。库用到了 `net.kyori.adventure.text.Component` 和 `org.bukkit.inventory.view.AnvilView`，所以不支持 Spigot。
- Gradle，因为这个库以源码子项目的形式引入。它的编译目标是 `io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT`。

## 安装

没有可以直接依赖的 Maven 工件，取源码：

```kotlin
// settings.gradle.kts
include("EnderRealmFastGUI")
project(":EnderRealmFastGUI").projectDir = file("libs/EnderRealmFastGUI")
```

```kotlin
// your plugin build.gradle.kts
dependencies {
    implementation(project(":EnderRealmFastGUI"))
}
```

库自身不声明任何运行时依赖（Paper 只是 compile-only），所以打包要做的只是把它的类放进你的插件 JAR，例如借助 Gradle Shadow 插件。捆绑该库分发出去的插件需要履行 [许可证](#许可证) 里 GPL-3.0 的义务。

## 第一个界面

`onEnable()` 里调用一次 `init()`，内部监听器就注册到你的插件上了。准备工作只有这一步：

```java
import cn.enderrealm.fastgui.EnderRealmFastGUI;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        EnderRealmFastGUI.init(this);
    }
}
```

下面这个商店有装饰边框、一个按钮、一个出货槽、一个投放槽：

```java
import cn.enderrealm.fastgui.inventory.config.ContainerType;
import cn.enderrealm.fastgui.inventory.config.SlotPermission;
import cn.enderrealm.fastgui.inventory.core.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public void openShop(Player player) {
    ItemStack glassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

    InventoryGUI shop = InventoryGUI.builder()
            .type(ContainerType.CHEST)
            .title("Shop")
            .rows(3)
            .containerPermission(SlotPermission.READ_ONLY)
            .playerInventoryPermission(SlotPermission.READ_ONLY)
            // anything still empty becomes the border
            .fill(glassPane, SlotPermission.READ_ONLY)
            // a button: clicking runs your code, the diamond never moves
            .slot(11, new ItemStack(Material.DIAMOND),
                    click -> click.getPlayer().sendMessage("You clicked slot " + click.getSlot()),
                    SlotPermission.INTERACT_ONLY)
            // output tray: the player may take from it, never feed it
            .slot(13, new ItemStack(Material.CHEST), SlotPermission.TAKE_ONLY)
            // input tray: the player may feed it, never take from it
            .slot(22, new ItemStack(Material.AIR), SlotPermission.PLACE_ONLY)
            .onClose(close -> close.getPlayer().sendMessage("Shop closed"))
            .build();

    shop.open(player);
}
```

玩家在这个窗口里的实际体验是：

- 11 号格响应点击，包括冲它按 shift 或按数字键，但钻石一步都不挪，玩家做什么都没法把它抽进自己背包。
- 13 号格的表现就像一只真箱子：物品可以拿出来，但往里塞会被拒绝。
- 22 号格收物品，收了就不许再拿回去——这正是合成输入格或者存物格要的形态。
- 玩家自己的背包在下面被 `READ_ONLY` 锁住。只有你显式放开的格子还能动。扣钱流程进行中时，这是你想要的。
- 关闭窗口时走 `onClose`，未完成的中间状态可以在这里丢掉。

> **边框不会吞掉你的格子。** `fill(...)` 只填它出现在链上那一刻还是空的格子，所以通常的顺序是：区域权限、`fill`、然后是你真正在意的格子。

## 每个格子要回答的四个问题

一个 `SlotPermission` 就是四个互相独立的布尔值。模型的全部就是这些，而它正好让一个格子的邻居可以是完全不同的东西——一个是按钮，下一个是商店输入口：

| 预设 | `clickable` | `takeable` | `placeable` | `movable` | 典型用途 |
| --- | --- | --- | --- | --- | --- |
| `READ_ONLY` | 否 | 否 | 否 | 否 | 装饰、背景填充 |
| `INTERACT_ONLY` | 是 | 否 | 否 | 否 | 按钮；容器区域的默认值 |
| `TAKE_ONLY` | 否 | 是 | 否 | 否 | 产出、结果槽 |
| `PLACE_ONLY` | 否 | 否 | 是 | 否 | 输入槽 |
| `TAKE_AND_PLACE` | 否 | 是 | 是 | 否 | 交换槽 |
| `FULL_ACCESS` | 是 | 是 | 是 | 是 | 玩家背包；该区域的默认值 |

一个格子最终生效的权限，按下面顺序确定：

1. 你显式传给 `slot(...)` 或 `playerSlot(...)` 的那个；
2. 区域默认值——顶部容器用 `containerPermission(...)`，底部 9 格和上方 27 格背包分别可用 `playerHotbarPermission(...)`、`playerMainPermission(...)`；
3. 整个背包区域的兜底 `playerInventoryPermission(...)`。

`playerSlot(index, ...)` 用的是背包自己的坐标：`0-8` 是快捷栏，`9-35` 是主背包区，换算成底层 raw slot 由库来做。预设凑不出你要的组合时，直接拼布尔位：

```java
SlotPermission clickableAndPlaceable = SlotPermission.builder()
        .clickable(true)
        .placeable(true)
        .build();
```

有三个场景值得单说，因为它们通常都是大家手搓的部分：

- **抢不走的菜单。** 容器区域设成 `READ_ONLY`、按钮设成 `INTERACT_ONLY` 之后，shift 点按钮照样触发你的回调，但物品照样动不了。从背包发起的双击收集，只要容器里存在同类且不可拿走的物品就会被拒绝——这条正是朴素的 `setCancelled(true)` 处理漏掉的那个漏洞。
- **倒不空的输入槽。** 把输入格标成 `PLACE_ONLY`：物品进得去、出不来，朝玩家背包方向的 shift 点击会被丢弃。如果你的回调打算把这一格退回去，那就自己用 `updateSlot` 写。
- **还能整理自己背包的玩家。** 背包区域默认是 `FULL_ACCESS`，所以菜单开着的时候玩家仍然可以整理随身物品。扣款流程要严谨时，用 `playerInventoryPermission(SlotPermission.READ_ONLY)` 收紧，再单独放开快捷栏：`.playerHotbarPermission(SlotPermission.INTERACT_ONLY)`。

另外还有两条会影响手感的规则：权限层放行之后，你的回调仍然可以调用 `setCancelled(true)` 否决这次操作；拖拽只有当它涉及的每一个格子都 `movable` 时才会派发出去。

## 一次点击是怎么走到你的代码的

```text
player touches a slot
  -> Bukkit InventoryClickEvent
  -> GUIListener: is the top inventory's holder one of ours?  no -> ignored
  -> resolve that slot's SlotPermission (explicit, then region, then default)
  -> refuse the movement, or let vanilla perform it
  -> your lambda, with an InventoryClickEvent of the library's own
  -> if the event was refused and the slot holds an item: re-assert it next tick
```

这条链上有两个决定值得知道：

- **界面靠 `InventoryHolder` 认，不靠标题。** 库创建的每个容器都包着一个 `GUIHolder`，所以一个界面是靠对象身份被认出来的。两个都叫 "Shop" 的界面不会互相串话，玩家去开世界里的真箱子也不会被截走。顺带的好处是，库里过滤器面对服务器上无关的点击时几乎不花钱。
- **被拒绝的点击会在下一 tick 补一次。** 当库取消了一次针对不可拿走格子的点击，它会在当前 tick 结束后把这个格子的物品重新写一遍。Paper 取消事件时本身会发一个纠正包，但如果没有这次补写，已经在本地把物品挪走的客户端可能继续显示一个服务端并不认可的状态。

## 重绘已经打开的界面

`updateSlot` 和 `updateSlots` 直接写进玩家正看着的那个背包，所以一次购买当场就完成，不必关掉界面：

```java
.slot(11, buyButton, click -> {
    InventoryGUI gui = click.getGui();
    if (purchase(click.getPlayer())) {
        // item only: slot 11 keeps its handler and its permission
        gui.updateSlot(11, new ItemStack(Material.DIAMOND_BLOCK));
        // item and a new handler
        gui.updateSlot(13, refundButton, refund -> refund.getGui().updateSlot(13, buyButton));
        // several slots at once
        gui.updateSlots(Map.of(14, stockItemA, 15, stockItemB));
    }
}, SlotPermission.INTERACT_ONLY)
```

这段接在[第一个界面](#第一个界面)那条链之后，`purchase(Player)` 代表你自己的方法。

`removeSlot(slot)` 会把一个格子的物品、处理器和权限一起清掉，`clear()` 对整个容器做同样的事，`getSlot(slot)` 读回库最后一次写入的物品。`refresh(player)` 是关掉再打开——只有整个布局都变了才用它，因为玩家能感觉到那一下。

## 两个不必自己写的控件

两者都挂在已经 build 出来的界面上，并且自己重绘自己那一格：

```java
import cn.enderrealm.fastgui.inventory.template.BooleanSwitchTemplate;
import cn.enderrealm.fastgui.inventory.template.DropdownSlotTemplate;

InventoryGUI settings = InventoryGUI.builder()
        .type(ContainerType.CHEST)
        .title("Settings")
        .rows(1)
        .build();

new BooleanSwitchTemplate(settings, 3, true,
        (player, enabled) -> player.sendMessage("Announcements: " + enabled));

new DropdownSlotTemplate<>(settings, 5, new ItemStack(Material.CLOCK),
        List.of("Easy", "Normal", "Hard"),
        value -> value,
        (player, value) -> player.sendMessage("Difficulty: " + value));

settings.open(player);
```

`BooleanSwitchTemplate` 开的时候显示绿色羊毛、关的时候显示红色羊毛，点一下就翻转；另一个构造器允许你传入自己的两种展示物品——在设置页里，拉杆或屏障往往比羊毛更好读。

`DropdownSlotTemplate<T>` 把整份选项写进物品 lore，并高亮当前一项。左键往后走、右键往前走，列表首尾相接，所以一个格子就能装下任意取值数量的设置项。`setSelected(value)` 只移动高亮、不会触发你的回调，界面打开时恢复已保存的值就用它。

## 箱子之外的容器

`CHEST`（27 格，可按 9 的倍数在 9–54 之间调整）、`HOPPER`（5）、`FURNACE`（3）、`DROPPER`（9）、`DISPENSER`（9）、`BREWING_STAND`（5）、`ANVIL`（3）、`SHULKER_BOX`（27）、`BARREL`（27）、`STONECUTTER`（4）、`LOOM`（4）、`GRINDSTONE`（3）、`CARTOGRAPHY`（4）、`CRAFTING`（10）。

只有箱子可以改尺寸，所以 `rows(...)` 和 `size(...)` 只对它有意义；其他类型给出对不上的格数时，`build()` 会带明确异常尽早失败，而不是端出一个破窗口。非矩形容器保持原生形状，这本身就是目的：一个漏斗形状的菜单玩家一眼就懂，格子也正好在他预期的位置上。

铁砧是唯一带额外信息的容器。它的点击事件另外提供 `getRenameText()` 和 `getAnvilView()`，可以读到玩家输入的文字，并在结果被取走之前调整经验消耗。其他容器类型下这两个值恒为 `null`。

## 动手前要知道的事

- **一个 `InventoryGUI` 实例只对应一个 `Inventory`。** 打开同一个实例的所有玩家共享它的格子和处理器。做静态公告板很方便，做个性化的东西就是错的：按玩家算的价格、各看各的页。内容因人而异时，请为每个玩家 build 一个实例。
- **`title(...)` 接收的是普通 `String`**，直接交给 `Bukkit.createInventory(...)`。
- **`init()` 每个插件类加载器只能跑一次。** 第二次调用抛 `IllegalStateException`，库也没有 shutdown 或反注册 API。`init()` 之前界面仍然能 build、能打开，但监听器还没注册，任何事件都不会走到你的回调——调试时这会表现成一次无声的失效。
- **`ClickType` 只覆盖七种动作**：`LEFT`、`RIGHT`、`SHIFT_LEFT`、`SHIFT_RIGHT`、`NUMBER_KEY`、`MIDDLE`、`DOUBLE_CLICK`。这套之外的原生动作一律上报为 `LEFT`，所以不要拿它们区分行为。
- **权限判断的是格子，不是物品。** 库不知道 11 号格那颗钻石值多少钱。价格校验、背包空间、冷却、放进来那摞物品是否合法，仍然是你回调的职责。

## 贡献代码

Fork 仓库后在新分支上开发，提 PR 时请说明动机而不只是改动内容——这里最有价值的信息通常是你想拦住哪一种 Bukkit 动作。改动请保持增量、沿用现有的 Javadoc 注释风格，并确认模块仍能通过 `build.gradle.kts` 里锁定的 Paper API 版本编译。

## 许可证

本库依据 GNU General Public License v3 分发，条款见 [LICENSE](LICENSE)。
