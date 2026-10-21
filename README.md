# EnderRealmFastGUI

> English documentation · [中文文档](README.zh-CN.md)

**Describe a container UI instead of wiring one.** A single expression declares the layout, the reactions, and what the player is allowed to touch; the library keeps the chest honest.

## Contents

- [Overview](#overview)
- [What it gives you](#what-it-gives-you)
- [Before and after](#before-and-after)
- [Requirements](#requirements)
- [Installation](#installation)
- [Your first GUI](#your-first-gui)
- [The four questions every slot answers](#the-four-questions-every-slot-answers)
- [How a click reaches your code](#how-a-click-reaches-your-code)
- [Repainting a GUI that is already open](#repainting-a-gui-that-is-already-open)
- [Two widgets you do not have to write](#two-widgets-you-do-not-have-to-write)
- [Containers beyond the chest](#containers-beyond-the-chest)
- [Things to know before you build](#things-to-know-before-you-build)
- [Contributing](#contributing)
- [License](#license)

## Overview

EnderRealmFastGUI is a library for building **interactive container UIs** in Paper plugins — menus whose whole interface happens inside an inventory window. It owns that job end to end: it creates the container, places items into slots, recognises which window on the server belongs to your code, decides whether a player's action on a given slot is allowed, and hands the interaction to your code when it is. It is not a plugin you drop into a server's `plugins` folder; it is compiled into your own plugin, and your plugin decides what the windows mean.

The reason a library is needed at all is a mismatch in what an inventory window is for. Minecraft's inventory screen is an **item-operating surface**: everything in it can be picked up, moved, split, stacked, swapped to a hotbar key, or swept away by a double click. A menu needs close to the opposite. Its diamond is a button, not merchandise; its glass panes are a border, not decoration the player is free to pocket; its lower half is the player's real backpack, which must not disturb the interface while they use it. Every behaviour that makes the inventory screen useful for items is a liability in a menu, and each one has to be refused deliberately.

The library's answer is to make **what a player may do with a slot** part of the declaration. Before a click, a shift-click, a hotbar swap or a drag is acted upon, the slot's permission — clickable, takeable, placeable, movable — decides whether that action is a UI operation or an item operation. A UI operation is refused as an item movement and, when the slot is clickable, delivered to your callback; an item operation is carried out by the game and still reported to your callback, so you can refuse it last-minute. That single mechanism is what lets one chest window be a read-only notice board, a shop with a deposit tray, and a storage box the player may rearrange freely, all described with the same few concepts. Everything else in the library — the builder, the callbacks, the live repainting, the ready-made switch and dropdown — exists to make declaring that model tolerable.

The scope is deliberately narrow. EnderRealmFastGUI renders windows and governs interaction with them. It does not page a large item list for you, does not bind GUI state to your data model, and does not build items on your behalf: layout decisions, prices, cooldowns and item metadata stay in your plugin, where they can be tested. The API surface therefore stays small — one builder, one GUI object, one permission type — and [Things to know before you build](#things-to-know-before-you-build) lists what that implies.

## What it gives you

- **Layout as one expression.** Container type, title, rows, per-slot items, decorative fill, and range fill chain together, so the shape of the menu is readable at a glance.
- **Callbacks instead of listeners.** Attach a lambda to a slot, or a global one to the whole GUI, plus `onOpen`, `onClose` and `onDrag`. Nothing is registered in your plugin.
- **A permission model with four answers per slot.** Click it, take from it, put into it, move through it. Presets cover the usual cases; the container region and the player's inventory region each get their own default, and any slot can override both.
- **Move protection that understands intent.** The library reads the Bukkit action, not just the slot, so an attempt to sweep, swap, or shift-click an item out of your GUI is refused while the click callback still runs.
- **Repainting without reopening.** Change one slot or a batch of slots while the player is looking at the menu; only `refresh()` reopens, and it flickers.
- **Two finished widgets.** A boolean switch and a single-slot dropdown selector, both of which re-render themselves in place.
- **Fourteen container shapes.** Chests from one to six rows, plus hoppers, furnaces, anvils, brewing stands and the rest, each keeping its native layout.

## Before and after

The classic version of a shop button, as it grows in a plain Bukkit plugin:

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

The same menu in EnderRealmFastGUI:

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

No listener, no registration, no raw-slot arithmetic, no holder interface of your own. What is left in your file is the part that is actually about your plugin: which item sits in which slot, and what happens when it is clicked.

## Requirements

- Java 21.
- Paper, or a fork that keeps the Paper API. The library uses `net.kyori.adventure.text.Component` and `org.bukkit.inventory.view.AnvilView`, so Spigot is out.
- Gradle, because the library is consumed as a source subproject. It is compiled against `io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT`.

## Installation

There is no published Maven artifact to depend on — take the source:

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

The library adds no runtime dependency of its own — Paper is compile-only — so packaging means getting its classes into your plugin JAR, for example with the Gradle Shadow plugin. A plugin distributed with the library bundled carries the GPL-3.0 obligations described in [License](#license).

## Your first GUI

One call in `onEnable()` registers the internal listener on your plugin. That is the whole setup:

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

Now a shop with a decorative background, one button, one output tray and one input tray:

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

What the player experiences in this window:

- Slot 11 answers to a click, including a shift-click or a number-key press aimed at it, but the diamond stays where it is. Nothing they do can pull it into their inventory.
- Slot 13 behaves like a real chest: they can take the item out, and stuffing it back in is refused.
- Slot 22 accepts an item and then refuses to hand it back — the shape of a crafting or deposit input.
- Their own backpack sits below, locked by `READ_ONLY`. Only the slots you explicitly open up can move, which is what you want while money is being charged.
- Closing the window runs `onClose`, so pending state can be discarded.

> **The border does not eat your slots.** `fill(...)` only touches slots that are still empty at the point it appears in the chain, so the usual order is: region permissions, `fill`, then the slots you care about.

## The four questions every slot answers

A `SlotPermission` is four independent booleans. That is the whole model, and it is what lets one slot be a button while the next one is a shop input:

| Preset | `clickable` | `takeable` | `placeable` | `movable` | Typical use |
| --- | --- | --- | --- | --- | --- |
| `READ_ONLY` | no | no | no | no | decoration, background filler |
| `INTERACT_ONLY` | yes | no | no | no | button — default for the container region |
| `TAKE_ONLY` | no | yes | no | no | output and result slots |
| `PLACE_ONLY` | no | no | yes | no | input slots |
| `TAKE_AND_PLACE` | no | yes | yes | no | exchange slots |
| `FULL_ACCESS` | yes | yes | yes | yes | player inventory — default for that region |

The permission applied to a slot is resolved in this order:

1. whatever you passed to `slot(...)` or `playerSlot(...)`;
2. the region default — `containerPermission(...)` for the top container, `playerHotbarPermission(...)` and `playerMainPermission(...)` for the bottom nine and the upper twenty-seven inventory slots;
3. `playerInventoryPermission(...)` as the fallback for the whole inventory region.

`playerSlot(index, ...)` speaks the inventory's own coordinates: `0-8` is the hotbar, `9-35` the main rows. The library converts it to the underlying raw slot for you. When a preset is not the combination you need, assemble the flags directly:

```java
SlotPermission clickableAndPlaceable = SlotPermission.builder()
        .clickable(true)
        .placeable(true)
        .build();
```

Three scenarios are worth spelling out, because they are the ones people usually hand-roll:

- **A menu that cannot be robbed.** With the container region `READ_ONLY` and buttons `INTERACT_ONLY`, shift-clicking a button still fires your callback and still cannot move the item. Double-click collection started from the backpack is refused as soon as the container holds a matching item that is not takeable, which is the leak that a naive `setCancelled(true)` handler misses.
- **An input tray that cannot be emptied.** Mark the tray `PLACE_ONLY`: items go in, nothing comes out, and a shift-click aimed back at the player's inventory is dropped. If your callback wants to refund the tray, do it by writing the item yourself with `updateSlot`.
- **A player who can still use their own bag.** The inventory region defaults to `FULL_ACCESS`, so players can rearrange their belongings while your menu is open. Tighten it with `playerInventoryPermission(SlotPermission.READ_ONLY)` when a purchase is in flight, then loosen only the hotbar: `.playerHotbarPermission(SlotPermission.INTERACT_ONLY)`.

Two more rules that shape behaviour: your callback may veto an operation the permission layer allowed by calling `setCancelled(true)` on the event, and a drag is only reported when every slot it touches is `movable`.

## How a click reaches your code

```text
player touches a slot
  -> Bukkit InventoryClickEvent
  -> GUIListener: is the top inventory's holder one of ours?  no -> ignored
  -> resolve that slot's SlotPermission (explicit, then region, then default)
  -> refuse the movement, or let vanilla perform it
  -> your lambda, with an InventoryClickEvent of the library's own
  -> if the event was refused and the slot holds an item: re-assert it next tick
```

Two decisions in that chain are worth knowing about:

- **GUIs are identified by their `InventoryHolder`, not by their title.** Every inventory the library creates wraps a `GUIHolder`, so a menu is recognised by object identity. Two unrelated GUIs titled "Shop" never cross-talk, and a player opening a real chest in the world is not intercepted. It also means the library can ignore the rest of the server's clicks at almost no cost.
- **A refused click is re-asserted one tick later.** When the library cancels a click on a slot the player cannot take from, it writes the slot's item again after the current tick. Paper sends its own correction packet when it cancels, and without the re-assertion a client that already moved the item locally could keep showing a state the server rejected.

## Repainting a GUI that is already open

`updateSlot` and `updateSlots` write into the inventory the player is looking at, so a purchase resolves in place instead of closing the window:

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

This fragment continues the chain from [Your first GUI](#your-first-gui); `purchase(Player)` stands for your own method.

`removeSlot(slot)` drops a slot's item, handler and permission together, `clear()` does that for the whole container, and `getSlot(slot)` returns the item the library last wrote. `refresh(player)` closes and reopens the window — use it only when the entire layout changed, because the player sees it happen.

## Two widgets you do not have to write

Both are attached to a GUI you already built, and both redraw their own slot:

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

`BooleanSwitchTemplate` shows green wool when it is on and red wool when it is off, and flips on click; a second constructor accepts your own two items, which is usually what you want — a lever and a barrier read more clearly than wool in a settings page.

`DropdownSlotTemplate<T>` puts the whole option list in the item's lore and highlights the current entry. Left click walks forward, right click walks back, and the list wraps around, so a single slot can carry a setting with any number of values. `setSelected(value)` moves the highlight without firing your callback, which is how you restore a saved value when the page opens.

## Containers beyond the chest

`CHEST` (27 slots, resizable from 9 to 54 in multiples of 9), `HOPPER` (5), `FURNACE` (3), `DROPPER` (9), `DISPENSER` (9), `BREWING_STAND` (5), `ANVIL` (3), `SHULKER_BOX` (27), `BARREL` (27), `STONECUTTER` (4), `LOOM` (4), `GRINDSTONE` (3), `CARTOGRAPHY` (4), `CRAFTING` (10).

Only the chest is resizable, so `rows(...)` and `size(...)` are meaningful for it alone; asking for another size makes `build()` fail early with an explicit exception rather than a broken window. Non-rectangular types keep their native shape, which is the point: a hopper-shaped menu is instantly legible to a player, and slots sit where the player already expects them.

The anvil is the one container with extra payload. Its click events also expose `getRenameText()` and `getAnvilView()`, letting you read what the player typed and adjust the experience cost before the result is taken. Both are `null` for every other container type.

## Things to know before you build

- **One `InventoryGUI` instance owns one `Inventory`.** Every player who opens the same instance shares its slots and handlers. That is convenient for a static announcement board and wrong for anything personalised: per-player prices, per-player paging. Build one instance per player when the content differs.
- **`title(...)` is a plain `String`** handed to `Bukkit.createInventory(...)`.
- **`init()` runs once per plugin class loader.** A second call throws `IllegalStateException`, and there is no shutdown or unregister API. Before `init()` a GUI still builds and opens, but no event reaches your callbacks, because the listener is not registered yet — which can look like a silent failure while debugging.
- **`ClickType` covers seven actions**: `LEFT`, `RIGHT`, `SHIFT_LEFT`, `SHIFT_RIGHT`, `NUMBER_KEY`, `MIDDLE`, `DOUBLE_CLICK`. Native actions outside that set are reported as `LEFT`, so do not branch on them.
- **Permissions judge slots, not items.** The library has no idea what a diamond in slot 11 is worth. Price checks, inventory space, cooldowns and stack validation on a placed item remain your callback's job.

## Contributing

Fork the repository, work on a branch, and open a pull request that explains the motivation rather than only the diff — the interesting part of a change here is usually which Bukkit action you were trying to catch. Keep changes incremental, preserve the existing Javadoc style, and make sure the module still compiles against the Paper API version pinned in `build.gradle.kts`.

## License

Distributed under the GNU General Public License v3, as described in [LICENSE](LICENSE).
