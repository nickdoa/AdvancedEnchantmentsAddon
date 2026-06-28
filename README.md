# AdvancedEnchantmentsAddon

Addon features, fixes, and custom effects for AdvancedEnchantments.

This public build does not include the Active Soul Enchant System and does not hook into SoulGems. It only depends on AdvancedEnchantments. EliteMobs is optional and is used only for summon spawn protection when present.

## Requirements

- A Spigot/Paper server running Java 17+
- AdvancedEnchantments
- Optional: EliteMobs

## Installation

1. Download `AdvancedEnchantmentsAddon-0.1.0.jar`.
2. Put the jar in your server `plugins` folder.
3. Make sure AdvancedEnchantments is installed.
4. Restart the server.
5. Edit the generated config files if needed.
6. Run `/aea reload` after config changes.

## What It Adds

- Custom AdvancedEnchantments effects for damage, healing, summons, silence, lifesteal, reflection, set effects, and utility mechanics.
- Heroic Upgrade item support.
- Godly Transmog Scroll support.
- Heroic Black Scroll support.
- Holy Water and Holy White Scroll corruption handling.
- Custom Lore Line item support.
- `/bless` command for curing negative potion effects.
- Death-save enchant handling such as Phoenix-style lethal damage prevention.
- Summon protection and summon throttling for guards, spirits, ruse zombies, obsidian guardians, and epidemic carriers.
- Custom set effects for Dimensional Shift and Winter's Mercy.

## Not Included

- No SoulGems dependency.
- No SoulGems inventory draining.
- No Soul Mode checks.
- No while-equipped Active Soul upkeep loop.

Soul costs that still exist in addon mechanics, such as death-save, Inertia, and Immortal, use souls stored on the relevant AdvancedEnchantments item.

## Commands

| Command | Description |
| --- | --- |
| `/aea reload` | Reloads the addon config files. |
| `/aea giveitem <player> <item> [amount] [success|max]` | Gives addon items with tab completion. |
| `/advancedenchantmentsaddon reload` | Full alias for `/aea reload`. |
| `/bless` | Removes configured negative potion effects from yourself. |

Addon item names for `/aea giveitem`:

- `heroicupgrade`
- `godlytransmogscroll`
- `heroicblackscroll`
- `holywater`
- `loreline`

## Permissions

| Permission | Default | Description |
| --- | --- | --- |
| `advancedenchantmentsaddon.reload` | op | Allows `/aea reload`. |
| `advancedenchantmentsaddon.test` | op | Allows temporary addon test commands. |
| `advancedenchantmentsaddon.bless` | true | Allows `/bless`. |
| `advancedenchantmentsaddon.bless.cooldown.bypass` | op | Bypasses the `/bless` cooldown. |
| `advancedenchantmentsaddon.heroicupgrade.give` | op | Allows giving Heroic Upgrades through `/ae giveitem` or `/aea giveitem`. |
| `advancedenchantmentsaddon.godlytransmog.give` | op | Allows giving Godly Transmog Scrolls through `/ae giveitem` or `/aea giveitem`. |
| `advancedenchantmentsaddon.heroicblackscroll.give` | op | Allows giving Heroic Black Scrolls through `/ae giveitem` or `/aea giveitem`. |
| `advancedenchantmentsaddon.holywater.give` | op | Allows giving Holy Water through `/ae giveitem` or `/aea giveitem`. |
| `advancedenchantmentsaddon.loreline.give` | op | Allows giving Lore Lines through `/ae giveitem` or `/aea giveitem`. |

## Config Files

| File | Purpose |
| --- | --- |
| `config.yml` | Item settings, messages, sounds, scroll behavior, `/bless`, and general addon options. |
| `enchants.yml` | Custom enchant mechanic settings, summon settings, silence behavior, Divine Immolation, lifesteal, destruction aura, death-save rules, and related combat tuning. |
| `sets.yml` | Custom set-effect settings for Dimensional Shift and Winter's Mercy. |
| `plugin.yml` | Bukkit plugin metadata, commands, dependencies, and permissions. |

## Custom Effects

The addon registers the following AdvancedEnchantments effects:

- `DISTANCE_DAMAGE`
- `DEATH_SAVE`
- `DAMAGE_NO_KNOCKBACK`
- `BLEED_NO_KNOCKBACK`
- `TRUE_INVISIBILITY`
- `RUSE_ZOMBIES`
- `VISUAL_SPIRITS`
- `OBSIDIAN_GUARDIANS`
- `EPIDEMIC_CARRIER`
- `DIVINE_IMMOLATION`
- `LIFESTEAL_ACCURATE`
- `CHAIN_LIFESTEAL`
- `PARADOX_HEAL`
- `DESTRUCTION_AURA`
- `RAGE_MULTIPLIER`
- `DAMAGE_MULTIPLIER`
- `EXECUTE_DAMAGE`
- `SILENCE`
- `SOLITUDE`
- `PERFECT_SOLITUDE`
- `ALIEN_HUNGER_RESIST`
- `ENLIGHTENED_HEAL`
- `BLOOD_LUST`
- `AEGIS`
- `BLOOD_LINK`
- `VALOR`
- `MARTYR_VALOR`
- `ENCHANT_REFLECT`
- `HEROIC_ENCHANT_REFLECT`
- `SOUL_HARDENED`
- `METAPHYSICAL`
- `POLYMORPHIC_METAPHYSICAL`
- `CREEPER_ARMOR`
- `CUSTOM_CREEPER_ARMOR`
- `INERTIA_CLEANSE`
- `IMMORTAL_REPAIR`
- `DIMINISH_NEXT`
- `VENGEFUL_DIMINISH_NEXT`
- `DIMENSIONAL_SHIFT`
- `WINTERS_MERCY`

## Building From Source

The build script expects an AdvancedEnchantments jar locally because that dependency is not included in the repository.

1. Create a `libs` folder.
2. Put your AdvancedEnchantments jar in `libs`.
3. Run:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1
```

By default, the script expects:

```text
libs\AdvancedEnchantments-9.22.9.jar
```

If your jar has a different name, pass it explicitly:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1 -AdvancedEnchantmentsJar "libs\AdvancedEnchantments-your-version.jar"
```

The built jar will be created at:

```text
build\libs\AdvancedEnchantmentsAddon-0.1.0.jar
```

## Notes

- `build/`, `libs/`, and local test enchantment configs are intentionally ignored by git.
- This addon is intended to be configured alongside AdvancedEnchantments, not as a replacement for it.
- If you are migrating from a private/internal build that used SoulGems, remove any old `soulgems.yml` expectations before installing this public version.
