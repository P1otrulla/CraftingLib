# CraftingLib 4.0

**Custom recipes for every station, on every Bukkit server from 1.8 to the latest Paper.**
Shaped and shapeless crafting, furnaces, brewing, stonecutter, smithing table, anvil and grindstone, with exact item
matching, ingredient amounts, recipe book integration and okaeri-configs serialization. One API, and the version
differences are handled for you.

<!-- GIF: hero - a custom item crafted from a named/enchanted ingredient -> docs/gifs/hero.gif -->

```java
this.craftingLib.getRecipeManager().registerRecipe(BukkitRecipes.shaped("super-pickaxe")
        .withPattern("DDD", " S ", " S ")
        .withIngredient('D', Material.DIAMOND_BLOCK, 3)
        .withIngredient('S', Material.STICK)
        .withResult(superPickaxe)
        .build());
```

---

## Contents

- [Features](#features)
- [Version support](#version-support)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Recipe types](#recipe-types)
- [Match modes](#match-modes)
- [Recipe book](#recipe-book)
- [Replacing vanilla recipes](#replacing-vanilla-recipes)
- [Configs (okaeri-configs)](#configs-okaeri-configs)
- [Modules](#modules)
- [How it works](#how-it-works)
- [Limitations](#limitations)
- [Building](#building)

---

## Features

- **Every recipe type**: shaped, shapeless, furnace / blast furnace / smoker / campfire, brewing, stonecutter, smithing, anvil and grindstone.
- **Exact matching**: ingredients are compared with their name, lore, enchantments and NBT, or by material only, or by plugin data only.
- **Ingredient amounts**: e.g. *3 diamond blocks in each slot*. Shift-click crafts as many as the grid and inventory allow.
- **2x2 and 3x3 grids**: small recipes work in the player inventory too. Patterns can be placed anywhere in the grid and mirrored.
- **Recipe book**: recipes are unlocked for players, grouped and sorted into tabs. On Paper, clicking a recipe fills the grid correctly, including exact items and amounts.
- **Replacing vanilla recipes**: remove and block recipes with the same result or input.
- **Crafter support** (1.21+): custom recipes work in the autocrafter.
- **Version adapters**: capabilities are detected at runtime (class and method presence), not from version strings, so forks and backports work too.
- **okaeri-configs**: every recipe type can be stored in and loaded from YAML.
- **Fail-fast API**: immutable recipes, builders that validate early, and error messages that name the recipe key and the problem.

<!-- GIF: recipe book autofill with an exact-match recipe -> docs/gifs/autofill.gif -->

---

## Version support

✅ supported · ⚠️ partial (see note) · ❌ not available

### Recipe types

| Feature | 1.8 – 1.10 | 1.11 | 1.12 | 1.13 | 1.14 – 1.15 | 1.16 – 1.19 | 1.20 | 1.21+ | Notes |
|---|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|---|
| Shaped / shapeless crafting | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 2x2 and 3x3 |
| Ingredient amounts | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | crafting, anvil, grindstone |
| Crafter (autocrafter) | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | only recipes with amount 1 per slot |
| Furnace | ⚠️ | ⚠️ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ | ✅ | before 1.14 the input matches by material only |
| Blast furnace / smoker / campfire | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | |
| Stonecutter | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | |
| Smithing table | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | template required on 1.20+, not allowed before |
| Anvil | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | |
| Grindstone | ❌ | ❌ | ❌ | ❌ | ❌ | ⚠️ | ✅ | ✅ | needs `PrepareGrindstoneEvent` (1.19.3+) |
| Brewing | ❌ | ❌ | ❌ | ❌ | ❌ | ⚠️ | ✅ | ✅ | **Paper only** (`PotionMix`) |

### Matching and recipe book

| Feature | 1.8 – 1.11 | 1.12 | 1.13 | 1.14 – 1.19 | 1.20 | 1.21+ | Notes |
|---|:-:|:-:|:-:|:-:|:-:|:-:|---|
| `EXACT` / `TYPE` match modes | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | |
| `PERSISTENT_DATA` match mode | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | crafting, anvil, grindstone, Paper brewing |
| Replace vanilla recipes | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | recipes are removed if the server allows it, otherwise blocked by the listener |
| Keyed recipes (`NamespacedKey`) | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | before 1.12, recipes are recognized by result |
| Recipe discovery (unlock for players) | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | crafting and cooking recipes |
| Recipe book groups | ❌ | ❌ | ⚠️ | ✅ | ✅ | ✅ | 1.13: crafting only; 1.14+: also cooking and stonecutting |
| Recipe book categories (tabs) | ❌ | ❌ | ❌ | ⚠️ | ✅ | ✅ | cooking 1.19.3+, crafting 1.20+ |
| Recipe resend to online players | ❌ | ❌ | ❌ | ⚠️ | ⚠️ | ✅ | Paper, where `addRecipe(recipe, true)` is implemented |
| Recipe book autofill | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | **Paper only** (`PlayerRecipeBookClickEvent`) |

Features the server lacks are skipped silently when they are cosmetic (recipe book groups, categories, resend).
They fail fast when they are functional: registering a recipe type the server cannot handle throws
`UnsupportedOperationException` with the reason, e.g. `Stonecutting recipes require Minecraft 1.14+`.

**Requirements:** Java 11+ at runtime (the bytecode targets Java 11). Compiled against Paper API 1.21.3.

---

## Installation

The library is not on a public repository yet, so publish it to your local Maven repository:

```
./gradlew publishToMavenLocal
```

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("dev.piotrulla:craftinglib-bukkit:4.0.0-SNAPSHOT")
    // optional: YAML serialization of recipes
    implementation("dev.piotrulla:craftinglib-bukkit-okaeri-serdes:4.0.0-SNAPSHOT")
}
```

Shade the library into your plugin and **relocate it**, so two plugins with different versions do not clash:

```kotlin
plugins {
    id("com.gradleup.shadow") version "<latest>"
}

tasks.shadowJar {
    relocate("dev.piotrulla.craftinglib", "your.plugin.libs.craftinglib")
}
```

---

## Quick start

```java
public final class ExamplePlugin extends JavaPlugin {

    private BukkitCraftingLib craftingLib;

    @Override
    public void onEnable() {
        this.craftingLib = BukkitCraftingLib.create(this);

        this.craftingLib.getRecipeManager().registerRecipe(BukkitRecipes.shaped("better-torches")
                .withPattern("C", "S")
                .withIngredient('C', Material.COAL)
                .withIngredient('S', Material.STICK)
                .withResult(Material.TORCH, 8)
                .replaceVanillaRecipes(true)
                .build());
    }

    @Override
    public void onDisable() {
        this.craftingLib.shutdown();
    }
}
```

`BukkitCraftingLib.create(plugin)` detects what the server supports and registers the listeners.
`shutdown()` unregisters every recipe (and locks it in players' recipe books again) and removes the listeners.

Every recipe type has its own manager:

| Manager | Recipe |
|---|---|
| `getRecipeManager()` | shaped / shapeless crafting |
| `getSmeltingManager()` | furnace, blast furnace, smoker, campfire |
| `getBrewingManager()` | brewing stand |
| `getStonecuttingManager()` | stonecutter |
| `getSmithingManager()` | smithing table |
| `getAnvilManager()` | anvil |
| `getGrindstoneManager()` | grindstone |

Managers share one API: `registerRecipe`, `unregisterRecipe(key)`, `unregisterAll`, `findRecipe(key)` and `getRecipes`.
Registering a key that is already registered replaces the old recipe.

---

## Recipe types

Every builder comes from `BukkitRecipes` and accepts both `ItemStack`s and `Material` shortcuts.
**Keys** must match `[a-z0-9/._-]`. On 1.12+ they become the recipe's `NamespacedKey`.

These settings are shared by every builder:

| Method | Default | Description |
|---|---|---|
| `withResult(item)` / `withResult(material, amount)` | required | result item (copied, so later changes to your stack do not leak in) |
| `withMatchMode(mode)` | `EXACT` | how ingredients are compared, see [Match modes](#match-modes) |
| `replaceVanillaRecipes(boolean)` | `false` | see [Replacing vanilla recipes](#replacing-vanilla-recipes) |
| `discoverable(boolean)` | `true` | unlock in every player's recipe book |
| `withRecipeBookGroup(group)` | none | recipes in the same group share one recipe book entry |
| `withRecipeBookCategory(category)` | server default | recipe book tab |

### Shaped

<!-- GIF: shaped recipe with amounts + shift-click -> docs/gifs/shaped.gif -->

```java
BukkitRecipes.shaped("super-pickaxe")
        .withPattern("DDD", " S ", " S ")
        .withIngredient('D', Material.DIAMOND_BLOCK, 3)   // 3 blocks in every D slot
        .withIngredient('S', Material.STICK)
        .withResult(superPickaxe)
        .withMirroring(false)                             // default: true
        .build();
```

- The pattern has 1–3 rows of 1–3 characters, and a space means an empty slot. Empty borders are trimmed, so the pattern can sit anywhere in the grid.
- Recipes that fit in 2x2 also work in the player inventory.
- The amount in a slot is the number of items needed there. Shift-click crafts as many times as the ingredients and free space allow.

### Shapeless

```java
BukkitRecipes.shapeless("quick-bread")
        .withIngredient(Material.WHEAT, 2)
        .withIngredient(Material.WHEAT)
        .withResult(Material.BREAD, 3)
        .build();
```

Up to 9 ingredients, placed in any slots.

### Smelting (furnace, blast furnace, smoker, campfire)

<!-- GIF: custom ore smelting in a blast furnace -> docs/gifs/smelting.gif -->

```java
BukkitRecipes.smelting("mithril-ingot")
        .withCookingType(CookingType.BLASTING)   // FURNACE (default), BLASTING, SMOKING, CAMPFIRE
        .withInput(mithrilOre)
        .withResult(mithrilIngot)
        .withExperience(1.5F)                    // default 0
        .withCookingTime(80)                     // ticks, default: vanilla time of the station
        .build();
```

The input amount must be 1. `replaceVanillaRecipes(true)` removes other recipes of the same station that take the same input.

### Brewing (Paper)

<!-- GIF: custom potion brewed from a custom ingredient -> docs/gifs/brewing.gif -->

```java
BukkitRecipes.brewing("super-night-vision")
        .withInput(nightVisionPotion)
        .withIngredient(Material.GLOW_BERRIES)
        .withResult(superNightVisionPotion)
        .build();
```

Registered as a Paper `PotionMix`. Input and ingredient amounts must be 1. Brewing has no vanilla recipes to replace.

### Stonecutter (1.14+)

```java
BukkitRecipes.stonecutting("cheap-bricks")
        .withInput(Material.STONE)
        .withResult(Material.STONE_BRICKS, 2)
        .withRecipeBookGroup("bricks")
        .build();
```

### Smithing table (1.16+)

```java
BukkitRecipes.smithing("mithril-sword")
        .withTemplate(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)   // 1.20+: required, before: not allowed
        .withBase(Material.DIAMOND_SWORD)
        .withAddition(mithrilIngot)
        .withResult(mithrilSword)
        .build();
```

### Anvil (1.11+)

<!-- GIF: anvil recipe taking levels -> docs/gifs/anvil.gif -->

```java
BukkitRecipes.anvil("sharpen-sword")
        .withLeft(Material.IRON_SWORD)
        .withRight(Material.FLINT, 4)    // both amounts are consumed
        .withResult(sharpSword)
        .withLevelCost(5)                // default 1
        .build();
```

The level cost is checked and taken from the player; creative players craft for free.

### Grindstone (1.19.3+)

<!-- GIF: grindstone recipe giving experience -> docs/gifs/grindstone.gif -->

```java
BukkitRecipes.grindstone("scrap-sword")
        .withInput(Material.DIAMOND_SWORD)
        .withSecondInput(Material.FLINT, 2)   // optional; without it the other slot must be empty
        .withResult(Material.DIAMOND, 2)
        .withExperience(10)                   // default 0
        .build();
```

The inputs can go in either grindstone slot. Their amounts are consumed and the experience goes to the player.

---

## Match modes

| Mode | Compares | Use for |
|---|---|---|
| `EXACT` (default) | material + all item data (`ItemStack#isSimilar`: name, lore, enchantments, NBT, damage) | custom items that must be identical |
| `TYPE` | material only (on 1.8–1.12 this also ignores data values such as wool colour) | "any wheat" recipes |
| `PERSISTENT_DATA` | material + `PersistentDataContainer`, ignoring name, lore, damage and enchantments | custom items tagged by your plugin, even after the player renames or enchants them |

Where each mode works:

| Recipe type | `EXACT` | `TYPE` | `PERSISTENT_DATA` |
|---|:-:|:-:|:-:|
| Crafting, anvil, grindstone | ✅ | ✅ | ✅ (1.14+) |
| Smelting | ✅ (1.14+) | ✅ | ❌ |
| Stonecutter, smithing | ✅ | ✅ | ❌ |
| Brewing (Paper) | ✅ | ✅ | ✅ (Paper with `PotionMix#createPredicateChoice`) |

Smelting, stonecutter and smithing recipes run inside the server, which cannot compare plugin data, so
`PERSISTENT_DATA` fails at registration with a `CraftingException`.

```java
BukkitRecipes.shapeless("reforge")
        .withIngredient(taggedBlade)            // any item with the same material and plugin data
        .withResult(reforgedBlade)
        .withMatchMode(MatchMode.PERSISTENT_DATA)
        .build();
```

---

## Recipe book

<!-- GIF: new recipe unlocked toast + grouped entry in the recipe book -> docs/gifs/recipe-book.gif -->

```java
BukkitRecipes.shaped("ruby-helmet")
        .withPattern("RRR", "R R")
        .withIngredient('R', ruby)
        .withResult(rubyHelmet)
        .withRecipeBookGroup("ruby-armor")                     // one entry for the whole set
        .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)  // tab
        .discoverable(true)                                    // default
        .build();
```

- **Discovery (1.13+)**: discoverable crafting and cooking recipes are unlocked for online players and again for everyone who joins. Unregistering locks them again.
- **Groups**: crafting (1.13+), cooking and stonecutter (1.14+).
- **Categories**: crafting recipes can use `BUILDING`, `REDSTONE`, `EQUIPMENT` and `MISC`; cooking recipes can use `FOOD`, `BLOCKS` and `MISC`. A category from the wrong book fails at `build()`. Other recipe types reject categories.
- **Resend (Paper)**: recipes added or removed while players are online are sent to their clients right away.
- **Autofill (Paper 1.21+)**: when a player clicks one of your crafting recipes in the recipe book, the library fills the grid itself. It picks items by the recipe's match mode and places the right amounts. Shift-click fills as many crafts as possible, and each further click adds one more craft. Items already in the grid go back to the player first. Vanilla autofill only knows materials, so without this it would put in plain items for an `EXACT` recipe.

---

## Replacing vanilla recipes

`replaceVanillaRecipes(true)` means different things per station:

| Type | Replaces |
|---|---|
| Crafting | every other recipe with the same **result material**; they are removed from the server and blocked in the crafting listener |
| Smelting | recipes of the **same station** with the **same input** |
| Stonecutter | recipes with the same input and the same result material |
| Smithing | recipes accepting the same template, base and addition |
| Brewing, anvil, grindstone | not supported (nothing to replace), `build()` fails |

Recipes registered by the library itself are never removed.

---

## Configs (okaeri-configs)

Every recipe type has an okaeri-configs serializer (okaeri-configs **6.1**). Register the pack next to `SerdesBukkit`,
which handles the item stacks:

```java
public final class RecipesConfig extends OkaeriConfig {

    private List<CraftingRecipe<ItemStack>> crafting = new ArrayList<>();
    private List<SmeltingRecipe<ItemStack>> smelting = new ArrayList<>();
    private List<GrindstoneRecipe<ItemStack>> grindstone = new ArrayList<>();

    // getters...
}

RecipesConfig config = ConfigManager.create(RecipesConfig.class, it -> {
    it.configure(opt -> {
        opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit(), new CraftingLibSerdesPack());
        opt.bindFile(new File(this.getDataFolder(), "recipes.yml"));
    });
    it.saveDefaults();
    it.load(true);
});

config.getCrafting().forEach(this.craftingLib.getRecipeManager()::registerRecipe);
config.getSmelting().forEach(this.craftingLib.getSmeltingManager()::registerRecipe);
config.getGrindstone().forEach(this.craftingLib.getGrindstoneManager()::registerRecipe);
```

Optional settings are only written when they differ from the default. `<item>` below stands for an item stack in the
`SerdesBukkit` format.

```yaml
crafting:
- key: super-pickaxe
  type: SHAPED
  pattern: [DDD, ' S ', ' S ']
  ingredients:
    D: <item>
    S: <item>
  mirrored: true
  result: <item>
  match-mode: EXACT
  replace-vanilla: true            # optional, default false
  discoverable: false              # optional, default true
  recipe-book-group: pickaxes      # optional
  recipe-book-category: EQUIPMENT  # optional
- key: quick-bread
  type: SHAPELESS
  ingredients: [<item>, <item>]
  result: <item>
  match-mode: TYPE
```

Fields specific to each type (on top of `key`, `result`, `match-mode` and the optional settings above):

| Type | Fields |
|---|---|
| Crafting | `type` (`SHAPED` / `SHAPELESS`), `pattern`, `ingredients`, `mirrored` |
| Smelting | `cooking-type` (default `FURNACE`), `input`, `experience`, `cooking-time` |
| Brewing | `input`, `ingredient` |
| Stonecutter | `input` |
| Smithing | `template` (optional), `base`, `addition` |
| Anvil | `left`, `right`, `level-cost` (default 1) |
| Grindstone | `input`, `second-input` (optional), `experience` (default 0) |

A missing required field fails with a clear message, e.g. `Recipe 'sharpen' is missing required field 'left'`.

---

## Modules

| Module | Contents | Depends on |
|---|---|---|
| `craftinglib-core` | Platform-independent recipes, patterns, grid matching, abstract builders, managers | annotations only |
| `craftinglib-bukkit` | `BukkitCraftingLib`, `BukkitRecipes`, version adapters, listeners, recipe book | core, paper-api (compileOnly) |
| `craftinglib-bukkit-okaeri-serdes` | serializers for every recipe type, `CraftingLibSerdesPack` | bukkit, okaeri-configs |
| `craftinglib-tests` | MockBukkit integration tests (not published, Java 17) | everything |

The core is generic over the item type (`ItemAdapter<T>`), so another platform only needs an adapter, concrete
builders and a `PlatformRecipeRegistrar`.

---

## How it works

- **Crafting**: each recipe is also registered as a material-level server recipe, only so that the server fires crafting events. The listener is the source of truth. It matches the grid against the registered recipes (item data, amounts, mirroring), sets the result and performs the craft by hand. When a grid matches our server recipe but none of our recipes, the result is cleared; the same happens for recipes whose result material is replaced.
- **Smelting, stonecutter, smithing, brewing**: registered as native server recipes (`RecipeChoice`, or `PotionMix` for brewing), so vanilla handles them.
- **Anvil, grindstone**: these stations have no recipe API, so listeners show the result on the prepare event and perform the take by hand.
- **Version adapters** are chosen by API capability: `RecipeChoice` present → modern (1.13+), `NamespacedKey` present → namespaced (1.12), otherwise legacy (1.8–1.11). Optional features (resend, categories, `AnvilView` and so on) are enabled only when the running server actually implements them. The check looks at the implementation, not just the interface. Version-specific classes are loaded only after the check passes.

---

## Limitations

- **1.8–1.11 have no recipe keys**: a server recipe counts as ours when its result is similar to one of our results, so two custom recipes with identical results cannot be unregistered separately there.
- **`replaceVanillaRecipes(true)` on crafting** removes every foreign recipe with the same result material, including other plugins' recipes.
- **Crafting result slot**: left, right and shift clicks are supported. Number keys and drop do nothing for custom recipes.
- **Crafting remainders**: only `*_BUCKET` → `BUCKET`.
- **Crafter**: only recipes consuming 1 item per slot; others are cancelled.
- **Anvil**: anvil damage is not simulated.
- **Threading**: not thread-safe, so register recipes on the main thread.

---

## Building

```
./gradlew build                 # compile + tests
./gradlew publishToMavenLocal   # install into ~/.m2
```

Build logic lives in `buildSrc` convention plugins (`craftinglib-java`, `craftinglib-paper`, `craftinglib-okaeri`,
`craftinglib-junit`, `craftinglib-publish`), so each module pulls only the repositories and dependencies it needs.
Versions are defined in `buildSrc/src/main/kotlin/Versions.kt`.

## License

Copyright (C) 2026 PIOTRULLA

CraftingLib is licensed under the [GNU Lesser General Public License v3.0](COPYING.LESSER), which builds on the
[GNU General Public License v3.0](COPYING).

In short:

- **You can** use CraftingLib in any plugin, open or closed source, free or paid, and shade it into your jar.
- **You must** keep the license notice, and publish the source of your **changes to CraftingLib itself** under the LGPL-3.0 if you distribute them.
- Your own plugin code that only uses the library stays under whatever license you choose.
