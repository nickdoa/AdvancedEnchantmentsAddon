package me.nickd.advancedenchantmentsaddon;

import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.api.ItemApplyEvent;
import net.advancedplugins.ae.api.SoulUseEvent;
import net.advancedplugins.ae.enchanthandler.enchantments.AdvancedEnchantment;
import net.advancedplugins.ae.enchanthandler.enchantments.AdvancedGroup;
import net.advancedplugins.ae.features.souls.SoulsAPI;
import net.advancedplugins.ae.impl.effects.api.AbilityPreactivateEvent;
import net.advancedplugins.ae.impl.effects.armorutils.ArmorEquipEvent;
import net.advancedplugins.ae.impl.effects.effects.abilities.AdvancedAbility;
import net.advancedplugins.ae.impl.effects.effects.actions.ActionExecution;
import net.advancedplugins.ae.impl.effects.effects.actions.ActionExecutionBuilder;
import net.advancedplugins.ae.impl.effects.effects.actions.execution.ExecutionTask;
import net.advancedplugins.ae.impl.effects.effects.actions.utils.RollItemType;
import net.advancedplugins.ae.impl.effects.effects.effects.AdvancedEffect;
import net.advancedplugins.ae.impl.utils.ACooldown;
import net.advancedplugins.ae.utils.nbt.NBTapi;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdvancedEnchantmentsAddonPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private static final java.util.Set<String> BLESS_REMOVE_EFFECT_TYPES = new java.util.HashSet<>(Arrays.asList(
            "BAD_OMEN",
            "BLINDNESS",
            "CONFUSION",
            "DARKNESS",
            "GLOWING",
            "HARM",
            "HUNGER",
            "LEVITATION",
            "POISON",
            "RAID_OMEN",
            "SLOW",
            "SLOW_DIGGING",
            "TRIAL_OMEN",
            "UNLUCK",
            "WEAKNESS",
            "WITHER"
    ));
    private static final java.util.Set<String> HEROIC_UPGRADE_ITEM_NAMES = new java.util.HashSet<>(Arrays.asList(
            "heroicupgrade",
            "heroicupgrades",
            "heroic_upgrade",
            "heroic_upgrades",
            "heroic-upgrade",
            "heroic-upgrades"
    ));
    private static final java.util.Set<String> GODLY_TRANSMOG_SCROLL_ITEM_NAMES = new java.util.HashSet<>(Arrays.asList(
            "godlytransmogscroll",
            "godlytransmogscrolls",
            "godly_transmog_scroll",
            "godly_transmog_scrolls",
            "godly-transmog-scroll",
            "godly-transmog-scrolls",
            "godlytransmog",
            "godly_transmog",
            "godly-transmog"
    ));
    private static final java.util.Set<String> HEROIC_BLACK_SCROLL_ITEM_NAMES = new java.util.HashSet<>(Arrays.asList(
            "heroicblackscroll",
            "heroicblackscrolls",
            "heroic_black_scroll",
            "heroic_black_scrolls",
            "heroic-black-scroll",
            "heroic-black-scrolls"
    ));
    private static final java.util.Set<String> HOLY_WATER_ITEM_NAMES = new java.util.HashSet<>(Arrays.asList(
            "holywater",
            "holywaters",
            "holy_water",
            "holy_waters",
            "holy-water",
            "holy-waters"
    ));
    private static final java.util.Set<String> LORE_LINE_ITEM_NAMES = new java.util.HashSet<>(Arrays.asList(
            "loreline",
            "lorelines",
            "lore_line",
            "lore_lines",
            "lore-line",
            "lore-lines",
            "itemlore",
            "itemlores",
            "item_lore",
            "item_lores",
            "item-lore",
            "item-lores"
    ));
    private static final List<String> ADDON_GIVE_ITEM_NAMES = Arrays.asList(
            "heroicupgrade",
            "godlytransmogscroll",
            "heroicblackscroll",
            "holywater",
            "loreline"
    );
    private static final List<String> GIVE_AMOUNT_COMPLETIONS = Arrays.asList("1", "8", "16", "32", "64");
    private static final java.util.Set<String> METAPHYSICAL_SLOW_ENCHANTS =
            new java.util.HashSet<>(Arrays.asList(
                    "trap",
                    "snare",
                    "pummel"
            ));
    private static final java.util.Set<String> POLYMORPHIC_METAPHYSICAL_SLOW_ENCHANTS =
            new java.util.HashSet<>(Arrays.asList(
                    "trap",
                    "snare",
                    "pummel",
                    "frozen",
                    "iceaspect"
            ));
    private static final List<String> DEFAULT_SILENCE_DISABLED_ENCHANTS = Arrays.asList(
            "endershift", "molten", "selfdestruct", "plaguecarrier", "ragdoll", "trickster",
            "smokebomb", "undeadruse", "roboticruse", "voodoo", "cactus", "shockwave",
            "hardened", "wither", "tank", "valor", "dodge", "guardians",
            "obsidianguardians", "heavy", "marksman", "diminish", "deathbringer",
            "armored", "enlighted", "enlightened", "deathgod", "planetarydeathbringer",
            "divineenlighted", "vengefuldiminish", "etherealdodge", "paladinarmored",
            "arrowdeflect", "arrowbreak", "metaphysical", "angelic", "spirits"
    );
    private static final Pattern HOLY_WHITE_SCROLL_COUNT_PATTERN = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");
    private static final String WINTER_MERCY_SNOWBALL_METADATA = "advancedenchantmentsaddon_winter_mercy_snowball";
    private static final String BLEED_DAMAGE_METADATA = "advancedenchantmentsaddon_bleed_damage";
    private static final String BLEED_DAMAGE_SOURCE_METADATA = "advancedenchantmentsaddon_bleed_damage_source";
    private static final String DESTRUCTION_DAMAGE_METADATA = "advancedenchantmentsaddon_destruction_damage";
    private static final String ADDON_SUMMON_OWNER_METADATA = "advancedenchantmentsaddon_summon_owner";
    private static final int GODLY_TRANSMOG_MENU_SIZE = 27;
    private static final int GODLY_TRANSMOG_MAX_ENCHANTS = 18;
    private static final int GODLY_TRANSMOG_PREVIEW_SLOT = 22;
    private static final String ADDON_SUMMON_METADATA = "advancedenchantmentsaddon_summon";
    private static final String ADDON_SUMMON_HIDDEN_NAME = "AEA_SUMMON";

    private final ThreadLocal<HolyWhiteScrollPendingApplication> pendingHolyWhiteScrollApplication = new ThreadLocal<>();
    private final Map<String, DeathSaveRule> deathSaveRules = new HashMap<>();
    private final Map<String, Long> messageThrottle = new HashMap<>();
    private final Map<String, Long> deathSaveCooldowns = new HashMap<>();
    private final Map<UUID, Long> blessCooldowns = new HashMap<>();
    private final Map<UUID, TransmogSession> transmogSessions = new HashMap<>();
    private final Map<UUID, PendingTextItemEdit> pendingTextItemEdits = new HashMap<>();
    private final Map<UUID, FallingBlock> dimensionalShiftBlocks = new HashMap<>();
    private final Map<UUID, Location> dimensionalShiftFrozenTargets = new HashMap<>();
    private final Map<UUID, RecentDamageSource> dimensionalShiftRecentDamageSources = new HashMap<>();
    private final Map<UUID, UUID> winterMercySnowballs = new HashMap<>();
    private final Map<UUID, WinterMercyReduction> winterMercyReductions = new HashMap<>();
    private final Map<UUID, Long> winterMercyCooldowns = new HashMap<>();
    private final Map<UUID, List<WinterMercySnowBlock>> winterMercySnowPatches = new HashMap<>();
    private final Map<WinterMercyBlockKey, Integer> winterMercyTemporaryBlockCounts = new HashMap<>();
    private final Map<UUID, BukkitTask> trueInvisibilityTasks = new HashMap<>();
    private final Map<UUID, Long> trueInvisibilityExpiresAt = new HashMap<>();
    private final Map<UUID, UUID> ruseZombieOwners = new HashMap<>();
    private final Map<UUID, UUID> ruseZombieTargets = new HashMap<>();
    private final Map<UUID, UUID> nativeGuardianOwners = new HashMap<>();
    private final Map<UUID, UUID> obsidianGuardianOwners = new HashMap<>();
    private final Map<UUID, UUID> obsidianGuardianTargets = new HashMap<>();
    private final Map<UUID, ObsidianGuardianSettings> obsidianGuardianSettings = new HashMap<>();
    private final Map<UUID, Long> obsidianGuardianAttackReadyTicks = new HashMap<>();
    private final Map<UUID, Long> guardSummonActivationTicks = new HashMap<>();
    private final Map<UUID, Long> inertiaSoundThrottles = new HashMap<>();
    private final Map<UUID, Long> inertiaMessageThrottles = new HashMap<>();
    private final Map<UUID, Integer> restoredDrunkSlownessLevels = new HashMap<>();
    private final Map<UUID, Integer> inertiaSuppressedDrunkSlownessLevels = new HashMap<>();
    private final Map<UUID, Long> customSlownessTargets = new HashMap<>();
    private final Map<UUID, Long> customSlownessCleanseAfterTicks = new HashMap<>();
    private final Map<UUID, Long> polymorphicMetaphysicalSlowResists = new HashMap<>();
    private final Map<UUID, Long> enemyStunCreeperSlownessTargets = new HashMap<>();
    private final Map<UUID, Long> soulHardenedBlockReadyTicks = new HashMap<>();
    private final Map<UUID, Long> immortalFeedbackTicks = new HashMap<>();
    private final Map<String, Long> enchantReflectCooldowns = new HashMap<>();
    private final Map<UUID, Map<String, Long>> silencedEnchantExpires = new HashMap<>();
    private final Map<String, Long> silenceCooldowns = new HashMap<>();
    private final Map<UUID, AegisAttackWindow> aegisAttackWindows = new HashMap<>();
    private final Map<UUID, DivineImmolationSession> divineImmolationSessions = new HashMap<>();
    private final Map<String, Long> divineImmolationNotificationTicks = new HashMap<>();
    private final Map<UUID, DestructionAuraSession> destructionAuraSessions = new HashMap<>();
    private final Map<UUID, java.util.Set<UUID>> destructionAuraDebuffedTargets = new HashMap<>();
    private final Map<UUID, Long> destructionVelocitySuppressions = new HashMap<>();
    private final Map<UUID, Long> noKnockbackVelocitySuppressions = new HashMap<>();
    private final Map<EntityDamageEvent, AdditiveDamageMultiplierState> additiveDamageMultipliers =
            new java.util.WeakHashMap<>();
    private final Map<UUID, RageComboState> rageCombos = new HashMap<>();
    private final Map<UUID, DamageCapState> nextDamageCaps = new HashMap<>();
    private final Map<UUID, EpidemicCarrierSettings> epidemicCarrierSettings = new HashMap<>();
    private final java.util.List<PendingSummonProtection> pendingSummonProtections = new java.util.ArrayList<>();
    private final java.util.Set<UUID> addonSummonIds = new java.util.HashSet<>();
    private final java.util.Set<UUID> visualSpiritIds = new java.util.HashSet<>();

    private FileConfiguration setsConfig;
    private FileConfiguration enchantsConfig;
    private int messageThrottleMillis;
    private int blessCooldownMillis;
    private boolean effectNotificationsEnabled;
    private BukkitTask serverTickTask;
    private long serverTick;
    private boolean debugDeathSave;
    private NamespacedKey heroicUpgradeItemKey;
    private NamespacedKey heroicUpgradeSuccessKey;
    private NamespacedKey heroicUpgradedTypeKey;
    private NamespacedKey heroicMaxDurabilityKey;
    private NamespacedKey heroicDamageKey;
    private NamespacedKey godlyTransmogScrollItemKey;
    private NamespacedKey heroicBlackScrollItemKey;
    private NamespacedKey heroicBlackScrollSuccessKey;
    private NamespacedKey holyWhiteScrollCorruptionCountKey;
    private NamespacedKey holyWhiteScrollCorruptedKey;
    private NamespacedKey holyWhiteScrollMaxApplicationsKey;
    private NamespacedKey holyWaterItemKey;
    private NamespacedKey holyWaterMaxApplicationsKey;
    private NamespacedKey loreLineItemKey;
    private NamespacedKey loreLineCountKey;
    private Material heroicUpgradeMaterial;
    private Material godlyTransmogScrollMaterial;
    private Material heroicBlackScrollMaterial;
    private Material holyWaterMaterial;
    private Material loreLineMaterial;
    private int heroicDefaultSuccess;
    private int heroicBlackScrollDefaultMinSuccess;
    private int heroicBlackScrollDefaultMaxSuccess;
    private int holyWhiteScrollMaxApplications;
    private int holyWaterDefaultMaxApplications;
    private int loreLineMaxLinesPerItem;
    private double heroicDurabilityBonusMultiplier;

    @Override
    public void onEnable() {
        initializeKeys();
        saveDefaultConfig();
        reloadPluginConfig();
        registerCustomEffects();

        Bukkit.getPluginManager().registerEvents(this, this);
        registerEliteMobsSpawnBlocker();
        if (getCommand("aeaddon") != null) {
            getCommand("aeaddon").setExecutor(this);
            getCommand("aeaddon").setTabCompleter(this);
        }
        if (getCommand("bless") != null) {
            getCommand("bless").setExecutor(this);
            getCommand("bless").setTabCompleter(this);
        }

        startServerTickTask();
        getLogger().info("Loaded " + deathSaveRules.size() + " death-save rule(s).");
    }

    private void initializeKeys() {
        heroicUpgradeItemKey = new NamespacedKey(this, "heroic_upgrade_item");
        heroicUpgradeSuccessKey = new NamespacedKey(this, "heroic_upgrade_success");
        heroicUpgradedTypeKey = new NamespacedKey(this, "heroic_upgraded_type");
        heroicMaxDurabilityKey = new NamespacedKey(this, "heroic_max_durability");
        heroicDamageKey = new NamespacedKey(this, "heroic_damage");
        godlyTransmogScrollItemKey = new NamespacedKey(this, "godly_transmog_scroll_item");
        heroicBlackScrollItemKey = new NamespacedKey(this, "heroic_black_scroll_item");
        heroicBlackScrollSuccessKey = new NamespacedKey(this, "heroic_black_scroll_success");
        holyWhiteScrollCorruptionCountKey = new NamespacedKey(this, "holy_white_scroll_corruption_count");
        holyWhiteScrollCorruptedKey = new NamespacedKey(this, "holy_white_scroll_corrupted");
        holyWhiteScrollMaxApplicationsKey = new NamespacedKey(this, "holy_white_scroll_max_applications");
        holyWaterItemKey = new NamespacedKey(this, "holy_water_item");
        holyWaterMaxApplicationsKey = new NamespacedKey(this, "holy_water_max_applications");
        loreLineItemKey = new NamespacedKey(this, "lore_line_item");
        loreLineCountKey = new NamespacedKey(this, "lore_line_count");
    }

    private void registerCustomEffects() {
        registerCustomEffect(new DistanceDamageEffect(this));
        registerCustomEffect(new DeathSaveEffect(this));
        registerCustomEffect(new NoKnockbackDamageEffect(this));
        registerCustomEffect(new NoKnockbackBleedEffect(this));
        registerCustomEffect(new TrueInvisibilityEffect(this));
        registerCustomEffect(new RuseZombiesEffect(this));
        registerCustomEffect(new VisualSpiritsEffect(this));
        registerCustomEffect(new ObsidianGuardiansEffect(this));
        registerCustomEffect(new EpidemicCarrierEffect(this));
        registerCustomEffect(new DivineImmolationEffect(this));
        registerCustomEffect(new AccurateLifestealEffect(this));
        registerCustomEffect(new ChainLifestealEffect(this));
        registerCustomEffect(new ParadoxHealEffect(this));
        registerCustomEffect(new DestructionAuraEffect(this));
        registerCustomEffect(new RageMultiplierEffect(this));
        registerCustomEffect(new DamageMultiplierEffect(this));
        registerCustomEffect(new ExecuteDamageEffect(this));
        registerCustomEffect(new SilenceEffect(this));
        registerCustomEffect(new SolitudeEffect(this));
        registerCustomEffect(new PerfectSolitudeEffect(this));
        registerCustomEffect(new AlienHungerResistEffect(this));
        registerCustomEffect(new EnlightenedHealEffect(this));
        registerCustomEffect(new BloodLustEffect(this));
        registerCustomEffect(new AegisEffect(this));
        registerCustomEffect(new BloodLinkEffect(this));
        registerCustomEffect(new ValorEffect(this));
        registerCustomEffect(new MartyrValorEffect(this));
        registerCustomEffect(new EnchantReflectEffect(this));
        registerCustomEffect(new HeroicEnchantReflectEffect(this));
        registerCustomEffect(new SoulHardenedEffect(this));
        registerCustomEffect(new MetaphysicalEffect(this));
        registerCustomEffect(new PolymorphicMetaphysicalEffect(this));
        registerCustomEffect(new CreeperArmorEffect(this));
        registerCustomEffect(new CustomCreeperArmorEffect(this));
        registerCustomEffect(new InertiaCleanseEffect(this));
        registerCustomEffect(new ImmortalRepairEffect(this));
        registerCustomEffect(new DiminishNextEffect(this));
        registerCustomEffect(new VengefulDiminishNextEffect(this));
        registerCustomEffect(new DimensionalShiftEffect(this));
        registerCustomEffect(new WintersMercyEffect(this));
    }

    private void registerCustomEffect(AdvancedEffect effect) {
        try {
            AEAPI.registerEffect(this, effect);
            getLogger().info("Registered AdvancedEnchantments effect " + effect.getName() + ".");
        } catch (RuntimeException exception) {
            getLogger().warning("Could not register " + effect.getName() + ": " + exception.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void registerEliteMobsSpawnBlocker() {
        if (Bukkit.getPluginManager().getPlugin("EliteMobs") == null) {
            return;
        }

        try {
            Class<?> rawEventClass = Class.forName("com.magmaguy.elitemobs.api.EliteMobSpawnEvent");
            if (!Event.class.isAssignableFrom(rawEventClass)) {
                return;
            }

            Class<? extends Event> eventClass = (Class<? extends Event>) rawEventClass;
            EventExecutor executor = (listener, event) -> cancelEliteMobSpawnForSummon(event);
            Bukkit.getPluginManager().registerEvent(
                    eventClass, this, EventPriority.HIGHEST, executor, this, false);
            getLogger().info("Registered EliteMobs summon-spawn protection.");
        } catch (ClassNotFoundException ignored) {
            // EliteMobs is present but this version does not expose the spawn event we know how to use.
        } catch (RuntimeException exception) {
            getLogger().warning("Could not register EliteMobs summon-spawn protection: "
                    + exception.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (serverTickTask != null) {
            serverTickTask.cancel();
            serverTickTask = null;
        }
        for (TransmogSession session : new java.util.ArrayList<>(transmogSessions.values())) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player != null && player.isOnline() && !session.applied()) {
                refundTransmogScroll(player, session);
            }
        }
        transmogSessions.clear();
        for (PendingTextItemEdit session : new java.util.ArrayList<>(pendingTextItemEdits.values())) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player != null && player.isOnline()) {
                refundPendingTextItem(player, session);
            }
        }
        pendingTextItemEdits.clear();
        for (FallingBlock block : new java.util.ArrayList<>(dimensionalShiftBlocks.values())) {
            if (block != null && block.isValid()) {
                block.remove();
            }
        }
        dimensionalShiftBlocks.clear();
        dimensionalShiftFrozenTargets.clear();
        dimensionalShiftRecentDamageSources.clear();
        for (UUID snowballId : new java.util.ArrayList<>(winterMercySnowballs.keySet())) {
            Entity snowball = Bukkit.getEntity(snowballId);
            if (snowball != null && snowball.isValid()) {
                snowball.removeMetadata(WINTER_MERCY_SNOWBALL_METADATA, this);
                snowball.remove();
            }
        }
        winterMercySnowballs.clear();
        winterMercyReductions.clear();
        winterMercyCooldowns.clear();
        restoreAllWinterMercySnowPatches();
        winterMercyTemporaryBlockCounts.clear();
        for (UUID playerId : new java.util.ArrayList<>(trueInvisibilityTasks.keySet())) {
            stopTrueInvisibility(playerId, true);
        }
        trueInvisibilityTasks.clear();
        trueInvisibilityExpiresAt.clear();
        ruseZombieOwners.clear();
        ruseZombieTargets.clear();
        nativeGuardianOwners.clear();
        obsidianGuardianOwners.clear();
        obsidianGuardianTargets.clear();
        obsidianGuardianSettings.clear();
        obsidianGuardianAttackReadyTicks.clear();
        epidemicCarrierSettings.clear();
        guardSummonActivationTicks.clear();
        inertiaSoundThrottles.clear();
        inertiaMessageThrottles.clear();
        inertiaSuppressedDrunkSlownessLevels.clear();
        customSlownessTargets.clear();
        customSlownessCleanseAfterTicks.clear();
        polymorphicMetaphysicalSlowResists.clear();
        enemyStunCreeperSlownessTargets.clear();
        soulHardenedBlockReadyTicks.clear();
        immortalFeedbackTicks.clear();
        for (UUID playerId : new java.util.ArrayList<>(restoredDrunkSlownessLevels.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                clearRestoredDrunkSlowness(player);
            }
        }
        restoredDrunkSlownessLevels.clear();
        enchantReflectCooldowns.clear();
        silencedEnchantExpires.clear();
        silenceCooldowns.clear();
        aegisAttackWindows.clear();
        for (DivineImmolationSession session : new java.util.ArrayList<>(divineImmolationSessions.values())) {
            session.cancel();
        }
        divineImmolationSessions.clear();
        divineImmolationNotificationTicks.clear();
        for (DestructionAuraSession session : new java.util.ArrayList<>(destructionAuraSessions.values())) {
            session.cancel();
        }
        destructionAuraSessions.clear();
        destructionAuraDebuffedTargets.clear();
        destructionVelocitySuppressions.clear();
        noKnockbackVelocitySuppressions.clear();
        additiveDamageMultipliers.clear();
        rageCombos.clear();
        nextDamageCaps.clear();
        pendingSummonProtections.clear();
        addonSummonIds.clear();
        for (UUID spiritId : new java.util.ArrayList<>(visualSpiritIds)) {
            Entity spirit = Bukkit.getEntity(spiritId);
            if (spirit != null && spirit.isValid()) {
                spirit.remove();
            }
        }
        visualSpiritIds.clear();
        pendingHolyWhiteScrollApplication.remove();
    }

    private void reloadPluginConfig() {
        reloadConfig();
        getConfig().options().copyDefaults(true);
        reloadSetsConfig();
        reloadEnchantsConfig();
        patchAdvancedEnchantmentsEffectTargets();
        removeRetiredConfigKeys();
        saveConfig();
        deathSaveRules.clear();

        FileConfiguration enchantConfig = getEnchantsConfig();
        messageThrottleMillis = Math.max(0, getConfig().getInt("settings.message-throttle-seconds", 2)) * 1000;
        blessCooldownMillis = Math.max(0, getConfig().getInt("settings.bless.cooldown-seconds", 60)) * 1000;
        effectNotificationsEnabled = enchantConfig.getBoolean("settings.effect-notifications.enabled", true);
        debugDeathSave = enchantConfig.getBoolean("settings.debug-death-save", false);
        heroicDefaultSuccess = clamp(getConfig().getInt("heroic-upgrade.default-success", 100), 0, 100);
        heroicDurabilityBonusMultiplier = 1.0D
                + Math.max(0.0D, getConfig().getDouble("heroic-upgrade.durability-bonus-percent", 5.0D)) / 100.0D;
        heroicUpgradeMaterial = Material.matchMaterial(getConfig().getString("heroic-upgrade.item.material", "YELLOW_DYE"));
        if (heroicUpgradeMaterial == null || !heroicUpgradeMaterial.isItem()) {
            heroicUpgradeMaterial = Material.YELLOW_DYE;
        }
        godlyTransmogScrollMaterial = Material.matchMaterial(getConfig().getString(
                "godly-transmog-scroll.item.material", "PAPER"));
        if (godlyTransmogScrollMaterial == null || !godlyTransmogScrollMaterial.isItem()) {
            godlyTransmogScrollMaterial = Material.PAPER;
        }
        heroicBlackScrollMaterial = Material.matchMaterial(getConfig().getString(
                "heroic-black-scroll.item.material", "BLACK_DYE"));
        if (heroicBlackScrollMaterial == null || !heroicBlackScrollMaterial.isItem()) {
            heroicBlackScrollMaterial = Material.BLACK_DYE;
        }
        holyWaterMaterial = Material.matchMaterial(getConfig().getString(
                "holy-water.item.material", "MILK_BUCKET"));
        if (holyWaterMaterial == null || !holyWaterMaterial.isItem()) {
            holyWaterMaterial = Material.MILK_BUCKET;
        }
        loreLineMaterial = Material.matchMaterial(getConfig().getString(
                "lore-line.item.material", "ORANGE_DYE"));
        if (loreLineMaterial == null || !loreLineMaterial.isItem()) {
            loreLineMaterial = Material.ORANGE_DYE;
        }
        heroicBlackScrollDefaultMinSuccess = clamp(getConfig().getInt(
                "heroic-black-scroll.default-success-min", 17), 0, 100);
        heroicBlackScrollDefaultMaxSuccess = clamp(getConfig().getInt(
                "heroic-black-scroll.default-success-max", 20), 0, 100);
        if (heroicBlackScrollDefaultMaxSuccess < heroicBlackScrollDefaultMinSuccess) {
            int previousMin = heroicBlackScrollDefaultMinSuccess;
            heroicBlackScrollDefaultMinSuccess = heroicBlackScrollDefaultMaxSuccess;
            heroicBlackScrollDefaultMaxSuccess = previousMin;
        }
        holyWhiteScrollMaxApplications = Math.max(1, getConfig().getInt(
                "holy-white-scroll-corruption.max-applications", 3));
        holyWaterDefaultMaxApplications = Math.max(1, getConfig().getInt(
                "holy-water.default-max-applications", 4));
        loreLineMaxLinesPerItem = Math.max(1, getConfig().getInt("lore-line.max-lines-per-item", 2));
        ConfigurationSection deathSaves = enchantConfig.getConfigurationSection("death-save-enchants");
        if (deathSaves != null) {
            for (String key : deathSaves.getKeys(false)) {
                ConfigurationSection section = deathSaves.getConfigurationSection(key);
                if (section == null || !section.getBoolean("enabled", true)) {
                    continue;
                }

                DeathSaveRule rule = new DeathSaveRule(key, section);
                deathSaveRules.put(rule.enchantName(), rule);
            }
        }
    }

    private void removeRetiredConfigKeys() {
        getConfig().set("settings.enchant-proc-throttle", null);
        getConfig().set("valor", null);
    }

    private void patchAdvancedEnchantmentsEffectTargets() {
        Plugin advancedEnchantments = Bukkit.getPluginManager().getPlugin("AdvancedEnchantments");
        if (advancedEnchantments == null || advancedEnchantments.getDataFolder() == null
                || !advancedEnchantments.getDataFolder().isDirectory()) {
            return;
        }

        java.util.concurrent.atomic.AtomicInteger changedFiles = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger changedLines = new java.util.concurrent.atomic.AtomicInteger();
        try (java.util.stream.Stream<java.nio.file.Path> paths =
                     Files.walk(advancedEnchantments.getDataFolder().toPath(), 4)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.endsWith(".yml") || name.endsWith(".yaml");
                    })
                    .forEach(path -> patchAdvancedEnchantmentsEffectTargetFile(path, changedFiles, changedLines));
        } catch (IOException exception) {
            getLogger().warning("Could not scan AdvancedEnchantments config files for addon effect targets: "
                    + exception.getMessage());
        }

        if (changedLines.get() > 0) {
            getLogger().warning("Patched " + changedLines.get() + " targetless addon effect line(s) in "
                    + changedFiles.get() + " AdvancedEnchantments file(s). Reloading AdvancedEnchantments so AE uses them.");
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ae reload");
                }
            }, 20L);
        }
    }

    private void patchAdvancedEnchantmentsEffectTargetFile(java.nio.file.Path path,
                                                          java.util.concurrent.atomic.AtomicInteger changedFiles,
                                                          java.util.concurrent.atomic.AtomicInteger changedLines) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> patched = new java.util.ArrayList<>(lines.size());
            int localChanges = 0;
            for (String line : lines) {
                String patchedLine = patchAddonEffectTargetLine(line);
                if (!patchedLine.equals(line)) {
                    localChanges++;
                }
                patched.add(patchedLine);
            }

            if (localChanges <= 0) {
                return;
            }

            Files.write(path, patched, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            changedFiles.incrementAndGet();
            changedLines.addAndGet(localChanges);
        } catch (IOException exception) {
            getLogger().warning("Could not patch addon effect targets in " + path + ": " + exception.getMessage());
        }
    }

    private String patchAddonEffectTargetLine(String line) {
        if (line == null || line.contains(" @")) {
            return line;
        }

        String trimmed = line.trim();
        if (!trimmed.startsWith("- ")) {
            return line;
        }

        int contentStart = line.indexOf('-') + 1;
        while (contentStart < line.length() && Character.isWhitespace(line.charAt(contentStart))) {
            contentStart++;
        }
        if (contentStart >= line.length()) {
            return line;
        }

        char quote = line.charAt(contentStart);
        boolean quoted = quote == '\'' || quote == '"';
        int effectStart = quoted ? contentStart + 1 : contentStart;
        int effectEnd = line.length();
        if (quoted) {
            int quotedEnd = line.lastIndexOf(quote);
            if (quotedEnd > effectStart) {
                effectEnd = quotedEnd;
            }
        }

        String effectText = line.substring(effectStart, effectEnd).trim();
        if (effectText.isEmpty() || effectText.contains(" @")) {
            return line;
        }

        String effectName = effectText.split("\\s+", 2)[0].split(":", 2)[0];
        String target = defaultAddonEffectTarget(effectName);
        if (target == null) {
            return line;
        }

        return line.substring(0, effectEnd) + " " + target + line.substring(effectEnd);
    }

    private void removeRetiredEnchantConfigKeys(File enchantsFile) {
        FileConfiguration enchantConfig = getEnchantsConfig();
        boolean changed = false;

        if (enchantConfig.contains("divine-immolation.burn-seconds")) {
            enchantConfig.set("divine-immolation.burn-seconds", null);
            changed = true;
        }
        if (enchantConfig.contains("destruction.levels")) {
            enchantConfig.set("destruction.levels", null);
            changed = true;
        }
        if (enchantConfig.contains("settings.enchant-proc-throttle")) {
            enchantConfig.set("settings.enchant-proc-throttle", null);
            changed = true;
        }
        if (enchantConfig.contains("valor")) {
            enchantConfig.set("valor", null);
            changed = true;
        }

        int divineIntervalTicks = Math.max(1, enchantConfig.getInt("divine-immolation.interval-ticks", 20));
        int minimumVisualWitherTicks = Math.min(39, divineIntervalTicks + 5);
        int visualWitherTicks = enchantConfig.getInt("divine-immolation.wither-duration-ticks", -1);
        if (visualWitherTicks > 0 && visualWitherTicks < minimumVisualWitherTicks) {
            enchantConfig.set("divine-immolation.wither-duration-ticks", minimumVisualWitherTicks);
            changed = true;
        }
        if ("&a&l%victimname%'s%space%epidemic%space%carrier".equals(
                enchantConfig.getString("epidemic-carriers.name", ""))) {
            enchantConfig.set("epidemic-carriers.name", "&a&l%victimname%'s%space%Stun%space%Creeper");
            changed = true;
        }
        if (enchantConfig.getBoolean("ruse-zombies.spawn-at-target", false)) {
            enchantConfig.set("ruse-zombies.spawn-at-target", false);
            changed = true;
        }
        if (enchantConfig.getBoolean("epidemic-carriers.spawn-at-target", false)) {
            enchantConfig.set("epidemic-carriers.spawn-at-target", false);
            changed = true;
        }
        if (enchantConfig.getInt("settings.guard-summon-throttle.cooldown-ticks", -1) == 60) {
            enchantConfig.set("settings.guard-summon-throttle.cooldown-ticks", 100);
            changed = true;
        }
        List<String> summonThrottleExemptions =
                enchantConfig.getStringList("settings.guard-summon-throttle.exempt-enchants");
        boolean hasSelfDestructExemption = false;
        for (String exemption : summonThrottleExemptions) {
            if (normalize(exemption).equals("selfdestruct")) {
                hasSelfDestructExemption = true;
                break;
            }
        }
        if (!hasSelfDestructExemption) {
            summonThrottleExemptions.add("selfdestruct");
            enchantConfig.set("settings.guard-summon-throttle.exempt-enchants", summonThrottleExemptions);
            changed = true;
        }
        if ("&6&l** INERTIA **".equals(enchantConfig.getString("inertia.message", ""))) {
            enchantConfig.set("inertia.message", "&6&l* INERTIA [&7%souls% souls remaining&6&l] *");
            changed = true;
        }
        if (!enchantConfig.contains("inertia.cleanse-delay-ticks")) {
            enchantConfig.set("inertia.cleanse-delay-ticks", 3);
            changed = true;
        }
        List<String> reflectBlockedEnchants = enchantConfig.getStringList("enchant-reflect.blocked-enchants");
        boolean changedReflectBlockedEnchants = false;
        for (String requiredBlockedEnchant : Arrays.asList("rage", "solitude", "perfectsolitude")) {
            boolean present = false;
            for (String blockedEnchant : reflectBlockedEnchants) {
                if (normalize(blockedEnchant).equals(requiredBlockedEnchant)) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                reflectBlockedEnchants.add(requiredBlockedEnchant);
                changedReflectBlockedEnchants = true;
            }
        }
        if (changedReflectBlockedEnchants) {
            enchantConfig.set("enchant-reflect.blocked-enchants", reflectBlockedEnchants);
            changed = true;
        }
        List<String> silenceDisabledEnchants = enchantConfig.getStringList("silence.disabled-enchants");
        boolean changedSilenceDisabledEnchants = false;
        for (String requiredDisabledEnchant : Arrays.asList("deathbringer", "planetarydeathbringer")) {
            boolean present = false;
            for (String disabledEnchant : silenceDisabledEnchants) {
                if (normalize(disabledEnchant).equals(requiredDisabledEnchant)) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                silenceDisabledEnchants.add(requiredDisabledEnchant);
                changedSilenceDisabledEnchants = true;
            }
        }
        if (changedSilenceDisabledEnchants) {
            enchantConfig.set("silence.disabled-enchants", silenceDisabledEnchants);
            changed = true;
        }
        if ("&6&l* ENCHANT REFLECT * &8%enchant% reflected to %attacker name%.".equals(
                enchantConfig.getString("enchant-reflect.message", ""))) {
            enchantConfig.set("enchant-reflect.message",
                    "&6&l* ENCHANT REFLECT * &8Reflected &6%enchant% &8back to &f%attacker name%&8.");
            changed = true;
        }
        if ("&d&l* HEROIC ENCHANT REFLECT * &8%enchant% reflected to %attacker name%.".equals(
                enchantConfig.getString("heroic-enchant-reflect.message", ""))) {
            enchantConfig.set("heroic-enchant-reflect.message",
                    "&d&l* HEROIC ENCHANT REFLECT * &8Reflected &d%enchant% &8back to &f%attacker name%&8.");
            changed = true;
        }
        int restoredDrunkDuration = enchantConfig.getInt("inertia.restored-drunk-slowness-duration-ticks", -1);
        if (restoredDrunkDuration == 120000 || restoredDrunkDuration == Integer.MAX_VALUE) {
            enchantConfig.set("inertia.restored-drunk-slowness-duration-ticks", -1);
            changed = true;
        }
        if (!enchantConfig.contains("immortal.feedback-cooldown-ticks")) {
            enchantConfig.set("immortal.enabled", true);
            enchantConfig.set("immortal.interval-ticks", 20);
            enchantConfig.set("immortal.souls-per-piece", 5);
            enchantConfig.set("immortal.feedback-cooldown-ticks", 200);
            enchantConfig.set("immortal.sounds", Collections.singletonList("ENTITY_PLAYER_LEVELUP:0.5:0.5"));
            enchantConfig.set("immortal.message", Arrays.asList(
                    "",
                    "&6&l** IMMORTAL ** &8[%pieces%x]",
                    "&7You have &7&n%souls%&r &7souls left.",
                    ""));
            changed = true;
        }

        if (changed) {
            saveYamlConfig(enchantConfig, enchantsFile, "enchants.yml");
        }
    }

    private void reloadEnchantsConfig() {
        File enchantsFile = new File(getDataFolder(), "enchants.yml");
        boolean existed = enchantsFile.exists();
        if (!existed) {
            saveResource("enchants.yml", false);
        }

        enchantsConfig = YamlConfiguration.loadConfiguration(enchantsFile);
        try {
            String defaultText = readResourceText("enchants.yml");
            if (!defaultText.isEmpty()) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new StringReader(defaultText));
                if (appendMissingDefaults(enchantsFile, enchantsConfig, defaults, defaultText)) {
                    enchantsConfig = YamlConfiguration.loadConfiguration(enchantsFile);
                }
                enchantsConfig.setDefaults(defaults);
            }
        } catch (IOException exception) {
            getLogger().warning("Could not load enchants.yml defaults: " + exception.getMessage());
        }

        migrateLegacyEnchantConfig(enchantsFile, existed);
        removeRetiredEnchantConfigKeys(enchantsFile);
    }

    private void migrateLegacyEnchantConfig(File enchantsFile, boolean enchantsFileExisted) {
        boolean changedEnchants = false;
        boolean changedConfig = false;
        for (String path : legacyEnchantConfigPaths()) {
            if (!getConfig().contains(path)) {
                continue;
            }

            if (!enchantsFileExisted || !enchantsConfig.contains(path)) {
                enchantsConfig.set(path, getConfig().get(path));
                changedEnchants = true;
            }
            getConfig().set(path, null);
            changedConfig = true;
        }

        if (changedEnchants) {
            saveYamlConfig(enchantsConfig, enchantsFile, "enchants.yml");
        }
        if (changedConfig) {
            saveConfig();
        }
    }

    private static List<String> legacyEnchantConfigPaths() {
        return Arrays.asList(
                "settings.debug-death-save",
                "settings.guard-summon-throttle",
                "settings.enchants",
                "settings.native-damage",
                "settings.effect-notifications",
                "ruse-zombies",
                "visual-spirits",
                "obsidian-guardians",
                "epidemic-carriers",
                "divine-immolation",
                "inertia",
                "diminish",
                "vengeful-diminish",
                "enchant-reflect",
                "heroic-enchant-reflect",
                "destruction",
                "rage",
                "chain-lifesteal",
                "death-save-enchants");
    }

    private void saveYamlConfig(FileConfiguration config, File file, String name) {
        try {
            config.save(file);
        } catch (IOException exception) {
            getLogger().warning("Could not save " + name + ": " + exception.getMessage());
        }
    }

    private FileConfiguration getEnchantsConfig() {
        return enchantsConfig == null ? getConfig() : enchantsConfig;
    }

    private void reloadSetsConfig() {
        File setsFile = new File(getDataFolder(), "sets.yml");
        if (!setsFile.exists()) {
            saveResource("sets.yml", false);
        }

        setsConfig = YamlConfiguration.loadConfiguration(setsFile);
        try {
            String defaultText = readResourceText("sets.yml");
            if (!defaultText.isEmpty()) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new StringReader(defaultText));
                if (appendMissingDefaults(setsFile, setsConfig, defaults, defaultText)) {
                    setsConfig = YamlConfiguration.loadConfiguration(setsFile);
                }
                setsConfig.setDefaults(defaults);
            }
        } catch (IOException exception) {
            getLogger().warning("Could not load sets.yml defaults: " + exception.getMessage());
        }
    }

    private FileConfiguration getSetsConfig() {
        return setsConfig == null ? getConfig() : setsConfig;
    }

    private String readResourceText(String resourceName) throws IOException {
        try (InputStream stream = getResource(resourceName)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private boolean appendMissingDefaults(File targetFile, FileConfiguration liveConfig,
                                          YamlConfiguration defaults, String defaultText) throws IOException {
        String updated = targetFile.exists()
                ? Files.readString(targetFile.toPath(), StandardCharsets.UTF_8)
                : "";
        List<String> missingSections = new java.util.ArrayList<>();
        for (String key : defaults.getKeys(false)) {
            if (!liveConfig.isSet(key)) {
                String section = extractTopLevelYamlSection(defaultText, key);
                if (!section.trim().isEmpty()) {
                    missingSections.add(section.stripTrailing());
                }
                continue;
            }

            ConfigurationSection defaultSection = defaults.getConfigurationSection(key);
            if (defaultSection == null || !liveConfig.isConfigurationSection(key)) {
                continue;
            }

            for (String childKey : defaultSection.getKeys(false)) {
                if (liveConfig.isSet(key + "." + childKey)) {
                    continue;
                }

                String childBlock = extractYamlChildBlock(defaultText, key, childKey);
                if (!childBlock.trim().isEmpty()) {
                    updated = insertYamlChildBlock(updated, key, childBlock.stripTrailing());
                }
            }
        }

        String existing = updated;
        String newline = existing.contains("\r\n") ? "\r\n" : "\n";
        if (!missingSections.isEmpty()) {
            StringBuilder append = new StringBuilder();
            if (!existing.isEmpty() && !existing.endsWith("\n") && !existing.endsWith("\r")) {
                append.append(newline);
            }
            if (!existing.trim().isEmpty()) {
                append.append(newline);
            }
            for (int index = 0; index < missingSections.size(); index++) {
                if (index > 0) {
                    append.append(newline).append(newline);
                }
                append.append(missingSections.get(index).replace("\n", newline)).append(newline);
            }
            updated = existing + append;
        }

        String original = targetFile.exists()
                ? Files.readString(targetFile.toPath(), StandardCharsets.UTF_8)
                : "";
        if (updated.equals(original)) {
            return false;
        }

        Files.writeString(targetFile.toPath(), updated, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    private static String extractTopLevelYamlSection(String yaml, String key) {
        String[] lines = yaml.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        int start = -1;
        for (int index = 0; index < lines.length; index++) {
            if (isYamlKeyLine(lines[index], key)) {
                start = index;
                break;
            }
        }
        if (start < 0) {
            return "";
        }

        int end = lines.length;
        for (int index = start + 1; index < lines.length; index++) {
            if (isTopLevelYamlKeyLine(lines[index])) {
                end = index;
                break;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int index = start; index < end; index++) {
            builder.append(lines[index]);
            if (index + 1 < end) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static String extractYamlChildBlock(String yaml, String parentKey, String childKey) {
        String[] lines = yaml.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        int parentStart = -1;
        for (int index = 0; index < lines.length; index++) {
            if (isYamlKeyLine(lines[index], parentKey)) {
                parentStart = index;
                break;
            }
        }
        if (parentStart < 0) {
            return "";
        }

        int parentEnd = lines.length;
        for (int index = parentStart + 1; index < lines.length; index++) {
            if (isTopLevelYamlKeyLine(lines[index])) {
                parentEnd = index;
                break;
            }
        }

        int childLine = -1;
        for (int index = parentStart + 1; index < parentEnd; index++) {
            if (isYamlKeyLineAtIndent(lines[index], childKey, 2)) {
                childLine = index;
                break;
            }
        }
        if (childLine < 0) {
            return "";
        }

        int start = childLine;
        while (start > parentStart + 1) {
            String previous = lines[start - 1];
            if (previous.trim().isEmpty()) {
                start--;
                continue;
            }
            if (countLeadingSpaces(previous) == 2 && previous.trim().startsWith("#")) {
                start--;
                continue;
            }
            break;
        }

        int end = parentEnd;
        for (int index = childLine + 1; index < parentEnd; index++) {
            if (isYamlChildKeyLine(lines[index], 2)) {
                end = index;
                break;
            }
        }
        while (end > childLine + 1) {
            String previous = lines[end - 1];
            if (previous.trim().isEmpty()
                    || (countLeadingSpaces(previous) == 2 && previous.trim().startsWith("#"))) {
                end--;
                continue;
            }
            break;
        }

        StringBuilder builder = new StringBuilder();
        for (int index = start; index < end; index++) {
            builder.append(lines[index]);
            if (index + 1 < end) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static String insertYamlChildBlock(String yaml, String parentKey, String childBlock) {
        if (childBlock == null || childBlock.trim().isEmpty()) {
            return yaml;
        }

        String normalized = yaml.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);
        int parentStart = -1;
        for (int index = 0; index < lines.length; index++) {
            if (isYamlKeyLine(lines[index], parentKey)) {
                parentStart = index;
                break;
            }
        }
        if (parentStart < 0) {
            return yaml;
        }

        int insertIndex = lines.length;
        for (int index = parentStart + 1; index < lines.length; index++) {
            if (isTopLevelYamlKeyLine(lines[index])) {
                insertIndex = index;
                break;
            }
        }

        String newline = yaml.contains("\r\n") ? "\r\n" : "\n";
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < insertIndex; index++) {
            builder.append(lines[index]).append(newline);
        }
        if (insertIndex > parentStart + 1 && builder.length() >= newline.length() * 2
                && !builder.substring(builder.length() - newline.length() * 2).equals(newline + newline)) {
            builder.append(newline);
        }
        builder.append(childBlock.replace("\n", newline)).append(newline);
        for (int index = insertIndex; index < lines.length; index++) {
            builder.append(lines[index]);
            if (index + 1 < lines.length) {
                builder.append(newline);
            }
        }
        return builder.toString();
    }

    private static boolean isYamlKeyLineAtIndent(String line, String key, int indent) {
        if (line == null || countLeadingSpaces(line) != indent) {
            return false;
        }

        String trimmed = line.trim();
        return trimmed.equals(key + ":") || trimmed.startsWith(key + ": ");
    }

    private static boolean isYamlChildKeyLine(String line, int indent) {
        if (line == null || countLeadingSpaces(line) != indent || line.trim().startsWith("#")) {
            return false;
        }

        int colonIndex = line.trim().indexOf(':');
        if (colonIndex <= 0) {
            return false;
        }

        String key = line.trim().substring(0, colonIndex).trim();
        return key.matches("[A-Za-z0-9_-]+");
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private static boolean isYamlKeyLine(String line, String key) {
        if (line == null || line.startsWith(" ") || line.startsWith("\t")) {
            return false;
        }

        String trimmed = line.trim();
        return trimmed.equals(key + ":") || trimmed.startsWith(key + ": ");
    }

    private static boolean isTopLevelYamlKeyLine(String line) {
        if (line == null || line.isEmpty() || line.startsWith(" ") || line.startsWith("\t") || line.startsWith("#")) {
            return false;
        }

        int colonIndex = line.indexOf(':');
        if (colonIndex <= 0) {
            return false;
        }

        String key = line.substring(0, colonIndex).trim();
        return key.matches("[A-Za-z0-9_-]+");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void blockNativeDeathSaveActivation(AbilityPreactivateEvent event) {
        AdvancedAbility ability = event.getEffect();
        if (!usesDeathSave(ability)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void savePlayerFromLethalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!isLethalDamageEvent(event, player)) {
            return;
        }

        DeathSaveCandidate candidate = findDeathSaveCandidate(player);
        if (candidate == null) {
            if (debugDeathSave) {
                getLogger().info("Death-save check for " + player.getName() + " found no matching equipped enchant.");
            }
            return;
        }

        if (isDeathSaveOnCooldown(player, candidate)) {
            if (debugDeathSave) {
                getLogger().info("Death-save check for " + player.getName() + " found "
                        + candidate.displayName() + " but it is on cooldown.");
            }
            return;
        }

        if (!rollChance(candidate.chance())) {
            if (debugDeathSave) {
                getLogger().info("Death-save check for " + player.getName() + " rolled below "
                        + candidate.chance() + "% for " + candidate.displayName() + ".");
            }
            return;
        }

        if (!payDeathSaveSouls(player, candidate.item(), candidate.itemType(), candidate.souls(), candidate.displayName())) {
            if (debugDeathSave) {
                getLogger().info("Death-save check for " + player.getName() + " found "
                        + candidate.displayName() + " but could not pay " + candidate.souls() + " souls.");
            }
            return;
        }

        double maxHealth = getMaxHealth(player);
        if (!Double.isFinite(maxHealth) || maxHealth <= 0.0D) {
            return;
        }

        event.setCancelled(true);
        event.setDamage(0.0D);
        player.setHealth(Math.max(1.0D, Math.min(maxHealth, candidate.health(maxHealth))));
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.isOnline() && !player.isDead()) {
                player.setHealth(Math.max(1.0D, Math.min(getMaxHealth(player), candidate.health(getMaxHealth(player)))));
            }
        });
        startDeathSaveCooldown(player, candidate);
        playDeathSaveFeedback(player, getDeathSaveAttacker(event, player), candidate.effects());
        if (debugDeathSave) {
            getLogger().info("Death-save activated " + candidate.displayName() + " for " + player.getName()
                    + ", charged " + candidate.souls() + " souls.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void beforeAbilityPreactivate(AbilityPreactivateEvent event) {
        AdvancedAbility ability = event.getEffect();
        addDefaultTargetsForAddonMarkerEffects(ability);
        if (shouldCancelSilencedAbility(event, ability)) {
            event.setCancelled(true);
            return;
        }
        if (shouldCancelAbilityActivationFromNonDirectDamage(event)) {
            event.setCancelled(true);
            return;
        }
        trackSoulHardenedActivationWindow(event, ability);
        if (tryBlockSoulTrapWithSoulHardened(event, ability)) {
            event.setCancelled(true);
            return;
        }
        if (handleTargetlessAddonMarkerActivation(event, ability)) {
            event.setCancelled(true);
            return;
        }
        if (tryReflectOffensiveAbility(event, ability)) {
            event.setCancelled(true);
            return;
        }
        if (shouldCancelGuardSummonActivation(event, ability)) {
            event.setCancelled(true);
            return;
        }
        queuePolymorphicMetaphysicalResists(event, ability);
        trackCustomSlownessActivation(event, ability);
        if (isSummonAbility(ability)) {
            trackPendingSummonSpawns(event, ability);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void bypassNativeSoulUse(SoulUseEvent event) {
        if (itemHasDeathSaveEffect(event.getItem()) || itemHasDeathSaveRule(event.getItem())) {
            event.setCustom(true);
            event.setCancelled(true);
        }
    }

    private boolean shouldCancelGuardSummonActivation(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (!getEnchantsConfig().getBoolean("settings.guard-summon-throttle.enabled", true)
                || ability == null
                || !isGuardSummonAbility(ability)) {
            return false;
        }

        String enchantName = normalize(extractEnchantName(ability));
        if (isGuardSummonThrottleExempt(enchantName)) {
            return false;
        }

        LivingEntity owner = resolveAbilityThrottleOwner(event);
        if (owner == null) {
            return false;
        }

        int cooldownTicks = Math.max(1, getEnchantsConfig().getInt("settings.guard-summon-throttle.cooldown-ticks", 20));
        Long lastTick = guardSummonActivationTicks.get(owner.getUniqueId());
        if (lastTick != null && serverTick - lastTick < cooldownTicks) {
            return true;
        }

        guardSummonActivationTicks.put(owner.getUniqueId(), serverTick);
        return false;
    }

    private LivingEntity resolveAbilityThrottleOwner(AbilityPreactivateEvent event) {
        if (event == null) {
            return null;
        }

        LivingEntity owner = event.getMainEntity();
        if (owner == null && event.getActionExecution() != null && event.getActionExecution().getBuilder() != null) {
            owner = event.getActionExecution().getBuilder().getMain();
        }
        if (owner == null) {
            owner = getPrimaryVictim(event);
        }
        return owner;
    }

    private void trackSoulHardenedActivationWindow(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (ability == null || !normalize(extractEnchantName(ability)).equals("soulhardened")) {
            return;
        }

        LivingEntity defender = getPrimaryVictim(event);
        if (defender instanceof Player) {
            soulHardenedBlockReadyTicks.put(defender.getUniqueId(), serverTick + 3L);
        }
    }

    private boolean tryBlockSoulTrapWithSoulHardened(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (event == null || ability == null || !normalize(extractEnchantName(ability)).equals("soultrap")) {
            return false;
        }

        LivingEntity victim = getPrimaryVictim(event);
        if (!(victim instanceof Player)) {
            return false;
        }

        Player defender = (Player) victim;
        if (consumeSoulHardenedBlockWindow(defender)) {
            return true;
        }

        int level = getHighestArmorEnchantLevel(defender, "soulhardened", "soulhardened");
        if (level <= 0) {
            return false;
        }

        AdvancedAbility soulHardenedAbility = getAbility("soulhardened", "soulhardened", level);
        String cooldownName = soulHardenedAbility == null ? "soulhardened" : soulHardenedAbility.getNameNoLevel();
        if (isAeAbilityOnCooldown(defender, cooldownName)) {
            return false;
        }

        double chance = soulHardenedAbility == null ? getDefaultSoulHardenedChance(level) : soulHardenedAbility.getChance();
        if (!rollChance(chance)) {
            return false;
        }

        int cooldownSeconds = soulHardenedAbility == null ? 7 : Math.max(0, soulHardenedAbility.getCooldown());
        putAeAbilityCooldown(defender, cooldownName, cooldownSeconds);
        repairArmorSet(defender, getSoulHardenedRepairAmount(level));
        return true;
    }

    private boolean consumeSoulHardenedBlockWindow(Player defender) {
        Long expiresAtTick = soulHardenedBlockReadyTicks.remove(defender.getUniqueId());
        return expiresAtTick != null && expiresAtTick >= serverTick;
    }

    private boolean isAeAbilityOnCooldown(LivingEntity entity, String cooldownName) {
        if (entity == null || cooldownName == null || cooldownName.trim().isEmpty()) {
            return false;
        }
        try {
            return ACooldown.isInCooldown(entity, cooldownName);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private void putAeAbilityCooldown(LivingEntity entity, String cooldownName, int cooldownSeconds) {
        if (entity == null || cooldownName == null || cooldownName.trim().isEmpty() || cooldownSeconds <= 0) {
            return;
        }
        try {
            ACooldown.putToCooldown(entity, cooldownName, cooldownSeconds);
        } catch (RuntimeException ignored) {
            // AE cooldown internals are best-effort. The Soul Trap activation is still blocked for this hit.
        }
    }

    private static double getDefaultSoulHardenedChance(int level) {
        switch (clamp(level, 1, 3)) {
            case 1:
                return 20.0D;
            case 2:
                return 35.0D;
            default:
                return 50.0D;
        }
    }

    private int getSoulHardenedRepairAmount(int level) {
        String[] args = getEnchantEffectArguments("soulhardened", clamp(level, 1, 3), "SOUL_HARDENED");
        return args.length >= 1 ? Math.max(0, parsePositiveInt(args[0], 5)) : 5;
    }

    private void repairArmorSet(Player player, int amount) {
        if (player == null || amount <= 0 || player.getInventory() == null) {
            return;
        }
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            repairArmorPiece(armor, amount);
        }
    }

    private static boolean isGuardSummonAbility(AdvancedAbility ability) {
        if (ability == null || ability.getEffects() == null) {
            return false;
        }

        for (String effect : ability.getEffects()) {
            if (isGuardSummonEffect(effect)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSummonAbility(AdvancedAbility ability) {
        if (ability == null || ability.getEffects() == null) {
            return false;
        }

        for (String effect : ability.getEffects()) {
            if (isSummonEffect(effect)) {
                return true;
            }
        }
        return false;
    }

    private void addDefaultTargetsForAddonMarkerEffects(AdvancedAbility ability) {
        if (ability == null || ability.getEffects() == null) {
            return;
        }

        List<String> effects = ability.getEffects();
        for (int i = 0; i < effects.size(); i++) {
            String effect = effects.get(i);
            if (effect == null || effect.contains(" @")) {
                continue;
            }

            String effectName = stripEffectTarget(effect).split("\\s+", 2)[0]
                    .split(":", 2)[0]
                    .trim()
                    .toUpperCase(Locale.ROOT);
            String target = defaultAddonEffectTarget(effectName);
            if (target == null) {
                continue;
            }

            try {
                effects.set(i, effect + " " + target);
            } catch (UnsupportedOperationException ignored) {
                return;
            }
        }
    }

    private static String defaultAddonEffectTarget(String effectName) {
        String normalized = normalize(effectName);
        switch (normalized) {
            case "distancedamage":
            case "damagemultiplier":
            case "solitude":
            case "perfectsolitude":
                return "@Attacker";
            case "damagenoknockback":
            case "bleednoknockback":
            case "trueinvisibility":
            case "rusezombies":
            case "visualspirits":
            case "obsidianguardians":
            case "epidemiccarrier":
            case "divineimmolation":
            case "lifestealaccurate":
            case "chainlifesteal":
            case "paradoxheal":
            case "destructionaura":
            case "ragemultiplier":
            case "executedamage":
            case "silence":
            case "alienhungerresist":
            case "enlightenedheal":
            case "bloodlust":
            case "aegis":
            case "bloodlink":
            case "valor":
            case "martyrvalor":
            case "enchantreflect":
            case "heroicenchantreflect":
            case "soulhardened":
            case "metaphysical":
            case "polymorphicmetaphysical":
            case "creeperarmor":
            case "customcreeperarmor":
            case "diminishnext":
            case "vengefuldiminishnext":
                return "@Victim";
            default:
                return null;
        }
    }

    private boolean handleTargetlessAddonMarkerActivation(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (event == null || ability == null || ability.getEffects() == null
                || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null) {
            return false;
        }

        for (String effect : ability.getEffects()) {
            if (effect == null) {
                continue;
            }

            String command = stripEffectTarget(effect).split("\\s+", 2)[0];
            String[] pieces = command.split(":");
            if (pieces.length == 0) {
                continue;
            }

            String effectName = pieces[0].trim().toUpperCase(Locale.ROOT);
            String[] args = pieces.length <= 1 ? new String[0] : Arrays.copyOfRange(pieces, 1, pieces.length);
            if (effectName.equals("SILENCE")) {
                return applyTargetlessSilence(event, args);
            }
            if (effectName.equals("DIMINISH_NEXT")) {
                return applyTargetlessDiminishNext(event, false);
            }
            if (effectName.equals("VENGEFUL_DIMINISH_NEXT")) {
                return applyTargetlessDiminishNext(event, true);
            }
            if (effect.contains(" @")) {
                continue;
            }
            if (effectName.equals("RAGE_MULTIPLIER")) {
                return applyTargetlessRageMultiplier(event, args);
            }
            if (effectName.equals("EXECUTE_DAMAGE")) {
                return applyTargetlessExecuteDamage(event, args);
            }
            if (effectName.equals("LIFESTEAL_ACCURATE")) {
                return applyTargetlessAccurateLifesteal(event, args);
            }
        }

        return false;
    }

    private boolean applyTargetlessExecuteDamage(AbilityPreactivateEvent event, String[] args) {
        if (event == null || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        LivingEntity victim = firstValidLivingEntity(
                getPrimaryVictim(event),
                event.getActionExecution().getBuilder().getVictim(),
                event.getMainEntity());
        return applyExecuteDamage(sourceEvent, victim, args);
    }

    private boolean applyTargetlessSilence(AbilityPreactivateEvent event, String[] args) {
        if (event == null || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        LivingEntity victim = firstValidLivingEntity(
                getPrimaryVictim(event),
                event.getActionExecution().getBuilder().getVictim(),
                event.getMainEntity());
        LivingEntity attacker = firstValidLivingEntity(
                event.getActionExecution().getBuilder().getAttacker(),
                event.getActionExecution().getBuilder().getMain());
        if (sourceEvent instanceof EntityDamageByEntityEvent) {
            attacker = firstValidLivingEntity(
                    getLivingDamager(((EntityDamageByEntityEvent) sourceEvent).getDamager()), attacker);
        }

        return applySilence(sourceEvent, attacker, victim,
                event.getActionExecution().getBuilder().getItem(), args);
    }

    private boolean applyTargetlessAccurateLifesteal(AbilityPreactivateEvent event, String[] args) {
        if (args == null || args.length < 2) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageByEntityEvent)) {
            return false;
        }

        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) sourceEvent;
        LivingEntity attacker = firstValidLivingEntity(
                getLivingDamager(damageEvent.getDamager()),
                event.getActionExecution().getBuilder().getAttacker(),
                event.getMainEntity());
        LivingEntity victim = firstValidLivingEntity(
                damageEvent.getEntity() instanceof LivingEntity ? (LivingEntity) damageEvent.getEntity() : null,
                event.getActionExecution().getBuilder().getVictim(),
                getPrimaryVictim(event));
        if (attacker == null || victim == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return false;
        }

        double minDamage = Math.abs(parseDouble(args[0], Double.NaN));
        double maxDamage = Math.abs(parseDouble(args[1], Double.NaN));
        if (!Double.isFinite(minDamage) || !Double.isFinite(maxDamage)
                || minDamage <= 0.0D || maxDamage <= 0.0D) {
            return false;
        }

        String prefix = args.length >= 3 ? args[2] : "lifesteal";
        applyAccurateLifesteal(attacker, victim, minDamage, maxDamage, prefix);
        return true;
    }

    private boolean applyTargetlessRageMultiplier(AbilityPreactivateEvent event, String[] args) {
        if (args == null || args.length < 1) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageByEntityEvent)) {
            return false;
        }

        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) sourceEvent;
        LivingEntity attacker = firstValidLivingEntity(
                getLivingDamager(damageEvent.getDamager()),
                event.getActionExecution().getBuilder().getAttacker(),
                event.getMainEntity());
        LivingEntity victim = firstValidLivingEntity(
                damageEvent.getEntity() instanceof LivingEntity ? (LivingEntity) damageEvent.getEntity() : null,
                event.getActionExecution().getBuilder().getVictim(),
                getPrimaryVictim(event));
        if (attacker == null || victim == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return false;
        }

        double perStack = Math.abs(parseDouble(args[0], 0.1D));
        int maxStacks = args.length >= 2 ? parsePositiveInt(args[1], 5) : 5;
        if (!Double.isFinite(perStack) || perStack <= 0.0D || maxStacks <= 0) {
            return false;
        }

        int combo = resolveRageCombo(attacker, victim, maxStacks);
        if (combo <= 0) {
            return true;
        }

        double multiplier = 1.0D + (perStack * combo);
        applyAdditiveDamageMultiplier(damageEvent, multiplier);
        return true;
    }

    private boolean applyTargetlessDiminishNext(AbilityPreactivateEvent event, boolean reflectOverflow) {
        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageEvent)) {
            return false;
        }

        LivingEntity target = firstValidLivingEntity(
                getPrimaryVictim(event),
                event.getMainEntity(),
                event.getActionExecution().getBuilder().getVictim());
        if (target == null) {
            return false;
        }

        double damageTaken = Math.max(0.0D, ((EntityDamageEvent) sourceEvent).getFinalDamage());
        if (damageTaken <= 0.0D) {
            return true;
        }

        storeDamageCap(target, damageTaken / 2.0D, reflectOverflow, sourceEvent);
        return true;
    }

    private void queuePolymorphicMetaphysicalResists(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (event == null || ability == null || ability.getEffects() == null) {
            return;
        }

        String sourceEnchant = extractEnchantName(ability);
        if (!METAPHYSICAL_SLOW_ENCHANTS.contains(sourceEnchant)
                && !POLYMORPHIC_METAPHYSICAL_SLOW_ENCHANTS.contains(sourceEnchant)) {
            return;
        }

        java.util.Set<UUID> rolledTargets = new java.util.HashSet<>();
        for (String effect : ability.getEffects()) {
            if (!isSlowPotionEffect(effect)) {
                continue;
            }

            for (LivingEntity target : resolveCustomEffectTargets(event, effect)) {
                if (!(target instanceof Player) || !rolledTargets.add(target.getUniqueId())) {
                    continue;
                }

                Player player = (Player) target;
                MetaphysicalResistSource resistSource = resolveMetaphysicalResistSource(player, sourceEnchant);
                if (resistSource == null || !rollChance(getMetaphysicalResistChance(resistSource))) {
                    continue;
                }

                polymorphicMetaphysicalSlowResists.put(player.getUniqueId(), serverTick + 8L);
            }
        }
    }

    private MetaphysicalResistSource resolveMetaphysicalResistSource(Player player, String sourceEnchant) {
        int polymorphicLevel = getHighestArmorEnchantLevel(player,
                "polymorphicmetaphysical", "polymorphicmetaphysical");
        if (polymorphicLevel > 0 && POLYMORPHIC_METAPHYSICAL_SLOW_ENCHANTS.contains(sourceEnchant)) {
            return new MetaphysicalResistSource("polymorphicmetaphysical", clamp(polymorphicLevel, 1, 10));
        }

        int metaphysicalLevel = getHighestArmorEnchantLevel(player, "metaphysical", "metaphysical");
        if (metaphysicalLevel > 0 && METAPHYSICAL_SLOW_ENCHANTS.contains(sourceEnchant)) {
            return new MetaphysicalResistSource("metaphysical", clamp(metaphysicalLevel, 1, 10));
        }

        return null;
    }

    private double getMetaphysicalResistChance(MetaphysicalResistSource source) {
        int safeLevel = clamp(source.level(), 1, 10);
        String effectName = source.enchantName().equals("polymorphicmetaphysical")
                ? "POLYMORPHIC_METAPHYSICAL"
                : "METAPHYSICAL";
        String[] args = getEnchantEffectArguments(source.enchantName(), safeLevel, effectName);
        if (args.length == 0 && source.enchantName().equals("metaphysical")) {
            args = getEnchantEffectArguments(source.enchantName(), safeLevel, "POLYMORPHIC_METAPHYSICAL");
        }
        if (args.length >= 1) {
            double configured = parseDouble(args[0], Double.NaN);
            if (Double.isFinite(configured)) {
                return Math.max(0.0D, Math.min(100.0D, configured));
            }
        }

        AdvancedAbility ability = getAbility(source.enchantName(), source.enchantName(), safeLevel);
        if (ability != null && Double.isFinite(ability.getChance()) && ability.getChance() > 0.0D) {
            return Math.max(0.0D, Math.min(100.0D, ability.getChance()));
        }

        switch (safeLevel) {
            case 1:
                return 30.0D;
            case 2:
                return 60.0D;
            case 3:
                return 75.0D;
            default:
                return 90.0D;
        }
    }

    private static boolean isSlowPotionEffect(String effect) {
        String command = stripEffectTarget(effect).split("\\s+", 2)[0];
        String[] pieces = command.split(":");
        return pieces.length >= 2
                && pieces[0].equalsIgnoreCase("POTION")
                && canonicalPotionTypeName(pieces[1]).equals("SLOW");
    }

    private void trackCustomSlownessActivation(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (event == null || ability == null || ability.getEffects() == null) {
            return;
        }

        Event sourceEvent = event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null
                ? null
                : event.getActionExecution().getBuilder().getEvent();
        for (String effect : ability.getEffects()) {
            if (!isSlowPotionEffect(effect)) {
                continue;
            }

            String command = stripEffectTarget(effect).split("\\s+", 2)[0];
            String[] pieces = command.split(":");
            String effectName = pieces[0].trim().toUpperCase(Locale.ROOT);
            long expiresAtTick = serverTick + resolveCustomSlowMarkerTicks(effectName, pieces);
            java.util.LinkedHashSet<LivingEntity> targets = new java.util.LinkedHashSet<>(
                    resolveCustomEffectTargets(event, effect));
            addDirectDamageSlowTargetFallback(targets, sourceEvent, effect);
            for (LivingEntity target : targets) {
                if (!(target instanceof Player)) {
                    continue;
                }
                if (hasActiveCustomEffectMarker(polymorphicMetaphysicalSlowResists, (Player) target)) {
                    continue;
                }
                markCustomSlownessTarget((Player) target, expiresAtTick);
            }
        }
    }

    private void addDirectDamageSlowTargetFallback(java.util.Set<LivingEntity> targets, Event sourceEvent,
                                                   String effect) {
        if (!(sourceEvent instanceof EntityDamageByEntityEvent) || effect == null) {
            return;
        }

        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) sourceEvent;
        String upper = effect.toUpperCase(Locale.ROOT);
        if (upper.contains("@VICTIM") && damageEvent.getEntity() instanceof LivingEntity) {
            targets.add((LivingEntity) damageEvent.getEntity());
        }
        if (upper.contains("@ATTACKER")) {
            LivingEntity damager = getLivingDamager(damageEvent.getDamager());
            if (damager != null) {
                targets.add(damager);
            }
        }
    }

    private void markCustomSlownessTarget(Player target, long expiresAtTick) {
        customSlownessTargets.put(target.getUniqueId(), expiresAtTick);
        int delayTicks = getInertiaCleanseDelayTicks();
        customSlownessCleanseAfterTicks.put(target.getUniqueId(), serverTick + delayTicks);
        UUID targetId = target.getUniqueId();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Player player = Bukkit.getPlayer(targetId);
            if (player != null) {
                cleanseInertiaEffects(player);
            }
        }, Math.max(1L, delayTicks + 1L));
    }

    private void markEnemyStunCreeperSlownessTarget(Player target, int durationTicks) {
        if (target == null || durationTicks <= 0) {
            return;
        }
        enemyStunCreeperSlownessTargets.put(target.getUniqueId(),
                serverTick + Math.max(4L, Math.min(20L, durationTicks)));
    }

    private int getInertiaCleanseDelayTicks() {
        return clamp(getEnchantsConfig().getInt("inertia.cleanse-delay-ticks", 3), 1, 20);
    }

    private long resolveCustomSlowMarkerTicks(String effectName, String[] pieces) {
        int durationTicks = 100;
        if (effectName.equals("POTION") && pieces.length >= 4) {
            durationTicks = parsePositiveInt(pieces[3], 100);
        }
        return Math.max(20L, Math.min(1200L, durationTicks + 40L));
    }

    private List<LivingEntity> resolveCustomEffectTargets(AbilityPreactivateEvent event, String effect) {
        if (event == null || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null) {
            return Collections.emptyList();
        }

        String upper = effect == null ? "" : effect.toUpperCase(Locale.ROOT);
        LivingEntity victim = getPrimaryVictim(event);
        LivingEntity attacker = firstValidLivingEntity(
                event.getActionExecution().getBuilder().getAttacker(),
                event.getActionExecution().getBuilder().getMain(),
                event.getMainEntity());
        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (sourceEvent instanceof EntityDamageByEntityEvent) {
            attacker = firstValidLivingEntity(
                    getLivingDamager(((EntityDamageByEntityEvent) sourceEvent).getDamager()), attacker);
        }

        if (upper.contains("@ATTACKER")) {
            return attacker == null ? Collections.emptyList() : Collections.singletonList(attacker);
        }
        if (upper.contains("@VICTIM")) {
            return victim == null ? Collections.emptyList() : Collections.singletonList(victim);
        }
        if (upper.contains("@AOE")) {
            LivingEntity center = firstValidLivingEntity(victim, event.getMainEntity(), attacker);
            if (center == null || center.getWorld() == null) {
                return Collections.emptyList();
            }

            double radius = parseAoeRadius(upper, 5.0D);
            double radiusSquared = radius * radius;
            List<LivingEntity> targets = new java.util.ArrayList<>();
            for (Entity nearby : center.getWorld().getNearbyEntities(center.getLocation(), radius, radius, radius)) {
                if (nearby instanceof LivingEntity
                        && nearby.getLocation().distanceSquared(center.getLocation()) <= radiusSquared) {
                    targets.add((LivingEntity) nearby);
                }
            }
            return targets;
        }

        LivingEntity fallback = firstValidLivingEntity(event.getMainEntity(), victim, attacker);
        return fallback == null ? Collections.emptyList() : Collections.singletonList(fallback);
    }

    private static double parseAoeRadius(String upperEffect, double fallback) {
        Matcher matcher = Pattern.compile("R\\s*=\\s*([0-9.]+)").matcher(upperEffect);
        return matcher.find() ? parseDouble(matcher.group(1), fallback) : fallback;
    }

    private static boolean isGuardSummonEffect(String effect) {
        if (effect == null) {
            return false;
        }

        String trimmed = effect.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        String effectName = trimmed.split("\\s+", 2)[0].split(":", 2)[0].toUpperCase(Locale.ROOT);
        return effectName.equals("GUARD")
                || effectName.equals("RUSE_ZOMBIES")
                || effectName.equals("VISUAL_SPIRITS")
                || effectName.equals("OBSIDIAN_GUARDIANS")
                || effectName.equals("EPIDEMIC_CARRIER");
    }

    private static boolean isSummonEffect(String effect) {
        if (effect == null) {
            return false;
        }

        String trimmed = effect.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        String effectName = trimmed.split("\\s+", 2)[0].split(":", 2)[0].toUpperCase(Locale.ROOT);
        return effectName.equals("GUARD")
                || effectName.equals("RUSE_ZOMBIES")
                || effectName.equals("VISUAL_SPIRITS")
                || effectName.equals("OBSIDIAN_GUARDIANS")
                || effectName.equals("EPIDEMIC_CARRIER");
    }

    private boolean shouldCancelAbilityActivationFromNonDirectDamage(AbilityPreactivateEvent event) {
        if (!getEnchantsConfig().getBoolean("settings.enchants.block-from-non-direct-damage", true)
                || event == null
                || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageEvent)) {
            return false;
        }

        EntityDamageEvent damageEvent = (EntityDamageEvent) sourceEvent;
        Entity damaged = damageEvent.getEntity();
        if (damaged != null && (damaged.hasMetadata("ae_ignore")
                || damaged.hasMetadata("ae_damage_event_not_going_to_run")
                || damaged.hasMetadata("advancedenchantmentsaddon_divine_immolation")
                || damaged.hasMetadata("advancedenchantmentsaddon_tick_damage"))) {
            return true;
        }

        if (isTickOrCustomDamageCause(damageEvent.getCause())) {
            return true;
        }

        if (sourceEvent instanceof EntityDamageByEntityEvent) {
            LivingEntity damager = getLivingDamager(((EntityDamageByEntityEvent) sourceEvent).getDamager());
            return damager != null && isSummonedEntity(damager);
        }

        return true;
    }

    private static boolean isTickOrCustomDamageCause(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.CUSTOM
                || cause == EntityDamageEvent.DamageCause.FIRE
                || cause == EntityDamageEvent.DamageCause.FIRE_TICK
                || cause == EntityDamageEvent.DamageCause.HOT_FLOOR
                || cause == EntityDamageEvent.DamageCause.LAVA
                || cause == EntityDamageEvent.DamageCause.MAGIC
                || cause == EntityDamageEvent.DamageCause.POISON
                || cause == EntityDamageEvent.DamageCause.WITHER
                || cause == EntityDamageEvent.DamageCause.DRAGON_BREATH
                || cause == EntityDamageEvent.DamageCause.STARVATION
                || cause == EntityDamageEvent.DamageCause.SUFFOCATION;
    }

    private void trackPendingSummonSpawns(AbilityPreactivateEvent event, AdvancedAbility ability) {
        java.util.Set<EntityType> entityTypes = extractSummonEntityTypes(ability);
        java.util.List<Location> centers = new java.util.ArrayList<>();
        addProtectionCenter(centers, event.getMainEntity());
        addProtectionCenter(centers, event.getOtherEntity());
        addProtectionCenter(centers, getPrimaryVictim(event));
        LivingEntity owner = resolveAbilityThrottleOwner(event);
        UUID ownerId = owner == null ? null : owner.getUniqueId();

        if (centers.isEmpty()) {
            return;
        }

        long expiresAtTick = serverTick + 4L;
        pendingSummonProtections.removeIf(protection -> protection.expired(serverTick));
        for (Location center : centers) {
            pendingSummonProtections.add(new PendingSummonProtection(center, entityTypes, expiresAtTick, ownerId));
        }
    }

    private static void addProtectionCenter(java.util.List<Location> centers, LivingEntity entity) {
        if (entity != null && entity.isValid() && entity.getWorld() != null) {
            centers.add(entity.getLocation().clone());
        }
    }

    private static java.util.Set<EntityType> extractSummonEntityTypes(AdvancedAbility ability) {
        java.util.Set<EntityType> types = new java.util.HashSet<>();
        if (ability == null || ability.getEffects() == null) {
            return types;
        }

        for (String effect : ability.getEffects()) {
            String trimmed = effect == null ? "" : effect.trim();
            String rawEffect = trimmed.split("\\s+", 2)[0];
            String[] pieces = rawEffect.split(":");
            if (pieces.length == 0) {
                continue;
            }

            String effectName = pieces[0].toUpperCase(Locale.ROOT);
            if (effectName.equals("RUSE_ZOMBIES")) {
                types.add(EntityType.ZOMBIE);
                continue;
            }
            if (effectName.equals("VISUAL_SPIRITS")) {
                types.add(EntityType.BLAZE);
                continue;
            }
            if (effectName.equals("OBSIDIAN_GUARDIANS")) {
                types.add(EntityType.IRON_GOLEM);
                continue;
            }
            if (effectName.equals("EPIDEMIC_CARRIER")) {
                types.add(EntityType.CREEPER);
                continue;
            }
            if (effectName.equals("GUARD") && pieces.length >= 2) {
                EntityType type = parseGuardEntityType(pieces[1]);
                if (type != null) {
                    types.add(type);
                }
            }
        }
        return types;
    }

    private static EntityType parseGuardEntityType(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("CHARGED_CREEPER")) {
            normalized = "CREEPER";
        }
        try {
            return EntityType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private boolean isGuardSummonThrottleExempt(String enchantName) {
        List<String> exempt = getEnchantsConfig().getStringList("settings.guard-summon-throttle.exempt-enchants");
        if (exempt.isEmpty() && !getEnchantsConfig().contains("settings.guard-summon-throttle.exempt-enchants")) {
            exempt = Arrays.asList("epidemic", "epidemiccarrier", "plaguecarrier", "selfdestruct");
        }
        for (String entry : exempt) {
            if (normalize(entry).equals(enchantName)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHeroicGiveItemPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (handleAddonGiveItemCommand(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHeroicGiveItemServerCommand(ServerCommandEvent event) {
        if (handleAddonGiveItemCommand(event.getSender(), event.getCommand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void beforeHolyWhiteScrollApply(InventoryClickEvent event) {
        pendingHolyWhiteScrollApplication.remove();
        if (!isHolyWhiteScrollCorruptionEnabled() || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack scroll = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (!isHolyWhiteScrollItem(scroll) || !isUsableItem(target)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (isHolyWhiteScrollCorrupted(target)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            hideRejectedHolyWhiteScrollCursor(event, player, scroll);
            event.setCurrentItem(applyHolyWhiteScrollCorrupted(target));
            sendThrottledWithFallback(player, "messages.holy-white-scroll-corrupted",
                    "&cThat item is corrupted and cannot be protected.");
            player.updateInventory();
            return;
        }

        if (hasHolyWhiteScrollProtection(target)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            hideRejectedHolyWhiteScrollCursor(event, player, scroll);
            sendThrottledWithFallback(player, "messages.holy-white-scroll-already-protected",
                    "&cThat item already has a Holy White Scroll applied.");
            player.updateInventory();
            return;
        }

        pendingHolyWhiteScrollApplication.set(new HolyWhiteScrollPendingApplication(
                player.getUniqueId(),
                event.getRawSlot(),
                getHolyWhiteScrollCorruptionCount(target)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockHolyWhiteScrollItemApplyEvent(ItemApplyEvent event) {
        if (!isHolyWhiteScrollCorruptionEnabled()
                || event.getAeItem() == null
                || !event.getAeItem().nbtKey.equalsIgnoreCase("holywhitescroll")) {
            return;
        }

        if (isHolyWhiteScrollCorrupted(event.getItem())) {
            event.setCancelled(true);
            sendThrottledWithFallback(event.getPlayer(), "messages.holy-white-scroll-corrupted",
                    "&cThat item is corrupted and cannot be protected.");
            return;
        }

        if (hasHolyWhiteScrollProtection(event.getItem())) {
            event.setCancelled(true);
            sendThrottledWithFallback(event.getPlayer(), "messages.holy-white-scroll-already-protected",
                    "&cThat item already has a Holy White Scroll applied.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void afterHolyWhiteScrollApply(InventoryClickEvent event) {
        HolyWhiteScrollPendingApplication pending = pendingHolyWhiteScrollApplication.get();
        pendingHolyWhiteScrollApplication.remove();
        if (pending == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!pending.playerId().equals(player.getUniqueId()) || pending.rawSlot() != event.getRawSlot()) {
            return;
        }

        ItemStack updated = event.getCurrentItem();
        if (!hasHolyWhiteScrollProtection(updated)) {
            return;
        }

        int maxApplications = getHolyWhiteScrollMaxApplications(updated);
        int nextCount = Math.min(maxApplications, Math.max(
                pending.previousCount(),
                getHolyWhiteScrollCorruptionCount(updated)) + 1);
        event.setCurrentItem(applyHolyWhiteScrollSemiCorruption(updated, nextCount));
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void removeIllegalHolyWhiteScrollFromCorruptedItem(InventoryClickEvent event) {
        if (!isHolyWhiteScrollCorruptionEnabled() || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        if (!isHolyWhiteScrollCorrupted(current) || !hasHolyWhiteScrollProtection(current)) {
            return;
        }

        event.setCurrentItem(applyHolyWhiteScrollCorrupted(current));
        Player player = (Player) event.getWhoClicked();
        sendThrottled(player, "messages.holy-white-scroll-corrupted");
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyHeroicUpgrade(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack upgrade = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (!isHeroicUpgradeItem(upgrade) || !isUsableItem(target)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (isHeroicUpgradedItem(target)) {
            sendThrottled(player, "messages.heroic-already-upgraded");
            return;
        }

        HeroicTargetType targetType = resolveHeroicTargetType(target.getType());
        if (targetType == null || target.getAmount() != 1) {
            sendThrottled(player, "messages.heroic-invalid-target");
            return;
        }

        int success = getHeroicUpgradeSuccess(upgrade);
        consumeCursorItem(event);
        if (!rollChance(success)) {
            playConfiguredSounds(player, "sounds.heroic-upgrade-failed");
            sendThrottled(player, "messages.heroic-failed",
                    "%success%", String.valueOf(success));
            player.updateInventory();
            return;
        }

        event.setCurrentItem(createHeroicUpgradedItem(target, targetType));
        playConfiguredSounds(player, "sounds.heroic-upgrade-success",
                Collections.singletonList("ENTITY_PLAYER_LEVELUP:1.0:1.0"));
        sendThrottled(player, "messages.heroic-applied",
                "%success%", String.valueOf(success));
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyGodlyTransmogScroll(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack scroll = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (!isGodlyTransmogScrollItem(scroll) || !isUsableItem(target)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory targetInventory = event.getClickedInventory();
        if (targetInventory == null || target.getAmount() != 1) {
            sendThrottled(player, "messages.godly-transmog-invalid-target");
            return;
        }

        List<TransmogEnchant> enchants = findTransmogEnchants(target);
        if (enchants.isEmpty()) {
            sendThrottled(player, "messages.godly-transmog-no-enchants");
            return;
        }
        if (enchants.size() > GODLY_TRANSMOG_MAX_ENCHANTS) {
            sendThrottled(player, "messages.godly-transmog-too-many-enchants",
                    "%max%", String.valueOf(GODLY_TRANSMOG_MAX_ENCHANTS));
            return;
        }

        ItemStack refundScroll = scroll.clone();
        refundScroll.setAmount(1);
        consumeCursorItem(event);

        TransmogSession previous = transmogSessions.remove(player.getUniqueId());
        if (previous != null && !previous.applied()) {
            refundTransmogScroll(player, previous);
        }

        TransmogSession session = new TransmogSession(
                player.getUniqueId(),
                target.clone(),
                targetInventory,
                event.getSlot(),
                refundScroll,
                enchants);
        transmogSessions.put(player.getUniqueId(), session);

        Bukkit.getScheduler().runTask(this, () -> {
            if (!player.isOnline()) {
                refundTransmogScroll(player, session);
                transmogSessions.remove(player.getUniqueId(), session);
                return;
            }
            openGodlyTransmogEditor(player, session);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyHeroicBlackScroll(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack scroll = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (!isHeroicBlackScrollItem(scroll) || !isUsableItem(target)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || target.getAmount() != 1) {
            sendThrottled(player, "messages.heroic-black-scroll-invalid-target");
            return;
        }

        List<TransmogEnchant> heroicEnchants = findHeroicBlackScrollCandidates(target);
        if (heroicEnchants.isEmpty()) {
            sendThrottled(player, "messages.heroic-black-scroll-no-enchants");
            return;
        }

        TransmogEnchant removedEnchant = heroicEnchants.get(ThreadLocalRandom.current().nextInt(heroicEnchants.size()));
        int success = getHeroicBlackScrollSuccess(scroll);
        ItemStack book;
        try {
            book = AEAPI.createEnchantmentBook(removedEnchant.rawEnchantName(),
                    removedEnchant.level(),
                    success,
                    100 - success,
                    player);
        } catch (RuntimeException exception) {
            getLogger().warning("Could not create Heroic Black Scroll book for "
                    + removedEnchant.rawEnchantName() + ": " + exception.getMessage());
            sendThrottled(player, "messages.heroic-black-scroll-book-failed");
            return;
        }

        ItemStack updatedTarget = removeHeroicBlackScrollEnchant(target, removedEnchant);
        consumeCursorItem(event);
        event.setCurrentItem(updatedTarget);
        giveOrDropItem(player, book);
        playConfiguredSounds(player, "sounds.heroic-black-scroll-applied");
        sendThrottled(player, "messages.heroic-black-scroll-applied",
                "%enchant%", removedEnchant.displayName(),
                "%success%", String.valueOf(success));
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyHolyWater(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack holyWater = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (!isHolyWaterItem(holyWater) || !isUsableItem(target)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || target.getAmount() != 1 || !hasHolyWhiteScrollCorruptionData(target)) {
            sendThrottled(player, "messages.holy-water-invalid-target");
            return;
        }

        int waterMax = getHolyWaterMaxApplications(holyWater);
        int currentMax = getHolyWhiteScrollMaxApplications(target);
        if (waterMax <= currentMax) {
            sendThrottled(player, "messages.holy-water-max-too-low",
                    "%current%", String.valueOf(currentMax),
                    "%max%", String.valueOf(waterMax));
            return;
        }

        ItemStack updatedTarget = applyHolyWaterMaxApplications(target, waterMax);
        consumeCursorItem(event);
        event.setCurrentItem(updatedTarget);
        playConfiguredSounds(player, "sounds.holy-water-applied");
        sendThrottled(player, "messages.holy-water-applied",
                "%count%", String.valueOf(getHolyWhiteScrollCorruptionCount(updatedTarget)),
                "%max%", String.valueOf(waterMax));
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void beginTextItemEdit(PlayerInteractEvent event) {
        if (!isRightClick(event)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!isLoreLineItem(item)) {
            return;
        }

        event.setCancelled(true);
        PendingTextItemEdit previous = pendingTextItemEdits.remove(player.getUniqueId());
        if (previous != null) {
            refundPendingTextItem(player, previous);
        }

        ItemStack refundItem = item.clone();
        refundItem.setAmount(1);
        consumeInteractionItem(event, player);
        pendingTextItemEdits.put(player.getUniqueId(), new PendingTextItemEdit(
                player.getUniqueId(), refundItem));
        sendTextItemPrompt(player);
        playConfiguredSounds(player, "sounds.lore-line-prompt");
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void completeTextItemEdit(AsyncPlayerChatEvent event) {
        PendingTextItemEdit session = pendingTextItemEdits.remove(event.getPlayer().getUniqueId());
        if (session == null) {
            return;
        }

        event.setCancelled(true);
        String text = event.getMessage() == null ? "" : event.getMessage().trim();
        Bukkit.getScheduler().runTask(this, () -> completeTextItemEdit(event.getPlayer(), session, text));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refundTextItemEditOnQuit(PlayerQuitEvent event) {
        PendingTextItemEdit session = pendingTextItemEdits.remove(event.getPlayer().getUniqueId());
        if (session != null) {
            refundPendingTextItem(event.getPlayer(), session);
        }
        dimensionalShiftFrozenTargets.remove(event.getPlayer().getUniqueId());
        dimensionalShiftRecentDamageSources.remove(event.getPlayer().getUniqueId());
        dimensionalShiftRecentDamageSources.entrySet().removeIf(entry ->
                entry.getValue().attackerId().equals(event.getPlayer().getUniqueId()));
        winterMercyReductions.remove(event.getPlayer().getUniqueId());
        winterMercyCooldowns.remove(event.getPlayer().getUniqueId());
        removeWinterMercySnowballsForTarget(event.getPlayer().getUniqueId());
        restoreWinterMercySnowPatch(event.getPlayer().getUniqueId());
        stopTrueInvisibility(event.getPlayer().getUniqueId(), false);
        guardSummonActivationTicks.remove(event.getPlayer().getUniqueId());
        inertiaSoundThrottles.remove(event.getPlayer().getUniqueId());
        inertiaMessageThrottles.remove(event.getPlayer().getUniqueId());
        restoredDrunkSlownessLevels.remove(event.getPlayer().getUniqueId());
        inertiaSuppressedDrunkSlownessLevels.remove(event.getPlayer().getUniqueId());
        enemyStunCreeperSlownessTargets.remove(event.getPlayer().getUniqueId());
        soulHardenedBlockReadyTicks.remove(event.getPlayer().getUniqueId());
        aegisAttackWindows.remove(event.getPlayer().getUniqueId());
        enchantReflectCooldowns.keySet().removeIf(key -> key.startsWith(event.getPlayer().getUniqueId() + ":"));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void rememberDimensionalShiftDamageSource(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = (LivingEntity) event.getEntity();
        LivingEntity attacker = getLivingDamager(event.getDamager());
        if (attacker == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        dimensionalShiftRecentDamageSources.put(victim.getUniqueId(),
                new RecentDamageSource(attacker.getUniqueId(), System.currentTimeMillis()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void markSummonSpawnBeforeEliteMobs(CreatureSpawnEvent event) {
        PendingSummonProtection protection = findPendingSummonProtection(event.getEntityType(), event.getLocation());
        if (protection != null) {
            markEntityAsAddonSummon(event.getEntity(), true, protection.ownerId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void removeKnockbackFromNativeCustomDamage(EntityDamageEvent event) {
        if (!getEnchantsConfig().getBoolean("settings.native-damage.remove-custom-damage-knockback", true)
                || !(event.getEntity() instanceof LivingEntity)
                || event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }

        LivingEntity target = (LivingEntity) event.getEntity();
        org.bukkit.util.Vector velocity = target.getVelocity().clone();
        restoreVelocity(target, velocity);
        Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(target, velocity), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void cancelWinterMercySnowballDamage(EntityDamageByEntityEvent event) {
        UUID targetId = getWinterMercySnowballTarget(event.getDamager());
        if (targetId == null) {
            return;
        }

        event.setCancelled(true);
        if (event.getEntity() instanceof LivingEntity
                && event.getEntity().getUniqueId().equals(targetId)) {
            applyWinterMercyReduction((LivingEntity) event.getEntity());
            playWinterMercyHitSounds(event.getEntity().getLocation());
        }
        winterMercySnowballs.remove(event.getDamager().getUniqueId());
        event.getDamager().removeMetadata(WINTER_MERCY_SNOWBALL_METADATA, this);
        event.getDamager().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void forceRuseZombieGuardTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Zombie)) {
            return;
        }

        UUID zombieId = event.getEntity().getUniqueId();
        if (!ruseZombieOwners.containsKey(zombieId)) {
            return;
        }

        LivingEntity assignedTarget = getRuseZombieAssignedTarget(zombieId);
        if (assignedTarget == null) {
            event.setCancelled(true);
            event.setTarget(null);
            if (event.getEntity().isValid()) {
                event.getEntity().remove();
            }
            removeRuseZombieTracking(zombieId);
            return;
        }

        if (event.getTarget() != null
                && event.getTarget().getUniqueId().equals(assignedTarget.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(this, () -> {
            if (event.getEntity().isValid()
                    && !event.getEntity().isDead()
                    && assignedTarget.isValid()
                    && !assignedTarget.isDead()) {
                ((Zombie) event.getEntity()).setTarget(assignedTarget);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventVisualSpiritTargeting(EntityTargetLivingEntityEvent event) {
        if (!isVisualSpirit(event.getEntity())) {
            return;
        }

        event.setCancelled(true);
        event.setTarget(null);
        if (event.getEntity() instanceof Blaze) {
            Blaze spirit = (Blaze) event.getEntity();
            spirit.setTarget(null);
            spirit.setAI(false);
            spirit.setAware(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventVisualSpiritDamage(EntityDamageByEntityEvent event) {
        if (!isVisualSpiritDamageSource(event.getDamager())) {
            return;
        }

        event.setCancelled(true);
        event.setDamage(0.0D);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventVisualSpiritProjectiles(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Entity) || !isVisualSpirit((Entity) shooter)) {
            return;
        }

        event.setCancelled(true);
        event.getEntity().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventRuseZombieFriendlyFire(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie)) {
            return;
        }

        UUID zombieId = event.getDamager().getUniqueId();
        UUID ownerId = ruseZombieOwners.get(zombieId);
        UUID targetId = ruseZombieTargets.get(zombieId);
        if (ownerId == null) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)
                || targetId == null
                || !event.getEntity().getUniqueId().equals(targetId)) {
            event.setCancelled(true);
            LivingEntity assignedTarget = getRuseZombieAssignedTarget(zombieId);
            if (assignedTarget != null) {
                ((Zombie) event.getDamager()).setTarget(assignedTarget);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventSummonsFromTakingDivineImmolationDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || !isSummonedEntity(event.getEntity())) {
            return;
        }

        Entity damager = event.getDamager();
        if (event.getEntity().hasMetadata("advancedenchantmentsaddon_divine_immolation")
                || (damager != null && damager.hasMetadata("advancedenchantmentsaddon_divine_immolation"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void applyRuseZombieHungerOnHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie) || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        UUID targetId = ruseZombieTargets.get(event.getDamager().getUniqueId());
        if (targetId == null || !targetId.equals(event.getEntity().getUniqueId())) {
            return;
        }

        applyRuseZombieHunger((LivingEntity) event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void forceObsidianGuardianTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof IronGolem)) {
            return;
        }

        UUID guardianId = event.getEntity().getUniqueId();
        if (!obsidianGuardianOwners.containsKey(guardianId)) {
            return;
        }

        LivingEntity assignedTarget = getObsidianGuardianAssignedTarget(guardianId);
        if (assignedTarget == null) {
            event.setCancelled(true);
            event.setTarget(null);
            if (event.getEntity().isValid()) {
                event.getEntity().remove();
            }
            removeObsidianGuardianTracking(guardianId);
            return;
        }

        if (event.getTarget() != null
                && event.getTarget().getUniqueId().equals(assignedTarget.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(this, () -> {
            if (event.getEntity().isValid()
                    && !event.getEntity().isDead()
                    && assignedTarget.isValid()
                    && !assignedTarget.isDead()) {
                ((IronGolem) event.getEntity()).setTarget(assignedTarget);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void handleObsidianGuardianHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof IronGolem)) {
            return;
        }

        UUID guardianId = event.getDamager().getUniqueId();
        UUID ownerId = obsidianGuardianOwners.get(guardianId);
        UUID targetId = obsidianGuardianTargets.get(guardianId);
        if (ownerId == null) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)
                || targetId == null
                || !event.getEntity().getUniqueId().equals(targetId)) {
            event.setCancelled(true);
            LivingEntity assignedTarget = getObsidianGuardianAssignedTarget(guardianId);
            if (assignedTarget != null) {
                ((IronGolem) event.getDamager()).setTarget(assignedTarget);
            }
            return;
        }

        ObsidianGuardianSettings settings = obsidianGuardianSettings.getOrDefault(
                guardianId, ObsidianGuardianSettings.defaults());
        Long attackReadyTick = obsidianGuardianAttackReadyTicks.get(guardianId);
        if (attackReadyTick != null && serverTick < attackReadyTick) {
            event.setCancelled(true);
            LivingEntity assignedTarget = getObsidianGuardianAssignedTarget(guardianId);
            if (assignedTarget != null) {
                ((IronGolem) event.getDamager()).setTarget(assignedTarget);
            }
            return;
        }

        LivingEntity victim = (LivingEntity) event.getEntity();
        event.setDamage(settings.damage());
        victim.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW, settings.slowTicks(), settings.slowAmplifier(), true, false, false), true);

        Entity ownerEntity = Bukkit.getEntity(ownerId);
        if (ownerEntity instanceof LivingEntity && ownerEntity.isValid()) {
            pullToward(victim, (LivingEntity) ownerEntity, settings.pullStrength());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void applyValorDamageReduction(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getDamage() <= 0.0D
                || isTickOrCustomDamageCause(event.getCause())) {
            return;
        }

        Player victim = (Player) event.getEntity();
        ItemStack weapon = victim.getInventory().getItemInMainHand();
        if (!isSword(weapon)) {
            return;
        }

        double multiplier = 1.0D;
        if (isNonHeroicSword(weapon)) {
            multiplier *= getStackedReductionMultiplier(victim, "valor", "VALOR");
        }
        multiplier *= getStackedReductionMultiplier(victim, "martyrvalor", "MARTYR_VALOR");

        double minimumMultiplier = Math.max(0.0D, 1.0D - getValorStyleMaxReductionPercent() / 100.0D);
        multiplier = Math.max(minimumMultiplier, Math.min(1.0D, multiplier));
        if (multiplier >= 1.0D) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void applyAegisDamageReduction(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getDamage() <= 0.0D
                || isTickOrCustomDamageCause(event.getCause())) {
            return;
        }

        LivingEntity attacker = getLivingDamager(event.getDamager());
        if (!(attacker instanceof Player)
                || attacker.getUniqueId().equals(event.getEntity().getUniqueId())
                || isSummonedEntity(attacker)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        AegisSettings settings = getAegisSettings(victim);
        if (settings == null || settings.reductionPercent() <= 0.0D) {
            return;
        }

        AegisAttackWindow window = aegisAttackWindows.computeIfAbsent(victim.getUniqueId(),
                ignored -> new AegisAttackWindow());
        if (!window.registerAndIsAdditional(attacker.getUniqueId(), serverTick,
                settings.initialEnemies(), settings.windowTicks())) {
            return;
        }

        double multiplier = Math.max(0.0D, 1.0D - settings.reductionPercent() / 100.0D);
        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void applyBloodLustFromBleedDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getFinalDamage() <= 0.0D
                || !event.getEntity().hasMetadata(BLEED_DAMAGE_METADATA)) {
            return;
        }

        Player bleedVictim = (Player) event.getEntity();
        LivingEntity source = resolveBleedDamageSource(bleedVictim, event);
        triggerBloodLustHeals(bleedVictim, source, event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void applyBloodLinkFromGuardianDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)
                || event.getFinalDamage() <= 0.0D) {
            return;
        }

        Player owner = resolveGuardianOwner(event.getEntity());
        if (owner == null || owner.isDead() || !owner.isValid()) {
            return;
        }

        BloodLinkSettings settings = getBloodLinkSettings(owner);
        if (settings == null || settings.chance() <= 0.0D || settings.maxHeal() <= 0.0D
                || !rollChance(settings.chance())) {
            return;
        }

        double healAmount = rollChainLifestealDamage(settings.minHeal(), settings.maxHeal());
        double beforeHealth = owner.getHealth();
        healLivingEntity(owner, healAmount);
        sendBloodLinkMessage(owner, (LivingEntity) event.getEntity(), owner.getHealth() - beforeHealth);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyStoredDamageCap(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)
                || event.getDamage() <= 0.0D
                || isTickOrCustomDamageCause(event.getCause())) {
            return;
        }

        LivingEntity victim = (LivingEntity) event.getEntity();
        DamageCapState state = nextDamageCaps.get(victim.getUniqueId());
        if (state == null || state.sourceEvent() == event) {
            return;
        }

        nextDamageCaps.remove(victim.getUniqueId());
        double finalDamage = Math.max(0.0D, event.getFinalDamage());
        double cap = Math.max(0.0D, state.maxDamage());
        if (finalDamage <= cap) {
            return;
        }

        double overflow = finalDamage - cap;
        if (event.getDamage() <= 0.0D || cap <= 0.0D) {
            event.setDamage(0.0D);
        } else {
            event.setDamage(event.getDamage() * Math.max(0.0D, cap / finalDamage));
        }

        if (state.reflectOverflow() && overflow > 0.0D) {
            LivingEntity attacker = getLivingDamager(event.getDamager());
            if (attacker != null && !attacker.getUniqueId().equals(victim.getUniqueId())) {
                damageWithoutKnockback(attacker, victim, overflow);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void cleanupAddonSummonDeath(EntityDeathEvent event) {
        UUID entityId = event.getEntity().getUniqueId();
        if (addonSummonIds.remove(entityId)
                || visualSpiritIds.remove(entityId)
                || ruseZombieOwners.containsKey(entityId)
                || ruseZombieTargets.containsKey(entityId)
                || nativeGuardianOwners.containsKey(entityId)
                || obsidianGuardianOwners.containsKey(entityId)
                || obsidianGuardianTargets.containsKey(entityId)
                || obsidianGuardianSettings.containsKey(entityId)
                || obsidianGuardianAttackReadyTicks.containsKey(entityId)
                || epidemicCarrierSettings.containsKey(entityId)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
        event.getEntity().removeMetadata(ADDON_SUMMON_METADATA, this);
        event.getEntity().removeMetadata(ADDON_SUMMON_OWNER_METADATA, this);
        nativeGuardianOwners.remove(entityId);
        removeRuseZombieTracking(entityId);
        removeObsidianGuardianTracking(entityId);
        epidemicCarrierSettings.remove(entityId);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventEpidemicCarrierBlockDamage(EntityExplodeEvent event) {
        if (!isEpidemicCarrier(event.getEntity())) {
            return;
        }

        event.blockList().clear();
        event.setYield(0.0F);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void applyEpidemicCarrierExplosionDebuffs(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || !(event.getDamager() instanceof Creeper)
                || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        EpidemicCarrierSettings settings = epidemicCarrierSettings.get(event.getDamager().getUniqueId());
        if (settings == null || event.getEntity().getUniqueId().equals(settings.ownerId())) {
            return;
        }

        Player target = (Player) event.getEntity();
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.BLINDNESS, settings.blindnessTicks(), settings.blindnessAmplifier(),
                true, false, false), true);
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.CONFUSION, settings.confusionTicks(), settings.confusionAmplifier(),
                true, false, false), true);
        markEnemyStunCreeperSlownessTarget(target, settings.slownessTicks());
        markCustomSlownessTarget(target, serverTick + Math.max(1, settings.slownessTicks()));
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW, settings.slownessTicks(), settings.slownessAmplifier(),
                true, false, false), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void applyCreeperArmorExplosionProtection(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)
                || (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            return;
        }

        Player player = (Player) event.getEntity();
        org.bukkit.util.Vector velocity = player.getVelocity().clone();
        CustomCreeperArmorSettings customSettings = getCustomCreeperArmorSettings(player);
        if (customSettings != null) {
            boolean enemyCustomCreeper = event instanceof EntityDamageByEntityEvent
                    && isEnemyEpidemicCarrierDamage(player, ((EntityDamageByEntityEvent) event).getDamager());
            double cancelChance = enemyCustomCreeper
                    ? customSettings.customCreeperBlockChance()
                    : customSettings.explosionBlockChance();
            if (rollChance(cancelChance)) {
                event.setCancelled(true);
            }

            if (customSettings.noKnockback()) {
                restoreVelocity(player, velocity);
                Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(player, velocity), 1L);
                Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(player, velocity), 2L);
            }
            return;
        }

        CreeperArmorSettings settings = getCreeperArmorSettings(player);
        if (settings == null) {
            return;
        }
        if (rollChance(settings.explosionBlockChance())) {
            event.setCancelled(true);
        }
        if (settings.healChance() > 0.0D && rollChance(settings.healChance())) {
            healLivingEntity(player, rollRangeAmount(settings.minHeal(), settings.maxHeal()));
        }
        if (settings.noKnockback()) {
            restoreVelocity(player, velocity);
            Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(player, velocity), 1L);
            Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(player, velocity), 2L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void blockCustomCreeperArmorSlowness(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getNewEffect() == null
                || event.getNewEffect().getType() != PotionEffectType.SLOW) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (hasActiveCustomEffectMarker(polymorphicMetaphysicalSlowResists, player)) {
            event.setCancelled(true);
            return;
        }

        if (!hasActiveCustomEffectMarker(enemyStunCreeperSlownessTargets, player)) {
            return;
        }
        enemyStunCreeperSlownessTargets.remove(player.getUniqueId());

        CustomCreeperArmorSettings settings = getCustomCreeperArmorSettings(player);
        if (settings != null && rollChance(settings.slownessBlockChance())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventAlienImplantsHungerLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (event.getFoodLevel() >= player.getFoodLevel()) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment == null ? null : equipment.getHelmet();
        int level = getEnchantLevelOnItem("alienimplants", "alienimplants", helmet);
        if (level <= 0 || !rollChance(getAlienHungerResistChance(level))) {
            return;
        }

        event.setFoodLevel(player.getFoodLevel());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void handleWinterMercySnowballHit(ProjectileHitEvent event) {
        UUID targetId = getWinterMercySnowballTarget(event.getEntity());
        if (targetId == null) {
            return;
        }

        winterMercySnowballs.remove(event.getEntity().getUniqueId());
        Entity hitEntity = event.getHitEntity();
        if (hitEntity instanceof LivingEntity && hitEntity.getUniqueId().equals(targetId)) {
            applyWinterMercyReduction((LivingEntity) hitEntity);
            playWinterMercyHitSounds(hitEntity.getLocation());
        }
        event.getEntity().removeMetadata(WINTER_MERCY_SNOWBALL_METADATA, this);
        if (event.getEntity().isValid()) {
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockBreak(BlockBreakEvent event) {
        if (!isWinterMercyTemporaryBlock(event.getBlock())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockDamage(BlockDamageEvent event) {
        if (!isWinterMercyTemporaryBlock(event.getBlock())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockFade(BlockFadeEvent event) {
        if (!isWinterMercyTemporaryBlock(event.getBlock())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockBurn(BlockBurnEvent event) {
        if (!isWinterMercyTemporaryBlock(event.getBlock())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!event.getBlocks().stream().anyMatch(this::isWinterMercyTemporaryBlock)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!event.getBlocks().stream().anyMatch(this::isWinterMercyTemporaryBlock)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockEntityExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isWinterMercyTemporaryBlock);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventWinterMercyTemporaryBlockExplosion(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isWinterMercyTemporaryBlock);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void reduceWinterMercyIncomingDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity target = (LivingEntity) event.getEntity();
        WinterMercyReduction reduction = winterMercyReductions.get(target.getUniqueId());
        if (reduction == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now > reduction.expiresAtMillis()) {
            winterMercyReductions.remove(target.getUniqueId());
            return;
        }

        double multiplier = 1.0D - (Math.max(0.0D, Math.min(100.0D, reduction.percent())) / 100.0D);
        event.setDamage(Math.max(0.0D, event.getDamage() * multiplier));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void removeDimensionalShiftBlockOnLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)
                || !dimensionalShiftBlocks.containsKey(event.getEntity().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        dimensionalShiftBlocks.remove(event.getEntity().getUniqueId());
        event.getEntity().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void lockDimensionalShiftFrozenPlayer(PlayerMoveEvent event) {
        Location frozenLocation = dimensionalShiftFrozenTargets.get(event.getPlayer().getUniqueId());
        if (frozenLocation == null || event.getTo() == null) {
            return;
        }
        if (!event.getPlayer().getWorld().equals(frozenLocation.getWorld())) {
            dimensionalShiftFrozenTargets.remove(event.getPlayer().getUniqueId());
            return;
        }
        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Location lockedTo = frozenLocation.clone();
        lockedTo.setYaw(event.getTo().getYaw());
        lockedTo.setPitch(event.getTo().getPitch());
        event.setTo(lockedTo);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void handleGodlyTransmogMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || !(event.getView().getTopInventory().getHolder() instanceof TransmogMenuHolder)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        TransmogSession session = transmogSessions.get(player.getUniqueId());
        if (session == null) {
            player.closeInventory();
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= GODLY_TRANSMOG_MENU_SIZE) {
            return;
        }

        if (slot == GODLY_TRANSMOG_PREVIEW_SLOT) {
            applyGodlyTransmogSession(player, session);
            return;
        }

        if (slot >= session.enchants().size()) {
            return;
        }

        if (session.selectedIndex() < 0) {
            session.setSelectedIndex(slot);
        } else if (session.selectedIndex() == slot) {
            session.setSelectedIndex(-1);
        } else {
            Collections.swap(session.enchants(), session.selectedIndex(), slot);
            session.setSelectedIndex(-1);
        }
        refreshGodlyTransmogEditor(player, session);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void handleGodlyTransmogMenuDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof TransmogMenuHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void handleGodlyTransmogMenuClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player) || !(event.getInventory().getHolder() instanceof TransmogMenuHolder)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        TransmogSession session = transmogSessions.remove(player.getUniqueId());
        if (session != null && !session.applied()) {
            refundTransmogScroll(player, session);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void finalizeHolyWhiteScrollCorruptionAfterRespawn(PlayerRespawnEvent event) {
        if (!isHolyWhiteScrollCorruptionEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, () -> finalizeHolyWhiteScrollCorruption(player), 2L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleHeroicItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (!isHeroicUpgradedItem(item) || event.getDamage() <= 0) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }

        int maxDurability = getHeroicMaxDurability(meta);
        if (maxDurability <= 0) {
            return;
        }

        int currentDamage = getHeroicDamage(meta, item.getType(), maxDurability);
        int nextDamage = currentDamage + event.getDamage();
        int visibleMaxDamage = Math.max(1, item.getType().getMaxDurability()) - 1;

        if (nextDamage >= maxDurability) {
            setHeroicDamage(meta, maxDurability);
            ((Damageable) meta).setDamage(Math.max(0, visibleMaxDamage));
            item.setItemMeta(meta);
            event.setDamage(Math.max(1, visibleMaxDamage + 1));
            return;
        }

        event.setCancelled(true);
        setHeroicDamage(meta, nextDamage);
        syncHeroicVisualDamage((Damageable) meta, item.getType(), nextDamage, maxDurability);
        item.setItemMeta(meta);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void preventDestructionAuraDurabilityDamage(PlayerItemDamageEvent event) {
        if (event.getPlayer() != null && event.getPlayer().hasMetadata(DESTRUCTION_DAMAGE_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void preventDestructionAuraVelocity(org.bukkit.event.player.PlayerVelocityEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        Long destructionExpiresAtTick = destructionVelocitySuppressions.get(playerId);
        Long noKnockbackExpiresAtTick = noKnockbackVelocitySuppressions.get(playerId);
        long expiresAtTick = Math.max(
                destructionExpiresAtTick == null ? 0L : destructionExpiresAtTick,
                noKnockbackExpiresAtTick == null ? 0L : noKnockbackExpiresAtTick);
        if (expiresAtTick <= serverTick) {
            destructionVelocitySuppressions.remove(playerId);
            noKnockbackVelocitySuppressions.remove(playerId);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void notifyStaticPotionArmorChange(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        scheduleDrunkSlownessRestore(event, player);

        if (!effectNotificationsEnabled || !event.isSendMessage()) {
            return;
        }

        Map<String, StaticPotionNotification> oldEffects = collectStaticPotionNotifications(event.getOldArmorPiece());
        Map<String, StaticPotionNotification> newEffects = collectStaticPotionNotifications(event.getNewArmorPiece());

        for (Map.Entry<String, StaticPotionNotification> entry : newEffects.entrySet()) {
            if (!oldEffects.containsKey(entry.getKey())) {
                sendEffectNotification(player, entry.getValue(), true);
            }
        }

        for (Map.Entry<String, StaticPotionNotification> entry : oldEffects.entrySet()) {
            if (!newEffects.containsKey(entry.getKey())) {
                sendEffectNotification(player, entry.getValue(), false);
            }
        }
    }

    private void scheduleDrunkSlownessRestore(ArmorEquipEvent event, Player player) {
        if (!getEnchantsConfig().getBoolean("inertia.restore-drunk-slowness-on-boot-removal", true)
                || event.getType() == null) {
            return;
        }

        String type = event.getType().name();
        boolean removedInertiaBoots = "BOOTS".equals(type)
                && getEnchantLevelOnItem("inertia", "inertia", event.getOldArmorPiece()) > 0
                && getEnchantLevelOnItem("inertia", "inertia", event.getNewArmorPiece()) <= 0;
        boolean mayNeedCleanup = "HELMET".equals(type) || "BOOTS".equals(type);
        if (!removedInertiaBoots && !mayNeedCleanup) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this,
                () -> reconcileRestoredDrunkSlowness(player, removedInertiaBoots), 2L);
    }

    private void startServerTickTask() {
        if (!isEnabled()) {
            return;
        }

        if (serverTickTask != null) {
            serverTickTask.cancel();
        }

        serverTickTask = new BukkitRunnable() {
            @Override
            public void run() {
                serverTick++;
                tickPersistentDestructionAuras();
                tickProactiveInertiaCleanses();
                tickPassiveImmortalRepairs();
                pendingSummonProtections.removeIf(protection -> protection.expired(serverTick));
                if (serverTick % 1200L == 0L) {
                    guardSummonActivationTicks.clear();
                    addonSummonIds.removeIf(entityId -> Bukkit.getEntity(entityId) == null);
                    nativeGuardianOwners.keySet().removeIf(entityId -> Bukkit.getEntity(entityId) == null);
                    rageCombos.entrySet().removeIf(entry -> entry.getValue().expired(serverTick, 1200L));
                    customSlownessTargets.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    customSlownessCleanseAfterTicks.keySet().removeIf(playerId ->
                            !customSlownessTargets.containsKey(playerId));
                    polymorphicMetaphysicalSlowResists.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    enemyStunCreeperSlownessTargets.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    soulHardenedBlockReadyTicks.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    destructionVelocitySuppressions.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    noKnockbackVelocitySuppressions.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    silenceCooldowns.entrySet().removeIf(entry -> entry.getValue() <= serverTick);
                    silencedEnchantExpires.values().forEach(map ->
                            map.entrySet().removeIf(entry -> entry.getValue() <= serverTick));
                    silencedEnchantExpires.entrySet().removeIf(entry -> entry.getValue().isEmpty()
                            || Bukkit.getPlayer(entry.getKey()) == null);
                    immortalFeedbackTicks.entrySet().removeIf(entry -> {
                        Player player = Bukkit.getPlayer(entry.getKey());
                        return player == null || !player.isOnline();
                    });
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    private void tickProactiveInertiaCleanses() {
        int intervalTicks = clamp(getEnchantsConfig().getInt("inertia.scan-interval-ticks", 2), 1, 20);
        if (serverTick % intervalTicks != 0L) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            handleInertiaDrunkSuppression(player);
            cleanseInertiaEffects(player);
            reconcileRestoredDrunkSlowness(player, false);
        }
    }

    private void cleanseInertiaEffects(Player player) {
        if (player == null || !player.isOnline() || !hasInertiaBlockedEffect(player)) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();
        ItemStack boots = equipment == null ? null : equipment.getBoots();
        int level = getEnchantLevelOnItem("inertia", "inertia", boots);
        if (level <= 0) {
            return;
        }

        boolean hadBlockedEffect = hasInertiaBlockedEffect(player);
        int souls = getInertiaSoulCost(level);
        if (!payDeathSaveSouls(player, boots, RollItemType.BOOTS, souls, "Inertia")) {
            return;
        }

        if (clearInertiaBlockedEffects(player) || hadBlockedEffect) {
            int remainingSouls = getAvailableSouls(boots);
            sendInertiaCleanseMessage(player, remainingSouls);
            playInertiaCleanseSound(player);
        }
    }

    private void handleInertiaDrunkSuppression(Player player) {
        if (player == null || !player.isOnline()
                || !getEnchantsConfig().getBoolean("inertia.restore-drunk-slowness-on-boot-removal", true)) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment == null ? null : equipment.getHelmet();
        ItemStack boots = equipment == null ? null : equipment.getBoots();
        int drunkAmplifier = getDrunkSlownessAmplifier(helmet);
        UUID playerId = player.getUniqueId();
        if (drunkAmplifier < 0) {
            inertiaSuppressedDrunkSlownessLevels.remove(playerId);
            return;
        }

        int inertiaLevel = getEnchantLevelOnItem("inertia", "inertia", boots);
        if (inertiaLevel <= 0) {
            return;
        }

        clearRestoredDrunkSlowness(player);
        PotionEffect current = player.getPotionEffect(PotionEffectType.SLOW);
        boolean customSlownessActive = hasActiveCustomEffectMarker(customSlownessTargets, player);
        boolean currentLooksLikeDrunk = current != null
                && current.getAmplifier() <= drunkAmplifier
                && !customSlownessActive;
        Integer suppressedAmplifier = inertiaSuppressedDrunkSlownessLevels.get(playerId);
        boolean alreadySuppressed = suppressedAmplifier != null && suppressedAmplifier == drunkAmplifier;

        if (alreadySuppressed) {
            if (currentLooksLikeDrunk) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            return;
        }

        int souls = getInertiaSoulCost(inertiaLevel);
        if (!payDeathSaveSouls(player, boots, RollItemType.BOOTS, souls, "Inertia")) {
            if (!currentLooksLikeDrunk && !customSlownessActive) {
                applyDrunkSlowness(player, drunkAmplifier, false);
            }
            return;
        }

        if (currentLooksLikeDrunk) {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        inertiaSuppressedDrunkSlownessLevels.put(playerId, drunkAmplifier);
        int remainingSouls = getAvailableSouls(boots);
        sendInertiaCleanseMessage(player, remainingSouls);
        playInertiaCleanseSound(player);
    }

    private int getInertiaSoulCost(int level) {
        String[] args = getEnchantEffectArguments("inertia", clamp(level, 1, 10), "INERTIA_CLEANSE");
        if (args.length >= 1) {
            return Math.max(0, parsePositiveInt(args[0], 0));
        }
        return getNativeSoulCost("inertia", "inertia", level);
    }

    private boolean hasInertiaBlockedEffect(Player player) {
        return player.hasPotionEffect(PotionEffectType.SLOW)
                && hasReadyCustomSlownessMarker(player);
    }

    private boolean clearInertiaBlockedEffects(Player player) {
        boolean cleared = false;
        if (player.hasPotionEffect(PotionEffectType.SLOW)
                && hasReadyCustomSlownessMarker(player)) {
            player.removePotionEffect(PotionEffectType.SLOW);
            customSlownessTargets.remove(player.getUniqueId());
            customSlownessCleanseAfterTicks.remove(player.getUniqueId());
            cleared = true;
        }
        return cleared;
    }

    private boolean hasReadyCustomSlownessMarker(Player player) {
        if (!hasActiveCustomEffectMarker(customSlownessTargets, player)) {
            customSlownessCleanseAfterTicks.remove(player.getUniqueId());
            return false;
        }

        Long cleanseAfterTick = customSlownessCleanseAfterTicks.get(player.getUniqueId());
        return cleanseAfterTick == null || serverTick >= cleanseAfterTick;
    }

    private boolean hasActiveCustomEffectMarker(Map<UUID, Long> markers, Player player) {
        Long expiresAtTick = markers.get(player.getUniqueId());
        if (expiresAtTick == null) {
            return false;
        }
        if (expiresAtTick <= serverTick) {
            markers.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    private void sendInertiaCleanseMessage(Player player, int remainingSouls) {
        String message = getEnchantsConfig().getString("inertia.message", "&6&l** INERTIA **");
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        int throttleMillis = Math.max(0, getEnchantsConfig().getInt("inertia.message-throttle-millis",
                getEnchantsConfig().getInt("inertia.sound-throttle-millis", 250)));
        Long lastSent = inertiaMessageThrottles.get(player.getUniqueId());
        if (lastSent != null && now - lastSent < throttleMillis) {
            return;
        }

        inertiaMessageThrottles.put(player.getUniqueId(), now);
        player.sendMessage(colorize(message
                .replace("%souls%", String.valueOf(Math.max(0, remainingSouls)))
                .replace("%amount%", String.valueOf(Math.max(0, remainingSouls)))
                .replace("%player%", player.getName())
                .replace("%player name%", player.getName())));
    }

    private void playInertiaCleanseSound(Player player) {
        long now = System.currentTimeMillis();
        int throttleMillis = Math.max(0, getEnchantsConfig().getInt("inertia.sound-throttle-millis", 250));
        Long lastPlayed = inertiaSoundThrottles.get(player.getUniqueId());
        if (lastPlayed != null && now - lastPlayed < throttleMillis) {
            return;
        }

        inertiaSoundThrottles.put(player.getUniqueId(), now);
        List<String> sounds = getEnchantsConfig().getStringList("inertia.sounds");
        if (sounds.isEmpty() && !getEnchantsConfig().contains("inertia.sounds")) {
            sounds = Collections.singletonList("ENTITY_PLAYER_BREATH:0.6:0.7");
        }
        for (String sound : sounds) {
            playConfiguredSound(player, sound);
        }
    }

    private void reconcileRestoredDrunkSlowness(Player player, boolean removedInertiaBoots) {
        if (player == null || !player.isOnline()) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment == null ? null : equipment.getHelmet();
        ItemStack boots = equipment == null ? null : equipment.getBoots();
        boolean wearingInertiaBoots = getEnchantLevelOnItem("inertia", "inertia", boots) > 0;
        int drunkSlownessAmplifier = getDrunkSlownessAmplifier(helmet);
        UUID playerId = player.getUniqueId();

        if (drunkSlownessAmplifier < 0) {
            inertiaSuppressedDrunkSlownessLevels.remove(playerId);
            clearRestoredDrunkSlowness(player);
            return;
        }

        if (wearingInertiaBoots) {
            clearRestoredDrunkSlowness(player);
            return;
        }

        Integer suppressedAmplifier = inertiaSuppressedDrunkSlownessLevels.remove(playerId);
        PotionEffect current = player.getPotionEffect(PotionEffectType.SLOW);
        if (suppressedAmplifier != null
                || current == null
                || current.getAmplifier() <= drunkSlownessAmplifier) {
            applyDrunkSlowness(player, drunkSlownessAmplifier, true);
        }
    }

    private void clearRestoredDrunkSlowness(Player player) {
        Integer restoredAmplifier = restoredDrunkSlownessLevels.remove(player.getUniqueId());
        if (restoredAmplifier == null) {
            return;
        }

        PotionEffect current = player.getPotionEffect(PotionEffectType.SLOW);
        if (current != null
                && current.getAmplifier() == restoredAmplifier
                && (isEffectivelyInfinitePotionDuration(current.getDuration())
                || current.getDuration() > 1000)) {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }

    private void applyDrunkSlowness(Player player, int amplifier, boolean trackRestored) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW, getRestoredDrunkSlownessDurationTicks(), amplifier, true, false, false), true);
        if (trackRestored) {
            restoredDrunkSlownessLevels.put(player.getUniqueId(), amplifier);
        }
    }

    private int getRestoredDrunkSlownessDurationTicks() {
        int configured = getEnchantsConfig().getInt("inertia.restored-drunk-slowness-duration-ticks", -1);
        if (configured <= 0 || configured == Integer.MAX_VALUE) {
            return -1;
        }
        return Math.max(20, configured);
    }

    private boolean isEffectivelyInfinitePotionDuration(int durationTicks) {
        return durationTicks < 0 || durationTicks == Integer.MAX_VALUE;
    }

    private int getDrunkSlownessAmplifier(ItemStack helmet) {
        int level = getEnchantLevelOnItem("drunk", "drunk", helmet);
        if (level <= 0) {
            return -1;
        }

        AdvancedEnchantment enchantment = getEnchantmentInstance("drunk", "drunk");
        if (enchantment != null) {
            for (String effect : getEnchantEffects(enchantment, "drunk", "drunk", level)) {
                Integer amplifier = parsePotionAmplifier(effect, "SLOW");
                if (amplifier != null) {
                    return amplifier;
                }
            }
        }

        return level >= 4 ? 2 : level >= 3 ? 1 : 0;
    }

    private Integer parsePotionAmplifier(String effect, String wantedPotionType) {
        if (effect == null) {
            return null;
        }

        String command = stripEffectTarget(effect).split("\\s+", 2)[0];
        String[] pieces = command.split(":");
        if (pieces.length < 3 || !pieces[0].equalsIgnoreCase("POTION")) {
            return null;
        }

        if (!canonicalPotionTypeName(pieces[1]).equals(canonicalPotionTypeName(wantedPotionType))) {
            return null;
        }
        return parsePositiveInt(pieces[2], 0);
    }

    private int getAvailableSouls(ItemStack item) {
        return isUsableItem(item) ? SoulsAPI.getSoulsOnItem(item) : 0;
    }

    private boolean canPaySingleDrain(ItemStack item, int amount) {
        return isUsableItem(item) && SoulsAPI.getSoulsOnItem(item) >= amount;
    }

    private int getNativeSoulCost(String rawEnchantName, String enchantName, int level) {
        try {
            return Math.max(0, AEAPI.getEnchantmentInstance(rawEnchantName).getAbility(level).getSouls());
        } catch (RuntimeException ignored) {
            try {
                return Math.max(0, AEAPI.getEnchantmentInstance(enchantName).getAbility(level).getSouls());
            } catch (RuntimeException ignoredAgain) {
                return 0;
            }
        }
    }

    private int getEnchantLevelOnItem(String rawEnchantName, String enchantName, ItemStack item) {
        int level = AEAPI.getEnchantLevel(rawEnchantName, item);
        return level > 0 ? level : AEAPI.getEnchantLevel(enchantName, item);
    }

    private ItemStack getItem(Player player, RollItemType itemType) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            return null;
        }

        if (itemType == null) {
            return equipment.getItemInMainHand();
        }

        switch (itemType.name()) {
            case "OFFHAND":
                return equipment.getItemInOffHand();
            case "HELMET":
                return equipment.getHelmet();
            case "CHESTPLATE":
                return equipment.getChestplate();
            case "LEGGINGS":
                return equipment.getLeggings();
            case "BOOTS":
                return equipment.getBoots();
            case "HAND":
            case "HANDS":
            case "MAIN":
            default:
                return equipment.getItemInMainHand();
        }
    }

    private void setItem(Player player, RollItemType itemType, ItemStack item) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            return;
        }

        if (itemType == null) {
            equipment.setItemInMainHand(item);
            return;
        }

        switch (itemType.name()) {
            case "OFFHAND":
                equipment.setItemInOffHand(item);
                break;
            case "HELMET":
                equipment.setHelmet(item);
                break;
            case "CHESTPLATE":
                equipment.setChestplate(item);
                break;
            case "LEGGINGS":
                equipment.setLeggings(item);
                break;
            case "BOOTS":
                equipment.setBoots(item);
                break;
            case "HAND":
            case "HANDS":
            case "MAIN":
            default:
                equipment.setItemInMainHand(item);
                break;
        }
    }

    private String extractEnchantName(AdvancedAbility ability) {
        return normalize(ability.getNameNoLevel());
    }

    private int extractLevel(AdvancedAbility ability) {
        String[] pieces = ability.getName().split(",");
        if (pieces.length > 1) {
            return parsePositiveInt(pieces[1], 1);
        }
        if (ability.getSection() != null) {
            return parsePositiveInt(ability.getSection().getName(), 1);
        }
        return 1;
    }

    private String prettyEnchantName(String enchantName) {
        return enchantName;
    }

    private String prettyDeathSaveName(String rawEnchantName, String enchantName) {
        DeathSaveRule rule = deathSaveRules.get(enchantName);
        return rule == null ? rawEnchantName : rule.displayName();
    }

    private boolean handleAddonGiveItemCommand(CommandSender sender, String commandLine) {
        return handleHeroicGiveItemCommand(sender, commandLine)
                || handleGodlyTransmogGiveItemCommand(sender, commandLine)
                || handleHeroicBlackScrollGiveItemCommand(sender, commandLine)
                || handleHolyWaterGiveItemCommand(sender, commandLine)
                || handleLoreLineGiveItemCommand(sender, commandLine);
    }

    private boolean handleAeaGiveItemCommand(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sendAeaGiveItemUsage(sender, label);
            return true;
        }

        if (handleAddonGiveItemCommand(sender, "ae " + String.join(" ", args))) {
            return true;
        }

        sendAeaGiveItemUsage(sender, label);
        return true;
    }

    private void sendAeaGiveItemUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label
                + " giveitem <player> <heroicupgrade|godlytransmogscroll|heroicblackscroll|holywater|loreline> [amount] [success|max]");
    }

    private boolean handleHeroicGiveItemCommand(CommandSender sender, String commandLine) {
        if (commandLine == null) {
            return false;
        }

        String[] args = commandLine.trim().replaceFirst("^/", "").split("\\s+");
        if (args.length < 4
                || !isAdvancedEnchantmentsCommand(args[0])
                || !args[1].equalsIgnoreCase("giveitem")
                || !isHeroicUpgradeItemName(args[3])) {
            return false;
        }

        if (!sender.hasPermission("ae.giveitem")
                && !sender.hasPermission("advancedenchantmentsaddon.heroicupgrade.give")) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.heroic-no-permission", "&cYou do not have permission to give Heroic Upgrades.")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.heroic-offline-player", "&cThat player is not online.")));
            return true;
        }

        HeroicGiveParameters parameters = parseHeroicGiveParameters(args);
        if (parameters == null) {
            sender.sendMessage(colorize(getConfig().getString("messages.heroic-give-usage",
                    "&cUsage: /ae giveitem <player> heroicupgrade [amount] [success]")));
            return true;
        }

        giveHeroicUpgrade(target, parameters.amount(), parameters.success());
        sender.sendMessage(colorize(getConfig().getString("messages.heroic-given",
                "&aGave &f%amount%x &aHeroic Upgrade &7(%success%% success)&a to &f%player%&a.")
                .replace("%amount%", String.valueOf(parameters.amount()))
                .replace("%success%", String.valueOf(parameters.success()))
                .replace("%player%", target.getName())));
        return true;
    }

    private boolean handleGodlyTransmogGiveItemCommand(CommandSender sender, String commandLine) {
        if (commandLine == null) {
            return false;
        }

        String[] args = commandLine.trim().replaceFirst("^/", "").split("\\s+");
        if (args.length < 4
                || !isAdvancedEnchantmentsCommand(args[0])
                || !args[1].equalsIgnoreCase("giveitem")
                || !isGodlyTransmogScrollItemName(args[3])) {
            return false;
        }

        if (!sender.hasPermission("ae.giveitem")
                && !sender.hasPermission("advancedenchantmentsaddon.godlytransmog.give")) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.godly-transmog-no-permission",
                    "&cYou do not have permission to give Godly Transmog Scrolls.")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.godly-transmog-offline-player", "&cThat player is not online.")));
            return true;
        }

        int amount = parseGodlyTransmogAmount(args);
        if (amount <= 0) {
            sender.sendMessage(colorize(getConfig().getString("messages.godly-transmog-give-usage",
                    "&cUsage: /ae giveitem <player> godlytransmogscroll [amount]")));
            return true;
        }

        giveGodlyTransmogScroll(target, amount);
        sender.sendMessage(colorize(getConfig().getString("messages.godly-transmog-given",
                "&aGave &f%amount%x &dGodly Transmog Scroll &ato &f%player%&a.")
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", target.getName())));
        return true;
    }

    private boolean handleHeroicBlackScrollGiveItemCommand(CommandSender sender, String commandLine) {
        if (commandLine == null) {
            return false;
        }

        String[] args = commandLine.trim().replaceFirst("^/", "").split("\\s+");
        if (args.length < 4
                || !isAdvancedEnchantmentsCommand(args[0])
                || !args[1].equalsIgnoreCase("giveitem")
                || !isHeroicBlackScrollItemName(args[3])) {
            return false;
        }

        if (!sender.hasPermission("ae.giveitem")
                && !sender.hasPermission("advancedenchantmentsaddon.heroicblackscroll.give")) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.heroic-black-scroll-no-permission",
                    "&cYou do not have permission to give Heroic Black Scrolls.")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.heroic-black-scroll-offline-player", "&cThat player is not online.")));
            return true;
        }

        HeroicGiveParameters parameters = parseHeroicBlackScrollGiveParameters(args);
        if (parameters == null) {
            sender.sendMessage(colorize(getConfig().getString("messages.heroic-black-scroll-give-usage",
                    "&cUsage: /ae giveitem <player> heroicblackscroll [amount] [success]")));
            return true;
        }

        giveHeroicBlackScroll(target, parameters.amount(), parameters.success());
        sender.sendMessage(colorize(getConfig().getString("messages.heroic-black-scroll-given",
                "&aGave &f%amount%x &dHeroic Black Scroll &7(%success%% success)&a to &f%player%&a.")
                .replace("%amount%", String.valueOf(parameters.amount()))
                .replace("%success%", String.valueOf(parameters.success()))
                .replace("%player%", target.getName())));
        return true;
    }

    private boolean handleHolyWaterGiveItemCommand(CommandSender sender, String commandLine) {
        if (commandLine == null) {
            return false;
        }

        String[] args = commandLine.trim().replaceFirst("^/", "").split("\\s+");
        if (args.length < 4
                || !isAdvancedEnchantmentsCommand(args[0])
                || !args[1].equalsIgnoreCase("giveitem")
                || !isHolyWaterItemName(args[3])) {
            return false;
        }

        if (!sender.hasPermission("ae.giveitem")
                && !sender.hasPermission("advancedenchantmentsaddon.holywater.give")) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.holy-water-no-permission",
                    "&cYou do not have permission to give Holy Water.")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.holy-water-offline-player", "&cThat player is not online.")));
            return true;
        }

        HolyWaterGiveParameters parameters = parseHolyWaterGiveParameters(args);
        if (parameters == null) {
            sender.sendMessage(colorize(getConfig().getString("messages.holy-water-give-usage",
                    "&cUsage: /ae giveitem <player> holywater [amount] [max]")));
            return true;
        }

        giveHolyWater(target, parameters.amount(), parameters.maxApplications());
        sender.sendMessage(colorize(getConfig().getString("messages.holy-water-given",
                "&aGave &f%amount%x &eHoly Water &7(Max: %max%)&a to &f%player%&a.")
                .replace("%amount%", String.valueOf(parameters.amount()))
                .replace("%max%", String.valueOf(parameters.maxApplications()))
                .replace("%player%", target.getName())));
        return true;
    }

    private boolean handleLoreLineGiveItemCommand(CommandSender sender, String commandLine) {
        if (commandLine == null) {
            return false;
        }

        String[] args = commandLine.trim().replaceFirst("^/", "").split("\\s+");
        if (args.length < 4
                || !isAdvancedEnchantmentsCommand(args[0])
                || !args[1].equalsIgnoreCase("giveitem")
                || !isLoreLineItemName(args[3])) {
            return false;
        }

        if (!sender.hasPermission("ae.giveitem")
                && !sender.hasPermission("advancedenchantmentsaddon.loreline.give")) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.lore-line-no-permission",
                    "&cYou do not have permission to give Lore Lines.")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(colorize(getConfig().getString(
                    "messages.lore-line-offline-player", "&cThat player is not online.")));
            return true;
        }

        int amount = parseGodlyTransmogAmount(args);
        if (amount <= 0) {
            sender.sendMessage(colorize(getConfig().getString("messages.lore-line-give-usage",
                    "&cUsage: /ae giveitem <player> loreline [amount]")));
            return true;
        }

        giveLoreLine(target, amount);
        sender.sendMessage(colorize(getConfig().getString("messages.lore-line-given",
                "&aGave &f%amount%x &6Lore Line &ato &f%player%&a.")
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", target.getName())));
        return true;
    }

    private static boolean isAdvancedEnchantmentsCommand(String label) {
        return label != null
                && (label.equalsIgnoreCase("ae") || label.equalsIgnoreCase("advancedenchantments"));
    }

    private static boolean isHeroicUpgradeItemName(String value) {
        return HEROIC_UPGRADE_ITEM_NAMES.contains(normalize(value));
    }

    private static boolean isGodlyTransmogScrollItemName(String value) {
        return GODLY_TRANSMOG_SCROLL_ITEM_NAMES.contains(normalize(value));
    }

    private static boolean isHeroicBlackScrollItemName(String value) {
        return HEROIC_BLACK_SCROLL_ITEM_NAMES.contains(normalize(value));
    }

    private static boolean isHolyWaterItemName(String value) {
        return HOLY_WATER_ITEM_NAMES.contains(normalize(value));
    }

    private static boolean isLoreLineItemName(String value) {
        return LORE_LINE_ITEM_NAMES.contains(normalize(value));
    }

    private static boolean hasAnyAddonGiveItemPermission(CommandSender sender) {
        return canGiveHeroicUpgrade(sender)
                || canGiveGodlyTransmogScroll(sender)
                || canGiveHeroicBlackScroll(sender)
                || canGiveHolyWater(sender)
                || canGiveLoreLine(sender);
    }

    private static boolean canGiveAddonItem(CommandSender sender, String itemName) {
        return (isHeroicUpgradeItemName(itemName) && canGiveHeroicUpgrade(sender))
                || (isGodlyTransmogScrollItemName(itemName) && canGiveGodlyTransmogScroll(sender))
                || (isHeroicBlackScrollItemName(itemName) && canGiveHeroicBlackScroll(sender))
                || (isHolyWaterItemName(itemName) && canGiveHolyWater(sender))
                || (isLoreLineItemName(itemName) && canGiveLoreLine(sender));
    }

    private static boolean canGiveHeroicUpgrade(CommandSender sender) {
        return sender.hasPermission("ae.giveitem")
                || sender.hasPermission("advancedenchantmentsaddon.heroicupgrade.give");
    }

    private static boolean canGiveGodlyTransmogScroll(CommandSender sender) {
        return sender.hasPermission("ae.giveitem")
                || sender.hasPermission("advancedenchantmentsaddon.godlytransmog.give");
    }

    private static boolean canGiveHeroicBlackScroll(CommandSender sender) {
        return sender.hasPermission("ae.giveitem")
                || sender.hasPermission("advancedenchantmentsaddon.heroicblackscroll.give");
    }

    private static boolean canGiveHolyWater(CommandSender sender) {
        return sender.hasPermission("ae.giveitem")
                || sender.hasPermission("advancedenchantmentsaddon.holywater.give");
    }

    private static boolean canGiveLoreLine(CommandSender sender) {
        return sender.hasPermission("ae.giveitem")
                || sender.hasPermission("advancedenchantmentsaddon.loreline.give");
    }

    private static int parseGodlyTransmogAmount(String[] args) {
        int amount = 1;
        boolean amountSet = false;
        for (int i = 4; i < args.length; i++) {
            String token = args[i] == null ? "" : args[i].trim();
            if (token.isEmpty()) {
                continue;
            }
            int parsed = parsePositiveInt(token, -1);
            if (parsed <= 0 || amountSet) {
                return -1;
            }
            amount = parsed;
            amountSet = true;
        }
        return amount;
    }

    private HeroicGiveParameters parseHeroicBlackScrollGiveParameters(String[] args) {
        int amount = 1;
        int success = randomHeroicBlackScrollSuccess();
        boolean amountSet = false;
        boolean successSet = false;

        for (int i = 4; i < args.length; i++) {
            String token = args[i] == null ? "" : args[i].trim();
            if (token.isEmpty()) {
                continue;
            }

            String lower = token.toLowerCase(Locale.ROOT);
            if (lower.startsWith("success:") || lower.startsWith("chance:")) {
                int parsed = parseChance(token.substring(token.indexOf(':') + 1), -1);
                if (parsed < 0) {
                    return null;
                }
                success = parsed;
                successSet = true;
                continue;
            }

            int parsed = parsePositiveInt(token.replace("%", ""), -1);
            if (parsed < 0) {
                return null;
            }
            if (!amountSet) {
                amount = parsed;
                amountSet = true;
            } else if (!successSet) {
                success = clamp(parsed, 0, 100);
                successSet = true;
            } else {
                return null;
            }
        }

        if (amount <= 0) {
            return null;
        }
        return new HeroicGiveParameters(amount, clamp(success, 0, 100));
    }

    private HolyWaterGiveParameters parseHolyWaterGiveParameters(String[] args) {
        int amount = 1;
        int maxApplications = holyWaterDefaultMaxApplications;
        boolean amountSet = false;
        boolean maxSet = false;

        for (int i = 4; i < args.length; i++) {
            String token = args[i] == null ? "" : args[i].trim();
            if (token.isEmpty()) {
                continue;
            }

            String lower = token.toLowerCase(Locale.ROOT);
            if (lower.startsWith("max:") || lower.startsWith("limit:") || lower.startsWith("applications:")) {
                int parsed = parsePositiveInt(token.substring(token.indexOf(':') + 1), -1);
                if (parsed <= 0) {
                    return null;
                }
                maxApplications = parsed;
                maxSet = true;
                continue;
            }

            int parsed = parsePositiveInt(token, -1);
            if (parsed <= 0) {
                return null;
            }
            if (!amountSet) {
                amount = parsed;
                amountSet = true;
            } else if (!maxSet) {
                maxApplications = parsed;
                maxSet = true;
            } else {
                return null;
            }
        }

        if (amount <= 0 || maxApplications <= 0) {
            return null;
        }
        return new HolyWaterGiveParameters(amount, maxApplications);
    }

    private HeroicGiveParameters parseHeroicGiveParameters(String[] args) {
        int amount = 1;
        int success = heroicDefaultSuccess;
        boolean amountSet = false;
        boolean successSet = false;

        for (int i = 4; i < args.length; i++) {
            String token = args[i] == null ? "" : args[i].trim();
            if (token.isEmpty()) {
                continue;
            }

            String lower = token.toLowerCase(Locale.ROOT);
            if (lower.startsWith("success:") || lower.startsWith("chance:")) {
                int parsed = parseChance(token.substring(token.indexOf(':') + 1), -1);
                if (parsed < 0) {
                    return null;
                }
                success = parsed;
                successSet = true;
                continue;
            }

            int parsed = parsePositiveInt(token.replace("%", ""), -1);
            if (parsed < 0) {
                return null;
            }
            if (!amountSet) {
                amount = parsed;
                amountSet = true;
            } else if (!successSet) {
                success = clamp(parsed, 0, 100);
                successSet = true;
            } else {
                return null;
            }
        }

        if (amount <= 0) {
            return null;
        }
        return new HeroicGiveParameters(amount, clamp(success, 0, 100));
    }

    private void giveHeroicUpgrade(Player player, int amount, int success) {
        int remaining = amount;
        int maxStackSize = Math.max(1, heroicUpgradeMaterial.getMaxStackSize());
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, maxStackSize);
            ItemStack item = createHeroicUpgradeItem(success, stackAmount);
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stackAmount;
        }
    }

    private ItemStack createHeroicUpgradeItem(int success, int amount) {
        ItemStack item = new ItemStack(heroicUpgradeMaterial, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        String defaultName = "&#FB8A53&lH&#FC9957&le&#FCA75A&lr&#FDB65E&lo&#FEC562&li&#FED365&lc "
                + "&#FFE269&lU&#FED365&lp&#FEC562&lg&#FDB65E&lr&#FCA75A&la&#FC9957&ld&#FB8A53&le "
                + "&6&l(&e%success%%&6&l)";
        meta.setDisplayName(colorize(getConfig().getString("heroic-upgrade.item.name", defaultName)
                .replace("%success%", String.valueOf(success))));

        List<String> lore = getConfig().getStringList("heroic-upgrade.item.lore");
        if (lore.isEmpty()) {
            lore = Arrays.asList(
                    "&7Apply to any diamond or netherite armor",
                    "&7piece or weapon to imbue it with the power",
                    "&7of &6heroic resolve&7.",
                    "",
                    "&7This will increase the item's base stats and",
                    "&7convert it to gold material if it is a weapon."
            );
        }
        List<String> coloredLore = new java.util.ArrayList<>();
        for (String line : lore) {
            coloredLore.add(colorize(line));
        }
        meta.setLore(coloredLore);
        meta.getPersistentDataContainer().set(heroicUpgradeItemKey, PersistentDataType.INTEGER, 1);
        meta.getPersistentDataContainer().set(heroicUpgradeSuccessKey, PersistentDataType.INTEGER, clamp(success, 0, 100));
        item.setItemMeta(meta);
        return item;
    }

    private void giveGodlyTransmogScroll(Player player, int amount) {
        int remaining = amount;
        int maxStackSize = Math.max(1, godlyTransmogScrollMaterial.getMaxStackSize());
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, maxStackSize);
            ItemStack item = createGodlyTransmogScrollItem(stackAmount);
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stackAmount;
        }
    }

    private ItemStack createGodlyTransmogScrollItem(int amount) {
        ItemStack item = new ItemStack(godlyTransmogScrollMaterial, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize(getConfig().getString("godly-transmog-scroll.item.name",
                "&d&lGodly Transmog Scroll")));

        List<String> lore = getConfig().getStringList("godly-transmog-scroll.item.lore");
        if (lore.isEmpty()) {
            lore = Arrays.asList(
                    "&7Organizes enchants by &e&nrarity&7 on item",
                    "&7and adds the &dlore &bcount&7 to name.",
                    "",
                    "&e&oPlace scroll on item to apply."
            );
        }
        List<String> coloredLore = new java.util.ArrayList<>();
        for (String line : lore) {
            coloredLore.add(colorize(line));
        }
        meta.setLore(coloredLore);
        meta.getPersistentDataContainer().set(godlyTransmogScrollItemKey, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    private void giveHeroicBlackScroll(Player player, int amount, int success) {
        int remaining = amount;
        int maxStackSize = Math.max(1, heroicBlackScrollMaterial.getMaxStackSize());
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, maxStackSize);
            ItemStack item = createHeroicBlackScrollItem(success, stackAmount);
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stackAmount;
        }
    }

    private ItemStack createHeroicBlackScrollItem(int success, int amount) {
        ItemStack item = new ItemStack(heroicBlackScrollMaterial, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize(getConfig().getString("heroic-black-scroll.item.name",
                "&d&lHeroic Black Scroll").replace("%success%", String.valueOf(success))));

        List<String> lore = getConfig().getStringList("heroic-black-scroll.item.lore");
        if (lore.isEmpty()) {
            lore = Arrays.asList(
                    "&7Removes a random &dHeroic &7enchantment",
                    "&7from an item and converts",
                    "&7it into a &d%success%% &7success book.",
                    "",
                    "&d&l(!) &dRemoves Heroic Enchantments"
            );
        }
        List<String> coloredLore = new java.util.ArrayList<>();
        for (String line : lore) {
            coloredLore.add(colorize(line.replace("%success%", String.valueOf(success))));
        }
        meta.setLore(coloredLore);
        meta.getPersistentDataContainer().set(heroicBlackScrollItemKey, PersistentDataType.INTEGER, 1);
        meta.getPersistentDataContainer().set(heroicBlackScrollSuccessKey, PersistentDataType.INTEGER, clamp(success, 0, 100));
        item.setItemMeta(meta);
        return item;
    }

    private void giveHolyWater(Player player, int amount, int maxApplications) {
        int remaining = amount;
        int maxStackSize = Math.max(1, holyWaterMaterial.getMaxStackSize());
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, maxStackSize);
            ItemStack item = createHolyWaterItem(maxApplications, stackAmount);
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stackAmount;
        }
    }

    private ItemStack createHolyWaterItem(int maxApplications, int amount) {
        int safeMax = Math.max(1, maxApplications);
        ItemStack item = new ItemStack(holyWaterMaterial, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize(getConfig().getString("holy-water.item.name",
                "&6&lHoly Water [&eMax: %max%&6&l]").replace("%max%", String.valueOf(safeMax))));

        List<String> lore = getConfig().getStringList("holy-water.item.lore");
        if (lore.isEmpty()) {
            lore = Arrays.asList(
                    "&eA rare consumable item that",
                    "&eincreases the max number of Holy White Scrolls",
                    "&ethat can be applied to an item by %increase%.",
                    "",
                    "&7Apply to any (semi) corrupt item."
            );
        }
        int increase = Math.max(0, safeMax - holyWhiteScrollMaxApplications);
        List<String> coloredLore = new java.util.ArrayList<>();
        for (String line : lore) {
            coloredLore.add(colorize(line
                    .replace("%max%", String.valueOf(safeMax))
                    .replace("%increase%", String.valueOf(increase))));
        }
        meta.setLore(coloredLore);
        meta.getPersistentDataContainer().set(holyWaterItemKey, PersistentDataType.INTEGER, 1);
        meta.getPersistentDataContainer().set(holyWaterMaxApplicationsKey, PersistentDataType.INTEGER, safeMax);
        item.setItemMeta(meta);
        return item;
    }

    private void giveLoreLine(Player player, int amount) {
        giveStackedItem(player, amount, loreLineMaterial.getMaxStackSize(), this::createLoreLineItem);
    }

    private void giveStackedItem(Player player, int amount, int maxStackSize,
                                 java.util.function.IntFunction<ItemStack> factory) {
        int remaining = amount;
        int safeMaxStackSize = Math.max(1, maxStackSize);
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, safeMaxStackSize);
            ItemStack item = factory.apply(stackAmount);
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stackAmount;
        }
    }

    private ItemStack createLoreLineItem(int amount) {
        ItemStack item = new ItemStack(loreLineMaterial, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize(getConfig().getString("lore-line.item.name", "&6&lCustom Lore Tag")));
        List<String> lore = getConfig().getStringList("lore-line.item.lore");
        if (lore.isEmpty()) {
            lore = Arrays.asList(
                    "&7Add one line of custom lore",
                    "&7to the item in your hand.",
                    "",
                    "&6&oRight click to use."
            );
        }
        List<String> coloredLore = new java.util.ArrayList<>();
        for (String line : lore) {
            coloredLore.add(colorize(line.replace("%max%", String.valueOf(loreLineMaxLinesPerItem))));
        }
        meta.setLore(coloredLore);
        meta.getPersistentDataContainer().set(loreLineItemKey, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> colorizeLines(List<String> lines) {
        List<String> colored = new java.util.ArrayList<>();
        for (String line : lines) {
            colored.add(colorize(line));
        }
        return colored;
    }

    private boolean isHeroicUpgradeItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(heroicUpgradeItemKey, PersistentDataType.INTEGER);
    }

    private boolean isGodlyTransmogScrollItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(godlyTransmogScrollItemKey, PersistentDataType.INTEGER);
    }

    private boolean isHeroicBlackScrollItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(heroicBlackScrollItemKey, PersistentDataType.INTEGER);
    }

    private boolean isHolyWaterItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(holyWaterItemKey, PersistentDataType.INTEGER);
    }

    private boolean isLoreLineItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(loreLineItemKey, PersistentDataType.INTEGER);
    }

    private int getHeroicUpgradeSuccess(ItemStack item) {
        if (!isHeroicUpgradeItem(item)) {
            return heroicDefaultSuccess;
        }

        Integer success = item.getItemMeta().getPersistentDataContainer()
                .get(heroicUpgradeSuccessKey, PersistentDataType.INTEGER);
        return clamp(success == null ? heroicDefaultSuccess : success, 0, 100);
    }

    private int getHeroicBlackScrollSuccess(ItemStack item) {
        if (!isHeroicBlackScrollItem(item)) {
            return randomHeroicBlackScrollSuccess();
        }

        Integer success = item.getItemMeta().getPersistentDataContainer()
                .get(heroicBlackScrollSuccessKey, PersistentDataType.INTEGER);
        return clamp(success == null ? randomHeroicBlackScrollSuccess() : success, 0, 100);
    }

    private int getHolyWaterMaxApplications(ItemStack item) {
        if (!isHolyWaterItem(item)) {
            return holyWaterDefaultMaxApplications;
        }

        Integer maxApplications = item.getItemMeta().getPersistentDataContainer()
                .get(holyWaterMaxApplicationsKey, PersistentDataType.INTEGER);
        return Math.max(1, maxApplications == null ? holyWaterDefaultMaxApplications : maxApplications);
    }

    private int randomHeroicBlackScrollSuccess() {
        if (heroicBlackScrollDefaultMaxSuccess <= heroicBlackScrollDefaultMinSuccess) {
            return heroicBlackScrollDefaultMinSuccess;
        }
        return ThreadLocalRandom.current().nextInt(heroicBlackScrollDefaultMinSuccess,
                heroicBlackScrollDefaultMaxSuccess + 1);
    }

    private void consumeCursorItem(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (!isUsableItem(cursor)) {
            return;
        }

        if (cursor.getAmount() <= 1) {
            event.setCursor(null);
            return;
        }

        ItemStack updated = cursor.clone();
        updated.setAmount(cursor.getAmount() - 1);
        event.setCursor(updated);
    }

    private boolean isRightClick(PlayerInteractEvent event) {
        return event != null
                && event.getAction() != null
                && event.getAction().name().startsWith("RIGHT_CLICK");
    }

    private void consumeInteractionItem(PlayerInteractEvent event, Player player) {
        EquipmentSlot hand = event.getHand() == null ? EquipmentSlot.HAND : event.getHand();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = hand == EquipmentSlot.OFF_HAND
                ? inventory.getItemInOffHand()
                : inventory.getItemInMainHand();
        if (!isUsableItem(item)) {
            return;
        }

        if (item.getAmount() <= 1) {
            if (hand == EquipmentSlot.OFF_HAND) {
                inventory.setItemInOffHand(new ItemStack(Material.AIR));
            } else {
                inventory.setItemInMainHand(new ItemStack(Material.AIR));
            }
            return;
        }

        ItemStack updated = item.clone();
        updated.setAmount(item.getAmount() - 1);
        if (hand == EquipmentSlot.OFF_HAND) {
            inventory.setItemInOffHand(updated);
        } else {
            inventory.setItemInMainHand(updated);
        }
    }

    private void sendTextItemPrompt(Player player) {
        player.sendMessage(colorize(getConfig().getString("messages.lore-line-prompt",
                "&eType the lore line in chat. Color codes are allowed.")));
    }

    private void completeTextItemEdit(Player player, PendingTextItemEdit session, String text) {
        if (player == null || !player.isOnline()) {
            return;
        }

        if (text == null || text.trim().isEmpty()) {
            refundPendingTextItem(player, session);
            sendTextItemFailure(player, "empty");
            player.updateInventory();
            return;
        }

        ItemStack target = player.getInventory().getItemInMainHand();
        if (!isValidTextEditTarget(target)) {
            refundPendingTextItem(player, session);
            sendTextItemFailure(player, "no-target");
            player.updateInventory();
            return;
        }

        int currentLoreLines = getCustomLoreLineCount(target);
        if (currentLoreLines >= loreLineMaxLinesPerItem) {
            refundPendingTextItem(player, session);
            sendTextItemFailure(player, "max-reached",
                    "%current%", String.valueOf(currentLoreLines),
                    "%max%", String.valueOf(loreLineMaxLinesPerItem));
            player.updateInventory();
            return;
        }

        ItemStack updated = applyCustomLoreLine(target, text);
        player.getInventory().setItemInMainHand(updated);
        sendThrottled(player, "messages.lore-line-applied");
        playConfiguredSounds(player, "sounds.lore-line-applied");
        player.updateInventory();
    }

    private void sendTextItemFailure(Player player, String reason, String... replacements) {
        String path = "messages.lore-line-" + reason;
        String fallback;
        if (reason.equals("empty")) {
            fallback = "&cLore line cannot be blank. Your Lore Line was returned.";
        } else if (reason.equals("max-reached")) {
            fallback = "&cThat item already has the maximum of &f%max% &ccustom lore lines. Your Lore Line was returned.";
        } else {
            fallback = "&cHold the item you want to add lore to in your main hand. Your Lore Line was returned.";
        }

        String message = getConfig().getString(path, fallback);
        if (message == null || message.isEmpty()) {
            message = fallback;
        }
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        player.sendMessage(colorize(message));
        playConfiguredSounds(player, "sounds.lore-line-failed");
    }

    private boolean isValidTextEditTarget(ItemStack item) {
        return isUsableItem(item) && !isAddonUtilityItem(item);
    }

    private boolean isAddonUtilityItem(ItemStack item) {
        return isHeroicUpgradeItem(item)
                || isGodlyTransmogScrollItem(item)
                || isHeroicBlackScrollItem(item)
                || isHolyWaterItem(item)
                || isLoreLineItem(item);
    }

    private ItemStack applyCustomLoreLine(ItemStack item, String line) {
        ItemStack updated = item.clone();
        ItemMeta meta = updated.getItemMeta();
        if (meta == null) {
            return updated;
        }

        List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new java.util.ArrayList<>(meta.getLore())
                : new java.util.ArrayList<>();
        lore.add(colorize(line));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(loreLineCountKey, PersistentDataType.INTEGER,
                getCustomLoreLineCount(meta) + 1);
        updated.setItemMeta(meta);
        return updated;
    }

    private int getCustomLoreLineCount(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return 0;
        }
        return getCustomLoreLineCount(item.getItemMeta());
    }

    private int getCustomLoreLineCount(ItemMeta meta) {
        if (meta == null) {
            return 0;
        }

        Integer count = meta.getPersistentDataContainer().get(loreLineCountKey, PersistentDataType.INTEGER);
        return Math.max(0, count == null ? 0 : count);
    }

    private void refundPendingTextItem(Player player, PendingTextItemEdit session) {
        if (session == null || !isUsableItem(session.refundItem())) {
            return;
        }
        giveOrDropItem(player, session.refundItem().clone());
    }

    private boolean isHolyWhiteScrollCorruptionEnabled() {
        return getConfig().getBoolean("holy-white-scroll-corruption.enabled", true);
    }

    private boolean isHolyWhiteScrollItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }
        if (isHeroicUpgradeItem(item)
                || isGodlyTransmogScrollItem(item)
                || isHeroicBlackScrollItem(item)
                || isHolyWaterItem(item)
                || isLoreLineItem(item)) {
            return false;
        }

        try {
            if (NBTapi.contains("holywhitescroll", item)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // Fall through to API and text checks.
        }

        try {
            if (AEAPI.hasHolyWhiteScroll(item) && !hasHolyWhiteScrollProtection(item)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // Fall through to text checks.
        }

        return looksLikeHolyWhiteScrollItem(item);
    }

    private void hideRejectedHolyWhiteScrollCursor(InventoryClickEvent event, Player player, ItemStack scroll) {
        ItemStack originalCursor = scroll.clone();
        event.setCursor(null);
        Bukkit.getScheduler().runTask(this, () -> restoreRejectedHolyWhiteScrollCursor(player, originalCursor));
    }

    private void restoreRejectedHolyWhiteScrollCursor(Player player, ItemStack originalCursor) {
        if (player == null || !player.isOnline()) {
            return;
        }

        ItemStack currentCursor = player.getItemOnCursor();
        if (!isUsableItem(currentCursor)) {
            player.setItemOnCursor(originalCursor);
        } else if (!sameItemAndAmount(currentCursor, originalCursor)) {
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(originalCursor);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
        player.updateInventory();
    }

    private static boolean sameItemAndAmount(ItemStack first, ItemStack second) {
        if (!isUsableItem(first) || !isUsableItem(second)) {
            return false;
        }
        return first.getAmount() == second.getAmount() && first.isSimilar(second);
    }

    private boolean hasHolyWhiteScrollProtection(ItemStack item) {
        if (!isUsableItem(item)) {
            return false;
        }

        try {
            if (NBTapi.contains("holywhitescrolled", item)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // Fall through to lore check.
        }

        try {
            if (!hasHolyWhiteScrollItemMarker(item) && AEAPI.hasHolyWhiteScroll(item)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // Fall through to lore check.
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null
                && meta.hasLore()
                && meta.getLore() != null
                && findHolyWhiteScrollProtectedLoreIndex(meta.getLore()) >= 0;
    }

    private boolean hasHolyWhiteScrollItemMarker(ItemStack item) {
        if (!isUsableItem(item)) {
            return false;
        }

        try {
            return NBTapi.contains("holywhitescroll", item);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private boolean looksLikeHolyWhiteScrollItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        if (meta.hasDisplayName()) {
            String name = cleanDisplayName(meta.getDisplayName()).toUpperCase(Locale.ROOT);
            if (name.contains("HOLY") && name.contains("WHITE") && name.contains("SCROLL")) {
                return true;
            }
        }

        if (!meta.hasLore() || meta.getLore() == null) {
            return false;
        }

        for (String line : meta.getLore()) {
            String stripped = cleanDisplayName(line).toUpperCase(Locale.ROOT);
            if (stripped.contains("PROTECT") && stripped.contains("DEATH")) {
                return true;
            }
        }
        return false;
    }

    private boolean isHolyWhiteScrollCorrupted(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        if (hasHolyWhiteScrollCorruptedMarkerOrLore(meta)) {
            return true;
        }

        return getHolyWhiteScrollCorruptionCount(item) >= getHolyWhiteScrollMaxApplications(item)
                && !hasHolyWhiteScrollProtection(item);
    }

    private int getHolyWhiteScrollCorruptionCount(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        Integer count = meta.getPersistentDataContainer()
                .get(holyWhiteScrollCorruptionCountKey, PersistentDataType.INTEGER);
        if (count != null && count > 0) {
            return Math.max(0, count);
        }

        HolyWhiteScrollCorruptionNumbers loreNumbers = findHolyWhiteScrollCorruptionNumbers(meta);
        if (loreNumbers != null) {
            return Math.max(0, loreNumbers.count());
        }

        return hasHolyWhiteScrollCorruptedMarkerOrLore(meta) ? getHolyWhiteScrollMaxApplications(item) : 0;
    }

    private int getHolyWhiteScrollMaxApplications(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return holyWhiteScrollMaxApplications;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return holyWhiteScrollMaxApplications;
        }

        Integer maxApplications = meta.getPersistentDataContainer()
                .get(holyWhiteScrollMaxApplicationsKey, PersistentDataType.INTEGER);
        if (maxApplications != null && maxApplications > 0) {
            return maxApplications;
        }

        HolyWhiteScrollCorruptionNumbers loreNumbers = findHolyWhiteScrollCorruptionNumbers(meta);
        return loreNumbers == null ? holyWhiteScrollMaxApplications : Math.max(1, loreNumbers.max());
    }

    private boolean hasHolyWhiteScrollCorruptionData(ItemStack item) {
        return getHolyWhiteScrollCorruptionCount(item) > 0 || isHolyWhiteScrollCorrupted(item);
    }

    private boolean hasHolyWhiteScrollCorruptedMarkerOrLore(ItemMeta meta) {
        if (meta == null) {
            return false;
        }

        Integer corrupted = meta.getPersistentDataContainer().get(holyWhiteScrollCorruptedKey, PersistentDataType.INTEGER);
        return (corrupted != null && corrupted > 0)
                || (meta.hasLore()
                && meta.getLore() != null
                && findHolyWhiteScrollCorruptedLoreIndex(meta.getLore()) >= 0);
    }

    private ItemStack applyHolyWhiteScrollSemiCorruption(ItemStack item, int count) {
        ItemStack updated = item.clone();
        ItemMeta meta = updated.getItemMeta();
        if (meta == null) {
            return updated;
        }

        int maxApplications = getHolyWhiteScrollMaxApplications(updated);
        int safeCount = clamp(count, 1, maxApplications);
        meta.getPersistentDataContainer().set(holyWhiteScrollMaxApplicationsKey,
                PersistentDataType.INTEGER, maxApplications);
        meta.getPersistentDataContainer().set(holyWhiteScrollCorruptionCountKey, PersistentDataType.INTEGER, safeCount);
        meta.getPersistentDataContainer().remove(holyWhiteScrollCorruptedKey);

        List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new java.util.ArrayList<>(meta.getLore())
                : new java.util.ArrayList<>();
        removeHolyWhiteScrollCorruptionLore(lore);

        int protectedIndex = findHolyWhiteScrollProtectedLoreIndex(lore);
        String corruptionLine = formatHolyWhiteScrollSemiCorruptLore(safeCount, maxApplications);
        if (protectedIndex >= 0) {
            lore.add(protectedIndex + 1, corruptionLine);
        } else {
            lore.add(corruptionLine);
        }

        meta.setLore(lore);
        updated.setItemMeta(meta);
        return updated;
    }

    private ItemStack applyHolyWhiteScrollCorrupted(ItemStack item) {
        ItemStack updated = item.clone();
        int maxApplications = getHolyWhiteScrollMaxApplications(updated);
        if (hasHolyWhiteScrollProtection(updated)) {
            try {
                updated = AEAPI.removeHolyWhiteScroll(updated);
            } catch (RuntimeException ignored) {
                // Continue and still write the corrupted marker/lore.
            }
        }

        ItemMeta meta = updated.getItemMeta();
        if (meta == null) {
            return updated;
        }

        meta.getPersistentDataContainer().set(holyWhiteScrollMaxApplicationsKey,
                PersistentDataType.INTEGER, maxApplications);
        meta.getPersistentDataContainer().set(holyWhiteScrollCorruptionCountKey,
                PersistentDataType.INTEGER, maxApplications);
        meta.getPersistentDataContainer().set(holyWhiteScrollCorruptedKey, PersistentDataType.INTEGER, 1);

        List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new java.util.ArrayList<>(meta.getLore())
                : new java.util.ArrayList<>();
        int insertionIndex = findHolyWhiteScrollCorruptionLoreIndex(lore);
        if (insertionIndex < 0) {
            insertionIndex = findHolyWhiteScrollProtectedLoreIndex(lore);
            if (insertionIndex >= 0) {
                insertionIndex++;
            }
        }
        removeHolyWhiteScrollCorruptionLore(lore);
        if (insertionIndex < 0 || insertionIndex > lore.size()) {
            insertionIndex = lore.size();
        }
        lore.add(insertionIndex, formatHolyWhiteScrollCorruptedLore());

        meta.setLore(lore);
        updated.setItemMeta(meta);
        return updated;
    }

    private ItemStack applyHolyWaterMaxApplications(ItemStack item, int maxApplications) {
        ItemStack updated = item.clone();
        ItemMeta meta = updated.getItemMeta();
        if (meta == null) {
            return updated;
        }

        int safeMax = Math.max(1, maxApplications);
        int count = Math.max(1, getHolyWhiteScrollCorruptionCount(updated));
        meta.getPersistentDataContainer().set(holyWhiteScrollMaxApplicationsKey, PersistentDataType.INTEGER, safeMax);
        meta.getPersistentDataContainer().set(holyWhiteScrollCorruptionCountKey,
                PersistentDataType.INTEGER, Math.min(count, safeMax));
        meta.getPersistentDataContainer().remove(holyWhiteScrollCorruptedKey);
        updated.setItemMeta(meta);

        if (count >= safeMax) {
            return applyHolyWhiteScrollCorrupted(updated);
        }
        return applyHolyWhiteScrollSemiCorruption(updated, count);
    }

    private void finalizeHolyWhiteScrollCorruption(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        boolean changed = false;
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            ItemStack updated = finalizeHolyWhiteScrollCorruption(item);
            if (updated != item) {
                inventory.setItem(slot, updated);
                changed = true;
            }
        }

        ItemStack[] armor = inventory.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            ItemStack updated = finalizeHolyWhiteScrollCorruption(armor[i]);
            if (updated != armor[i]) {
                armor[i] = updated;
                changed = true;
            }
        }
        inventory.setArmorContents(armor);

        ItemStack offHand = inventory.getItemInOffHand();
        ItemStack updatedOffHand = finalizeHolyWhiteScrollCorruption(offHand);
        if (updatedOffHand != offHand) {
            inventory.setItemInOffHand(updatedOffHand);
            changed = true;
        }

        if (changed) {
            player.updateInventory();
        }
    }

    private ItemStack finalizeHolyWhiteScrollCorruption(ItemStack item) {
        if (!isUsableItem(item)
                || getHolyWhiteScrollCorruptionCount(item) < getHolyWhiteScrollMaxApplications(item)) {
            return item;
        }

        return applyHolyWhiteScrollCorrupted(item);
    }

    private String formatHolyWhiteScrollSemiCorruptLore(int count, int maxApplications) {
        return colorize(getConfig().getString("holy-white-scroll-corruption.semi-corrupt-lore",
                "&c&lSEMI CORRUPT (&7%count%/%max% holy white scrolls applied&c&l)")
                .replace("%count%", String.valueOf(count))
                .replace("%max%", String.valueOf(maxApplications)));
    }

    private String formatHolyWhiteScrollCorruptedLore() {
        return colorize(getConfig().getString("holy-white-scroll-corruption.corrupted-lore",
                "&c&lCORRUPTED (&7cannot be protected&c&l)"));
    }

    private static int findHolyWhiteScrollProtectedLoreIndex(List<String> lore) {
        if (lore == null) {
            return -1;
        }

        for (int i = 0; i < lore.size(); i++) {
            String stripped = cleanDisplayName(lore.get(i)).toUpperCase(Locale.ROOT);
            if (stripped.contains("HOLY") && stripped.contains("PROTECTED")) {
                return i;
            }
        }
        return -1;
    }

    private static int findHolyWhiteScrollCorruptionLoreIndex(List<String> lore) {
        if (lore == null) {
            return -1;
        }

        for (int i = 0; i < lore.size(); i++) {
            if (isHolyWhiteScrollCorruptionLore(lore.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int findHolyWhiteScrollCorruptedLoreIndex(List<String> lore) {
        if (lore == null) {
            return -1;
        }

        for (int i = 0; i < lore.size(); i++) {
            String stripped = cleanDisplayName(lore.get(i)).toUpperCase(Locale.ROOT);
            if (stripped.startsWith("CORRUPTED")) {
                return i;
            }
        }
        return -1;
    }

    private static HolyWhiteScrollCorruptionNumbers findHolyWhiteScrollCorruptionNumbers(ItemMeta meta) {
        if (meta == null || !meta.hasLore() || meta.getLore() == null) {
            return null;
        }

        for (String line : meta.getLore()) {
            HolyWhiteScrollCorruptionNumbers numbers = parseHolyWhiteScrollCorruptionNumbers(line);
            if (numbers != null) {
                return numbers;
            }
        }
        return null;
    }

    private static HolyWhiteScrollCorruptionNumbers parseHolyWhiteScrollCorruptionNumbers(String line) {
        String stripped = cleanDisplayName(line).toUpperCase(Locale.ROOT);
        if (!stripped.startsWith("SEMI CORRUPT")) {
            return null;
        }

        Matcher matcher = HOLY_WHITE_SCROLL_COUNT_PATTERN.matcher(stripped);
        if (!matcher.find()) {
            return null;
        }

        int count = parsePositiveInt(matcher.group(1), 0);
        int max = parsePositiveInt(matcher.group(2), 0);
        if (count <= 0 || max <= 0) {
            return null;
        }
        return new HolyWhiteScrollCorruptionNumbers(count, max);
    }

    private static void removeHolyWhiteScrollCorruptionLore(List<String> lore) {
        if (lore != null) {
            lore.removeIf(AdvancedEnchantmentsAddonPlugin::isHolyWhiteScrollCorruptionLore);
        }
    }

    private static boolean isHolyWhiteScrollCorruptionLore(String line) {
        String stripped = cleanDisplayName(line).toUpperCase(Locale.ROOT);
        return stripped.startsWith("SEMI CORRUPT") || stripped.startsWith("CORRUPTED");
    }

    private void openGodlyTransmogEditor(Player player, TransmogSession session) {
        TransmogMenuHolder holder = new TransmogMenuHolder(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, GODLY_TRANSMOG_MENU_SIZE,
                colorize(getConfig().getString("godly-transmog-scroll.menu-title", "&dGodly Transmog")));
        holder.setInventory(inventory);
        player.openInventory(inventory);
        refreshGodlyTransmogEditor(player, session);
    }

    private void refreshGodlyTransmogEditor(Player player, TransmogSession session) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (!(inventory.getHolder() instanceof TransmogMenuHolder)) {
            return;
        }

        inventory.clear();
        for (int i = 0; i < session.enchants().size() && i < GODLY_TRANSMOG_MAX_ENCHANTS; i++) {
            inventory.setItem(i, createTransmogEnchantPane(session.enchants().get(i), i == session.selectedIndex()));
        }
        inventory.setItem(GODLY_TRANSMOG_PREVIEW_SLOT, applyGodlyTransmogOrder(session.originalItem(), session.enchants()));
    }

    private ItemStack createTransmogEnchantPane(TransmogEnchant enchant, boolean selected) {
        ItemStack item = new ItemStack(getTransmogPaneMaterial(enchant.groupName()));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(enchant.displayLine());
        if (selected) {
            meta.setLore(Arrays.asList("", colorize("&a&l* SELECTED *")));
        }
        item.setItemMeta(meta);
        return item;
    }

    private void applyGodlyTransmogSession(Player player, TransmogSession session) {
        ItemStack updated = applyGodlyTransmogOrder(session.originalItem(), session.enchants());
        session.targetInventory().setItem(session.targetSlot(), updated);
        session.setApplied(true);
        transmogSessions.remove(player.getUniqueId());
        playConfiguredSounds(player, "sounds.godly-transmog-applied");
        sendThrottled(player, "messages.godly-transmog-applied");
        player.closeInventory();
        player.updateInventory();
    }

    private void refundTransmogScroll(Player player, TransmogSession session) {
        if (session == null || !isUsableItem(session.refundScroll())) {
            return;
        }

        giveOrDropItem(player, session.refundScroll().clone());
        player.updateInventory();
    }

    private void giveOrDropItem(Player player, ItemStack item) {
        if (!isUsableItem(item)) {
            return;
        }

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
        for (ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private List<TransmogEnchant> findHeroicBlackScrollCandidates(ItemStack item) {
        List<TransmogEnchant> enchants = findTransmogEnchants(item);
        if (enchants.isEmpty()) {
            return Collections.emptyList();
        }

        List<TransmogEnchant> heroicEnchants = new java.util.ArrayList<>();
        for (TransmogEnchant enchant : enchants) {
            if ("heroic".equals(normalize(enchant.groupName()))) {
                heroicEnchants.add(enchant);
            }
        }
        return heroicEnchants;
    }

    private ItemStack removeHeroicBlackScrollEnchant(ItemStack item, TransmogEnchant removedEnchant) {
        ItemStack updated = item.clone();
        try {
            updated = AEAPI.removeEnchantment(updated, removedEnchant.rawEnchantName());
        } catch (RuntimeException exception) {
            getLogger().warning("Could not remove heroic enchant " + removedEnchant.rawEnchantName()
                    + " from item data: " + exception.getMessage());
        }

        ItemMeta meta = updated.getItemMeta();
        if (meta == null || !meta.hasLore() || meta.getLore() == null) {
            return updated;
        }

        List<String> lore = new java.util.ArrayList<>(meta.getLore());
        String displayKey = normalizeEnchantLoreForMatch(removedEnchant.displayName());
        String lineKey = normalizeEnchantLoreForMatch(removedEnchant.displayLine());
        lore.removeIf(line -> {
            String key = normalizeEnchantLoreForMatch(line);
            return !key.isEmpty() && (key.equals(displayKey) || key.equals(lineKey));
        });
        meta.setLore(lore);
        updated.setItemMeta(meta);
        return updated;
    }

    private List<TransmogEnchant> findTransmogEnchants(ItemStack item) {
        Map<String, Integer> enchantLevels = AEAPI.getEnchantmentsOnItem(item);
        if (enchantLevels == null || enchantLevels.isEmpty()) {
            return Collections.emptyList();
        }

        List<TransmogEnchant> remaining = new java.util.ArrayList<>();
        for (Map.Entry<String, Integer> entry : enchantLevels.entrySet()) {
            String rawEnchantName = entry.getKey();
            String enchantName = normalize(rawEnchantName);
            int level = Math.max(1, entry.getValue());
            AdvancedEnchantment enchantment = getEnchantmentInstance(rawEnchantName, enchantName);
            String displayName = enchantment == null
                    ? ensureRomanLevelSuffix(titleCase(rawEnchantName.replace("_", " ").replace("-", " ")), level)
                    : formatEnchantDisplayName(enchantment, rawEnchantName, level);
            String displayLine = findGeneratedEnchantLoreLine(rawEnchantName, level, displayName);
            String groupName = resolveEnchantGroupName(enchantment, rawEnchantName);
            remaining.add(new TransmogEnchant(rawEnchantName, level, displayName, displayLine, groupName));
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta != null && meta.hasLore() ? meta.getLore() : Collections.emptyList();
        if (lore == null || lore.isEmpty()) {
            return remaining;
        }

        List<TransmogEnchant> ordered = new java.util.ArrayList<>();
        java.util.Set<TransmogEnchant> used = new java.util.HashSet<>();
        for (String line : lore) {
            TransmogEnchant match = findMatchingTransmogEnchant(line, remaining, used);
            if (match == null) {
                continue;
            }
            match.setDisplayLine(line);
            ordered.add(match);
            used.add(match);
        }

        for (TransmogEnchant enchant : remaining) {
            if (!used.contains(enchant)) {
                ordered.add(enchant);
            }
        }
        return ordered;
    }

    private TransmogEnchant findMatchingTransmogEnchant(String loreLine, List<TransmogEnchant> enchants,
                                                        java.util.Set<TransmogEnchant> used) {
        String key = normalizeEnchantLoreForMatch(loreLine);
        if (key.isEmpty()) {
            return null;
        }

        for (TransmogEnchant enchant : enchants) {
            if (used.contains(enchant)) {
                continue;
            }
            if (key.equals(normalizeEnchantLoreForMatch(enchant.displayName()))
                    || key.equals(normalizeEnchantLoreForMatch(enchant.displayLine()))) {
                return enchant;
            }
        }
        return null;
    }

    private ItemStack applyGodlyTransmogOrder(ItemStack item, List<TransmogEnchant> order) {
        ItemStack updated = item.clone();
        ItemMeta meta = updated.getItemMeta();
        if (meta == null) {
            return updated;
        }

        List<String> originalLore = meta.hasLore() && meta.getLore() != null
                ? new java.util.ArrayList<>(meta.getLore())
                : new java.util.ArrayList<>();
        List<String> newLore = new java.util.ArrayList<>();
        int insertionIndex = -1;
        for (String line : originalLore) {
            if (matchesAnyTransmogEnchant(line, order)) {
                if (insertionIndex < 0) {
                    insertionIndex = newLore.size();
                }
                continue;
            }
            newLore.add(line);
        }

        if (insertionIndex < 0) {
            insertionIndex = 0;
        }

        List<String> enchantLines = new java.util.ArrayList<>();
        for (TransmogEnchant enchant : order) {
            enchantLines.add(enchant.displayLine());
        }
        newLore.addAll(insertionIndex, enchantLines);
        meta.setLore(newLore);
        applyGodlyTransmogLoreCount(meta, order.size());
        updated.setItemMeta(meta);
        return updated;
    }

    private void applyGodlyTransmogLoreCount(ItemMeta meta, int count) {
        if (!getConfig().getBoolean("godly-transmog-scroll.add-lore-count-to-name", true)
                || meta == null
                || !meta.hasDisplayName()) {
            return;
        }

        String strippedName = ChatColor.stripColor(meta.getDisplayName());
        if (strippedName != null && strippedName.trim().matches(".*\\[\\d+\\]$")) {
            return;
        }

        String format = getConfig().getString("godly-transmog-scroll.name-count-format", " &d[&b%count%&d]");
        meta.setDisplayName(meta.getDisplayName() + colorize(format.replace("%count%", String.valueOf(count))));
    }

    private boolean matchesAnyTransmogEnchant(String loreLine, List<TransmogEnchant> order) {
        String key = normalizeEnchantLoreForMatch(loreLine);
        if (key.isEmpty()) {
            return false;
        }
        for (TransmogEnchant enchant : order) {
            if (key.equals(normalizeEnchantLoreForMatch(enchant.displayName()))
                    || key.equals(normalizeEnchantLoreForMatch(enchant.displayLine()))) {
                return true;
            }
        }
        return false;
    }

    private String findGeneratedEnchantLoreLine(String rawEnchantName, int level, String displayName) {
        try {
            List<String> generated = AEAPI.getEnchantLore(rawEnchantName, level);
            if (generated != null && !generated.isEmpty()) {
                return generated.get(0);
            }
        } catch (RuntimeException ignored) {
            // Fall back below.
        }
        return ChatColor.WHITE + displayName;
    }

    private String resolveEnchantGroupName(AdvancedEnchantment enchantment, String rawEnchantName) {
        if (enchantment != null) {
            try {
                AdvancedGroup group = enchantment.getGroup();
                if (group != null && group.getName() != null) {
                    return group.getName();
                }
            } catch (RuntimeException ignored) {
                // Fall through to AEAPI.
            }
            try {
                String groupName = enchantment.getGroupName();
                if (groupName != null && !groupName.trim().isEmpty()) {
                    return groupName;
                }
            } catch (RuntimeException ignored) {
                // Fall through to AEAPI.
            }
        }

        try {
            String groupName = AEAPI.getGroup(rawEnchantName);
            return groupName == null ? "" : groupName;
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    private static Material getTransmogPaneMaterial(String groupName) {
        switch (normalize(groupName)) {
            case "simple":
                return Material.WHITE_STAINED_GLASS_PANE;
            case "unique":
                return Material.LIME_STAINED_GLASS_PANE;
            case "elite":
                return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case "ultimate":
                return Material.YELLOW_STAINED_GLASS_PANE;
            case "legendary":
                return Material.ORANGE_STAINED_GLASS_PANE;
            case "soul":
                return Material.RED_STAINED_GLASS_PANE;
            case "heroic":
                return Material.MAGENTA_STAINED_GLASS_PANE;
            case "mastery":
                return Material.BROWN_STAINED_GLASS_PANE;
            default:
                return Material.GRAY_STAINED_GLASS_PANE;
        }
    }

    private static String normalizeEnchantLoreForMatch(String value) {
        String clean = cleanDisplayName(value);
        return clean.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
    }

    private ItemStack createHeroicUpgradedItem(ItemStack target, HeroicTargetType targetType) {
        Material originalMaterial = target.getType();
        Material newMaterial = targetType == HeroicTargetType.ARMOR
                ? toHeroicArmorMaterial(originalMaterial)
                : toHeroicWeaponMaterial(originalMaterial);

        ItemStack upgraded = target.clone();
        int customMaxDurability = getHeroicMaxDurabilityForOriginal(originalMaterial);
        int customDamage = getHeroicStartingDamage(target, customMaxDurability);
        ItemMeta preservedMeta = upgraded.getItemMeta();
        upgraded.setType(newMaterial);

        ItemMeta meta = preservedMeta == null
                ? upgraded.getItemMeta()
                : Bukkit.getItemFactory().asMetaFor(preservedMeta, newMaterial);
        if (meta == null) {
            return upgraded;
        }

        meta.getPersistentDataContainer().set(heroicUpgradedTypeKey, PersistentDataType.STRING, targetType.configName());
        meta.getPersistentDataContainer().set(heroicMaxDurabilityKey, PersistentDataType.INTEGER, customMaxDurability);
        setHeroicDamage(meta, customDamage);
        applyHeroicAttributes(meta, originalMaterial, newMaterial, targetType);
        appendHeroicLore(meta, targetType, originalMaterial);
        if (targetType == HeroicTargetType.ARMOR) {
            meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(255, 0, 0));
            }
        }
        if (meta instanceof Damageable) {
            syncHeroicVisualDamage((Damageable) meta, newMaterial, customDamage, customMaxDurability);
        }

        upgraded.setItemMeta(meta);
        return upgraded;
    }

    private static HeroicTargetType resolveHeroicTargetType(Material material) {
        if (material == null) {
            return null;
        }

        String name = material.name();
        if (name.endsWith("_HELMET")
                || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS")) {
            return isHeroicArmorTier(material) ? HeroicTargetType.ARMOR : null;
        }

        if (name.endsWith("_SWORD") || name.endsWith("_AXE") || isSpearMaterial(material)) {
            return HeroicTargetType.WEAPON;
        }

        return null;
    }

    private static boolean isHeroicArmorTier(Material material) {
        String name = material.name();
        return name.startsWith("DIAMOND_") || name.startsWith("NETHERITE_");
    }

    private static boolean isSpearMaterial(Material material) {
        String name = material.name();
        return name.equals("TRIDENT") || name.equals("SPEAR") || name.endsWith("_SPEAR");
    }

    private boolean isHeroicUpgradedItem(ItemStack item) {
        if (!isUsableItem(item) || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(heroicUpgradedTypeKey, PersistentDataType.STRING);
    }

    private static Material toHeroicArmorMaterial(Material material) {
        String name = material.name();
        if (name.endsWith("_HELMET")) {
            return Material.LEATHER_HELMET;
        }
        if (name.endsWith("_CHESTPLATE")) {
            return Material.LEATHER_CHESTPLATE;
        }
        if (name.endsWith("_LEGGINGS")) {
            return Material.LEATHER_LEGGINGS;
        }
        return Material.LEATHER_BOOTS;
    }

    private static Material toHeroicWeaponMaterial(Material material) {
        if (isSpearMaterial(material)) {
            return material;
        }
        return material.name().endsWith("_AXE") ? Material.GOLDEN_AXE : Material.GOLDEN_SWORD;
    }

    private int getHeroicMaxDurabilityForOriginal(Material material) {
        int netheriteDurability;
        switch (material.name().replace("DIAMOND_", "NETHERITE_")) {
            case "NETHERITE_HELMET":
                netheriteDurability = 407;
                break;
            case "NETHERITE_CHESTPLATE":
                netheriteDurability = 592;
                break;
            case "NETHERITE_LEGGINGS":
                netheriteDurability = 555;
                break;
            case "NETHERITE_BOOTS":
                netheriteDurability = 481;
                break;
            case "NETHERITE_AXE":
            case "NETHERITE_SWORD":
            case "TRIDENT":
            case "SPEAR":
            default:
                netheriteDurability = 2031;
                break;
        }
        return Math.max(netheriteDurability + 1, (int) Math.ceil(netheriteDurability * heroicDurabilityBonusMultiplier));
    }

    private int getHeroicStartingDamage(ItemStack target, int customMaxDurability) {
        if (!(target.getItemMeta() instanceof Damageable) || customMaxDurability <= 0) {
            return 0;
        }

        int originalMaxDurability = Math.max(1, target.getType().getMaxDurability());
        int originalDamage = Math.max(0, ((Damageable) target.getItemMeta()).getDamage());
        return Math.min(customMaxDurability - 1,
                (int) Math.floor((double) originalDamage / originalMaxDurability * customMaxDurability));
    }

    private void appendHeroicLore(ItemMeta meta, HeroicTargetType targetType, Material originalMaterial) {
        List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new java.util.ArrayList<>(meta.getLore())
                : new java.util.ArrayList<>();
        String armorLine = colorize(getConfig().getString("heroic-upgrade.applied-lore.armor",
                "&4This armor is stronger than netherite."));
        String weaponLine = colorize(getConfig().getString("heroic-upgrade.applied-lore.weapon",
                "&4This weapon is stronger than netherite."));
        lore.removeIf(line -> ChatColor.stripColor(line) != null
                && (ChatColor.stripColor(line).equalsIgnoreCase(ChatColor.stripColor(armorLine))
                || ChatColor.stripColor(line).equalsIgnoreCase(ChatColor.stripColor(weaponLine))));
        if (!lore.isEmpty() && !lore.get(lore.size() - 1).isEmpty()) {
            lore.add("");
        }
        lore.add(targetType == HeroicTargetType.ARMOR ? armorLine : weaponLine);
        if (targetType == HeroicTargetType.WEAPON) {
            appendHeroicWeaponTooltip(lore, originalMaterial);
        }
        meta.setLore(lore);
    }

    private static void appendHeroicWeaponTooltip(List<String> lore, Material originalMaterial) {
        lore.add("");
        lore.add(ChatColor.GRAY + "When in Main Hand:");
        lore.add(ChatColor.DARK_GREEN + " " + formatHeroicStatNumber(resolveHeroicAttackDamage(originalMaterial))
                + " Attack Damage");
        lore.add(ChatColor.DARK_GREEN + " " + formatHeroicStatNumber(resolveHeroicAttackSpeed(originalMaterial))
                + " Attack Speed");
    }

    private void applyHeroicAttributes(ItemMeta meta, Material originalMaterial, Material material, HeroicTargetType targetType) {
        if (targetType == HeroicTargetType.ARMOR) {
            EquipmentSlot slot = getArmorSlot(material);
            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
            meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, heroicModifier(
                    "armor", Attribute.GENERIC_ARMOR, slot, getHeroicArmorValue(material)));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, heroicModifier(
                    "armor_toughness", Attribute.GENERIC_ARMOR_TOUGHNESS, slot, 2.0D));
            return;
        }

        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        double displayedDamage = resolveHeroicAttackDamage(originalMaterial);
        double displayedAttackSpeed = resolveHeroicAttackSpeed(originalMaterial);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, heroicModifier(
                "base_attack_damage", Attribute.GENERIC_ATTACK_DAMAGE, EquipmentSlot.HAND, displayedDamage - 1.0D));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, heroicModifier(
                "base_attack_speed", Attribute.GENERIC_ATTACK_SPEED, EquipmentSlot.HAND, displayedAttackSpeed - 4.0D));
    }

    private static double resolveHeroicAttackDamage(Material originalMaterial) {
        if (isSpearMaterial(originalMaterial)) {
            return 5.0D;
        }
        return originalMaterial.name().endsWith("_AXE") ? 10.0D : 8.0D;
    }

    private static double resolveHeroicAttackSpeed(Material originalMaterial) {
        if (isSpearMaterial(originalMaterial)) {
            return 0.87D;
        }
        return originalMaterial.name().endsWith("_AXE") ? 1.0D : 1.6D;
    }

    private static String formatHeroicStatNumber(double value) {
        if (Math.rint(value) == value) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    private static AttributeModifier heroicModifier(String id, Attribute attribute, EquipmentSlot slot, double amount) {
        AttributeModifier keyedModifier = createKeyedHeroicModifier(id, amount, slot);
        if (keyedModifier != null) {
            return keyedModifier;
        }

        UUID uuid = UUID.nameUUIDFromBytes(("advancedenchantmentsaddon:heroic:"
                + id + ":" + attribute.name() + ":" + slot.name()).getBytes(StandardCharsets.UTF_8));
        return new AttributeModifier(uuid, "minecraft:" + id, amount,
                AttributeModifier.Operation.ADD_NUMBER, slot);
    }

    private static AttributeModifier createKeyedHeroicModifier(String id, double amount, EquipmentSlot slot) {
        try {
            Class<?> equipmentSlotGroupClass = Class.forName("org.bukkit.inventory.EquipmentSlotGroup");
            Object slotGroup = equipmentSlotGroupClass.getField(toEquipmentSlotGroupName(slot)).get(null);
            return (AttributeModifier) AttributeModifier.class
                    .getConstructor(NamespacedKey.class, double.class,
                            AttributeModifier.Operation.class, equipmentSlotGroupClass)
                    .newInstance(NamespacedKey.minecraft(id), amount,
                            AttributeModifier.Operation.ADD_NUMBER, slotGroup);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static String toEquipmentSlotGroupName(EquipmentSlot slot) {
        switch (slot) {
            case HAND:
                return "MAINHAND";
            case OFF_HAND:
                return "OFFHAND";
            case HEAD:
                return "HEAD";
            case CHEST:
                return "CHEST";
            case LEGS:
                return "LEGS";
            case FEET:
            default:
                return "FEET";
        }
    }

    private static EquipmentSlot getArmorSlot(Material material) {
        switch (material) {
            case LEATHER_HELMET:
                return EquipmentSlot.HEAD;
            case LEATHER_CHESTPLATE:
                return EquipmentSlot.CHEST;
            case LEATHER_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_BOOTS:
            default:
                return EquipmentSlot.FEET;
        }
    }

    private static double getHeroicArmorValue(Material material) {
        switch (material) {
            case LEATHER_HELMET:
                return 3.0D;
            case LEATHER_CHESTPLATE:
                return 8.0D;
            case LEATHER_LEGGINGS:
                return 6.0D;
            case LEATHER_BOOTS:
            default:
                return 3.0D;
        }
    }

    private int getHeroicMaxDurability(ItemMeta meta) {
        Integer maxDurability = meta.getPersistentDataContainer().get(heroicMaxDurabilityKey, PersistentDataType.INTEGER);
        return maxDurability == null ? 0 : maxDurability;
    }

    private int getHeroicDamage(ItemMeta meta, Material material, int maxDurability) {
        Integer damage = meta.getPersistentDataContainer().get(heroicDamageKey, PersistentDataType.INTEGER);
        if (damage != null) {
            return Math.max(0, damage);
        }

        if (!(meta instanceof Damageable) || maxDurability <= 0) {
            return 0;
        }

        int visibleMaxDurability = Math.max(1, material.getMaxDurability());
        return Math.max(0, (int) Math.floor((double) ((Damageable) meta).getDamage()
                / visibleMaxDurability * maxDurability));
    }

    private void setHeroicDamage(ItemMeta meta, int damage) {
        meta.getPersistentDataContainer().set(heroicDamageKey, PersistentDataType.INTEGER, Math.max(0, damage));
    }

    private static void syncHeroicVisualDamage(Damageable meta, Material material, int customDamage, int customMaxDurability) {
        int visibleMaxDurability = Math.max(1, material.getMaxDurability());
        int visibleMaxDamage = visibleMaxDurability - 1;
        int visibleDamage = customMaxDurability <= 0
                ? 0
                : (int) Math.floor((double) Math.max(0, customDamage) / customMaxDurability * visibleMaxDamage);
        meta.setDamage(clamp(visibleDamage, 0, Math.max(0, visibleMaxDamage)));
    }

    private Map<String, StaticPotionNotification> findEquippedStaticPotionNotifications(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            return Collections.emptyMap();
        }

        Map<String, StaticPotionNotification> notifications = new HashMap<>();
        collectStaticPotionNotifications(notifications, equipment.getHelmet());
        collectStaticPotionNotifications(notifications, equipment.getChestplate());
        collectStaticPotionNotifications(notifications, equipment.getLeggings());
        collectStaticPotionNotifications(notifications, equipment.getBoots());
        return notifications;
    }

    private Map<String, StaticPotionNotification> collectStaticPotionNotifications(ItemStack item) {
        Map<String, StaticPotionNotification> notifications = new HashMap<>();
        collectStaticPotionNotifications(notifications, item);
        return notifications;
    }

    private void collectStaticPotionNotifications(Map<String, StaticPotionNotification> notifications, ItemStack item) {
        if (!isUsableItem(item)) {
            return;
        }

        Map<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(item);
        if (enchants == null || enchants.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String rawEnchantName = entry.getKey();
            String enchantName = normalize(rawEnchantName);
            int level = Math.max(1, entry.getValue());
            AdvancedEnchantment enchantment = getEnchantmentInstance(rawEnchantName, enchantName);
            if (enchantment == null || !isStaticEnchantment(enchantment)) {
                continue;
            }

            String enchantDisplay = formatEnchantDisplayName(enchantment, rawEnchantName, level);
            for (String effect : getEnchantEffects(enchantment, rawEnchantName, enchantName, level)) {
                StaticPotionNotification notification = parseStaticPotionNotification(enchantName, enchantDisplay, effect);
                if (notification != null) {
                    notifications.putIfAbsent(notification.key(), notification);
                }
            }
        }
    }

    private AdvancedEnchantment getEnchantmentInstance(String rawEnchantName, String enchantName) {
        try {
            return AEAPI.getEnchantmentInstance(rawEnchantName);
        } catch (RuntimeException ignored) {
            try {
                return AEAPI.getEnchantmentInstance(enchantName);
            } catch (RuntimeException ignoredAgain) {
                return null;
            }
        }
    }

    private static boolean isStaticEnchantment(AdvancedEnchantment enchantment) {
        try {
            for (String type : enchantment.getTypes()) {
                if ("EFFECT_STATIC".equalsIgnoreCase(type)) {
                    return true;
                }
            }
        } catch (RuntimeException ignored) {
            return false;
        }
        return false;
    }

    private List<String> getEnchantEffects(AdvancedEnchantment enchantment, String rawEnchantName,
                                           String enchantName, int level) {
        try {
            List<String> effects = enchantment.getEffects(level);
            if (effects != null) {
                return effects;
            }
        } catch (RuntimeException ignored) {
            // Fall through to AEAPI's static helper.
        }

        try {
            List<String> effects = AEAPI.getEffects(rawEnchantName, level);
            if (effects != null) {
                return effects;
            }
        } catch (RuntimeException ignored) {
            // Fall through to normalized enchant name.
        }

        try {
            List<String> effects = AEAPI.getEffects(enchantName, level);
            return effects == null ? Collections.emptyList() : effects;
        } catch (RuntimeException ignored) {
            return Collections.emptyList();
        }
    }

    private StaticPotionNotification parseStaticPotionNotification(String enchantName, String enchantDisplay, String effect) {
        if (effect == null) {
            return null;
        }

        String command = stripEffectTarget(effect).split("\\s+", 2)[0];
        String[] pieces = command.split(":");
        if (pieces.length < 3 || !pieces[0].equalsIgnoreCase("POTION")) {
            return null;
        }

        String potionType = canonicalPotionTypeName(pieces[1]);
        int amplifier = parsePositiveInt(pieces[2], 0);
        String key = normalize(enchantName) + ":" + potionType + ":" + amplifier;
        return new StaticPotionNotification(key, potionType, amplifier, enchantDisplay,
                formatPotionEffectDisplay(potionType, amplifier));
    }

    private String formatEnchantDisplayName(AdvancedEnchantment enchantment, String rawEnchantName, int level) {
        String display = "";
        try {
            display = enchantment.getDisplay(level);
        } catch (RuntimeException ignored) {
            // Fall back below.
        }

        if (display == null || display.trim().isEmpty()) {
            try {
                display = enchantment.getDisplayNoColor();
            } catch (RuntimeException ignored) {
                display = rawEnchantName;
            }
        }

        display = cleanDisplayName(display);
        if (display.isEmpty()) {
            display = titleCase(rawEnchantName.replace("_", " ").replace("-", " "));
        }

        return ensureRomanLevelSuffix(display, level);
    }

    private void sendStaticPotionNotificationsForEffects(Player player, Iterable<PotionEffect> effects, boolean applying) {
        if (!effectNotificationsEnabled || effects == null) {
            return;
        }

        Map<String, StaticPotionNotification> notifications = findEquippedStaticPotionNotifications(player);
        if (notifications.isEmpty()) {
            return;
        }

        java.util.Set<String> sent = new java.util.HashSet<>();
        for (PotionEffect effect : effects) {
            if (effect == null) {
                continue;
            }
            for (StaticPotionNotification notification : notifications.values()) {
                if (notification.matches(effect) && sent.add(notification.key())) {
                    sendEffectNotification(player, notification, applying);
                }
            }
        }
    }

    private void sendEffectNotification(Player player, StaticPotionNotification notification, boolean applying) {
        String path = applying ? "messages.effect-applying" : "messages.effect-removing";
        String fallback = applying
                ? "&a&l[+] &a%enchant%: &7applying %effect%"
                : "&c&l[-] &c%enchant%: &7removing %effect%";
        String message = getConfig().getString(path, fallback)
                .replace("%enchant%", notification.enchantDisplay())
                .replace("%effect%", notification.effectDisplay());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void sendThrottled(Player player, String path, String... replacements) {
        sendThrottled(player, getConfig(), path, replacements);
    }

    private void sendSoulThrottled(Player player, String path, String... replacements) {
        sendThrottled(player, getConfig(), path, replacements);
    }

    private void sendThrottled(Player player, FileConfiguration source, String path, String... replacements) {
        if (messageThrottleMillis > 0) {
            String key = player.getUniqueId() + ":" + path;
            long now = System.currentTimeMillis();
            Long lastSent = messageThrottle.get(key);
            if (lastSent != null && now - lastSent < messageThrottleMillis) {
                return;
            }
            messageThrottle.put(key, now);
        }

        String message = source.getString(path, "");
        if (message == null || message.isEmpty()) {
            return;
        }

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void sendThrottledWithFallback(Player player, String path, String fallback, String... replacements) {
        if (messageThrottleMillis > 0) {
            String key = player.getUniqueId() + ":" + path;
            long now = System.currentTimeMillis();
            Long lastSent = messageThrottle.get(key);
            if (lastSent != null && now - lastSent < messageThrottleMillis) {
                return;
            }
            messageThrottle.put(key, now);
        }

        String message = getConfig().getString(path, fallback);
        if (message == null || message.isEmpty()) {
            message = fallback;
        }

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void playConfiguredSounds(Player player, String path) {
        for (String soundSpec : getSoundSpecs(path)) {
            playConfiguredSound(player, soundSpec);
        }
    }

    private void playConfiguredSounds(Player player, String path, List<String> fallbackSounds) {
        List<String> soundSpecs = getSoundSpecs(path);
        if (soundSpecs.isEmpty() && !getConfig().contains(path)) {
            soundSpecs = fallbackSounds;
        }
        for (String soundSpec : soundSpecs) {
            playConfiguredSound(player, soundSpec);
        }
    }

    private void playConfiguredSoundsAt(Location location, String path) {
        for (String soundSpec : getSoundSpecs(path)) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void playConfiguredSoundsAt(Location location, String path, String fallbackPath) {
        List<String> soundSpecs = getSoundSpecs(path);
        if (soundSpecs.isEmpty() && !getConfig().contains(path)) {
            soundSpecs = getSoundSpecs(fallbackPath);
        }
        for (String soundSpec : soundSpecs) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void playDrainTransactionSound(Player player) {
        playConfiguredSounds(player, "sounds.soul-use");
    }

    private List<String> getSoundSpecs(String path) {
        List<String> sounds = getConfig().getStringList(path);
        if (!sounds.isEmpty() || getConfig().contains(path)) {
            return sounds;
        }

        return Collections.emptyList();
    }

    private void playConfiguredSound(Player player, String soundSpec) {
        if (soundSpec == null || soundSpec.trim().isEmpty()) {
            return;
        }

        String[] pieces = soundSpec.split(":");
        String soundName = pieces[0].trim().toUpperCase(Locale.ROOT);
        float volume = pieces.length > 1 ? parseFloat(pieces[1], 1.0F) : 1.0F;
        float pitch = pieces.length > 2 ? parseFloat(pieces[2], 1.0F) : 1.0F;

        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            getLogger().warning("Invalid sound in config: " + soundSpec);
        }
    }

    private void playConfiguredSoundAt(Location location, String soundSpec) {
        if (location == null || location.getWorld() == null || soundSpec == null || soundSpec.trim().isEmpty()) {
            return;
        }

        String[] pieces = soundSpec.split(":");
        String soundName = pieces[0].trim().toUpperCase(Locale.ROOT);
        float volume = pieces.length > 1 ? parseFloat(pieces[1], 1.0F) : 1.0F;
        float pitch = pieces.length > 2 ? parseFloat(pieces[2], 1.0F) : 1.0F;

        try {
            Sound sound = Sound.valueOf(soundName);
            location.getWorld().playSound(location, sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            getLogger().warning("Invalid sound in config: " + soundSpec);
        }
    }

    private static boolean isUsableItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
    }

    private static boolean isIntegerText(String value) {
        return value != null && value.trim().matches("\\d+");
    }

    private static int parsePositiveInt(String value, int fallback) {
        try {
            return Math.max(0, Integer.parseInt(value.trim()));
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static int parseChance(String value, int fallback) {
        return clamp(parsePositiveInt(value.replace("%", ""), fallback), 0, 100);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float parseFloat(String value, float fallback) {
        try {
            return Float.parseFloat(value.trim());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private void damageWithoutKnockback(LivingEntity target, Entity damager, double damage) {
        if (target == null || target.isDead() || !target.isValid() || damage <= 0.0D) {
            return;
        }

        org.bukkit.util.Vector velocity = target.getVelocity().clone();
        if (target instanceof Player) {
            noKnockbackVelocitySuppressions.put(target.getUniqueId(), serverTick + 2L);
        }
        target.setMetadata("ae_ignore", new org.bukkit.metadata.FixedMetadataValue(this, true));
        try {
            if (damager != null && !damager.equals(target)) {
                target.damage(damage, damager);
            } else {
                target.damage(damage);
            }
        } finally {
            target.removeMetadata("ae_ignore", this);
        }
        restoreVelocity(target, velocity);
        Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(target, velocity), 1L);
    }

    private static void restoreVelocity(LivingEntity target, org.bukkit.util.Vector velocity) {
        if (target != null && !target.isDead() && target.isValid()) {
            target.setVelocity(velocity);
        }
    }

    private void spawnRuseZombies(LivingEntity owner, LivingEntity target, int count, String rawName) {
        if (owner == null || owner.isDead() || !owner.isValid() || owner.getWorld() == null) {
            return;
        }
        if (!isValidRuseZombieTarget(target, owner) || target.getUniqueId().equals(owner.getUniqueId())) {
            return;
        }

        int safeCount = clamp(count, 1, 20);
        int lifetimeTicks = clamp(getEnchantsConfig().getInt("ruse-zombies.lifetime-ticks", 140), 20, 1200);
        int speedAmplifier = clamp(getEnchantsConfig().getInt("ruse-zombies.speed-amplifier", 7), 0, 10);
        int monitorIntervalTicks = 5;
        int retargetIntervalTicks = clamp(getEnchantsConfig().getInt("ruse-zombies.retarget-interval-ticks", 0), 0, 100);
        double spawnRadius = Math.max(0.0D, Math.min(8.0D,
                getEnchantsConfig().getDouble("ruse-zombies.spawn-radius-blocks", 3.0D)));
        double followRange = Math.max(8.0D, Math.min(128.0D,
                getEnchantsConfig().getDouble("ruse-zombies.follow-range-blocks", 48.0D)));
        double baseMovementSpeed = Math.max(0.0D, Math.min(1.0D,
                getEnchantsConfig().getDouble("ruse-zombies.base-movement-speed", 0.32D)));
        double chaseNudge = Math.max(0.0D, Math.min(1.0D,
                getEnchantsConfig().getDouble("ruse-zombies.chase-nudge", 0.14D)));
        boolean fireResistance = getEnchantsConfig().getBoolean("ruse-zombies.fire-resistance", true);
        boolean spawnAtTarget = getEnchantsConfig().getBoolean("ruse-zombies.spawn-at-target", false);
        boolean showName = getEnchantsConfig().getBoolean("ruse-zombies.show-name", true);
        String name = resolveRuseZombieName(rawName, owner, target);
        LivingEntity spawnCenter = spawnAtTarget ? target : owner;

        for (int index = 0; index < safeCount; index++) {
            Location spawnLocation = getRuseZombieSpawnLocation(spawnCenter, spawnRadius);
            Zombie zombie = owner.getWorld().spawn(spawnLocation, Zombie.class);
            markEntityAsAddonSummon(zombie, false);
            zombie.setBaby(true);
            zombie.setAI(true);
            zombie.setAware(true);
            zombie.setCanPickupItems(false);
            zombie.setRemoveWhenFarAway(false);
            configureRuseZombieAttributes(zombie, followRange, baseMovementSpeed);
            clearRuseZombieEquipment(zombie);
            if (showName && !name.isEmpty()) {
                zombie.setCustomName(name);
                zombie.setCustomNameVisible(false);
            }
            zombie.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED, lifetimeTicks + 20, speedAmplifier, true, false, false), true);
            if (fireResistance) {
                zombie.addPotionEffect(new PotionEffect(
                        PotionEffectType.FIRE_RESISTANCE, lifetimeTicks + 20, 0, true, false, false), true);
            }
            ruseZombieOwners.put(zombie.getUniqueId(), owner.getUniqueId());
            ruseZombieTargets.put(zombie.getUniqueId(), target.getUniqueId());
            zombie.setTarget(target);
            monitorRuseZombie(zombie, target, lifetimeTicks, monitorIntervalTicks, retargetIntervalTicks, chaseNudge);
        }
    }

    private Location getRuseZombieSpawnLocation(LivingEntity owner, double spawnRadius) {
        Location base = owner.getLocation();
        if (spawnRadius <= 0.0D) {
            return base;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 8; attempt++) {
            double angle = random.nextDouble(0.0D, Math.PI * 2.0D);
            double distance = Math.sqrt(random.nextDouble()) * spawnRadius;
            Location candidate = base.clone().add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
            if (candidate.getBlock().isPassable()
                    && candidate.clone().add(0.0D, 1.0D, 0.0D).getBlock().isPassable()) {
                return candidate;
            }
        }
        return base;
    }

    private static boolean isValidRuseZombieTarget(LivingEntity target, LivingEntity source) {
        return target != null
                && source != null
                && target.isValid()
                && !target.isDead()
                && target.getWorld().equals(source.getWorld());
    }

    private void configureRuseZombieAttributes(Zombie zombie, double followRange, double baseMovementSpeed) {
        AttributeInstance followRangeAttribute = zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(followRange);
        }

        AttributeInstance speedAttribute = zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && baseMovementSpeed > 0.0D) {
            speedAttribute.setBaseValue(baseMovementSpeed);
        }
    }

    private void monitorRuseZombie(Zombie zombie, LivingEntity target, int lifetimeTicks,
                                   int monitorIntervalTicks, int retargetIntervalTicks, double chaseNudge) {
        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                ticks += monitorIntervalTicks;
                if (zombie == null || zombie.isDead() || !zombie.isValid() || ticks >= lifetimeTicks) {
                    if (zombie != null) {
                        removeRuseZombieTracking(zombie.getUniqueId());
                    }
                    if (zombie != null && zombie.isValid()) {
                        zombie.remove();
                    }
                    cancel();
                    return;
                }
                if (isValidRuseZombieTarget(target, zombie)) {
                    zombie.setAware(true);
                    LivingEntity currentTarget = zombie.getTarget();
                    boolean shouldRetarget = currentTarget == null
                            || (retargetIntervalTicks > 0 && ticks % retargetIntervalTicks == 0
                            && !currentTarget.getUniqueId().equals(target.getUniqueId()));
                    if (shouldRetarget) {
                        zombie.setTarget(target);
                    }
                    nudgeRuseZombieTowardTarget(zombie, target, chaseNudge);
                    return;
                }

                removeRuseZombieTracking(zombie.getUniqueId());
                zombie.remove();
                cancel();
            }
        }.runTaskTimer(this, monitorIntervalTicks, monitorIntervalTicks);
    }

    private static void nudgeRuseZombieTowardTarget(Zombie zombie, LivingEntity target, double chaseNudge) {
        if (chaseNudge <= 0.0D
                || zombie == null
                || target == null
                || !zombie.getWorld().equals(target.getWorld())) {
            return;
        }

        org.bukkit.util.Vector direction = target.getLocation().toVector().subtract(zombie.getLocation().toVector());
        direction.setY(0.0D);
        if (direction.lengthSquared() <= 4.0D) {
            return;
        }

        org.bukkit.util.Vector velocity = zombie.getVelocity().clone().add(direction.normalize().multiply(chaseNudge));
        double horizontalSquared = velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ();
        double maxHorizontal = 0.7D;
        if (horizontalSquared > maxHorizontal * maxHorizontal) {
            double scale = maxHorizontal / Math.sqrt(horizontalSquared);
            velocity.setX(velocity.getX() * scale);
            velocity.setZ(velocity.getZ() * scale);
        }
        zombie.setVelocity(velocity);
    }

    private String resolveRuseZombieName(String rawName, LivingEntity owner, LivingEntity target) {
        String configured = rawName == null || rawName.trim().isEmpty()
                ? getEnchantsConfig().getString("ruse-zombies.name", "&d%victimname%'s%space%Robot")
                : rawName;
        String ownerName = owner == null ? "" : owner.getName();
        String targetName = target == null ? "" : target.getName();
        return colorize(configured
                .replace("%space%", " ")
                .replace("{space}", " ")
                .replace("%owner name%", ownerName)
                .replace("%ownername%", ownerName)
                .replace("%victim name%", ownerName)
                .replace("%victimname%", ownerName)
                .replace("%attacker name%", targetName)
                .replace("%attackername%", targetName)
                .replace("%target name%", targetName)
                .replace("%targetname%", targetName));
    }

    private LivingEntity getRuseZombieAssignedTarget(UUID zombieId) {
        UUID targetId = ruseZombieTargets.get(zombieId);
        if (targetId == null) {
            return null;
        }

        Entity target = Bukkit.getEntity(targetId);
        return target instanceof LivingEntity
                && target.isValid()
                && !((LivingEntity) target).isDead()
                ? (LivingEntity) target
                : null;
    }

    private void removeRuseZombieTracking(UUID zombieId) {
        ruseZombieOwners.remove(zombieId);
        ruseZombieTargets.remove(zombieId);
    }

    private void applyRuseZombieHunger(LivingEntity target) {
        if (target == null || target.isDead() || !target.isValid()
                || !getEnchantsConfig().getBoolean("ruse-zombies.hunger-on-hit.enabled", true)) {
            return;
        }

        int durationTicks = clamp(getEnchantsConfig().getInt("ruse-zombies.hunger-on-hit.duration-ticks", 140), 1, 1200);
        int amplifier = clamp(getEnchantsConfig().getInt("ruse-zombies.hunger-on-hit.amplifier", 3), 0, 10);
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.HUNGER, durationTicks, amplifier, true, false, false), true);
    }

    private void spawnEpidemicCarriers(LivingEntity owner, LivingEntity target, int count,
                                       int blindnessAmplifier, int blindnessTicks,
                                       int confusionAmplifier, int confusionTicks,
                                       int slownessAmplifier, int slownessTicks,
                                       String rawName) {
        if (owner == null || owner.isDead() || !owner.isValid() || owner.getWorld() == null) {
            return;
        }
        if (!isValidRuseZombieTarget(target, owner) || target.getUniqueId().equals(owner.getUniqueId())) {
            return;
        }

        int safeCount = clamp(count, 1, 12);
        int lifetimeTicks = clamp(getEnchantsConfig().getInt("epidemic-carriers.lifetime-ticks", 240), 20, 1200);
        double spawnRadius = Math.max(0.0D, Math.min(8.0D,
                getEnchantsConfig().getDouble("epidemic-carriers.spawn-radius-blocks", 1.8D)));
        double followRange = Math.max(8.0D, Math.min(128.0D,
                getEnchantsConfig().getDouble("epidemic-carriers.follow-range-blocks", 48.0D)));
        double movementSpeed = Math.max(0.0D, Math.min(1.0D,
                getEnchantsConfig().getDouble("epidemic-carriers.movement-speed", 0.28D)));
        int explosionRadius = clamp(getEnchantsConfig().getInt("epidemic-carriers.explosion-radius", 3), 1, 12);
        boolean spawnAtTarget = getEnchantsConfig().getBoolean("epidemic-carriers.spawn-at-target", false);
        boolean showName = getEnchantsConfig().getBoolean("epidemic-carriers.show-name", true);
        String name = resolveEpidemicCarrierName(rawName, owner, target);
        LivingEntity spawnCenter = spawnAtTarget ? target : owner;
        EpidemicCarrierSettings settings = new EpidemicCarrierSettings(
                owner.getUniqueId(),
                target.getUniqueId(),
                clamp(blindnessAmplifier, 0, 10),
                clamp(blindnessTicks, 1, 1200),
                clamp(confusionAmplifier, 0, 10),
                clamp(confusionTicks, 1, 1200),
                clamp(slownessAmplifier, 0, 10),
                clamp(slownessTicks, 1, 1200));

        for (int index = 0; index < safeCount; index++) {
            Location spawnLocation = getRuseZombieSpawnLocation(spawnCenter, spawnRadius);
            Creeper creeper = owner.getWorld().spawn(spawnLocation, Creeper.class);
            markEntityAsAddonSummon(creeper, false);
            creeper.setPowered(true);
            creeper.setAI(true);
            creeper.setAware(true);
            creeper.setCanPickupItems(false);
            creeper.setRemoveWhenFarAway(false);
            creeper.setExplosionRadius(explosionRadius);
            configureEpidemicCarrierAttributes(creeper, followRange, movementSpeed);
            if (showName && !name.isEmpty()) {
                creeper.setCustomName(name);
                creeper.setCustomNameVisible(false);
            }
            epidemicCarrierSettings.put(creeper.getUniqueId(), settings);
            creeper.setTarget(target);
            monitorEpidemicCarrier(creeper, target, lifetimeTicks, 5);
        }
    }

    private void configureEpidemicCarrierAttributes(Creeper creeper, double followRange, double movementSpeed) {
        AttributeInstance followRangeAttribute = creeper.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(followRange);
        }

        AttributeInstance speedAttribute = creeper.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && movementSpeed > 0.0D) {
            speedAttribute.setBaseValue(movementSpeed);
        }
    }

    private void monitorEpidemicCarrier(Creeper creeper, LivingEntity target, int lifetimeTicks,
                                        int monitorIntervalTicks) {
        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                ticks += monitorIntervalTicks;
                if (creeper == null || creeper.isDead() || !creeper.isValid() || ticks >= lifetimeTicks) {
                    if (creeper != null) {
                        epidemicCarrierSettings.remove(creeper.getUniqueId());
                    }
                    if (creeper != null && creeper.isValid()) {
                        creeper.remove();
                    }
                    cancel();
                    return;
                }

                if (isValidRuseZombieTarget(target, creeper)) {
                    creeper.setAware(true);
                    LivingEntity currentTarget = creeper.getTarget();
                    if (currentTarget == null || !currentTarget.getUniqueId().equals(target.getUniqueId())) {
                        creeper.setTarget(target);
                    }
                    return;
                }

                epidemicCarrierSettings.remove(creeper.getUniqueId());
                creeper.remove();
                cancel();
            }
        }.runTaskTimer(this, monitorIntervalTicks, monitorIntervalTicks);
    }

    private String resolveEpidemicCarrierName(String rawName, LivingEntity owner, LivingEntity target) {
        String configured = rawName == null || rawName.trim().isEmpty()
                ? getEnchantsConfig().getString("epidemic-carriers.name", "&a&l%victimname%'s%space%Stun%space%Creeper")
                : rawName;
        String ownerName = owner == null ? "" : owner.getName();
        String targetName = target == null ? "" : target.getName();
        return colorize(configured
                .replace("%space%", " ")
                .replace("{space}", " ")
                .replace("%owner name%", ownerName)
                .replace("%ownername%", ownerName)
                .replace("%victim name%", ownerName)
                .replace("%victimname%", ownerName)
                .replace("%attacker name%", targetName)
                .replace("%attackername%", targetName)
                .replace("%target name%", targetName)
                .replace("%targetname%", targetName));
    }

    private static void clearRuseZombieEquipment(Zombie zombie) {
        EntityEquipment equipment = zombie.getEquipment();
        if (equipment == null) {
            return;
        }

        ItemStack air = new ItemStack(Material.AIR);
        equipment.setHelmet(air);
        equipment.setChestplate(air);
        equipment.setLeggings(air);
        equipment.setBoots(air);
        equipment.setItemInMainHand(air);
        equipment.setItemInOffHand(air);
        equipment.setHelmetDropChance(0.0F);
        equipment.setChestplateDropChance(0.0F);
        equipment.setLeggingsDropChance(0.0F);
        equipment.setBootsDropChance(0.0F);
        equipment.setItemInMainHandDropChance(0.0F);
        equipment.setItemInOffHandDropChance(0.0F);
    }

    private void spawnObsidianGuardians(LivingEntity owner, LivingEntity target, int count,
                                        int durationTicks, double damage, double pullStrength,
                                        int slowAmplifier, int slowTicks, String rawName) {
        if (owner == null || owner.isDead() || !owner.isValid() || owner.getWorld() == null) {
            return;
        }
        if (!isValidRuseZombieTarget(target, owner) || target.getUniqueId().equals(owner.getUniqueId())) {
            return;
        }

        int safeCount = clamp(count, 1, 10);
        int lifetimeTicks = clamp(durationTicks <= 0
                ? getEnchantsConfig().getInt("obsidian-guardians.lifetime-ticks", 240)
                : durationTicks, 20, 1200);
        double spawnRadius = Math.max(0.0D, Math.min(8.0D,
                getEnchantsConfig().getDouble("obsidian-guardians.spawn-radius-blocks", 1.6D)));
        double followRange = Math.max(8.0D, Math.min(128.0D,
                getEnchantsConfig().getDouble("obsidian-guardians.follow-range-blocks", 48.0D)));
        double movementSpeed = Math.max(0.0D, Math.min(1.0D,
                getEnchantsConfig().getDouble("obsidian-guardians.movement-speed", 0.5D)));
        double maxHealth = Math.max(1.0D, Math.min(2048.0D,
                getEnchantsConfig().getDouble("obsidian-guardians.health", 40.0D)));
        boolean silent = getEnchantsConfig().getBoolean("obsidian-guardians.silent", false);
        boolean showName = getEnchantsConfig().getBoolean("obsidian-guardians.show-name", true);
        int attackDelayTicks = clamp(getEnchantsConfig().getInt("obsidian-guardians.attack-delay-ticks", 20),
                0, 200);
        String name = resolveObsidianGuardianName(rawName, owner, target);
        ObsidianGuardianSettings settings = new ObsidianGuardianSettings(
                Math.max(0.0D, damage),
                Math.max(0.0D, Math.min(5.0D, pullStrength)),
                clamp(slowAmplifier, 0, 10),
                clamp(slowTicks, 1, 1200)
        );

        for (int index = 0; index < safeCount; index++) {
            Location spawnLocation = getRuseZombieSpawnLocation(owner, spawnRadius);
            IronGolem golem = owner.getWorld().spawn(spawnLocation, IronGolem.class);
            markEntityAsAddonSummon(golem, false);
            golem.setAI(true);
            golem.setAware(true);
            golem.setCanPickupItems(false);
            golem.setRemoveWhenFarAway(false);
            golem.setPlayerCreated(false);
            golem.setSilent(silent);
            configureObsidianGuardianAttributes(golem, followRange, movementSpeed, maxHealth, settings.damage());
            if (showName && !name.isEmpty()) {
                golem.setCustomName(name);
                golem.setCustomNameVisible(false);
            }
            obsidianGuardianOwners.put(golem.getUniqueId(), owner.getUniqueId());
            obsidianGuardianTargets.put(golem.getUniqueId(), target.getUniqueId());
            obsidianGuardianSettings.put(golem.getUniqueId(), settings);
            obsidianGuardianAttackReadyTicks.put(golem.getUniqueId(), serverTick + attackDelayTicks);
            golem.setTarget(target);
            monitorObsidianGuardian(golem, target, lifetimeTicks, 5);
        }
    }

    private void configureObsidianGuardianAttributes(IronGolem golem, double followRange,
                                                     double movementSpeed, double maxHealth,
                                                     double attackDamage) {
        AttributeInstance maxHealthAttribute = golem.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(maxHealth);
            golem.setHealth(Math.min(maxHealth, maxHealthAttribute.getValue()));
        }
        AttributeInstance followRangeAttribute = golem.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(followRange);
        }
        AttributeInstance speedAttribute = golem.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && movementSpeed > 0.0D) {
            speedAttribute.setBaseValue(movementSpeed);
        }
        AttributeInstance damageAttribute = golem.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (damageAttribute != null && attackDamage > 0.0D) {
            damageAttribute.setBaseValue(attackDamage);
        }
    }

    private void monitorObsidianGuardian(IronGolem golem, LivingEntity target, int lifetimeTicks,
                                         int monitorIntervalTicks) {
        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                ticks += monitorIntervalTicks;
                if (golem == null || golem.isDead() || !golem.isValid() || ticks >= lifetimeTicks) {
                    if (golem != null) {
                        removeObsidianGuardianTracking(golem.getUniqueId());
                    }
                    if (golem != null && golem.isValid()) {
                        golem.remove();
                    }
                    cancel();
                    return;
                }
                if (isValidRuseZombieTarget(target, golem)) {
                    golem.setAware(true);
                    LivingEntity currentTarget = golem.getTarget();
                    if (currentTarget == null || !currentTarget.getUniqueId().equals(target.getUniqueId())) {
                        golem.setTarget(target);
                    }
                    return;
                }

                removeObsidianGuardianTracking(golem.getUniqueId());
                golem.remove();
                cancel();
            }
        }.runTaskTimer(this, monitorIntervalTicks, monitorIntervalTicks);
    }

    private LivingEntity getObsidianGuardianAssignedTarget(UUID guardianId) {
        UUID targetId = obsidianGuardianTargets.get(guardianId);
        if (targetId == null) {
            return null;
        }

        Entity target = Bukkit.getEntity(targetId);
        return target instanceof LivingEntity
                && target.isValid()
                && !((LivingEntity) target).isDead()
                ? (LivingEntity) target
                : null;
    }

    private void removeObsidianGuardianTracking(UUID guardianId) {
        obsidianGuardianOwners.remove(guardianId);
        obsidianGuardianTargets.remove(guardianId);
        obsidianGuardianSettings.remove(guardianId);
        obsidianGuardianAttackReadyTicks.remove(guardianId);
    }

    private String resolveObsidianGuardianName(String rawName, LivingEntity owner, LivingEntity target) {
        String configured = rawName == null || rawName.trim().isEmpty()
                ? getEnchantsConfig().getString("obsidian-guardians.name", "&d&lObsidian Guardian")
                : rawName;
        String ownerName = owner == null ? "" : owner.getName();
        String targetName = target == null ? "" : target.getName();
        return colorize(configured
                .replace("%space%", " ")
                .replace("{space}", " ")
                .replace("%owner name%", ownerName)
                .replace("%ownername%", ownerName)
                .replace("%victim name%", ownerName)
                .replace("%victimname%", ownerName)
                .replace("%attacker name%", targetName)
                .replace("%attackername%", targetName)
                .replace("%target name%", targetName)
                .replace("%targetname%", targetName));
    }

    private void pullToward(LivingEntity target, LivingEntity anchor, double strength) {
        if (target == null || anchor == null || !target.getWorld().equals(anchor.getWorld()) || strength <= 0.0D) {
            return;
        }

        org.bukkit.util.Vector direction = anchor.getLocation().toVector().subtract(target.getLocation().toVector());
        direction.setY(0.0D);
        if (direction.lengthSquared() <= 0.001D) {
            return;
        }

        org.bukkit.util.Vector velocity = direction.normalize().multiply(strength);
        velocity.setY(Math.min(0.35D, Math.max(0.12D, strength * 0.12D)));
        target.setVelocity(velocity);
    }

    private boolean isSword(ItemStack item) {
        return isUsableItem(item) && item.getType().name().endsWith("_SWORD");
    }

    private boolean isNonHeroicSword(ItemStack item) {
        return isSword(item) && !isHeroicUpgradedItem(item);
    }

    private double getStackedValorReductionPercent(Player player) {
        return getStackedReductionPercent(player, "valor", "VALOR");
    }

    private double getStackedMartyrValorReductionPercent(Player player) {
        return getStackedReductionPercent(player, "martyrvalor", "MARTYR_VALOR");
    }

    private double getStackedReductionPercent(Player player, String enchantName, String effectName) {
        if (player == null || player.getInventory() == null) {
            return 0.0D;
        }

        double total = 0.0D;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            int level = getEnchantLevelOnItem(enchantName, enchantName, armor);
            if (level > 0) {
                total += getReductionPercent(enchantName, effectName, level);
            }
        }
        return total;
    }

    private double getStackedReductionMultiplier(Player player, String enchantName, String effectName) {
        if (player == null || player.getInventory() == null) {
            return 1.0D;
        }

        double multiplier = 1.0D;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            int level = getEnchantLevelOnItem(enchantName, enchantName, armor);
            if (level <= 0) {
                continue;
            }

            double reduction = Math.max(0.0D, Math.min(100.0D,
                    getReductionPercent(enchantName, effectName, level)));
            multiplier *= Math.max(0.0D, 1.0D - reduction / 100.0D);
        }
        return multiplier;
    }

    private double getValorStyleMaxReductionPercent() {
        return 60.0D;
    }

    private double getValorReductionPercent(int level) {
        return getReductionPercent("valor", "VALOR", level);
    }

    private double getReductionPercent(String enchantName, String effectName, int level) {
        int safeLevel = clamp(level, 1, 10);
        String[] args = getEnchantEffectArguments(enchantName, safeLevel, effectName);
        if (args.length >= 1) {
            double configured = parseDouble(args[0], Double.NaN);
            if (Double.isFinite(configured) && configured > 0.0D) {
                return configured;
            }
        }

        switch (safeLevel) {
            case 1:
                return 3.0D;
            case 2:
                return 6.0D;
            case 3:
                return 9.0D;
            case 4:
                return 12.0D;
            default:
                return 15.0D;
        }
    }

    private String[] getEnchantEffectArguments(String enchantName, int level, String effectName) {
        if (enchantName == null || effectName == null) {
            return new String[0];
        }

        try {
            AdvancedEnchantment enchantment = AEAPI.getEnchantmentInstance(normalize(enchantName));
            if (enchantment == null) {
                return new String[0];
            }

            List<String> effects = enchantment.getEffects(level);
            if (effects == null) {
                return new String[0];
            }

            String normalizedEffectName = effectName.toUpperCase(Locale.ROOT);
            for (String effect : effects) {
                String rawEffect = effect == null ? "" : effect.trim().split("\\s+", 2)[0];
                String[] pieces = rawEffect.split(":");
                if (pieces.length > 0 && pieces[0].equalsIgnoreCase(normalizedEffectName)) {
                    return Arrays.copyOfRange(pieces, 1, pieces.length);
                }
            }
        } catch (RuntimeException ignored) {
            return new String[0];
        }

        return new String[0];
    }

    private void startTrueInvisibility(Player player, int amplifier, int durationTicks) {
        if (player == null || !player.isOnline() || durationTicks <= 0) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        long requestedExpiresAt = now + (long) clamp(durationTicks, 1, 12000) * 50L;
        long expiresAt = Math.max(trueInvisibilityExpiresAt.getOrDefault(playerId, 0L), requestedExpiresAt);
        int remainingTicks = clamp((int) Math.ceil((expiresAt - now) / 50.0D), 1, 12000);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY, remainingTicks, Math.max(0, amplifier), true, false, false), true);
        trueInvisibilityExpiresAt.put(playerId, expiresAt);

        BukkitTask previousTask = trueInvisibilityTasks.remove(playerId);
        if (previousTask != null) {
            previousTask.cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Player current = Bukkit.getPlayer(playerId);
                Long currentExpiresAt = trueInvisibilityExpiresAt.get(playerId);
                if (current == null
                        || !current.isOnline()
                        || currentExpiresAt == null
                        || System.currentTimeMillis() > currentExpiresAt
                        || !current.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    stopTrueInvisibility(playerId, true);
                    cancel();
                    return;
                }

                hideTrueInvisibleArmor(current);
            }
        }.runTaskTimer(this, 0L, 5L);
        trueInvisibilityTasks.put(playerId, task);
    }

    private void stopTrueInvisibility(UUID playerId, boolean restoreArmor) {
        BukkitTask task = trueInvisibilityTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
        trueInvisibilityExpiresAt.remove(playerId);

        Player player = Bukkit.getPlayer(playerId);
        if (restoreArmor && player != null && player.isOnline()) {
            restoreTrueInvisibleArmor(player);
        }
    }

    private void hideTrueInvisibleArmor(Player player) {
        sendArmorEquipmentChangeToViewers(player, createArmorEquipmentChange(player, true));
    }

    private void restoreTrueInvisibleArmor(Player player) {
        sendArmorEquipmentChangeToViewers(player, createArmorEquipmentChange(player, false));
    }

    private void sendArmorEquipmentChangeToViewers(Player player, Map<EquipmentSlot, ItemStack> changes) {
        if (player == null || changes.isEmpty()) {
            return;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.getUniqueId().equals(player.getUniqueId())
                    || !viewer.getWorld().equals(player.getWorld())
                    || !viewer.canSee(player)) {
                continue;
            }
            try {
                viewer.sendEquipmentChange(player, changes);
            } catch (RuntimeException ignored) {
                // A fake equipment packet is cosmetic; failing it should not affect the enchant.
            }
        }
    }

    private static Map<EquipmentSlot, ItemStack> createArmorEquipmentChange(Player player, boolean hidden) {
        Map<EquipmentSlot, ItemStack> changes = new HashMap<>();
        if (hidden) {
            ItemStack air = new ItemStack(Material.AIR);
            changes.put(EquipmentSlot.HEAD, air);
            changes.put(EquipmentSlot.CHEST, air);
            changes.put(EquipmentSlot.LEGS, air);
            changes.put(EquipmentSlot.FEET, air);
            return changes;
        }

        EntityEquipment equipment = player.getEquipment();
        changes.put(EquipmentSlot.HEAD, cloneOrAir(equipment == null ? null : equipment.getHelmet()));
        changes.put(EquipmentSlot.CHEST, cloneOrAir(equipment == null ? null : equipment.getChestplate()));
        changes.put(EquipmentSlot.LEGS, cloneOrAir(equipment == null ? null : equipment.getLeggings()));
        changes.put(EquipmentSlot.FEET, cloneOrAir(equipment == null ? null : equipment.getBoots()));
        return changes;
    }

    private static ItemStack cloneOrAir(ItemStack item) {
        return isUsableItem(item) ? item.clone() : new ItemStack(Material.AIR);
    }

    private void spawnVisualSpirits(LivingEntity target, int durationSeconds, int amount,
                                    int regenerationAmplifier, String rawName) {
        if (target == null || target.isDead() || !target.isValid() || target.getWorld() == null) {
            return;
        }

        int safeAmount = clamp(amount, 1, 10);
        int durationTicks = clamp(durationSeconds, 1, 60) * 20;
        int regenAmplifier = clamp(regenerationAmplifier, 0, 10);
        double radius = Math.max(0.0D, Math.min(5.0D,
                getEnchantsConfig().getDouble("visual-spirits.spawn-radius-blocks", 1.4D)));
        double regenerationRadius = Math.max(0.5D, Math.min(16.0D,
                getEnchantsConfig().getDouble("visual-spirits.regeneration-radius-blocks", 5.0D)));
        int regenerationRefreshTicks = clamp(getEnchantsConfig().getInt("visual-spirits.regeneration-refresh-ticks", 20), 5, 100);
        int regenerationDurationTicks = clamp(getEnchantsConfig().getInt("visual-spirits.regeneration-duration-ticks", 60), 5, 200);
        double maxHealth = Math.max(1.0D, Math.min(2048.0D,
                getEnchantsConfig().getDouble("visual-spirits.health", 10.0D)));
        boolean silent = getEnchantsConfig().getBoolean("visual-spirits.silent", true);
        boolean glowing = getEnchantsConfig().getBoolean("visual-spirits.glowing", false);
        boolean showName = getEnchantsConfig().getBoolean("visual-spirits.show-name", true);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Blaze> spirits = new java.util.ArrayList<>();

        for (int index = 0; index < safeAmount; index++) {
            double angle = random.nextDouble(0.0D, Math.PI * 2.0D);
            double distance = Math.sqrt(random.nextDouble()) * radius;
            Location spawn = target.getLocation().clone().add(
                    Math.cos(angle) * distance,
                    random.nextDouble(0.0D, 0.75D),
                    Math.sin(angle) * distance);
            Blaze spirit = (Blaze) target.getWorld().spawnEntity(spawn, EntityType.BLAZE);
            markEntityAsAddonSummon(spirit, false);
            spirit.setAI(false);
            spirit.setAware(false);
            spirit.setCollidable(false);
            spirit.setCanPickupItems(false);
            spirit.setRemoveWhenFarAway(false);
            spirit.setInvulnerable(false);
            spirit.setSilent(silent);
            spirit.setGlowing(glowing);
            AttributeInstance maxHealthAttribute = spirit.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(maxHealth);
                spirit.setHealth(maxHealth);
            }
            String name = showName ? resolveVisualSpiritName(rawName, target) : "";
            if (showName && !name.isEmpty()) {
                spirit.setCustomName(name);
                spirit.setCustomNameVisible(false);
            } else {
                spirit.setCustomName(null);
                spirit.setCustomNameVisible(false);
            }
            visualSpiritIds.add(spirit.getUniqueId());
            spirits.add(spirit);
            Bukkit.getScheduler().runTaskLater(this, () -> {
                visualSpiritIds.remove(spirit.getUniqueId());
                if (spirit.isValid()) {
                    spirit.remove();
                }
            }, durationTicks);
        }

        if (!spirits.isEmpty()) {
            monitorVisualSpiritRegeneration(spirits, durationTicks, regenerationRefreshTicks,
                    regenerationDurationTicks, regenerationRadius, regenAmplifier);
        }
    }

    private void monitorVisualSpiritRegeneration(List<Blaze> spirits, int durationTicks, int refreshTicks,
                                                 int regenerationDurationTicks, double radius,
                                                 int regenerationAmplifier) {
        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                ticks += refreshTicks;
                spirits.removeIf(spirit -> spirit == null || spirit.isDead() || !spirit.isValid());
                if (spirits.isEmpty() || ticks >= durationTicks) {
                    cancel();
                    return;
                }

                applyVisualSpiritRegeneration(spirits, radius, regenerationDurationTicks, regenerationAmplifier);
            }
        }.runTaskTimer(this, 1L, refreshTicks);
    }

    private void applyVisualSpiritRegeneration(List<Blaze> spirits, double radius,
                                               int durationTicks, int amplifier) {
        double radiusSquared = radius * radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean inRange = false;
            for (Blaze spirit : spirits) {
                if (!player.getWorld().equals(spirit.getWorld())) {
                    continue;
                }
                if (player.getLocation().distanceSquared(spirit.getLocation()) <= radiusSquared) {
                    inRange = true;
                    break;
                }
            }
            if (inRange) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION, durationTicks, amplifier, true, true, true), true);
            }
        }
    }

    private String resolveVisualSpiritName(String rawName, LivingEntity target) {
        if (rawName == null || rawName.trim().isEmpty()) {
            return "";
        }

        String targetName = target == null ? "" : target.getName();
        return colorize(rawName
                .replace("%space%", " ")
                .replace("{space}", " ")
                .replace("%victim name%", targetName)
                .replace("%victimname%", targetName)
                .replace("%player name%", targetName)
                .replace("%playername%", targetName));
    }

    private void startDivineImmolation(LivingEntity caster, LivingEntity centerTarget, String[] args) {
        if (centerTarget == null || centerTarget.isDead() || !centerTarget.isValid()) {
            return;
        }

        int ticks = clamp(getEnchantsConfig().getInt("divine-immolation.ticks", 4), 1, 20);
        int intervalTicks = clamp(getEnchantsConfig().getInt("divine-immolation.interval-ticks", 20), 1, 100);
        double radius = Math.max(0.5D, Math.min(16.0D,
                getEnchantsConfig().getDouble("divine-immolation.radius-blocks", 5.0D)));
        double damage = Math.max(0.0D, Math.min(100.0D,
                getEnchantsConfig().getDouble("divine-immolation.damage-per-tick", 5.0D)));
        if (args != null && args.length >= 1) {
            damage = Math.max(0.0D, Math.min(100.0D, parseDouble(args[0], damage)));
        }
        int witherAmplifier = clamp(getEnchantsConfig().getInt("divine-immolation.wither-amplifier", 1), 0, 10);
        int witherDurationTicks = clamp(getEnchantsConfig().getInt("divine-immolation.wither-duration-ticks", 25), 1, 1200);
        int burnSeconds = 0;
        boolean affectSummons = getEnchantsConfig().getBoolean("divine-immolation.affect-summons", false);
        UUID targetId = centerTarget.getUniqueId();
        UUID casterId = caster == null ? null : caster.getUniqueId();
        long expiresAtTick = serverTick + (long) ticks * intervalTicks;

        DivineImmolationSession existing = divineImmolationSessions.get(targetId);
        if (existing != null) {
            existing.refresh(casterId, expiresAtTick, radius, damage, witherAmplifier,
                    witherDurationTicks, burnSeconds, affectSummons);
            return;
        }

        DivineImmolationSession session = new DivineImmolationSession(targetId, casterId, expiresAtTick,
                radius, damage, witherAmplifier, witherDurationTicks, burnSeconds, affectSummons);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                DivineImmolationSession active = divineImmolationSessions.get(targetId);
                if (active == null || serverTick >= active.expiresAtTick()) {
                    divineImmolationSessions.remove(targetId);
                    cancel();
                    return;
                }

                Entity targetEntity = Bukkit.getEntity(targetId);
                if (!(targetEntity instanceof LivingEntity)
                        || targetEntity.isDead()
                        || !targetEntity.isValid()) {
                    divineImmolationSessions.remove(targetId);
                    cancel();
                    return;
                }

                LivingEntity activeCaster = null;
                if (active.casterId() != null) {
                    Entity casterEntity = Bukkit.getEntity(active.casterId());
                    if (casterEntity instanceof LivingEntity && casterEntity.isValid()
                            && !((LivingEntity) casterEntity).isDead()) {
                        activeCaster = (LivingEntity) casterEntity;
                    }
                }

                applyDivineImmolationTick(activeCaster, (LivingEntity) targetEntity,
                        active.radius(), active.damage(), active.witherAmplifier(),
                        active.witherDurationTicks(), active.burnSeconds(), active.affectSummons());
            }
        }.runTaskTimer(this, 0L, intervalTicks);
        session.setTask(task);
        divineImmolationSessions.put(targetId, session);
    }

    private void applyDivineImmolationTick(LivingEntity caster, LivingEntity centerTarget, double radius,
                                           double damage, int witherAmplifier, int witherDurationTicks,
                                           int burnSeconds, boolean affectSummons) {
        if (centerTarget == null || centerTarget.isDead() || !centerTarget.isValid() || centerTarget.getWorld() == null) {
            return;
        }

        Location center = centerTarget.getLocation();
        double radiusSquared = radius * radius;
        for (Entity nearby : centerTarget.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(nearby instanceof LivingEntity)) {
                continue;
            }

            LivingEntity target = (LivingEntity) nearby;
            if (target.isDead() || !target.isValid()) {
                continue;
            }
            if (caster != null && target.getUniqueId().equals(caster.getUniqueId())) {
                continue;
            }
            if (!affectSummons && isSummonedEntity(target)) {
                continue;
            }
            if (target.getLocation().distanceSquared(center) > radiusSquared) {
                continue;
            }

            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.WITHER, witherDurationTicks, witherAmplifier, true, true, true), true);
            playDivineImmolationTickSounds(target);
            if (damage > 0.0D) {
                damageDivineImmolationTarget(target, caster, damage);
            }
        }
    }

    private void damageDivineImmolationTarget(LivingEntity target, LivingEntity caster, double damage) {
        if (target == null || target.isDead() || !target.isValid() || isSummonedEntity(target)) {
            return;
        }

        org.bukkit.util.Vector velocity = target.getVelocity().clone();
        target.setMetadata("advancedenchantmentsaddon_divine_immolation",
                new org.bukkit.metadata.FixedMetadataValue(this, true));
        try {
            if (caster != null && !caster.equals(target)) {
                target.damage(damage, caster);
            } else {
                target.damage(damage);
            }
        } finally {
            target.removeMetadata("advancedenchantmentsaddon_divine_immolation", this);
        }
        restoreVelocity(target, velocity);
        Bukkit.getScheduler().runTaskLater(this, () -> restoreVelocity(target, velocity), 1L);
    }

    private void playDivineImmolationTickSounds(LivingEntity target) {
        if (!(target instanceof Player)) {
            return;
        }

        List<String> sounds = getEnchantsConfig().getStringList("divine-immolation.tick-sounds");
        if (sounds.isEmpty() && !getEnchantsConfig().contains("divine-immolation.tick-sounds")) {
            sounds = Collections.singletonList("ENTITY_ZOMBIFIED_PIGLIN_AMBIENT:1.0:0.8");
        }
        for (String soundSpec : sounds) {
            playConfiguredSound((Player) target, soundSpec);
        }
    }

    private void playDivineImmolationHitSounds(LivingEntity target) {
        if (!(target instanceof Player)) {
            return;
        }

        List<String> sounds = getEnchantsConfig().getStringList("divine-immolation.hit-sounds");
        if (sounds.isEmpty() && !getEnchantsConfig().contains("divine-immolation.hit-sounds")) {
            sounds = Collections.singletonList("ENTITY_WITHER_HURT:1.0:0.7");
        }
        for (String soundSpec : sounds) {
            playConfiguredSound((Player) target, soundSpec);
        }
    }

    private void sendDivineImmolationMessages(LivingEntity caster, LivingEntity centerTarget) {
        String casterName = caster == null ? "" : caster.getName();
        String targetName = centerTarget == null ? "" : centerTarget.getName();
        if (caster instanceof Player) {
            String attackerMessage = getEnchantsConfig().getString("divine-immolation.attacker-message",
                    "&c&l** DIVINE IMMOLATION ** &8inflicted on %victim name%");
            ((Player) caster).sendMessage(colorize(attackerMessage
                    .replace("%attacker name%", casterName)
                    .replace("%attackername%", casterName)
                    .replace("%victim name%", targetName)
                    .replace("%victimname%", targetName)));
        }
        if (centerTarget instanceof Player) {
            String victimMessage = getEnchantsConfig().getString("divine-immolation.victim-message",
                    "&c&l** DIVINE IMMOLATION ** &8from %attacker name%");
            ((Player) centerTarget).sendMessage(colorize(victimMessage
                    .replace("%attacker name%", casterName)
                    .replace("%attackername%", casterName)
                    .replace("%victim name%", targetName)
                    .replace("%victimname%", targetName)));
        }
    }

    private boolean shouldSendDivineImmolationActivationNotice(LivingEntity caster, LivingEntity centerTarget) {
        int cooldownTicks = Math.max(0,
                getEnchantsConfig().getInt("divine-immolation.activation-notification-cooldown-ticks", 40));
        if (cooldownTicks <= 0) {
            return true;
        }

        String casterPart = caster == null ? "none" : caster.getUniqueId().toString();
        String targetPart = centerTarget == null ? "none" : centerTarget.getUniqueId().toString();
        String key = casterPart + ":" + targetPart;
        Long lastTick = divineImmolationNotificationTicks.get(key);
        if (lastTick != null && serverTick - lastTick < cooldownTicks) {
            return false;
        }

        divineImmolationNotificationTicks.put(key, serverTick);
        if (divineImmolationNotificationTicks.size() > 1000) {
            divineImmolationNotificationTicks.entrySet().removeIf(entry -> serverTick - entry.getValue() > 1200L);
        }
        return true;
    }

    private void applyChainLifesteal(LivingEntity attacker, LivingEntity centerTarget,
                                     double minDamage, double maxDamage, double radius) {
        if (attacker == null || attacker.isDead() || !attacker.isValid()
                || centerTarget == null || centerTarget.isDead() || !centerTarget.isValid()
                || centerTarget.getWorld() == null) {
            return;
        }

        double safeRadius = Math.max(0.5D, Math.min(32.0D, radius));
        if (!hasAdditionalRealPlayerNear(centerTarget, attacker, safeRadius, centerTarget)) {
            return;
        }

        double radiusSquared = safeRadius * safeRadius;
        java.util.Set<UUID> hitTargets = new java.util.HashSet<>();
        double totalSiphoned = 0.0D;

        totalSiphoned += applyChainLifestealToTarget(attacker, centerTarget, minDamage, maxDamage, hitTargets);
        for (Entity nearby : centerTarget.getWorld().getNearbyEntities(
                centerTarget.getLocation(), safeRadius, safeRadius, safeRadius)) {
            if (!(nearby instanceof LivingEntity)) {
                continue;
            }

            LivingEntity target = (LivingEntity) nearby;
            if (target.getLocation().distanceSquared(centerTarget.getLocation()) > radiusSquared) {
                continue;
            }

            totalSiphoned += applyChainLifestealToTarget(attacker, target, minDamage, maxDamage, hitTargets);
        }

        if (totalSiphoned > 0.0D) {
            healLivingEntity(attacker, totalSiphoned);
            sendChainLifestealAttackerMessage(attacker, totalSiphoned);
        }
    }

    private double applyChainLifestealToTarget(LivingEntity attacker, LivingEntity target,
                                               double minDamage, double maxDamage,
                                               java.util.Set<UUID> hitTargets) {
        if (!(target instanceof Player)
                || target.isDead()
                || !target.isValid()
                || target.getUniqueId().equals(attacker.getUniqueId())
                || isSummonedEntity(target)
                || !hitTargets.add(target.getUniqueId())) {
            return 0.0D;
        }

        double damage = rollChainLifestealDamage(minDamage, maxDamage);
        if (!Double.isFinite(damage) || damage <= 0.0D) {
            return 0.0D;
        }

        double beforeHealth = Math.max(0.0D, target.getHealth());
        damageWithoutKnockback(target, attacker, damage);

        double afterHealth = target.isDead() || !target.isValid() ? 0.0D : Math.max(0.0D, target.getHealth());
        double healthLost = Math.max(0.0D, beforeHealth - afterHealth);
        if (healthLost < 1.0D && beforeHealth > 0.0D && !target.isDead() && target.isValid()) {
            double forcedLoss = Math.min(1.0D, beforeHealth);
            target.setHealth(Math.max(0.0D, beforeHealth - forcedLoss));
            healthLost = forcedLoss;
        }
        if (healthLost > 0.0D) {
            sendChainLifestealVictimMessage((Player) target, attacker, healthLost);
        }
        return healthLost;
    }

    private void applyAccurateLifesteal(LivingEntity attacker, LivingEntity target,
                                        double minDamage, double maxDamage, String messagePrefix) {
        if (attacker == null || attacker.isDead() || !attacker.isValid()
                || target == null || target.isDead() || !target.isValid()) {
            return;
        }

        double siphoned = applySingleLifestealToTarget(attacker, target, minDamage, maxDamage);
        if (siphoned <= 0.0D) {
            return;
        }

        healLivingEntity(attacker, siphoned);
        sendConfiguredLifestealMessages(resolveLifestealMessagePrefix(messagePrefix), attacker, target, siphoned);
    }

    private double applySingleLifestealToTarget(LivingEntity attacker, LivingEntity target,
                                                double minDamage, double maxDamage) {
        if (!(target instanceof Player)
                || target.isDead()
                || !target.isValid()
                || target.getUniqueId().equals(attacker.getUniqueId())
                || isSummonedEntity(target)) {
            return 0.0D;
        }

        double damage = rollRangeAmount(minDamage, maxDamage);
        if (!Double.isFinite(damage) || damage <= 0.0D) {
            return 0.0D;
        }

        double beforeHealth = Math.max(0.0D, target.getHealth());
        damageWithoutKnockback(target, attacker, damage);

        double afterHealth = target.isDead() || !target.isValid() ? 0.0D : Math.max(0.0D, target.getHealth());
        double healthLost = Math.max(0.0D, beforeHealth - afterHealth);
        if (healthLost < 1.0D && beforeHealth > 0.0D && !target.isDead() && target.isValid()) {
            double forcedLoss = Math.min(1.0D, beforeHealth);
            target.setHealth(Math.max(0.0D, beforeHealth - forcedLoss));
            healthLost = forcedLoss;
        }
        return healthLost;
    }

    private String resolveLifestealMessagePrefix(String rawPrefix) {
        String normalized = normalize(rawPrefix);
        if (normalized.equals("demoniclifesteal") || normalized.equals("demonic")) {
            return "demonic-lifesteal";
        }
        return "lifesteal";
    }

    private void sendConfiguredLifestealMessages(String prefix, LivingEntity attacker,
                                                 LivingEntity target, double amount) {
        String enchantDisplay = prefix.equals("demonic-lifesteal") ? "DEMONIC LIFESTEAL" : "LIFESTEAL";
        if (attacker instanceof Player) {
            String message = getEnchantsConfig().getString(prefix + ".attacker-message",
                    "&c&l** " + enchantDisplay + " ** &7(&c+%amount% HP&7)");
            sendOptionalLifestealMessage((Player) attacker, message, attacker, target, amount);
        }
        if (target instanceof Player) {
            String message = getEnchantsConfig().getString(prefix + ".victim-message",
                    "&c&l** " + enchantDisplay + " ** &7(&c-%amount% HP&7)");
            sendOptionalLifestealMessage((Player) target, message, attacker, target, amount);
        }
    }

    private void sendOptionalLifestealMessage(Player recipient, String message, LivingEntity attacker,
                                              LivingEntity target, double amount) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String attackerName = attacker == null ? "" : attacker.getName();
        String victimName = target == null ? "" : target.getName();
        recipient.sendMessage(colorize(message
                .replace("%amount%", formatHpAmount(amount))
                .replace("%hp%", formatHpAmount(amount))
                .replace("%attacker name%", attackerName)
                .replace("%attackername%", attackerName)
                .replace("%victim name%", victimName)
                .replace("%victimname%", victimName)));
    }

    private boolean hasAdditionalRealPlayerNear(LivingEntity center, LivingEntity source,
                                                double radius, LivingEntity excludedTarget) {
        if (center == null || center.getWorld() == null) {
            return false;
        }

        double radiusSquared = radius * radius;
        for (Entity nearby : center.getWorld().getNearbyEntities(center.getLocation(), radius, radius, radius)) {
            if (!(nearby instanceof Player)
                    || nearby.getUniqueId().equals(center.getUniqueId())
                    || (source != null && nearby.getUniqueId().equals(source.getUniqueId()))
                    || (excludedTarget != null && nearby.getUniqueId().equals(excludedTarget.getUniqueId()))
                    || isSummonedEntity(nearby)
                    || nearby.getLocation().distanceSquared(center.getLocation()) > radiusSquared) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void applyParadoxHeal(LivingEntity defender, LivingEntity attacker,
                                  double divisor, double radius, EntityDamageEvent event) {
        if (defender == null || defender.getWorld() == null || event == null || divisor <= 0.0D) {
            return;
        }

        double safeRadius = Math.max(0.5D, Math.min(32.0D, radius));
        if (!hasAdditionalRealPlayerNear(defender, attacker, safeRadius, defender)) {
            return;
        }

        double healAmount = Math.max(0.0D, event.getFinalDamage() / divisor);
        if (healAmount <= 0.0D) {
            return;
        }

        double radiusSquared = safeRadius * safeRadius;
        for (Entity nearby : defender.getWorld().getNearbyEntities(defender.getLocation(), safeRadius, safeRadius, safeRadius)) {
            if (!(nearby instanceof Player)
                    || nearby.getUniqueId().equals(defender.getUniqueId())
                    || (attacker != null && nearby.getUniqueId().equals(attacker.getUniqueId()))
                    || isSummonedEntity(nearby)
                    || nearby.getLocation().distanceSquared(defender.getLocation()) > radiusSquared) {
                continue;
            }

            healLivingEntity((LivingEntity) nearby, healAmount);
            String message = getEnchantsConfig().getString("paradox.message", "&c&l** PARADOX **");
            if (message != null && !message.trim().isEmpty()) {
                ((Player) nearby).sendMessage(colorize(message
                        .replace("%amount%", formatHpAmount(healAmount))
                        .replace("%hp%", formatHpAmount(healAmount))
                        .replace("%victim name%", defender.getName())
                        .replace("%victimname%", defender.getName())));
            }
        }
    }

    private double rollChainLifestealDamage(double minDamage, double maxDamage) {
        return rollRangeAmount(minDamage, maxDamage);
    }

    private double rollRangeAmount(double minAmount, double maxAmount) {
        double min = Math.min(minAmount, maxAmount);
        double max = Math.max(minAmount, maxAmount);
        if (!Double.isFinite(min) || !Double.isFinite(max)) {
            return 0.0D;
        }

        if (Math.abs(min - Math.rint(min)) < 0.0001D
                && Math.abs(max - Math.rint(max)) < 0.0001D) {
            int minInt = (int) Math.rint(min);
            int maxInt = (int) Math.rint(max);
            return ThreadLocalRandom.current().nextInt(minInt, maxInt + 1);
        }

        if (Math.abs(max - min) < 0.0001D) {
            return min;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private void healLivingEntity(LivingEntity entity, double amount) {
        if (entity == null || entity.isDead() || !entity.isValid() || amount <= 0.0D) {
            return;
        }

        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = maxHealthAttribute == null ? 20.0D : maxHealthAttribute.getValue();
        entity.setHealth(Math.min(maxHealth, entity.getHealth() + amount));
    }

    private boolean applyExecuteDamage(Event sourceEvent, LivingEntity victim, String[] args) {
        if (!(sourceEvent instanceof EntityDamageEvent) || args == null || args.length < 2) {
            return false;
        }

        double thresholdHp = parseDouble(args[0], Double.NaN);
        double multiplier = parseDouble(args[1], Double.NaN);
        if (!Double.isFinite(thresholdHp) || !Double.isFinite(multiplier)
                || thresholdHp <= 0.0D || multiplier <= 0.0D) {
            return false;
        }

        if (victim == null && ((EntityDamageEvent) sourceEvent).getEntity() instanceof LivingEntity) {
            victim = (LivingEntity) ((EntityDamageEvent) sourceEvent).getEntity();
        }
        if (victim == null || victim.getHealth() >= thresholdHp) {
            return true;
        }

        applyAdditiveDamageMultiplier((EntityDamageEvent) sourceEvent, multiplier);
        return true;
    }

    private boolean applyDelayedEnlightenedHeal(ExecutionTask task, LivingEntity entity, String[] args) {
        if (task == null || task.getBuilder() == null || args == null || args.length < 2) {
            return false;
        }

        Event sourceEvent = task.getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageEvent)) {
            return false;
        }
        if (sourceEvent instanceof Cancellable && ((Cancellable) sourceEvent).isCancelled()) {
            return true;
        }

        EntityDamageEvent damageEvent = (EntityDamageEvent) sourceEvent;
        if (damageEvent.getFinalDamage() <= 0.0D) {
            return true;
        }

        LivingEntity target = firstValidLivingEntity(
                entity,
                task.getBuilder().getVictim(),
                damageEvent.getEntity() instanceof LivingEntity ? (LivingEntity) damageEvent.getEntity() : null);
        if (target == null) {
            return false;
        }

        double minHeal = Math.max(0.0D, parseDouble(args[0], 0.0D));
        double maxHeal = Math.max(0.0D, parseDouble(args[1], minHeal));
        double healAmount = rollRangeAmount(minHeal, maxHeal);
        if (healAmount <= 0.0D) {
            return true;
        }

        Bukkit.getScheduler().runTask(this, () -> {
            if (target.isValid() && !target.isDead()) {
                healLivingEntity(target, healAmount);
            }
        });
        return true;
    }

    private double getAlienHungerResistChance(int level) {
        int safeLevel = clamp(level, 1, 3);
        String[] args = getEnchantEffectArguments("alienimplants", safeLevel, "ALIEN_HUNGER_RESIST");
        if (args.length >= 1) {
            double configured = parseDouble(args[0], Double.NaN);
            if (Double.isFinite(configured)) {
                return Math.max(0.0D, Math.min(100.0D, configured));
            }
        }

        switch (safeLevel) {
            case 1:
                return 33.0D;
            case 2:
                return 66.0D;
            default:
                return 100.0D;
        }
    }

    private boolean shouldCancelSilencedAbility(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (event == null || ability == null) {
            return false;
        }

        String enchantName = normalize(extractEnchantName(ability));
        if (enchantName.isEmpty()) {
            return false;
        }

        java.util.LinkedHashSet<Player> candidates = new java.util.LinkedHashSet<>();
        addSilenceCandidate(candidates, event.getMainEntity());
        if (event.getActionExecution() != null && event.getActionExecution().getBuilder() != null) {
            addSilenceCandidate(candidates, event.getActionExecution().getBuilder().getMain());
            addSilenceCandidate(candidates, event.getActionExecution().getBuilder().getVictim());
        }
        addSilenceCandidate(candidates, getPrimaryVictim(event));

        for (Player candidate : candidates) {
            if (isEnchantSilenced(candidate, enchantName)) {
                return true;
            }
        }
        return false;
    }

    private static void addSilenceCandidate(java.util.Set<Player> candidates, LivingEntity entity) {
        if (entity instanceof Player) {
            candidates.add((Player) entity);
        }
    }

    private boolean isEnchantSilenced(Player player, String enchantName) {
        Map<String, Long> silenced = silencedEnchantExpires.get(player.getUniqueId());
        if (silenced == null || silenced.isEmpty()) {
            return false;
        }

        Long expiresAt = silenced.get(normalize(enchantName));
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= serverTick) {
            silenced.remove(normalize(enchantName));
            if (silenced.isEmpty()) {
                silencedEnchantExpires.remove(player.getUniqueId());
            }
            return false;
        }
        return true;
    }

    private boolean applySilence(Event sourceEvent, LivingEntity attacker, LivingEntity victim,
                                 ItemStack sourceItem, String[] args) {
        if (!(sourceEvent instanceof EntityDamageByEntityEvent)
                || !(attacker instanceof Player)
                || !(victim instanceof Player)
                || attacker.getUniqueId().equals(victim.getUniqueId())
                || args == null
                || args.length < 3) {
            return false;
        }

        Player playerAttacker = (Player) attacker;
        Player playerVictim = (Player) victim;
        ItemStack item = isUsableItem(sourceItem)
                ? sourceItem
                : playerAttacker.getInventory().getItemInMainHand();
        int silenceLevel = getEnchantLevelOnItem("silence", "silence", item);
        if (silenceLevel <= 0) {
            return true;
        }

        int durationArgIndex = 1;
        double baseChance = parseDouble(args[0], Double.NaN);
        if (args.length >= 4 && isIntegerText(args[0]) && isIntegerText(args[1])) {
            baseChance = parseDouble(args[0] + "." + args[1], baseChance);
            durationArgIndex = 2;
        }
        if (args.length <= durationArgIndex + 1) {
            return false;
        }

        double baseDurationSeconds = parseDouble(args[durationArgIndex], Double.NaN);
        int cooldownSeconds = parsePositiveInt(args[durationArgIndex + 1], 5);
        if (!Double.isFinite(baseChance) || !Double.isFinite(baseDurationSeconds)
                || baseChance <= 0.0D || baseDurationSeconds <= 0.0D) {
            return false;
        }

        String cooldownKey = playerAttacker.getUniqueId() + ":" + playerVictim.getUniqueId() + ":silence";
        Long cooldownExpires = silenceCooldowns.get(cooldownKey);
        if (cooldownExpires != null && cooldownExpires > serverTick) {
            return true;
        }

        SilenceModifier modifier = rollSilenceModifier(item);
        double chanceMultiplier = modifier == null ? 1.0D : modifier.multiplier();
        double finalChance = Math.min(100.0D, baseChance * chanceMultiplier);
        if (!rollChance(finalChance)) {
            return true;
        }

        int baseDisplaySeconds = Math.max(1, (int) Math.round(baseDurationSeconds));
        int finalDurationSeconds = modifier == null
                ? baseDisplaySeconds
                : Math.max(baseDisplaySeconds + 1, (int) Math.ceil(baseDurationSeconds * modifier.multiplier()));
        silenceCooldowns.put(cooldownKey, serverTick + Math.max(0, cooldownSeconds) * 20L);
        silencePlayer(playerVictim, finalDurationSeconds);
        sendSilenceMessage(playerVictim, baseDisplaySeconds, finalDurationSeconds, modifier != null);
        sendSilenceAttackerMessage(playerAttacker, playerVictim, baseDisplaySeconds, finalDurationSeconds, modifier != null);
        playSilenceSounds(playerVictim);
        return true;
    }

    private void silencePlayer(Player player, int durationSeconds) {
        long expiresAt = serverTick + Math.max(1, durationSeconds) * 20L;
        Map<String, Long> silenced = silencedEnchantExpires.computeIfAbsent(player.getUniqueId(),
                ignored -> new HashMap<>());
        for (String enchantName : getSilenceDisabledEnchantNames()) {
            String normalized = normalize(enchantName);
            if (normalized.isEmpty()) {
                continue;
            }

            Long current = silenced.get(normalized);
            if (current == null || current < expiresAt) {
                silenced.put(normalized, expiresAt);
            }
        }
    }

    private List<String> getSilenceDisabledEnchantNames() {
        List<String> configured = getEnchantsConfig().getStringList("silence.disabled-enchants");
        return configured.isEmpty() ? DEFAULT_SILENCE_DISABLED_ENCHANTS : configured;
    }

    private SilenceModifier rollSilenceModifier(ItemStack item) {
        SilenceModifier perfect = rollSilenceModifier(item, "perfectsolitude", "PERFECT_SOLITUDE",
                new double[]{20.0D, 35.0D, 50.0D}, new double[]{3.0D, 4.5D, 6.0D});
        if (perfect != null) {
            return perfect;
        }
        return rollSilenceModifier(item, "solitude", "SOLITUDE",
                new double[]{20.0D, 35.0D, 50.0D}, new double[]{1.75D, 2.35D, 3.0D});
    }

    private SilenceModifier rollSilenceModifier(ItemStack item, String enchantName, String effectName,
                                                double[] fallbackChances, double[] fallbackCaps) {
        int level = getEnchantLevelOnItem(enchantName, enchantName, item);
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, Math.min(fallbackChances.length, fallbackCaps.length));
        String[] args = getEnchantEffectArguments(enchantName, safeLevel, effectName);
        double chance = args.length >= 1 ? parseDouble(args[0], Double.NaN) : Double.NaN;
        double cap = args.length >= 2 ? parseDouble(args[1], Double.NaN) : Double.NaN;
        if (!Double.isFinite(chance)) {
            chance = fallbackChances[safeLevel - 1];
        }
        if (!Double.isFinite(cap)) {
            cap = fallbackCaps[safeLevel - 1];
        }
        cap = Math.max(1.1D, cap);

        if (!rollChance(chance)) {
            return null;
        }

        double multiplier = cap <= 1.1D
                ? 1.1D
                : ThreadLocalRandom.current().nextDouble(1.1D, cap);
        return new SilenceModifier(multiplier);
    }

    private void sendSilenceMessage(Player victim, int baseSeconds, int finalSeconds, boolean modified) {
        String path = modified ? "silence.modified-message" : "silence.message";
        String fallback = modified
                ? "&5&l* SILENCED&r &8[&m%base-duration%s&r &8%duration%s] &5&l*"
                : "&5&l* SILENCED&r &7[%duration%s] &5&l*";
        String message = getEnchantsConfig().getString(path, fallback);
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        victim.sendMessage(colorize(message
                .replace("%base-duration%", String.valueOf(baseSeconds))
                .replace("%duration%", String.valueOf(finalSeconds))
                .replace("%victim name%", victim.getName())
                .replace("%victimname%", victim.getName())));
    }

    private void sendSilenceAttackerMessage(Player attacker, Player victim, int baseSeconds,
                                            int finalSeconds, boolean modified) {
        String path = modified ? "silence.modified-attacker-message" : "silence.attacker-message";
        String fallback = modified
                ? "&5&l* SILENCE&r &8on &f%victim name% &8[&m%base-duration%s&r &8%duration%s] &5&l*"
                : "&5&l* SILENCE&r &8on &f%victim name% &7[%duration%s] &5&l*";
        String message = getEnchantsConfig().getString(path, fallback);
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        attacker.sendMessage(colorize(message
                .replace("%base-duration%", String.valueOf(baseSeconds))
                .replace("%duration%", String.valueOf(finalSeconds))
                .replace("%victim name%", victim.getName())
                .replace("%victimname%", victim.getName())
                .replace("%attacker name%", attacker.getName())
                .replace("%attackername%", attacker.getName())));
    }

    private void playSilenceSounds(Player victim) {
        List<String> sounds = getEnchantsConfig().getStringList("silence.sounds");
        if (sounds.isEmpty() && !getEnchantsConfig().contains("silence.sounds")) {
            sounds = Collections.singletonList("ENTITY_WITHER_HURT:1.0:0.7");
        }
        for (String sound : sounds) {
            playConfiguredSound(victim, sound);
        }
    }

    private void sendChainLifestealAttackerMessage(LivingEntity attacker, double amount) {
        if (!(attacker instanceof Player)) {
            return;
        }

        String attackerName = attacker.getName();
        String message = getEnchantsConfig().getString("chain-lifesteal.attacker-message",
                "&4&l** CHAIN LIFESTEAL ** &7(&c+%amount% HP&7)");
        ((Player) attacker).sendMessage(colorize(message
                .replace("%amount%", formatHpAmount(amount))
                .replace("%hp%", formatHpAmount(amount))
                .replace("%attacker name%", attackerName)
                .replace("%attackername%", attackerName)));
    }

    private void sendChainLifestealVictimMessage(Player victim, LivingEntity attacker, double amount) {
        String attackerName = attacker == null ? "" : attacker.getName();
        String message = getEnchantsConfig().getString("chain-lifesteal.victim-message",
                "&4&l** CHAIN LIFESTEAL ** &7(&c-%amount% HP&7)");
        victim.sendMessage(colorize(message
                .replace("%amount%", formatHpAmount(amount))
                .replace("%hp%", formatHpAmount(amount))
                .replace("%victim name%", victim.getName())
                .replace("%victimname%", victim.getName())
                .replace("%attacker name%", attackerName)
                .replace("%attackername%", attackerName)));
    }

    private static String formatHpAmount(double amount) {
        double rounded = Math.round(Math.max(0.0D, amount) * 100.0D) / 100.0D;
        String formatted = String.format(Locale.US, "%.2f", rounded);
        return formatted.replaceAll("\\.?0+$", "");
    }

    private void storeDamageCap(LivingEntity target, double maxDamage, boolean reflectOverflow, Event sourceEvent) {
        if (target == null || target.isDead() || !target.isValid() || maxDamage < 0.0D) {
            return;
        }

        nextDamageCaps.put(target.getUniqueId(), new DamageCapState(maxDamage, reflectOverflow, sourceEvent));
        if (target instanceof Player) {
            String path = reflectOverflow ? "vengeful-diminish.message" : "diminish.message";
            String fallback = reflectOverflow
                    ? "&d&l* VENGEFUL DIMINISH [&fNext Max DMG: %damage%] &d&l*"
                    : "&6&l* DIMINISH [&fNext Max DMG: %damage%] &6&l*";
            ((Player) target).sendMessage(colorize(getEnchantsConfig().getString(path, fallback)
                    .replace("%damage%", formatHpAmount(maxDamage))
                    .replace("%max damage%", formatHpAmount(maxDamage))
                    .replace("%maxdamage%", formatHpAmount(maxDamage))));
        }
    }

    private boolean storeDiminishCap(ExecutionTask task, LivingEntity entity, boolean reflectOverflow) {
        if (task == null || task.getBuilder() == null) {
            return false;
        }

        Event event = task.getBuilder().getEvent();
        if (!(event instanceof EntityDamageEvent)) {
            return false;
        }

        LivingEntity target = firstValidLivingEntity(
                entity,
                task.getBuilder().getVictim(),
                getDimensionalShiftDamageVictim(task),
                task.getBuilder().getMain());
        if (target == null) {
            return false;
        }

        double damageTaken = Math.max(0.0D, ((EntityDamageEvent) event).getFinalDamage());
        if (damageTaken <= 0.0D) {
            return false;
        }

        storeDamageCap(target, damageTaken / 2.0D, reflectOverflow, event);
        return true;
    }

    private boolean multiplyCurrentDamage(ExecutionTask task, double multiplier, boolean swordOnly) {
        if (task == null || task.getBuilder() == null) {
            return false;
        }

        Event event = task.getBuilder().getEvent();
        if (!(event instanceof EntityDamageEvent) || ((EntityDamageEvent) event).getDamage() <= 0.0D) {
            return false;
        }
        if (swordOnly && !isDamageTaskFromSword(task)) {
            return false;
        }

        double safeMultiplier = Math.max(0.0D, Math.min(10.0D, multiplier));
        EntityDamageEvent damageEvent = (EntityDamageEvent) event;
        applyAdditiveDamageMultiplier(damageEvent, safeMultiplier);
        return true;
    }

    private void applyAdditiveDamageMultiplier(EntityDamageEvent event, double multiplier) {
        if (event == null || !Double.isFinite(multiplier)) {
            return;
        }

        AdditiveDamageMultiplierState state = additiveDamageMultipliers.computeIfAbsent(
                event, ignored -> new AdditiveDamageMultiplierState(Math.max(0.0D, event.getDamage())));
        state.addMultiplier(multiplier);
        event.setDamage(Math.max(0.0D, state.damage()));
    }

    private boolean isDamageTaskFromSword(ExecutionTask task) {
        LivingEntity attacker = firstValidLivingEntity(
                task.getBuilder().getAttacker(),
                getDimensionalShiftDamageAttacker(task),
                task.getBuilder().getMain());
        if (!(attacker instanceof Player)) {
            return false;
        }

        return isUsableItem(((Player) attacker).getInventory().getItemInMainHand())
                && ((Player) attacker).getInventory().getItemInMainHand().getType().name().endsWith("_SWORD");
    }

    private boolean tryReflectOffensiveAbility(AbilityPreactivateEvent event, AdvancedAbility ability) {
        if (!getEnchantsConfig().getBoolean("enchant-reflect.enabled", true)
                || event == null
                || ability == null
                || event.getActionExecution() == null
                || event.getActionExecution().getBuilder() == null
                || !isOffensiveAbility(ability)
                || isReflectBlockedAbility(ability)) {
            return false;
        }

        Event sourceEvent = event.getActionExecution().getBuilder().getEvent();
        if (!(sourceEvent instanceof EntityDamageByEntityEvent)) {
            return false;
        }

        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) sourceEvent;
        LivingEntity attacker = getLivingDamager(damageEvent.getDamager());
        LivingEntity victim = getPrimaryVictim(event);
        if (!(victim instanceof Player)
                || attacker == null
                || attacker.getUniqueId().equals(victim.getUniqueId())
                || damageEvent.getEntity().getUniqueId().equals(attacker.getUniqueId())) {
            return false;
        }

        ReflectCandidate candidate = getReflectCandidate((Player) victim, ability);
        if (candidate == null
                || isEnchantReflectOnCooldown((Player) victim, candidate)
                || !rollChance(candidate.chance())) {
            return false;
        }

        startEnchantReflectCooldown((Player) victim, candidate);
        startReflectedSourceCooldown(event, ability, attacker);
        executeReflectedAbility(event, ability, (Player) victim, attacker);
        sendReflectMessage((Player) victim, attacker, ability, candidate.heroic());
        return true;
    }

    private void executeReflectedAbility(AbilityPreactivateEvent event, AdvancedAbility ability,
                                         Player defender, LivingEntity attacker) {
        if (event == null || ability == null || ability.getEffects() == null
                || defender == null || attacker == null) {
            return;
        }

        ActionExecutionBuilder originalBuilder = event.getActionExecution().getBuilder();
        ActionExecutionBuilder reflectedBuilder = new ActionExecutionBuilder(originalBuilder.getType())
                .setEvent(originalBuilder.getEvent())
                .setItem(originalBuilder.getItem())
                .setItemType(originalBuilder.getItemType())
                .setStackItem(originalBuilder.getStackItem())
                .setBlock(originalBuilder.getBlock())
                .setAttacker(defender)
                .setVictim(attacker)
                .setAttackerMain(false)
                .setDamageEventNotGoingToRun(originalBuilder.isDamageEventNotGoingToRun())
                .setSkipCooldown(true);

        reflectedBuilder.processVariables(
                "%attacker name%;" + defender.getName(),
                "%attackername%;" + defender.getName(),
                "%attacker%;" + defender.getName(),
                "%victim name%;" + attacker.getName(),
                "%victimname%;" + attacker.getName(),
                "%victim%;" + attacker.getName()
        );
        reflectedBuilder.globalVariables();

        AdvancedAbility reflectedAbility = cloneAbilityForReflection(ability);
        ActionExecution reflectedExecution = new ActionExecution(reflectedBuilder);
        reflectedExecution.getVariables().putAll(event.getActionExecution().getVariables());
        reflectedExecution.getVariables().putAll(reflectedBuilder.getVariables());
        reflectedExecution.getAllEffectsRaw().addAll(reflectedAbility.getEffects());
        reflectedExecution.getEffects().add(reflectedAbility);
        reflectedExecution.run();
    }

    private AdvancedAbility cloneAbilityForReflection(AdvancedAbility ability) {
        return AdvancedAbility.builder()
                .setName(ability.getName())
                .setSection(ability.getSection())
                .setCommand(ability.getCommand())
                .setCooldown(0)
                .setCooldownMessage(ability.getCooldownMessage())
                .setChance(100.0D)
                .setConditions(Collections.emptyList())
                .setBlacklist(copyStringList(ability.getBlacklist()))
                .setWhitelist(copyStringList(ability.getWhitelist()))
                .setWorldBlacklist(copyStringList(ability.getWorldBlacklist()))
                .setType(copyStringList(ability.getTypes()))
                .setRepeatingDelay(ability.getRepeatingDelay())
                .setRepeatingInstantApply(ability.isRepeatingInstantApply())
                .setSouls(0)
                .setEffects(new java.util.ArrayList<>(ability.getEffects()));
    }

    private static List<String> copyStringList(List<String> values) {
        return values == null ? Collections.emptyList() : new java.util.ArrayList<>(values);
    }

    private void startReflectedSourceCooldown(AbilityPreactivateEvent event, AdvancedAbility ability,
                                              LivingEntity attacker) {
        if (event == null || ability == null || ability.getCooldown() <= 0) {
            return;
        }

        LivingEntity cooldownOwner = firstValidLivingEntity(
                event.getActionExecution().getBuilder().getMain(),
                event.getActionExecution().getBuilder().getAttacker(),
                attacker);
        if (cooldownOwner == null) {
            return;
        }

        try {
            ACooldown.putToCooldown(cooldownOwner, ability.getNameNoLevel(), ability.getCooldown());
        } catch (RuntimeException ignored) {
            // AE cooldown internals are best-effort here; reflection still needs to block the original effect.
        }
    }

    private ReflectCandidate getReflectCandidate(Player defender, AdvancedAbility ability) {
        if (isSoulEnchantGroup(ability)) {
            return null;
        }

        int enchantRank = getEnchantGroupRank(ability);
        if (enchantRank <= 0) {
            return null;
        }

        int heroicLevel = getHighestArmorEnchantLevel(defender, "heroicenchantreflect", "heroicenchantreflect");
        if (heroicLevel > 0) {
            ReflectCandidate candidate = getReflectCandidateFromEnchant(
                    "heroicenchantreflect", heroicLevel, "HEROIC_ENCHANT_REFLECT", true, "HEROIC");
            if (candidate != null && enchantRank <= candidate.maxRank()) {
                return candidate;
            }
        }

        int normalLevel = getHighestArmorEnchantLevel(defender, "enchantreflect", "enchantreflect");
        if (normalLevel > 0) {
            ReflectCandidate candidate = getReflectCandidateFromEnchant(
                    "enchantreflect", normalLevel, "ENCHANT_REFLECT", false, "LEGENDARY");
            if (candidate != null && enchantRank <= candidate.maxRank()) {
                return candidate;
            }
        }

        return null;
    }

    private boolean isReflectBlockedAbility(AdvancedAbility ability) {
        if (ability == null) {
            return true;
        }

        String enchantName = normalize(extractEnchantName(ability));
        List<String> blocked = getEnchantsConfig().getStringList("enchant-reflect.blocked-enchants");
        if (blocked.isEmpty()) {
            blocked = Arrays.asList("rage", "solitude", "perfectsolitude");
        }
        for (String blockedEnchant : blocked) {
            if (enchantName.equals(normalize(blockedEnchant))) {
                return true;
            }
        }

        if (ability.getEffects() != null) {
            for (String effect : ability.getEffects()) {
                String effectName = effect == null ? "" : effect.trim().split("\\s+", 2)[0].split(":", 2)[0];
                if (isReflectBlockedEffectName(effectName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isReflectBlockedEffectName(String effectName) {
        String normalized = normalize(effectName);
        return normalized.equals("ragemultiplier")
                || normalized.equals("solitude")
                || normalized.equals("perfectsolitude")
                || normalized.equals("enchantreflect")
                || normalized.equals("heroicenchantreflect");
    }

    private ReflectCandidate getReflectCandidateFromEnchant(String enchantName, int level, String effectName,
                                                            boolean heroic, String fallbackGroup) {
        String[] args = getEnchantEffectArguments(enchantName, level, effectName);
        int maxRank = getGroupRank(args.length >= 1 ? args[0] : fallbackGroup);
        if (maxRank <= 0) {
            maxRank = getGroupRank(fallbackGroup);
        }
        AdvancedAbility ability = getAbility(enchantName, enchantName, level);
        double chance = ability == null ? Double.NaN : ability.getChance();
        if (!Double.isFinite(chance) || chance <= 0.0D) {
            chance = getReflectChance(level, heroic);
        }
        int cooldownSeconds = ability == null ? 10 : Math.max(10, ability.getCooldown());
        return new ReflectCandidate(heroic, Math.max(0.0D, Math.min(100.0D, chance)),
                maxRank, enchantName, cooldownSeconds);
    }

    private boolean isEnchantReflectOnCooldown(Player player, ReflectCandidate candidate) {
        Long expiresAt = enchantReflectCooldowns.get(getEnchantReflectCooldownKey(player, candidate));
        return expiresAt != null && System.currentTimeMillis() < expiresAt;
    }

    private void startEnchantReflectCooldown(Player player, ReflectCandidate candidate) {
        int cooldownSeconds = Math.max(10, candidate.cooldownSeconds());
        enchantReflectCooldowns.put(getEnchantReflectCooldownKey(player, candidate),
                System.currentTimeMillis() + cooldownSeconds * 1000L);
    }

    private String getEnchantReflectCooldownKey(Player player, ReflectCandidate candidate) {
        return player.getUniqueId() + ":" + candidate.enchantName();
    }

    private double getReflectChance(int level, boolean heroic) {
        int safeLevel = clamp(level, 1, 10);
        String prefix = heroic ? "heroic-enchant-reflect" : "enchant-reflect";
        String levelPath = prefix + ".levels." + safeLevel + ".chance";
        if (getEnchantsConfig().contains(levelPath)) {
            return Math.max(0.0D, Math.min(100.0D, getEnchantsConfig().getDouble(levelPath)));
        }

        double perLevel = getEnchantsConfig().getDouble(prefix + ".chance-per-level-percent", heroic ? 4.0D : 3.0D);
        double maxChance = getEnchantsConfig().getDouble(prefix + ".max-chance-percent", heroic ? 40.0D : 30.0D);
        return Math.max(0.0D, Math.min(100.0D, Math.min(maxChance, safeLevel * perLevel)));
    }

    private int getHighestArmorEnchantLevel(Player player, String rawEnchantName, String enchantName) {
        int highest = 0;
        if (player == null || player.getInventory() == null) {
            return highest;
        }

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            highest = Math.max(highest, getEnchantLevelOnItem(rawEnchantName, enchantName, armor));
        }
        return highest;
    }

    private static boolean isOffensiveAbility(AdvancedAbility ability) {
        if (ability.getTypes() == null) {
            return false;
        }
        for (String type : ability.getTypes()) {
            String normalized = normalize(type);
            if (normalized.equals("attack")
                    || normalized.equals("attackmob")
                    || normalized.equals("shoot")
                    || normalized.equals("shootmob")
                    || normalized.contains("attack")
                    || normalized.contains("shoot")) {
                return true;
            }
        }
        return false;
    }

    private int getEnchantGroupRank(AdvancedAbility ability) {
        return getGroupRank(getEnchantGroupName(ability));
    }

    private String getEnchantGroupName(AdvancedAbility ability) {
        try {
            AdvancedEnchantment enchantment = AEAPI.getEnchantmentInstance(extractEnchantName(ability));
            return enchantment == null ? null : enchantment.getGroupName();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private boolean isSoulEnchantGroup(AdvancedAbility ability) {
        return normalize(getEnchantGroupName(ability)).equals("soul");
    }

    private static int getGroupRank(String groupName) {
        String normalized = normalize(groupName);
        switch (normalized) {
            case "simple":
                return 1;
            case "unique":
                return 2;
            case "elite":
                return 3;
            case "ultimate":
                return 4;
            case "legendary":
                return 5;
            case "soul":
                return 6;
            case "heroic":
                return 7;
            case "mastery":
                return 8;
            default:
                return 0;
        }
    }

    private void sendReflectMessage(Player defender, LivingEntity attacker, AdvancedAbility ability, boolean heroic) {
        String path = heroic ? "heroic-enchant-reflect.message" : "enchant-reflect.message";
        String fallback = heroic
                ? "&d&l* HEROIC ENCHANT REFLECT * &8Reflected &d%enchant% &8back to &f%attacker name%&8."
                : "&6&l* ENCHANT REFLECT * &8Reflected &6%enchant% &8back to &f%attacker name%&8.";
        String attackerName = attacker == null ? "" : attacker.getName();
        defender.sendMessage(colorize(getEnchantsConfig().getString(path, fallback)
                .replace("%enchant%", prettyAbilityEnchantName(ability))
                .replace("%attacker name%", attackerName)
                .replace("%attackername%", attackerName)));
    }

    private String prettyAbilityEnchantName(AdvancedAbility ability) {
        if (ability == null) {
            return "";
        }

        String enchantName = extractEnchantName(ability);
        try {
            AdvancedEnchantment enchantment = AEAPI.getEnchantmentInstance(enchantName);
            if (enchantment != null) {
                String display = "";
                try {
                    display = enchantment.getDisplayNoColor();
                } catch (RuntimeException ignored) {
                    // Fall back below.
                }
                if (display == null || display.trim().isEmpty()) {
                    try {
                        display = enchantment.getDisplay(extractLevel(ability));
                    } catch (RuntimeException ignored) {
                        display = "";
                    }
                }

                display = cleanDisplayName(display);
                if (!display.isEmpty()) {
                    return display;
                }
            }
        } catch (RuntimeException ignored) {
            // Fall back below.
        }

        String configured = prettyEnchantName(enchantName);
        if (!configured.equals(enchantName)) {
            return cleanDisplayName(configured);
        }
        return titleCase(enchantName.replace("_", " ").replace("-", " "));
    }

    private void startDestructionAura(LivingEntity wearer, LivingEntity source,
                                      double damagePerTick, double radius, int durationTicks) {
        if (wearer == null || wearer.isDead() || !wearer.isValid() || wearer.getWorld() == null) {
            return;
        }

        UUID wearerId = wearer.getUniqueId();
        long expiresAtTick = serverTick + Math.max(1, durationTicks);
        double safeDamage = Math.max(0.0D, Math.min(100.0D, damagePerTick));
        double safeRadius = Math.max(0.5D, Math.min(16.0D, radius));
        UUID sourceId = source == null ? null : source.getUniqueId();

        DestructionAuraSession existing = destructionAuraSessions.get(wearerId);
        if (existing != null) {
            existing.refresh(sourceId, expiresAtTick, safeDamage, safeRadius);
            return;
        }

        DestructionAuraSession session = new DestructionAuraSession(
                wearerId, sourceId, expiresAtTick, safeDamage, safeRadius);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                DestructionAuraSession active = destructionAuraSessions.get(wearerId);
                if (active == null || serverTick >= active.expiresAtTick()) {
                    destructionAuraSessions.remove(wearerId);
                    cancel();
                    return;
                }

                Entity entity = Bukkit.getEntity(wearerId);
                if (!(entity instanceof LivingEntity)
                        || entity.isDead()
                        || !entity.isValid()
                        || entity.getWorld() == null) {
                    destructionAuraSessions.remove(wearerId);
                    cancel();
                    return;
                }

                LivingEntity activeWearer = (LivingEntity) entity;
                Entity sourceEntity = active.sourceId() == null ? null : Bukkit.getEntity(active.sourceId());
                damageDestructionAuraTargets(activeWearer,
                        sourceEntity instanceof LivingEntity ? (LivingEntity) sourceEntity : activeWearer,
                        active.damagePerTick(), active.radius(), active.messagedTargets());
            }
        }.runTaskTimer(this, 0L, 1L);
        session.setTask(task);
        destructionAuraSessions.put(wearerId, session);
    }

    private void tickPersistentDestructionAuras() {
        if (!getEnchantsConfig().getBoolean("destruction.enabled", true)) {
            destructionAuraDebuffedTargets.clear();
            return;
        }

        int intervalTicks = Math.max(1, getEnchantsConfig().getInt("destruction.damage-interval-ticks", 20));
        if (serverTick % intervalTicks != 0L) {
            return;
        }

        java.util.Set<UUID> activeWearers = new java.util.HashSet<>();
        for (Player wearer : Bukkit.getOnlinePlayers()) {
            if (wearer.isDead() || !wearer.isValid()) {
                continue;
            }

            int level = getHighestEquippedDestructionLevel(wearer);
            if (level <= 0) {
                continue;
            }

            activeWearers.add(wearer.getUniqueId());
            tickPersistentDestructionAura(wearer, level);
        }
        destructionAuraDebuffedTargets.keySet().retainAll(activeWearers);
    }

    private void tickPersistentDestructionAura(Player wearer, int level) {
        double damagePerTick = getDestructionAuraArgument(level, 0, level >= 4 ? 2.0D : 1.0D);
        double radius = getDestructionAuraArgument(level, 1, level == 1 ? 3.0D : level == 2 ? 4.0D : 5.0D);
        if (damagePerTick <= 0.0D || radius <= 0.0D || wearer.getWorld() == null) {
            return;
        }

        double safeRadius = Math.max(0.5D, Math.min(16.0D, radius));
        double radiusSquared = safeRadius * safeRadius;
        java.util.Set<UUID> debuffedTargets = destructionAuraDebuffedTargets.computeIfAbsent(
                wearer.getUniqueId(), ignored -> new java.util.HashSet<>());
        java.util.Set<UUID> currentTargets = new java.util.HashSet<>();
        Location center = wearer.getLocation();

        for (Entity nearby : wearer.getWorld().getNearbyEntities(center, safeRadius, safeRadius, safeRadius)) {
            if (!(nearby instanceof Player)) {
                continue;
            }

            Player target = (Player) nearby;
            if (target.getUniqueId().equals(wearer.getUniqueId())
                    || target.isDead()
                    || !target.isValid()
                    || target.getLocation().distanceSquared(center) > radiusSquared) {
                continue;
            }

            currentTargets.add(target.getUniqueId());
            if (debuffedTargets.add(target.getUniqueId())) {
                removeDestructionBuffs(target);
            }

            damageDestructionWithoutKnockback(target, wearer, damagePerTick);
        }

        debuffedTargets.retainAll(currentTargets);
    }

    private int getHighestEquippedDestructionLevel(Player player) {
        int highest = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            highest = Math.max(highest, getEnchantLevelOnItem("destruction", "destruction", armor));
        }
        return highest;
    }

    private void tickPassiveImmortalRepairs() {
        if (!getEnchantsConfig().getBoolean("immortal.enabled", true)) {
            return;
        }

        int intervalTicks = Math.max(1, getEnchantsConfig().getInt("immortal.interval-ticks", 20));
        if (serverTick % intervalTicks != 0L) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            repairImmortalArmor(player);
        }
    }

    private void repairImmortalArmor(Player player) {
        ImmortalSource source = findImmortalSource(player);
        if (source == null || source.level() <= 0 || source.repairAmount() <= 0) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            return;
        }

        int costPerPiece = Math.max(1, getEnchantsConfig().getInt("immortal.souls-per-piece", 5));
        int repairedPieces = 0;

        ItemStack helmet = equipment.getHelmet();
        if (canRepairArmorPiece(helmet)
                && payImmortalPieceSouls(player, source, costPerPiece)
                && repairArmorPiece(helmet, source.repairAmount())) {
            equipment.setHelmet(helmet);
            repairedPieces++;
        }

        ItemStack chestplate = equipment.getChestplate();
        if (canRepairArmorPiece(chestplate)
                && payImmortalPieceSouls(player, source, costPerPiece)
                && repairArmorPiece(chestplate, source.repairAmount())) {
            equipment.setChestplate(chestplate);
            repairedPieces++;
        }

        ItemStack leggings = equipment.getLeggings();
        if (canRepairArmorPiece(leggings)
                && payImmortalPieceSouls(player, source, costPerPiece)
                && repairArmorPiece(leggings, source.repairAmount())) {
            equipment.setLeggings(leggings);
            repairedPieces++;
        }

        ItemStack boots = equipment.getBoots();
        if (canRepairArmorPiece(boots)
                && payImmortalPieceSouls(player, source, costPerPiece)
                && repairArmorPiece(boots, source.repairAmount())) {
            equipment.setBoots(boots);
            repairedPieces++;
        }

        if (repairedPieces > 0) {
            sendImmortalFeedback(player, repairedPieces,
                    getAvailableSouls(getItem(player, source.itemType())));
        }
    }

    private boolean payImmortalPieceSouls(Player player, ImmortalSource source, int souls) {
        if (souls <= 0 || player.hasPermission("ae.bypass.souls")) {
            return true;
        }

        ItemStack currentSourceItem = getItem(player, source.itemType());
        if (!isUsableItem(currentSourceItem) || SoulsAPI.getSoulsOnItem(currentSourceItem) < souls) {
            return false;
        }

        setItem(player, source.itemType(), SoulsAPI.useSouls(currentSourceItem, souls));
        return true;
    }

    private ImmortalSource findImmortalSource(Player player) {
        EntityEquipment equipment = player == null ? null : player.getEquipment();
        if (equipment == null) {
            return null;
        }

        ImmortalSource best = null;
        best = betterImmortalSource(best, RollItemType.HELMET, equipment.getHelmet());
        best = betterImmortalSource(best, RollItemType.CHESTPLATE, equipment.getChestplate());
        best = betterImmortalSource(best, RollItemType.LEGGINGS, equipment.getLeggings());
        best = betterImmortalSource(best, RollItemType.BOOTS, equipment.getBoots());
        return best;
    }

    private ImmortalSource betterImmortalSource(ImmortalSource current, RollItemType itemType, ItemStack item) {
        int level = getEnchantLevelOnItem("immortal", "immortal", item);
        if (level <= 0 || (current != null && current.level() >= level)) {
            return current;
        }

        int repairAmount = getImmortalRepairAmount(level);
        return new ImmortalSource(itemType, item, level, repairAmount);
    }

    private int getImmortalRepairAmount(int level) {
        int safeLevel = clamp(level, 1, 10);
        String[] args = getEnchantEffectArguments("immortal", safeLevel, "IMMORTAL_REPAIR");
        if (args.length >= 1) {
            int configured = parsePositiveInt(args[0], 0);
            if (configured > 0) {
                return configured;
            }
        }

        switch (safeLevel) {
            case 1:
                return 4;
            case 2:
                return 6;
            case 3:
                return 8;
            default:
                return 10;
        }
    }

    private boolean canRepairArmorPiece(ItemStack item) {
        if (!isUsableItem(item) || item.getType().getMaxDurability() <= 0) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return false;
        }

        if (isHeroicUpgradedItem(item)) {
            int maxDurability = getHeroicMaxDurability(meta);
            return maxDurability > 0 && getHeroicDamage(meta, item.getType(), maxDurability) > 0;
        }

        return ((Damageable) meta).getDamage() > 0;
    }

    private boolean repairArmorPiece(ItemStack item, int amount) {
        if (!canRepairArmorPiece(item) || amount <= 0) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        Damageable damageable = (Damageable) meta;
        if (isHeroicUpgradedItem(item)) {
            int maxDurability = getHeroicMaxDurability(meta);
            int currentDamage = getHeroicDamage(meta, item.getType(), maxDurability);
            int nextDamage = Math.max(0, currentDamage - amount);
            setHeroicDamage(meta, nextDamage);
            syncHeroicVisualDamage(damageable, item.getType(), nextDamage, maxDurability);
            item.setItemMeta(meta);
            return true;
        }

        damageable.setDamage(Math.max(0, damageable.getDamage() - amount));
        item.setItemMeta(meta);
        return true;
    }

    private void sendImmortalFeedback(Player player, int repairedPieces, int remainingSouls) {
        int cooldownTicks = Math.max(0, getEnchantsConfig().getInt("immortal.feedback-cooldown-ticks", 200));
        Long lastTick = immortalFeedbackTicks.get(player.getUniqueId());
        if (lastTick != null && serverTick - lastTick < cooldownTicks) {
            return;
        }

        immortalFeedbackTicks.put(player.getUniqueId(), serverTick);
        List<String> messages = getEnchantsConfig().getStringList("immortal.message");
        if (messages.isEmpty() && !getEnchantsConfig().contains("immortal.message")) {
            messages = Arrays.asList(
                    "",
                    "&6&l** IMMORTAL ** &8[%pieces%x]",
                    "&7You have &7&n%souls%&r &7souls left.",
                    "");
        }
        for (String message : messages) {
            player.sendMessage(colorize(message
                    .replace("%pieces%", String.valueOf(repairedPieces))
                    .replace("%souls%", String.valueOf(Math.max(0, remainingSouls)))
                    .replace("%amount%", String.valueOf(Math.max(0, remainingSouls)))));
        }

        List<String> sounds = getEnchantsConfig().getStringList("immortal.sounds");
        if (sounds.isEmpty() && !getEnchantsConfig().contains("immortal.sounds")) {
            sounds = Collections.singletonList("ENTITY_PLAYER_LEVELUP:0.5:0.5");
        }
        for (String sound : sounds) {
            playConfiguredSound(player, sound);
        }
    }

    private double getDestructionAuraArgument(int level, int index, double fallback) {
        String[] args = getEnchantEffectArguments("destruction", clamp(level, 1, 5), "DESTRUCTION_AURA");
        if (args.length > index) {
            double configured = parseDouble(args[index], Double.NaN);
            if (Double.isFinite(configured) && configured > 0.0D) {
                return configured;
            }
        }
        return fallback;
    }

    private AegisSettings getAegisSettings(Player player) {
        int level = getHighestArmorEnchantLevel(player, "aegis", "aegis");
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, 6);
        String[] args = getEnchantEffectArguments("aegis", safeLevel, "AEGIS");
        int initialEnemies = Math.max(1, 8 - safeLevel);
        int windowTicks = 100;
        double reductionPercent = 50.0D;
        if (args.length >= 1) {
            initialEnemies = Math.max(1, parsePositiveInt(args[0], initialEnemies));
        }
        if (args.length >= 2) {
            windowTicks = Math.max(1, parsePositiveInt(args[1], windowTicks));
        }
        if (args.length >= 3) {
            reductionPercent = clampPercent(parseDouble(args[2], reductionPercent));
        }
        return new AegisSettings(initialEnemies, windowTicks, reductionPercent);
    }

    private BloodLustSettings getBloodLustSettings(Player player) {
        EntityEquipment equipment = player == null ? null : player.getEquipment();
        ItemStack chestplate = equipment == null ? null : equipment.getChestplate();
        int level = getEnchantLevelOnItem("bloodlust", "bloodlust", chestplate);
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, 6);
        String[] args = getEnchantEffectArguments("bloodlust", safeLevel, "BLOOD_LUST");
        double chance = getDefaultBloodLustChance(safeLevel);
        double heal = 1.0D;
        double radius = 7.0D;
        if (args.length >= 1) {
            chance = clampPercent(parseDouble(args[0], chance));
        }
        if (args.length >= 2) {
            heal = Math.max(0.0D, Math.min(20.0D, parseDouble(args[1], heal)));
        }
        if (args.length >= 3) {
            radius = Math.max(0.5D, Math.min(32.0D, parseDouble(args[2], radius)));
        }
        return new BloodLustSettings(chance, heal, radius);
    }

    private BloodLinkSettings getBloodLinkSettings(Player player) {
        int level = getHighestArmorEnchantLevel(player, "bloodlink", "bloodlink");
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, 5);
        String[] args = getEnchantEffectArguments("bloodlink", safeLevel, "BLOOD_LINK");
        double chance = safeLevel * 5.0D;
        double minHeal = 1.0D;
        double maxHeal = 2.0D;
        if (args.length >= 1) {
            chance = clampPercent(parseDouble(args[0], chance));
        }
        if (args.length >= 2) {
            minHeal = Math.max(0.0D, Math.min(20.0D, parseDouble(args[1], minHeal)));
        }
        if (args.length >= 3) {
            maxHeal = Math.max(minHeal, Math.min(20.0D, parseDouble(args[2], maxHeal)));
        }
        return new BloodLinkSettings(chance, minHeal, maxHeal);
    }

    private static double getDefaultBloodLustChance(int level) {
        switch (clamp(level, 1, 6)) {
            case 1:
                return 5.0D;
            case 2:
                return 10.0D;
            case 3:
                return 15.0D;
            case 4:
                return 20.0D;
            case 5:
                return 25.0D;
            default:
                return 30.0D;
        }
    }

    private void triggerBloodLustHeals(Player bleedVictim, LivingEntity source, double bleedDamage) {
        if (bleedVictim == null || bleedVictim.isDead() || !bleedVictim.isValid() || bleedVictim.getWorld() == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null
                    || player.isDead()
                    || !player.isValid()
                    || player.getUniqueId().equals(bleedVictim.getUniqueId())
                    || !player.getWorld().equals(bleedVictim.getWorld())) {
                continue;
            }

            BloodLustSettings settings = getBloodLustSettings(player);
            if (settings == null || settings.healAmount() <= 0.0D || settings.chance() <= 0.0D) {
                continue;
            }

            double radiusSquared = settings.radius() * settings.radius();
            if (player.getLocation().distanceSquared(bleedVictim.getLocation()) > radiusSquared
                    || !rollChance(settings.chance())) {
                continue;
            }

            double beforeHealth = player.getHealth();
            healLivingEntity(player, settings.healAmount());
            sendBloodLustMessage(player, bleedVictim, source, player.getHealth() - beforeHealth, bleedDamage);
        }
    }

    private void sendBloodLustMessage(Player player, Player bleedVictim, LivingEntity source,
                                      double healedAmount, double bleedDamage) {
        if (healedAmount <= 0.0D) {
            return;
        }

        String message = getEnchantsConfig().getString("blood-lust.message", "");
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String sourceName = source == null ? "" : source.getName();
        player.sendMessage(colorize(message
                .replace("%amount%", formatHpAmount(healedAmount))
                .replace("%heal%", formatHpAmount(healedAmount))
                .replace("%damage%", formatHpAmount(bleedDamage))
                .replace("%victim name%", bleedVictim.getName())
                .replace("%victimname%", bleedVictim.getName())
                .replace("%attacker name%", sourceName)
                .replace("%attackername%", sourceName)));
    }

    private void sendBloodLinkMessage(Player player, LivingEntity guardian, double healedAmount) {
        if (healedAmount <= 0.0D) {
            return;
        }

        String message = getEnchantsConfig().getString("blood-link.message", "");
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String guardianName = guardian == null ? "" : guardian.getName();
        player.sendMessage(colorize(message
                .replace("%amount%", formatHpAmount(healedAmount))
                .replace("%heal%", formatHpAmount(healedAmount))
                .replace("%guardian name%", guardianName)
                .replace("%guardianname%", guardianName)));
    }

    private Player resolveGuardianOwner(Entity guardian) {
        if (guardian == null) {
            return null;
        }

        UUID ownerId = obsidianGuardianOwners.get(guardian.getUniqueId());
        if (ownerId == null) {
            ownerId = nativeGuardianOwners.get(guardian.getUniqueId());
        }
        if (ownerId == null && guardian.hasMetadata(ADDON_SUMMON_OWNER_METADATA)) {
            for (org.bukkit.metadata.MetadataValue value : guardian.getMetadata(ADDON_SUMMON_OWNER_METADATA)) {
                if (value == null || value.getOwningPlugin() != this) {
                    continue;
                }
                try {
                    ownerId = UUID.fromString(String.valueOf(value.value()));
                    break;
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
        }
        return ownerId == null ? null : Bukkit.getPlayer(ownerId);
    }

    private LivingEntity resolveBleedDamageSource(Player target, EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            LivingEntity damager = getLivingDamager(((EntityDamageByEntityEvent) event).getDamager());
            if (damager != null) {
                return damager;
            }
        }

        if (target == null || !target.hasMetadata(BLEED_DAMAGE_SOURCE_METADATA)) {
            return null;
        }

        for (org.bukkit.metadata.MetadataValue value : target.getMetadata(BLEED_DAMAGE_SOURCE_METADATA)) {
            if (value == null || value.getOwningPlugin() != this) {
                continue;
            }
            try {
                Entity entity = Bukkit.getEntity(UUID.fromString(String.valueOf(value.value())));
                if (entity instanceof LivingEntity) {
                    return (LivingEntity) entity;
                }
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private void damageDestructionAuraTargets(LivingEntity wearer, LivingEntity source,
                                              double damagePerTick, double radius,
                                              java.util.Set<UUID> messagedTargets) {
        Location center = wearer.getLocation();
        double radiusSquared = radius * radius;
        for (Entity nearby : wearer.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(nearby instanceof LivingEntity)) {
                continue;
            }

            LivingEntity target = (LivingEntity) nearby;
            if (target.getUniqueId().equals(wearer.getUniqueId())
                    || target.isDead()
                    || !target.isValid()
                    || isSummonedEntity(target)
                    || target.getLocation().distanceSquared(center) > radiusSquared) {
                continue;
            }

            removeDestructionBuffs(target);
            if (damagePerTick > 0.0D) {
                damageDestructionWithoutKnockback(target, source, damagePerTick);
            }
        }
    }

    private void damageDestructionWithoutKnockback(LivingEntity target, Entity damager, double damage) {
        if (target == null || target.isDead() || !target.isValid() || damage <= 0.0D) {
            return;
        }

        target.setMetadata("advancedenchantmentsaddon_tick_damage",
                new org.bukkit.metadata.FixedMetadataValue(this, true));
        target.setMetadata(DESTRUCTION_DAMAGE_METADATA,
                new org.bukkit.metadata.FixedMetadataValue(this, true));
        if (target instanceof Player) {
            destructionVelocitySuppressions.put(target.getUniqueId(), serverTick + 2L);
        }
        try {
            damageWithoutKnockback(target, null, damage);
        } finally {
            target.removeMetadata("advancedenchantmentsaddon_tick_damage", this);
            target.removeMetadata(DESTRUCTION_DAMAGE_METADATA, this);
        }
    }

    private void damageBleedWithoutKnockback(LivingEntity target, Entity damager, double damage) {
        if (target == null || target.isDead() || !target.isValid() || damage <= 0.0D) {
            return;
        }

        target.setMetadata("advancedenchantmentsaddon_tick_damage",
                new org.bukkit.metadata.FixedMetadataValue(this, true));
        target.setMetadata(BLEED_DAMAGE_METADATA, new org.bukkit.metadata.FixedMetadataValue(this, true));
        if (damager != null) {
            target.setMetadata(BLEED_DAMAGE_SOURCE_METADATA,
                    new org.bukkit.metadata.FixedMetadataValue(this, damager.getUniqueId().toString()));
        }
        try {
            damageWithoutKnockback(target, null, damage);
        } finally {
            target.removeMetadata("advancedenchantmentsaddon_tick_damage", this);
            target.removeMetadata(BLEED_DAMAGE_METADATA, this);
            target.removeMetadata(BLEED_DAMAGE_SOURCE_METADATA, this);
        }
    }

    private void removeDestructionBuffs(LivingEntity target) {
        List<String> configuredEffects = getEnchantsConfig().getStringList("destruction.removed-effects");
        if (configuredEffects.isEmpty() && !getEnchantsConfig().contains("destruction.removed-effects")) {
            configuredEffects = Arrays.asList("INCREASE_DAMAGE", "REGENERATION", "ABSORPTION", "HASTE");
        }

        for (String effectName : configuredEffects) {
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                target.removePotionEffect(type);
            }
        }
    }

    private int resolveRageCombo(LivingEntity attacker, LivingEntity victim, int maxStacks) {
        if (attacker == null || victim == null || maxStacks <= 0) {
            return 0;
        }

        long resetTicks = Math.max(1L, getEnchantsConfig().getLong("rage.combo-reset-ticks", 60L));
        UUID attackerId = attacker.getUniqueId();
        UUID victimId = victim.getUniqueId();
        RageComboState state = rageCombos.get(attackerId);
        if (state == null || !state.victimId().equals(victimId) || state.expired(serverTick, resetTicks)) {
            state = new RageComboState(victimId, 0, -1L);
            rageCombos.put(attackerId, state);
        }

        if (state.lastTick() != serverTick) {
            state.increment(maxStacks, serverTick);
        }
        return state.count();
    }

    private boolean isSummonedEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        UUID entityId = entity.getUniqueId();
        return entity.hasMetadata(ADDON_SUMMON_METADATA)
                || addonSummonIds.contains(entityId)
                || visualSpiritIds.contains(entityId)
                || ruseZombieOwners.containsKey(entityId)
                || ruseZombieTargets.containsKey(entityId)
                || obsidianGuardianOwners.containsKey(entityId)
                || obsidianGuardianTargets.containsKey(entityId)
                || obsidianGuardianSettings.containsKey(entityId)
                || obsidianGuardianAttackReadyTicks.containsKey(entityId)
                || epidemicCarrierSettings.containsKey(entityId)
                || isNativeAeGuard(entity);
    }

    private boolean isEpidemicCarrier(Entity entity) {
        return entity != null && epidemicCarrierSettings.containsKey(entity.getUniqueId());
    }

    private boolean isEnemyEpidemicCarrierDamage(Player player, Entity damager) {
        if (player == null || damager == null) {
            return false;
        }

        EpidemicCarrierSettings settings = epidemicCarrierSettings.get(damager.getUniqueId());
        return settings != null && !player.getUniqueId().equals(settings.ownerId());
    }

    private CreeperArmorSettings getCreeperArmorSettings(Player player) {
        int level = getHighestEquippedCreeperArmorLevel(player);
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, 3);
        String[] args = getEnchantEffectArguments("creeperarmor", safeLevel, "CREEPER_ARMOR");
        if (args.length >= 4) {
            double minHeal = Math.max(0.0D, parseDouble(args[1], getDefaultCreeperArmorMinHeal(safeLevel)));
            double maxHeal = Math.max(minHeal, parseDouble(args[2], getDefaultCreeperArmorMaxHeal(safeLevel)));
            return new CreeperArmorSettings(
                    getDefaultCreeperArmorExplosionChance(safeLevel),
                    clampPercent(parseDouble(args[0], getDefaultCreeperArmorHealChance(safeLevel))),
                    minHeal,
                    maxHeal,
                    parseBooleanFlag(args[3], safeLevel >= 2));
        }

        return new CreeperArmorSettings(
                getDefaultCreeperArmorExplosionChance(safeLevel),
                getDefaultCreeperArmorHealChance(safeLevel),
                getDefaultCreeperArmorMinHeal(safeLevel),
                getDefaultCreeperArmorMaxHeal(safeLevel),
                safeLevel >= 2);
    }

    private int getHighestEquippedCreeperArmorLevel(Player player) {
        if (player == null || player.getInventory() == null) {
            return 0;
        }

        int highest = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            highest = Math.max(highest, getEnchantLevelOnItem("creeperarmor", "creeperarmor", armor));
        }
        return highest;
    }

    private static double getDefaultCreeperArmorExplosionChance(int level) {
        return 100.0D;
    }

    private static double getDefaultCreeperArmorHealChance(int level) {
        return clamp(level, 1, 3) >= 3 ? 35.0D : 0.0D;
    }

    private static double getDefaultCreeperArmorMinHeal(int level) {
        return clamp(level, 1, 3) >= 3 ? 1.0D : 0.0D;
    }

    private static double getDefaultCreeperArmorMaxHeal(int level) {
        return clamp(level, 1, 3) >= 3 ? 5.0D : 0.0D;
    }

    private CustomCreeperArmorSettings getCustomCreeperArmorSettings(Player player) {
        int level = getHighestEquippedCustomCreeperArmorLevel(player);
        if (level <= 0) {
            return null;
        }

        int safeLevel = clamp(level, 1, 3);
        String[] args = getEnchantEffectArguments("customcreeperarmor", safeLevel, "CUSTOM_CREEPER_ARMOR");
        if (args.length >= 4) {
            return new CustomCreeperArmorSettings(
                    clampPercent(parseDouble(args[0], getDefaultCustomCreeperArmorExplosionChance(safeLevel))),
                    clampPercent(parseDouble(args[1], getDefaultCustomCreeperArmorCustomChance(safeLevel))),
                    clampPercent(parseDouble(args[2], getDefaultCustomCreeperArmorSlownessChance(safeLevel))),
                    parseBooleanFlag(args[3], safeLevel >= 2));
        }

        return new CustomCreeperArmorSettings(
                getDefaultCustomCreeperArmorExplosionChance(safeLevel),
                getDefaultCustomCreeperArmorCustomChance(safeLevel),
                getDefaultCustomCreeperArmorSlownessChance(safeLevel),
                safeLevel >= 2);
    }

    private int getHighestEquippedCustomCreeperArmorLevel(Player player) {
        if (player == null || player.getInventory() == null) {
            return 0;
        }

        int highest = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            highest = Math.max(highest, getEnchantLevelOnItem("customcreeperarmor", "customcreeperarmor", armor));
        }
        return highest;
    }

    private static double getDefaultCustomCreeperArmorExplosionChance(int level) {
        return 100.0D;
    }

    private static double getDefaultCustomCreeperArmorCustomChance(int level) {
        switch (clamp(level, 1, 3)) {
            case 1:
                return 35.0D;
            case 2:
                return 70.0D;
            default:
                return 100.0D;
        }
    }

    private static double getDefaultCustomCreeperArmorSlownessChance(int level) {
        switch (clamp(level, 1, 3)) {
            case 1:
                return 40.0D;
            case 2:
                return 70.0D;
            default:
                return 100.0D;
        }
    }

    private static double clampPercent(double value) {
        if (!Double.isFinite(value)) {
            return 0.0D;
        }
        return Math.max(0.0D, Math.min(100.0D, value));
    }

    private static boolean parseBooleanFlag(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = normalize(value);
        if (normalized.equals("true") || normalized.equals("yes") || normalized.equals("on") || normalized.equals("1")) {
            return true;
        }
        if (normalized.equals("false") || normalized.equals("no") || normalized.equals("off") || normalized.equals("0")) {
            return false;
        }
        return fallback;
    }

    private boolean isVisualSpirit(Entity entity) {
        return entity != null && visualSpiritIds.contains(entity.getUniqueId());
    }

    private boolean isVisualSpiritDamageSource(Entity damager) {
        if (isVisualSpirit(damager)) {
            return true;
        }
        if (!(damager instanceof Projectile)) {
            return false;
        }

        ProjectileSource shooter = ((Projectile) damager).getShooter();
        return shooter instanceof Entity && isVisualSpirit((Entity) shooter);
    }

    private boolean isNativeAeGuard(Entity entity) {
        try {
            Class<?> guardEffectClass = Class.forName(
                    "net.advancedplugins.ae.impl.effects.effects.effects.internal.GuardEffect");
            Method getGuardEffect = guardEffectClass.getMethod("getGuardEffect");
            Object guardEffect = getGuardEffect.invoke(null);
            if (guardEffect == null) {
                return false;
            }
            Method isGuard = guardEffectClass.getMethod("isGuard", Entity.class);
            Object result = isGuard.invoke(guardEffect, entity);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private PendingSummonProtection findPendingSummonProtection(EntityType entityType, Location location) {
        if (entityType == null || location == null || location.getWorld() == null) {
            return null;
        }

        pendingSummonProtections.removeIf(protection -> protection.expired(serverTick));
        for (PendingSummonProtection protection : pendingSummonProtections) {
            if (protection.matches(entityType, location)) {
                return protection;
            }
        }
        return null;
    }

    private void markEntityAsAddonSummon(Entity entity, boolean hideNameUntilNextTick) {
        markEntityAsAddonSummon(entity, hideNameUntilNextTick, null);
    }

    private void markEntityAsAddonSummon(Entity entity, boolean hideNameUntilNextTick, UUID ownerId) {
        if (entity == null) {
            return;
        }

        addonSummonIds.add(entity.getUniqueId());
        entity.setMetadata(ADDON_SUMMON_METADATA, new org.bukkit.metadata.FixedMetadataValue(this, true));
        if (ownerId != null) {
            nativeGuardianOwners.put(entity.getUniqueId(), ownerId);
            entity.setMetadata(ADDON_SUMMON_OWNER_METADATA,
                    new org.bukkit.metadata.FixedMetadataValue(this, ownerId.toString()));
        }
        if (!hideNameUntilNextTick) {
            return;
        }

        String previousName = entity.getCustomName();
        boolean previousVisible = entity.isCustomNameVisible();
        entity.setCustomName(ADDON_SUMMON_HIDDEN_NAME);
        entity.setCustomNameVisible(false);
        Bukkit.getScheduler().runTask(this, () -> {
            if (!entity.isValid()) {
                return;
            }
            if (ADDON_SUMMON_HIDDEN_NAME.equals(entity.getCustomName())) {
                entity.setCustomName(previousName);
                entity.setCustomNameVisible(previousVisible);
            }
        });
    }

    private void cancelEliteMobSpawnForSummon(Event event) {
        if (!(event instanceof Cancellable)) {
            return;
        }

        try {
            Method getEntity = event.getClass().getMethod("getEntity");
            Object entity = getEntity.invoke(event);
            if (entity instanceof Entity && isSummonedEntity((Entity) entity)) {
                ((Cancellable) event).setCancelled(true);
            }
        } catch (ReflectiveOperationException exception) {
            getLogger().warning("Could not inspect EliteMobs spawn event: " + exception.getMessage());
        }
    }

    private void startDimensionalShift(LivingEntity caster, LivingEntity target, String rawMessage,
                                       int bursts, double spawnHeight, int layerDelayTicks, boolean sendMessage) {
        if (target == null || target.isDead() || !target.isValid()) {
            return;
        }

        FileConfiguration setConfig = getSetsConfig();
        int maxBursts = Math.max(1, setConfig.getInt("dimensional-shift.max-bursts", 5));
        int safeBursts = Math.max(1, Math.min(maxBursts, bursts));
        double safeSpawnHeight = Math.max(2.0D, Math.min(20.0D, spawnHeight));
        int safeLayerDelayTicks = clamp(layerDelayTicks, 1, 100);
        double damagePerBurst = Math.max(0.0D, setConfig.getDouble("dimensional-shift.damage-per-burst", 4.0D));
        int freezeTicks = Math.max(0, setConfig.getInt("dimensional-shift.freeze-ticks", 80));
        int unfreezeDelayTicks = Math.max(0, setConfig.getInt("dimensional-shift.unfreeze-delay-ticks", 2));
        double spawnHeightBonus = Math.max(0.0D, Math.min(20.0D,
                setConfig.getDouble("dimensional-shift.spawn-height-bonus", 3.0D)));
        int patchSize = clamp(setConfig.getInt("dimensional-shift.patch-size", 6), 1, 12);
        double collisionRadius = Math.max(0.25D, Math.min(6.0D,
                setConfig.getDouble("dimensional-shift.collision-radius", 2.0D)));
        int fallTimeoutTicks = clamp(setConfig.getInt("dimensional-shift.fall-timeout-ticks", 80), 1, 200);
        List<Material> materials = getDimensionalShiftMaterials();

        if (sendMessage) {
            sendDimensionalShiftMessage(caster, target, rawMessage);
        }
        for (int burst = 0; burst < safeBursts; burst++) {
            final int burstIndex = burst;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (target.isDead() || !target.isValid()) {
                    return;
                }

                List<FallingBlock> blocks = spawnDimensionalShiftBurst(
                        target, safeSpawnHeight, spawnHeightBonus, patchSize, materials);
                monitorDimensionalShiftBurst(
                        caster, target, blocks, burstIndex >= safeBursts - 1,
                        damagePerBurst, freezeTicks, unfreezeDelayTicks, fallTimeoutTicks, collisionRadius);
            }, (long) burst * safeLayerDelayTicks);
        }

        Bukkit.getScheduler().runTaskLater(this, () -> unfreezeDimensionalShiftTarget(target),
                20L + (long) safeBursts * safeLayerDelayTicks + fallTimeoutTicks);
    }

    private List<FallingBlock> spawnDimensionalShiftBurst(LivingEntity target, double spawnHeight,
                                                          double spawnHeightBonus, int patchSize,
                                                          List<Material> materials) {
        List<FallingBlock> blocks = new java.util.ArrayList<>();
        World world = target.getWorld();
        Location center = target.getLocation().add(0.0D, spawnHeight + spawnHeightBonus, 0.0D);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int half = patchSize / 2;
        int min = -half;
        int max = patchSize - half;
        double offset = patchSize % 2 == 0 ? 0.5D : 0.0D;
        double innerRadius = Math.max(1.0D, patchSize * 0.25D);

        for (int x = min; x < max; x++) {
            for (int z = min; z < max; z++) {
                double distance = Math.sqrt((x + offset) * (x + offset) + (z + offset) * (z + offset));
                double keepChance = distance <= innerRadius ? 0.95D : 0.62D;
                if (random.nextDouble() > keepChance) {
                    continue;
                }

                Material material = materials.get(random.nextInt(materials.size()));
                BlockData blockData = material.createBlockData();
                Location spawn = center.clone().add(x + offset, random.nextDouble(-0.10D, 0.10D), z + offset);
                FallingBlock block = world.spawnFallingBlock(spawn, blockData);
                block.setDropItem(false);
                block.setHurtEntities(false);
                block.setVelocity(new org.bukkit.util.Vector(
                        random.nextDouble(-0.015D, 0.015D),
                        -0.03D,
                        random.nextDouble(-0.015D, 0.015D)));
                dimensionalShiftBlocks.put(block.getUniqueId(), block);
                blocks.add(block);
            }
        }

        if (blocks.isEmpty()) {
            FallingBlock block = world.spawnFallingBlock(center, Material.END_STONE.createBlockData());
            block.setDropItem(false);
            block.setHurtEntities(false);
            dimensionalShiftBlocks.put(block.getUniqueId(), block);
            blocks.add(block);
        }
        return blocks;
    }

    private void monitorDimensionalShiftBurst(LivingEntity caster, LivingEntity target,
                                              List<FallingBlock> blocks, boolean finalBurst,
                                              double damagePerBurst, int freezeTicks,
                                              int unfreezeDelayTicks, int fallTimeoutTicks,
                                              double collisionRadius) {
        new BukkitRunnable() {
            private int ticks;
            private boolean hit;

            @Override
            public void run() {
                ticks++;
                if (target.isDead() || !target.isValid() || ticks > fallTimeoutTicks) {
                    cleanupDimensionalShiftBlocks(blocks);
                    if (finalBurst) {
                        unfreezeDimensionalShiftTarget(target);
                    }
                    cancel();
                    return;
                }

                blocks.removeIf(block -> block == null || block.isDead() || !block.isValid());
                if (!hit && blocks.stream().anyMatch(block -> isDimensionalShiftCollision(block, target, collisionRadius))) {
                    hit = true;
                    freezeDimensionalShiftTarget(target, freezeTicks);
                    damageWithoutKnockback(target, caster, damagePerBurst);
                    playDimensionalShiftHitSounds(target.getLocation());
                    cleanupDimensionalShiftBlocks(blocks);
                    if (finalBurst) {
                        Bukkit.getScheduler().runTaskLater(AdvancedEnchantmentsAddonPlugin.this,
                                () -> unfreezeDimensionalShiftTarget(target), unfreezeDelayTicks);
                    }
                    cancel();
                    return;
                }

                if (blocks.isEmpty()) {
                    if (finalBurst) {
                        unfreezeDimensionalShiftTarget(target);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    private boolean isDimensionalShiftCollision(FallingBlock block, LivingEntity target, double collisionRadius) {
        if (block == null || target == null || !block.getWorld().equals(target.getWorld())) {
            return false;
        }

        Location blockLocation = block.getLocation();
        Location targetLocation = target.getLocation();
        double dx = blockLocation.getX() - targetLocation.getX();
        double dz = blockLocation.getZ() - targetLocation.getZ();
        double horizontalDistanceSquared = dx * dx + dz * dz;
        return horizontalDistanceSquared <= collisionRadius * collisionRadius
                && blockLocation.getY() <= targetLocation.getY() + 2.25D
                && blockLocation.getY() >= targetLocation.getY() - 1.0D;
    }

    private void cleanupDimensionalShiftBlocks(List<FallingBlock> blocks) {
        for (FallingBlock block : new java.util.ArrayList<>(blocks)) {
            if (block == null) {
                continue;
            }
            dimensionalShiftBlocks.remove(block.getUniqueId());
            if (block.isValid()) {
                block.remove();
            }
        }
        blocks.clear();
    }

    private List<Material> getDimensionalShiftMaterials() {
        List<Material> materials = new java.util.ArrayList<>();
        for (String name : getSetsConfig().getStringList("dimensional-shift.materials")) {
            Material material = Material.matchMaterial(name);
            if (material != null && material.isBlock()) {
                materials.add(material);
            }
        }

        if (materials.isEmpty()) {
            materials.add(Material.END_STONE);
            materials.add(Material.NETHERRACK);
        }
        return materials;
    }

    private void playDimensionalShiftHitSounds(Location location) {
        FileConfiguration setConfig = getSetsConfig();
        List<String> soundSpecs = setConfig.getStringList("dimensional-shift.hit-sounds");
        if (soundSpecs.isEmpty()
                && !setConfig.isSet("dimensional-shift.hit-sounds")
                && getConfig().contains("sounds.dimensional-shift-hit")) {
            soundSpecs = getSoundSpecs("sounds.dimensional-shift-hit");
        }
        for (String soundSpec : soundSpecs) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void playDimensionalShiftActivationSounds(Location location) {
        for (String soundSpec : getSetsConfig().getStringList("dimensional-shift.activation-sounds")) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void freezeDimensionalShiftTarget(LivingEntity target, int freezeTicks) {
        if (target != null && target.isValid() && freezeTicks > 0) {
            if (getSetsConfig().getBoolean("dimensional-shift.show-vanilla-freeze-effect", false)) {
                target.setFreezeTicks(Math.max(target.getFreezeTicks(), freezeTicks));
            }
            if (target instanceof Player) {
                dimensionalShiftFrozenTargets.put(target.getUniqueId(), target.getLocation().clone());
            }
        }
    }

    private void unfreezeDimensionalShiftTarget(LivingEntity target) {
        if (target != null && target.isValid()) {
            dimensionalShiftFrozenTargets.remove(target.getUniqueId());
            target.setFreezeTicks(0);
        }
    }

    private void sendDimensionalShiftMessage(LivingEntity caster, LivingEntity target, String rawMessage) {
        String resolvedMessage = resolveDimensionalShiftMessage(rawMessage);
        if (resolvedMessage == null || resolvedMessage.trim().isEmpty()) {
            return;
        }

        String casterName = caster == null ? "" : caster.getName();
        String targetName = target == null ? "" : target.getName();
        String message = colorize(resolvedMessage
                .replace("%attacker name%", casterName)
                .replace("%attackername%", casterName)
                .replace("%attacker%", casterName)
                .replace("%victim name%", targetName)
                .replace("%victimname%", targetName)
                .replace("%victim%", targetName));

        Location center = caster == null ? target.getLocation() : caster.getLocation();
        playDimensionalShiftActivationSounds(center);
        double radius = Math.max(1.0D, getSetsConfig().getDouble("dimensional-shift.message-radius-blocks", 16.0D));
        double radiusSquared = radius * radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(center.getWorld())
                    && player.getLocation().distanceSquared(center) <= radiusSquared) {
                player.sendMessage(message);
            }
        }
    }

    private String resolveDimensionalShiftMessage(String rawMessage) {
        if (rawMessage == null) {
            return "";
        }

        String trimmed = rawMessage.trim();
        if (trimmed.equalsIgnoreCase("default") || trimmed.equalsIgnoreCase("config")) {
            return getSetsConfig().getString("dimensional-shift.message",
                    "&5&l*** DIMENSIONAL SHIFT <&c&l%victim name%&5&l> ***");
        }

        return trimmed
                .replace("%space%", " ")
                .replace("{space}", " ");
    }

    private boolean triggerConfiguredDimensionalShift(LivingEntity caster, List<LivingEntity> targets, String rawMessage) {
        if (caster == null || !caster.isValid() || caster.isDead() || targets == null || targets.isEmpty()) {
            return false;
        }

        FileConfiguration setConfig = getSetsConfig();
        String message = rawMessage == null || rawMessage.trim().isEmpty() ? "default" : rawMessage;
        int bursts = Math.max(1, setConfig.getInt("dimensional-shift.bursts", 2));
        double spawnHeight = Math.max(0.0D, setConfig.getDouble("dimensional-shift.spawn-height", 5.0D));
        int layerDelayTicks = setConfig.getInt("dimensional-shift.layer-delay-ticks", 14);

        sendDimensionalShiftMessage(caster, targets.get(0), message);
        for (LivingEntity shiftTarget : targets) {
            if (shiftTarget != null && shiftTarget.isValid() && !shiftTarget.isDead()) {
                startDimensionalShift(caster, shiftTarget, message, bursts, spawnHeight, layerDelayTicks, false);
            }
        }
        return true;
    }

    private List<LivingEntity> resolveDimensionalShiftTargets(LivingEntity caster, LivingEntity fallbackTarget) {
        double radius = Math.max(0.0D, getSetsConfig().getDouble("dimensional-shift.target-radius-blocks", 0.0D));
        if (radius <= 0.0D || caster == null || caster.getWorld() == null) {
            if (!isValidDimensionalShiftTarget(fallbackTarget, caster)) {
                return Collections.emptyList();
            }
            return Collections.singletonList(fallbackTarget);
        }

        List<LivingEntity> targets = new java.util.ArrayList<>();
        Location center = caster.getLocation();
        double radiusSquared = radius * radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isValidDimensionalShiftTarget(player, caster)) {
                continue;
            }
            if (!player.getWorld().equals(center.getWorld())) {
                continue;
            }
            if (player.getLocation().distanceSquared(center) <= radiusSquared) {
                targets.add(player);
            }
        }
        return targets;
    }

    private static boolean isValidDimensionalShiftTarget(LivingEntity target, LivingEntity caster) {
        return target != null
                && target.isValid()
                && !target.isDead()
                && (caster == null || !target.getUniqueId().equals(caster.getUniqueId()));
    }

    private boolean triggerConfiguredWinterMercy(LivingEntity caster, LivingEntity target, String rawMessage) {
        if (target == null || !target.isValid() || target.isDead()) {
            return false;
        }

        LivingEntity safeCaster = caster != null && caster.isValid() && !caster.isDead() ? caster : target;
        FileConfiguration setConfig = getSetsConfig();
        int durationTicks = clamp(setConfig.getInt("winter-mercy.duration-ticks", 200), 1, 1200);
        double radius = Math.max(0.5D, Math.min(12.0D, setConfig.getDouble("winter-mercy.radius-blocks", 4.0D)));
        double spawnHeight = Math.max(2.0D, Math.min(24.0D,
                setConfig.getDouble("winter-mercy.snowball-spawn-height", 7.0D)));
        int snowballsPerLayer = clamp(setConfig.getInt("winter-mercy.snowballs-per-layer",
                setConfig.getInt("winter-mercy.snowballs-per-wave", 18)), 1, 80);
        int fallTimeoutTicks = clamp(setConfig.getInt("winter-mercy.snowball-fall-timeout-ticks", 60), 5, 200);
        double snowballFallSpeed = Math.max(0.05D, Math.min(3.0D,
                setConfig.getDouble("winter-mercy.snowball-fall-speed", 0.75D)));
        double snowballHorizontalDrift = Math.max(0.0D, Math.min(1.0D,
                setConfig.getDouble("winter-mercy.snowball-horizontal-drift", 0.04D)));

        sendWinterMercyMessage(safeCaster, target, rawMessage);
        applyWinterMercyReduction(target);
        if (setConfig.getBoolean("winter-mercy.snow-ground", true)) {
            applyWinterMercySnowPatch(target, radius, durationTicks);
        }
        spawnWinterMercySnowballLayer(target, radius, spawnHeight, snowballsPerLayer, fallTimeoutTicks,
                snowballFallSpeed, snowballHorizontalDrift);
        return true;
    }

    private void spawnWinterMercySnowballLayer(LivingEntity target, double radius, double spawnHeight,
                                               int snowballsPerLayer, int fallTimeoutTicks,
                                               double fallSpeed, double horizontalDrift) {
        if (target == null || target.isDead() || !target.isValid()) {
            return;
        }

        World world = target.getWorld();
        Location center = target.getLocation();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < snowballsPerLayer; index++) {
            double angle = random.nextDouble(0.0D, Math.PI * 2.0D);
            double distance = index == 0 ? 0.0D : Math.sqrt(random.nextDouble()) * radius;
            double x = Math.cos(angle) * distance;
            double z = Math.sin(angle) * distance;
            Location spawn = center.clone().add(x, spawnHeight + random.nextDouble(-0.25D, 0.75D), z);
            Snowball snowball = world.spawn(spawn, Snowball.class);
            snowball.setMetadata(WINTER_MERCY_SNOWBALL_METADATA,
                    new org.bukkit.metadata.FixedMetadataValue(this, target.getUniqueId().toString()));
            snowball.setVelocity(new org.bukkit.util.Vector(
                    random.nextDouble(-horizontalDrift, horizontalDrift),
                    -fallSpeed,
                    random.nextDouble(-horizontalDrift, horizontalDrift)));
            winterMercySnowballs.put(snowball.getUniqueId(), target.getUniqueId());

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (winterMercySnowballs.remove(snowball.getUniqueId()) != null && snowball.isValid()) {
                    snowball.removeMetadata(WINTER_MERCY_SNOWBALL_METADATA, this);
                    snowball.remove();
                }
            }, fallTimeoutTicks);
        }
    }

    private UUID getWinterMercySnowballTarget(Entity entity) {
        if (entity == null) {
            return null;
        }

        UUID targetId = winterMercySnowballs.get(entity.getUniqueId());
        if (targetId != null) {
            return targetId;
        }
        if (!entity.hasMetadata(WINTER_MERCY_SNOWBALL_METADATA)) {
            return null;
        }

        for (org.bukkit.metadata.MetadataValue value : entity.getMetadata(WINTER_MERCY_SNOWBALL_METADATA)) {
            if (!this.equals(value.getOwningPlugin())) {
                continue;
            }
            try {
                return UUID.fromString(value.asString());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private void applyWinterMercyReduction(LivingEntity target) {
        FileConfiguration setConfig = getSetsConfig();
        double percent = Math.max(0.0D, Math.min(100.0D,
                setConfig.getDouble("winter-mercy.damage-reduction-percent", 10.0D)));
        int durationTicks = clamp(setConfig.getInt("winter-mercy.duration-ticks", 200), 1, 1200);
        applyWinterMercyReduction(target, percent, durationTicks);
    }

    private void applyWinterMercyReduction(LivingEntity target, double percent, int durationTicks) {
        if (target == null || !target.isValid() || target.isDead() || percent <= 0.0D) {
            return;
        }

        long expiresAtMillis = System.currentTimeMillis() + (long) durationTicks * 50L;
        WinterMercyReduction existing = winterMercyReductions.get(target.getUniqueId());
        if (existing != null && existing.expiresAtMillis() > expiresAtMillis && existing.percent() >= percent) {
            return;
        }
        winterMercyReductions.put(target.getUniqueId(), new WinterMercyReduction(percent, expiresAtMillis));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            WinterMercyReduction current = winterMercyReductions.get(target.getUniqueId());
            if (current != null && current.expiresAtMillis() <= expiresAtMillis) {
                winterMercyReductions.remove(target.getUniqueId());
            }
        }, durationTicks + 1L);
    }

    private void applyWinterMercySnowPatch(LivingEntity target, double radius, int durationTicks) {
        if (target == null || !target.isValid() || target.getWorld() == null) {
            return;
        }

        restoreWinterMercySnowPatch(target.getUniqueId());
        List<WinterMercySnowBlock> snapshots = new java.util.ArrayList<>();
        Location center = target.getLocation();
        World world = target.getWorld();
        List<Material> groundMaterials = getWinterMercyGroundMaterials();
        int blockRadius = (int) Math.ceil(radius);
        int baseY = center.getBlockY() - 1;
        double radiusSquared = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int x = center.getBlockX() - blockRadius; x <= center.getBlockX() + blockRadius; x++) {
            for (int z = center.getBlockZ() - blockRadius; z <= center.getBlockZ() + blockRadius; z++) {
                double dx = (x + 0.5D) - center.getX();
                double dz = (z + 0.5D) - center.getZ();
                if (dx * dx + dz * dz > radiusSquared) {
                    continue;
                }

                Block block = world.getBlockAt(x, baseY, z);
                if (isWinterMercyTemporaryBlock(block) || !canWinterMercyReplaceBlock(block)) {
                    continue;
                }

                Material temporaryMaterial = groundMaterials.get(random.nextInt(groundMaterials.size()));
                WinterMercySnowBlock snapshot = new WinterMercySnowBlock(
                        world.getUID(), x, baseY, z, block.getBlockData(), temporaryMaterial);
                snapshots.add(snapshot);
                trackWinterMercyTemporaryBlock(snapshot);
                block.setType(temporaryMaterial, false);
            }
        }

        if (snapshots.isEmpty()) {
            return;
        }

        winterMercySnowPatches.put(target.getUniqueId(), snapshots);
        List<WinterMercySnowBlock> appliedSnapshots = snapshots;
        Bukkit.getScheduler().runTaskLater(this,
                () -> restoreWinterMercySnowPatch(target.getUniqueId(), appliedSnapshots), durationTicks);
    }

    private static boolean canWinterMercyReplaceBlock(Block block) {
        if (block == null) {
            return false;
        }

        Material type = block.getType();
        if (!type.isBlock() || !type.isSolid() || type == Material.BEDROCK || type == Material.BARRIER) {
            return false;
        }
        if (block.getState() instanceof org.bukkit.block.TileState) {
            return false;
        }

        String name = type.name();
        return !name.contains("CHEST")
                && !name.contains("SHULKER")
                && !name.contains("BARREL")
                && !name.contains("SIGN")
                && !name.contains("DOOR")
                && !name.contains("FENCE")
                && !name.contains("GATE")
                && !name.contains("BUTTON")
                && !name.contains("PRESSURE_PLATE");
    }

    private void restoreWinterMercySnowPatch(UUID targetId) {
        List<WinterMercySnowBlock> snapshots = winterMercySnowPatches.remove(targetId);
        if (snapshots == null) {
            return;
        }

        for (WinterMercySnowBlock snapshot : snapshots) {
            World world = Bukkit.getWorld(snapshot.worldId());
            if (world != null) {
                Block block = world.getBlockAt(snapshot.x(), snapshot.y(), snapshot.z());
                block.setBlockData(snapshot.blockData(), false);
            }
            untrackWinterMercyTemporaryBlock(snapshot);
        }
    }

    private boolean isWinterMercyTemporaryBlock(Block block) {
        if (block == null) {
            return false;
        }
        return winterMercyTemporaryBlockCounts.containsKey(WinterMercyBlockKey.from(block));
    }

    private void trackWinterMercyTemporaryBlock(WinterMercySnowBlock snapshot) {
        WinterMercyBlockKey key = snapshot.key();
        winterMercyTemporaryBlockCounts.put(key, winterMercyTemporaryBlockCounts.getOrDefault(key, 0) + 1);
    }

    private void untrackWinterMercyTemporaryBlock(WinterMercySnowBlock snapshot) {
        WinterMercyBlockKey key = snapshot.key();
        Integer count = winterMercyTemporaryBlockCounts.get(key);
        if (count == null) {
            return;
        }
        if (count <= 1) {
            winterMercyTemporaryBlockCounts.remove(key);
        } else {
            winterMercyTemporaryBlockCounts.put(key, count - 1);
        }
    }

    private List<Material> getWinterMercyGroundMaterials() {
        List<Material> materials = new java.util.ArrayList<>();
        for (String name : getSetsConfig().getStringList("winter-mercy.ground-materials")) {
            Material material = Material.matchMaterial(name);
            if (material != null && material.isBlock() && material.isSolid()) {
                materials.add(material);
            }
        }

        if (materials.isEmpty()) {
            materials.add(Material.SNOW_BLOCK);
            materials.add(Material.ICE);
        }
        return materials;
    }

    private void restoreWinterMercySnowPatch(UUID targetId, List<WinterMercySnowBlock> expectedSnapshots) {
        List<WinterMercySnowBlock> currentSnapshots = winterMercySnowPatches.get(targetId);
        if (currentSnapshots != expectedSnapshots) {
            return;
        }
        restoreWinterMercySnowPatch(targetId);
    }

    private void restoreAllWinterMercySnowPatches() {
        for (UUID targetId : new java.util.ArrayList<>(winterMercySnowPatches.keySet())) {
            restoreWinterMercySnowPatch(targetId);
        }
    }

    private void removeWinterMercySnowballsForTarget(UUID targetId) {
        java.util.Iterator<Map.Entry<UUID, UUID>> iterator = winterMercySnowballs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, UUID> entry = iterator.next();
            if (!entry.getValue().equals(targetId)) {
                continue;
            }

            Entity snowball = Bukkit.getEntity(entry.getKey());
            if (snowball != null && snowball.isValid()) {
                snowball.removeMetadata(WINTER_MERCY_SNOWBALL_METADATA, this);
                snowball.remove();
            }
            iterator.remove();
        }
    }

    private void playWinterMercyActivationSounds(Location location) {
        for (String soundSpec : getSetsConfig().getStringList("winter-mercy.activation-sounds")) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void playWinterMercyHitSounds(Location location) {
        for (String soundSpec : getSetsConfig().getStringList("winter-mercy.hit-sounds")) {
            playConfiguredSoundAt(location, soundSpec);
        }
    }

    private void sendWinterMercyMessage(LivingEntity caster, LivingEntity target, String rawMessage) {
        Location center = target == null ? caster.getLocation() : target.getLocation();
        playWinterMercyActivationSounds(center);

        String resolvedMessage = resolveWinterMercyMessage(rawMessage);
        if (resolvedMessage == null || resolvedMessage.trim().isEmpty()) {
            return;
        }

        String casterName = caster == null ? "" : caster.getName();
        String targetName = target == null ? "" : target.getName();
        String message = colorize(resolvedMessage
                .replace("%attacker name%", casterName)
                .replace("%attackername%", casterName)
                .replace("%attacker%", casterName)
                .replace("%victim name%", targetName)
                .replace("%victimname%", targetName)
                .replace("%victim%", targetName));

        double radius = Math.max(1.0D, getSetsConfig().getDouble("winter-mercy.message-radius-blocks", 16.0D));
        double radiusSquared = radius * radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(center.getWorld())
                    && player.getLocation().distanceSquared(center) <= radiusSquared) {
                player.sendMessage(message);
            }
        }
    }

    private String resolveWinterMercyMessage(String rawMessage) {
        if (rawMessage == null) {
            return "";
        }

        String trimmed = rawMessage.trim();
        if (trimmed.equalsIgnoreCase("default") || trimmed.equalsIgnoreCase("config")) {
            return getSetsConfig().getString("winter-mercy.message",
                    "&b&l*** WINTER'S MERCY ***");
        }

        return trimmed
                .replace("%space%", " ")
                .replace("{space}", " ");
    }

    private LivingEntity getDimensionalShiftDamageVictim(ExecutionTask task) {
        Event event = task == null || task.getBuilder() == null ? null : task.getBuilder().getEvent();
        if (event instanceof EntityDamageEvent
                && ((EntityDamageEvent) event).getEntity() instanceof LivingEntity) {
            return (LivingEntity) ((EntityDamageEvent) event).getEntity();
        }
        return null;
    }

    private LivingEntity getDimensionalShiftDamageAttacker(ExecutionTask task) {
        Event event = task == null || task.getBuilder() == null ? null : task.getBuilder().getEvent();
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return null;
        }

        return getLivingDamager(((EntityDamageByEntityEvent) event).getDamager());
    }

    private LivingEntity getRecentDimensionalShiftDamageAttacker(LivingEntity victim) {
        if (victim == null) {
            return null;
        }

        RecentDamageSource source = dimensionalShiftRecentDamageSources.get(victim.getUniqueId());
        long now = System.currentTimeMillis();
        if (source == null || now - source.createdAtMillis() > 3000L) {
            dimensionalShiftRecentDamageSources.remove(victim.getUniqueId());
            return null;
        }

        Entity attacker = Bukkit.getEntity(source.attackerId());
        return attacker instanceof LivingEntity ? (LivingEntity) attacker : null;
    }

    private static LivingEntity getLivingDamager(Entity damager) {
        if (damager instanceof LivingEntity) {
            return (LivingEntity) damager;
        }
        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof LivingEntity) {
                return (LivingEntity) shooter;
            }
        }
        return null;
    }

    private static LivingEntity firstValidLivingEntity(LivingEntity... entities) {
        for (LivingEntity entity : entities) {
            if (entity != null && entity.isValid() && !entity.isDead()) {
                return entity;
            }
        }
        return null;
    }

    private static LivingEntity getOtherEntity(LivingEntity entity, ExecutionTask task) {
        if (entity == null || task == null || task.getBuilder() == null) {
            return null;
        }

        LivingEntity attacker = task.getBuilder().getAttacker();
        if (attacker != null && !attacker.getUniqueId().equals(entity.getUniqueId())) {
            return attacker;
        }

        LivingEntity victim = task.getBuilder().getVictim();
        if (victim != null && !victim.getUniqueId().equals(entity.getUniqueId())) {
            return victim;
        }

        LivingEntity main = task.getBuilder().getMain();
        if (main != null && !main.getUniqueId().equals(entity.getUniqueId())) {
            return main;
        }
        return null;
    }

    private static String colorize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(value));
    }

    private static String translateHexColorCodes(String value) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            if (index + 7 < value.length()
                    && value.charAt(index) == '&'
                    && value.charAt(index + 1) == '#'
                    && isHexColor(value, index + 2)) {
                builder.append(ChatColor.COLOR_CHAR).append('x');
                for (int offset = 2; offset < 8; offset++) {
                    builder.append(ChatColor.COLOR_CHAR).append(value.charAt(index + offset));
                }
                index += 7;
                continue;
            }
            builder.append(value.charAt(index));
        }
        return builder.toString();
    }

    private static boolean isHexColor(String value, int start) {
        for (int index = start; index < start + 6; index++) {
            char character = value.charAt(index);
            boolean digit = character >= '0' && character <= '9';
            boolean lower = character >= 'a' && character <= 'f';
            boolean upper = character >= 'A' && character <= 'F';
            if (!digit && !lower && !upper) {
                return false;
            }
        }
        return true;
    }

    private static boolean usesDeathSave(AdvancedAbility ability) {
        for (String effect : ability.getEffects()) {
            if (isDeathSaveEffect(effect)) {
                return true;
            }
        }
        return false;
    }

    private boolean itemHasDeathSaveEffect(ItemStack item) {
        if (!isUsableItem(item)) {
            return false;
        }

        Map<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(item);
        if (enchants == null || enchants.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            for (String effect : AEAPI.getEffects(entry.getKey(), Math.max(1, entry.getValue()))) {
                if (isDeathSaveEffect(effect)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean itemHasDeathSaveRule(ItemStack item) {
        if (!isUsableItem(item) || deathSaveRules.isEmpty()) {
            return false;
        }

        for (DeathSaveRule rule : deathSaveRules.values()) {
            if (getDeathSaveRuleLevelOnItem(rule, item) > 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDeathSaveEffect(String effect) {
        if (effect == null) {
            return false;
        }

        String effectName = effect.trim().split("\\s+", 2)[0].split(":", 2)[0];
        return effectName.equalsIgnoreCase("DEATH_SAVE");
    }

    private static LivingEntity getPrimaryVictim(AbilityPreactivateEvent event) {
        Event damageEvent = event.getActionExecution().getBuilder().getEvent();
        if (damageEvent instanceof EntityDamageEvent
                && ((EntityDamageEvent) damageEvent).getEntity() instanceof LivingEntity) {
            return (LivingEntity) ((EntityDamageEvent) damageEvent).getEntity();
        }

        LivingEntity victim = event.getActionExecution().getBuilder().getVictim();
        return victim == null ? event.getMainEntity() : victim;
    }

    private static boolean isLethalDamageEvent(Event event, LivingEntity victim) {
        return event instanceof EntityDamageEvent
                && victim != null
                && victim.getHealth() - ((EntityDamageEvent) event).getFinalDamage() <= 0.0D;
    }

    private static double getMaxHealth(LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return maxHealth == null ? 20.0D : maxHealth.getValue();
    }

    private static double resolveDeathSaveHealth(String[] args, double maxHealth) {
        if (args == null || args.length == 0 || args[0] == null) {
            return maxHealth;
        }

        String value = ChatColor.stripColor(args[0]).trim();
        if (value.isEmpty() || value.equalsIgnoreCase("full")) {
            return maxHealth;
        }

        if (value.endsWith("%")) {
            double percent = parseDouble(value.substring(0, value.length() - 1), Double.NaN);
            return Double.isFinite(percent) ? maxHealth * (percent / 100.0D) : maxHealth;
        }

        double health = parseDouble(value, Double.NaN);
        return Double.isFinite(health) ? health : maxHealth;
    }

    private static int resolveDeathSaveSouls(String[] args) {
        if (args == null || args.length < 2 || args[1] == null) {
            return 0;
        }
        return parsePositiveInt(args[1], 0);
    }

    private boolean payDeathSaveSouls(Player player, ItemStack item, RollItemType itemType, int souls) {
        return payDeathSaveSouls(player, item, itemType, souls, "Death Save");
    }

    private boolean payDeathSaveSouls(Player player, ItemStack item, RollItemType itemType, int souls, String displayName) {
        if (souls <= 0 || player.hasPermission("ae.bypass.souls")) {
            return true;
        }

        if (!isUsableItem(item)) {
            return false;
        }

        int availableSouls = getAvailableSouls(item);
        if (availableSouls < souls || !canPaySingleDrain(item, souls)) {
            sendSoulThrottled(player, "messages.not-enough-souls",
                    "%enchant%", displayName,
                    "%souls%", String.valueOf(souls),
                    "%available%", String.valueOf(availableSouls));
            return false;
        }

        ItemStack updated = SoulsAPI.useSouls(item, souls);
        setItem(player, itemType, updated);
        playDrainTransactionSound(player);
        return true;
    }

    private DeathSaveCandidate findDeathSaveCandidate(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            return null;
        }

        DeathSaveCandidate best = null;
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.HELMET, equipment.getHelmet()));
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.CHESTPLATE, equipment.getChestplate()));
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.LEGGINGS, equipment.getLeggings()));
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.BOOTS, equipment.getBoots()));
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.HAND, equipment.getItemInMainHand()));
        best = betterDeathSaveCandidate(best, findDeathSaveCandidate(RollItemType.OFFHAND, equipment.getItemInOffHand()));
        return best;
    }

    private DeathSaveCandidate findDeathSaveCandidate(RollItemType itemType, ItemStack item) {
        if (!isUsableItem(item)) {
            return null;
        }

        DeathSaveCandidate best = null;
        for (DeathSaveRule rule : deathSaveRules.values()) {
            if (!rule.appliesTo(itemType)) {
                continue;
            }

            int level = getDeathSaveRuleLevelOnItem(rule, item);
            if (level <= 0) {
                continue;
            }

            AdvancedAbility ability = getAbility(rule.rawEnchantName(), rule.enchantName(), level);
            double nativeChance = ability == null ? 100.0D : ability.getChance();
            int nativeCooldown = ability == null ? 0 : ability.getCooldown();
            int souls = rule.resolveSouls(level);

            DeathSaveCandidate candidate = new DeathSaveCandidate(
                    rule.rawEnchantName(),
                    rule.enchantName(),
                    rule.displayName(),
                    level,
                    rule.resolveChance(level, nativeChance),
                    rule.resolveCooldownSeconds(level, nativeCooldown),
                    souls,
                    new String[] { rule.resolveHealth(level), String.valueOf(souls) },
                    rule.resolveEffects(ability),
                    item,
                    itemType
            );
            best = betterDeathSaveCandidate(best, candidate);
        }

        Map<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(item);
        if (enchants == null || enchants.isEmpty()) {
            return best;
        }

        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String rawEnchantName = entry.getKey();
            String enchantName = normalize(rawEnchantName);
            int level = Math.max(1, entry.getValue());
            AdvancedAbility ability = getAbility(rawEnchantName, enchantName, level);
            if (ability == null) {
                continue;
            }

            for (String effect : ability.getEffects()) {
                if (!isDeathSaveEffect(effect)) {
                    continue;
                }

                String[] args = parseEffectArgs(effect);
                DeathSaveCandidate candidate = new DeathSaveCandidate(
                        rawEnchantName,
                        enchantName,
                        prettyDeathSaveName(rawEnchantName, enchantName),
                        level,
                        Math.max(0.0D, ability.getChance()),
                        Math.max(0, ability.getCooldown()),
                        resolveDeathSaveSouls(args),
                        args,
                        ability.getEffects(),
                        item,
                        itemType
                );
                best = betterDeathSaveCandidate(best, candidate);
            }
        }

        return best;
    }

    private int getDeathSaveRuleLevelOnItem(DeathSaveRule rule, ItemStack item) {
        int level = getEnchantLevelOnItem(rule.rawEnchantName(), rule.enchantName(), item);
        if (level > 0) {
            return level;
        }

        Map<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(item);
        if (enchants == null || enchants.isEmpty()) {
            return 0;
        }

        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            if (normalize(entry.getKey()).equals(rule.enchantName())) {
                return Math.max(1, entry.getValue());
            }
        }

        return 0;
    }

    private static DeathSaveCandidate betterDeathSaveCandidate(DeathSaveCandidate current, DeathSaveCandidate candidate) {
        if (candidate == null) {
            return current;
        }
        if (current == null || candidate.level() > current.level() || candidate.chance() > current.chance()) {
            return candidate;
        }
        return current;
    }

    private AdvancedAbility getAbility(String rawEnchantName, String enchantName, int level) {
        try {
            return AEAPI.getEnchantmentInstance(rawEnchantName).getAbility(level);
        } catch (RuntimeException ignored) {
            try {
                return AEAPI.getEnchantmentInstance(enchantName).getAbility(level);
            } catch (RuntimeException ignoredAgain) {
                return null;
            }
        }
    }

    private boolean isDeathSaveOnCooldown(Player player, DeathSaveCandidate candidate) {
        Long endsAt = deathSaveCooldowns.get(deathSaveCooldownKey(player, candidate));
        return endsAt != null && endsAt > System.currentTimeMillis();
    }

    private void startDeathSaveCooldown(Player player, DeathSaveCandidate candidate) {
        if (candidate.cooldownSeconds() <= 0) {
            return;
        }
        deathSaveCooldowns.put(
                deathSaveCooldownKey(player, candidate),
                System.currentTimeMillis() + candidate.cooldownSeconds() * 1000L);
    }

    private static String deathSaveCooldownKey(Player player, DeathSaveCandidate candidate) {
        return player.getUniqueId() + ":" + candidate.enchantName();
    }

    private static boolean rollChance(double chance) {
        return chance >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D) < chance;
    }

    private static String[] parseEffectArgs(String effect) {
        if (effect == null) {
            return new String[0];
        }

        String command = effect.trim().split("\\s+", 2)[0];
        String[] pieces = command.split(":");
        return pieces.length <= 1 ? new String[0] : Arrays.copyOfRange(pieces, 1, pieces.length);
    }

    private static String cleanDisplayName(String value) {
        if (value == null) {
            return "";
        }

        String display = ChatColor.translateAlternateColorCodes('&', value);
        display = ChatColor.stripColor(display);
        if (display == null) {
            return "";
        }

        display = display.replaceAll("%[^%]+%", "");
        display = display.replaceAll("\\s+", " ").trim();
        return display;
    }

    private static String ensureRomanLevelSuffix(String display, int level) {
        String trimmed = display.trim();
        String roman = romanNumeral(level);
        if (trimmed.toUpperCase(Locale.ROOT).endsWith(" " + roman)) {
            return trimmed;
        }

        String numericSuffix = " " + level;
        if (trimmed.endsWith(numericSuffix)) {
            return trimmed.substring(0, trimmed.length() - numericSuffix.length()) + " " + roman;
        }

        return trimmed + " " + roman;
    }

    private static String formatPotionEffectDisplay(String potionType, int amplifier) {
        return potionDisplayName(potionType) + " " + romanNumeral(amplifier + 1);
    }

    private static String potionDisplayName(String potionType) {
        String canonical = canonicalPotionTypeName(potionType);
        switch (canonical) {
            case "DAMAGE_RESISTANCE":
                return "Resistance";
            case "FAST_DIGGING":
                return "Haste";
            case "INCREASE_DAMAGE":
                return "Strength";
            case "SLOW_DIGGING":
                return "Mining Fatigue";
            case "CONFUSION":
                return "Nausea";
            case "JUMP":
                return "Jump Boost";
            case "HEAL":
                return "Instant Health";
            case "HARM":
                return "Instant Damage";
            default:
                return titleCase(canonical.replace("_", " "));
        }
    }

    private static String canonicalPotionTypeName(String value) {
        String name = value == null ? "" : value.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        switch (name) {
            case "HASTE":
                return "FAST_DIGGING";
            case "MINING_FATIGUE":
                return "SLOW_DIGGING";
            case "STRENGTH":
                return "INCREASE_DAMAGE";
            case "RESISTANCE":
                return "DAMAGE_RESISTANCE";
            case "NAUSEA":
                return "CONFUSION";
            case "JUMP_BOOST":
                return "JUMP";
            case "SLOWNESS":
                return "SLOW";
            case "INSTANT_HEALTH":
                return "HEAL";
            case "INSTANT_DAMAGE":
                return "HARM";
            default:
                return name;
        }
    }

    private static String potionEffectTypeName(PotionEffect effect) {
        return effect == null || effect.getType() == null ? "" : canonicalPotionTypeName(effect.getType().getName());
    }

    private static String titleCase(String value) {
        String[] words = value.toLowerCase(Locale.ROOT).trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                builder.append(word.substring(1));
            }
        }
        return builder.toString();
    }

    private static String romanNumeral(int value) {
        if (value <= 0 || value > 3999) {
            return String.valueOf(value);
        }

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder builder = new StringBuilder();
        int remaining = value;
        for (int i = 0; i < values.length; i++) {
            while (remaining >= values[i]) {
                builder.append(numerals[i]);
                remaining -= values[i];
            }
        }
        return builder.toString();
    }

    private void playDeathSaveFeedback(Player savedPlayer, Player attacker, List<String> effects) {
        for (String effect : effects) {
            if (effect == null) {
                continue;
            }

            String trimmed = effect.trim();
            String upper = trimmed.toUpperCase(Locale.ROOT);
            if (upper.startsWith("MESSAGE:")) {
                Player target = getDeathSaveFeedbackTarget(savedPlayer, attacker, trimmed);
                if (target != null) {
                    String message = replaceDeathSavePlaceholders(
                            stripEffectTarget(trimmed.substring("MESSAGE:".length())), savedPlayer, attacker);
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            } else if (upper.startsWith("PLAY_SOUND:")) {
                Player target = getDeathSaveFeedbackTarget(savedPlayer, attacker, trimmed);
                if (target != null) {
                    playDeathSaveSound(target, stripEffectTarget(trimmed.substring("PLAY_SOUND:".length())));
                }
            }
        }
    }

    private Player getDeathSaveAttacker(EntityDamageEvent event, Player savedPlayer) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return null;
        }

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        Player attacker = null;
        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                attacker = (Player) shooter;
            }
        }

        if (attacker == null || attacker.getUniqueId().equals(savedPlayer.getUniqueId())) {
            return null;
        }
        return attacker;
    }

    private static Player getDeathSaveFeedbackTarget(Player savedPlayer, Player attacker, String effect) {
        String upper = effect.toUpperCase(Locale.ROOT);
        if (upper.contains("@ATTACKER")) {
            return attacker;
        }
        return savedPlayer;
    }

    private static String replaceDeathSavePlaceholders(String message, Player savedPlayer, Player attacker) {
        String attackerName = attacker == null ? "" : attacker.getName();
        return message
                .replace("%victim name%", savedPlayer.getName())
                .replace("%victim%", savedPlayer.getName())
                .replace("%player name%", savedPlayer.getName())
                .replace("%player_name%", savedPlayer.getName())
                .replace("%attacker name%", attackerName)
                .replace("%attacker%", attackerName);
    }

    private static String stripEffectTarget(String value) {
        int targetIndex = value.lastIndexOf(" @");
        return (targetIndex >= 0 ? value.substring(0, targetIndex) : value).trim();
    }

    private void playDeathSaveSound(Player player, String soundSpec) {
        String[] pieces = soundSpec.split(":");
        if (pieces.length == 0 || pieces[0].trim().isEmpty()) {
            return;
        }

        String soundName = pieces[0].trim().toUpperCase(Locale.ROOT);
        if (soundName.equals("ITEM_BREAK")) {
            soundName = "ENTITY_ITEM_BREAK";
        }

        float pitch = pieces.length > 1 ? parseFloat(pieces[1], 1.0F) : 1.0F;
        float volume = pieces.length > 2 ? parseFloat(pieces[2], 1.0F) : 1.0F;
        try {
            player.playSound(player.getLocation(), Sound.valueOf(soundName), volume, pitch);
        } catch (IllegalArgumentException ignored) {
            getLogger().warning("Invalid DEATH_SAVE sound: " + soundSpec);
        }
    }

    private static final class DeathSaveCandidate {
        private final String rawEnchantName;
        private final String enchantName;
        private final String displayName;
        private final int level;
        private final double chance;
        private final int cooldownSeconds;
        private final int souls;
        private final String[] args;
        private final List<String> effects;
        private final ItemStack item;
        private final RollItemType itemType;

        private DeathSaveCandidate(String rawEnchantName, String enchantName, String displayName, int level,
                                   double chance, int cooldownSeconds, int souls,
                                   String[] args, List<String> effects,
                                   ItemStack item, RollItemType itemType) {
            this.rawEnchantName = rawEnchantName;
            this.enchantName = enchantName;
            this.displayName = displayName;
            this.level = level;
            this.chance = chance;
            this.cooldownSeconds = cooldownSeconds;
            this.souls = souls;
            this.args = args;
            this.effects = effects;
            this.item = item;
            this.itemType = itemType;
        }

        private String rawEnchantName() {
            return rawEnchantName;
        }

        private String enchantName() {
            return enchantName;
        }

        private String displayName() {
            return displayName;
        }

        private int level() {
            return level;
        }

        private double chance() {
            return chance;
        }

        private int cooldownSeconds() {
            return cooldownSeconds;
        }

        private int souls() {
            return souls;
        }

        private double health(double maxHealth) {
            return resolveDeathSaveHealth(args, maxHealth);
        }

        private List<String> effects() {
            return effects;
        }

        private ItemStack item() {
            return item;
        }

        private RollItemType itemType() {
            return itemType;
        }
    }

    private static final class StaticPotionNotification {
        private final String key;
        private final String potionType;
        private final int amplifier;
        private final String enchantDisplay;
        private final String effectDisplay;

        private StaticPotionNotification(String key, String potionType, int amplifier,
                                         String enchantDisplay, String effectDisplay) {
            this.key = key;
            this.potionType = potionType;
            this.amplifier = amplifier;
            this.enchantDisplay = enchantDisplay;
            this.effectDisplay = effectDisplay;
        }

        private String key() {
            return key;
        }

        private String enchantDisplay() {
            return enchantDisplay;
        }

        private String effectDisplay() {
            return effectDisplay;
        }

        private boolean matches(PotionEffect effect) {
            return effect != null
                    && amplifier == effect.getAmplifier()
                    && potionType.equals(potionEffectTypeName(effect));
        }
    }

    private static final class MetaphysicalResistSource {
        private final String enchantName;
        private final int level;

        private MetaphysicalResistSource(String enchantName, int level) {
            this.enchantName = enchantName;
            this.level = level;
        }

        private String enchantName() {
            return enchantName;
        }

        private int level() {
            return level;
        }
    }

    private static final class HeroicGiveParameters {
        private final int amount;
        private final int success;

        private HeroicGiveParameters(int amount, int success) {
            this.amount = amount;
            this.success = success;
        }

        private int amount() {
            return amount;
        }

        private int success() {
            return success;
        }
    }

    private static final class HolyWaterGiveParameters {
        private final int amount;
        private final int maxApplications;

        private HolyWaterGiveParameters(int amount, int maxApplications) {
            this.amount = amount;
            this.maxApplications = maxApplications;
        }

        private int amount() {
            return amount;
        }

        private int maxApplications() {
            return maxApplications;
        }
    }

    private static final class HolyWhiteScrollCorruptionNumbers {
        private final int count;
        private final int max;

        private HolyWhiteScrollCorruptionNumbers(int count, int max) {
            this.count = count;
            this.max = max;
        }

        private int count() {
            return count;
        }

        private int max() {
            return max;
        }
    }

    private static final class PendingTextItemEdit {
        private final UUID playerId;
        private final ItemStack refundItem;

        private PendingTextItemEdit(UUID playerId, ItemStack refundItem) {
            this.playerId = playerId;
            this.refundItem = refundItem;
        }

        private UUID playerId() {
            return playerId;
        }

        private ItemStack refundItem() {
            return refundItem;
        }
    }

    private static final class HolyWhiteScrollPendingApplication {
        private final UUID playerId;
        private final int rawSlot;
        private final int previousCount;

        private HolyWhiteScrollPendingApplication(UUID playerId, int rawSlot, int previousCount) {
            this.playerId = playerId;
            this.rawSlot = rawSlot;
            this.previousCount = previousCount;
        }

        private UUID playerId() {
            return playerId;
        }

        private int rawSlot() {
            return rawSlot;
        }

        private int previousCount() {
            return previousCount;
        }
    }

    private static final class TransmogEnchant {
        private final String rawEnchantName;
        private final int level;
        private final String displayName;
        private final String groupName;
        private String displayLine;

        private TransmogEnchant(String rawEnchantName, int level, String displayName, String displayLine, String groupName) {
            this.rawEnchantName = rawEnchantName;
            this.level = level;
            this.displayName = displayName;
            this.displayLine = displayLine;
            this.groupName = groupName;
        }

        private String rawEnchantName() {
            return rawEnchantName;
        }

        private int level() {
            return level;
        }

        private String displayName() {
            return displayName;
        }

        private String displayLine() {
            return displayLine;
        }

        private void setDisplayLine(String displayLine) {
            this.displayLine = displayLine;
        }

        private String groupName() {
            return groupName;
        }
    }

    private static final class TransmogSession {
        private final UUID playerId;
        private final ItemStack originalItem;
        private final Inventory targetInventory;
        private final int targetSlot;
        private final ItemStack refundScroll;
        private final List<TransmogEnchant> enchants;
        private int selectedIndex = -1;
        private boolean applied;

        private TransmogSession(UUID playerId, ItemStack originalItem, Inventory targetInventory,
                                int targetSlot, ItemStack refundScroll, List<TransmogEnchant> enchants) {
            this.playerId = playerId;
            this.originalItem = originalItem;
            this.targetInventory = targetInventory;
            this.targetSlot = targetSlot;
            this.refundScroll = refundScroll;
            this.enchants = enchants;
        }

        private UUID playerId() {
            return playerId;
        }

        private ItemStack originalItem() {
            return originalItem;
        }

        private Inventory targetInventory() {
            return targetInventory;
        }

        private int targetSlot() {
            return targetSlot;
        }

        private ItemStack refundScroll() {
            return refundScroll;
        }

        private List<TransmogEnchant> enchants() {
            return enchants;
        }

        private int selectedIndex() {
            return selectedIndex;
        }

        private void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        private boolean applied() {
            return applied;
        }

        private void setApplied(boolean applied) {
            this.applied = applied;
        }
    }

    private static final class TransmogMenuHolder implements InventoryHolder {
        private final UUID playerId;
        private Inventory inventory;

        private TransmogMenuHolder(UUID playerId) {
            this.playerId = playerId;
        }

        private UUID playerId() {
            return playerId;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private enum HeroicTargetType {
        ARMOR("armor"),
        WEAPON("weapon");

        private final String configName;

        HeroicTargetType(String configName) {
            this.configName = configName;
        }

        private String configName() {
            return configName;
        }
    }

    private static final class RecentDamageSource {
        private final UUID attackerId;
        private final long createdAtMillis;

        private RecentDamageSource(UUID attackerId, long createdAtMillis) {
            this.attackerId = attackerId;
            this.createdAtMillis = createdAtMillis;
        }

        private UUID attackerId() {
            return attackerId;
        }

        private long createdAtMillis() {
            return createdAtMillis;
        }
    }

    private static final class WinterMercyReduction {
        private final double percent;
        private final long expiresAtMillis;

        private WinterMercyReduction(double percent, long expiresAtMillis) {
            this.percent = percent;
            this.expiresAtMillis = expiresAtMillis;
        }

        private double percent() {
            return percent;
        }

        private long expiresAtMillis() {
            return expiresAtMillis;
        }
    }

    private static final class WinterMercySnowBlock {
        private final UUID worldId;
        private final int x;
        private final int y;
        private final int z;
        private final BlockData blockData;
        private final Material temporaryMaterial;

        private WinterMercySnowBlock(UUID worldId, int x, int y, int z, BlockData blockData, Material temporaryMaterial) {
            this.worldId = worldId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockData = blockData;
            this.temporaryMaterial = temporaryMaterial;
        }

        private UUID worldId() {
            return worldId;
        }

        private int x() {
            return x;
        }

        private int y() {
            return y;
        }

        private int z() {
            return z;
        }

        private BlockData blockData() {
            return blockData;
        }

        private Material temporaryMaterial() {
            return temporaryMaterial;
        }

        private WinterMercyBlockKey key() {
            return new WinterMercyBlockKey(worldId, x, y, z);
        }
    }

    private static final class WinterMercyBlockKey {
        private final UUID worldId;
        private final int x;
        private final int y;
        private final int z;

        private WinterMercyBlockKey(UUID worldId, int x, int y, int z) {
            this.worldId = worldId;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private static WinterMercyBlockKey from(Block block) {
            return new WinterMercyBlockKey(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof WinterMercyBlockKey)) {
                return false;
            }

            WinterMercyBlockKey otherKey = (WinterMercyBlockKey) other;
            return x == otherKey.x
                    && y == otherKey.y
                    && z == otherKey.z
                    && worldId.equals(otherKey.worldId);
        }

        @Override
        public int hashCode() {
            int result = worldId.hashCode();
            result = 31 * result + x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bless")) {
            if (!sender.hasPermission("advancedenchantmentsaddon.bless")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use /" + label + ".");
                return true;
            }

            if (args.length != 0) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label);
                return true;
            }

            Player player = (Player) sender;
            long remainingCooldownMillis = getBlessRemainingCooldownMillis(player);
            if (remainingCooldownMillis > 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        getConfig().getString("messages.bless-cooldown", "&cYou can use /bless again in &f%seconds%s&c.")
                                .replace("%seconds%", String.valueOf((remainingCooldownMillis + 999L) / 1000L))));
                return true;
            }

            blessPlayer(player);
            startBlessCooldown(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    getConfig().getString("messages.blessed", "&e&l* BLESSED! *")));
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("giveitem")) {
            return handleAeaGiveItemCommand(sender, label, args);
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("test")) {
            if (!sender.hasPermission("advancedenchantmentsaddon.test")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            String testName = normalize(args[1]);
            if (!testName.equals("dimensionalshift") && !testName.equals("wintersmercy")) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " test <dimensionalshift|wintersmercy> [player]");
                return true;
            }

            Player target;
            if (args.length >= 3) {
                target = Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "That player is not online.");
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " test " + args[1] + " <player>");
                return true;
            }

            Player caster = sender instanceof Player ? (Player) sender : target;
            if (testName.equals("dimensionalshift")) {
                triggerConfiguredDimensionalShift(caster, Collections.singletonList(target), "default");
                sender.sendMessage(ChatColor.GREEN + "Triggered Dimensional Shift test on " + target.getName() + ".");
            } else {
                triggerConfiguredWinterMercy(caster, target, "default");
                sender.sendMessage(ChatColor.GREEN + "Triggered Winter's Mercy test on " + target.getName() + ".");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("advancedenchantmentsaddon.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            reloadPluginConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    getConfig().getString("messages.reloaded", "&aAdvancedEnchantmentsAddon reloaded.")));
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " reload");
        if (hasAnyAddonGiveItemPermission(sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " giveitem <player> <item> [amount] [success|max]");
        }
        if (sender.hasPermission("advancedenchantmentsaddon.test")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " test <dimensionalshift|wintersmercy> [player]");
        }
        return true;
    }

    private void blessPlayer(Player player) {
        List<PotionEffect> activeEffects = new java.util.ArrayList<>(player.getActivePotionEffects());
        List<PotionEffect> removedEffects = new java.util.ArrayList<>();
        for (PotionEffect effect : activeEffects) {
            if (shouldRemoveByBless(effect)) {
                removedEffects.add(effect);
                player.removePotionEffect(effect.getType());
            }
        }
        sendStaticPotionNotificationsForEffects(player, removedEffects, false);
        player.setFireTicks(0);
        player.setFreezeTicks(0);
    }

    private static boolean shouldRemoveByBless(PotionEffect effect) {
        return effect != null
                && effect.getType() != null
                && BLESS_REMOVE_EFFECT_TYPES.contains(canonicalPotionTypeName(effect.getType().getName()));
    }

    private long getBlessRemainingCooldownMillis(Player player) {
        if (blessCooldownMillis <= 0 || player.hasPermission("advancedenchantmentsaddon.bless.cooldown.bypass")) {
            return 0L;
        }

        Long cooldownEndsAt = blessCooldowns.get(player.getUniqueId());
        if (cooldownEndsAt == null) {
            return 0L;
        }

        long remaining = cooldownEndsAt - System.currentTimeMillis();
        if (remaining <= 0L) {
            blessCooldowns.remove(player.getUniqueId());
            return 0L;
        }
        return remaining;
    }

    private void startBlessCooldown(Player player) {
        if (blessCooldownMillis <= 0 || player.hasPermission("advancedenchantmentsaddon.bless.cooldown.bypass")) {
            return;
        }
        blessCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + blessCooldownMillis);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("bless")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> options = new java.util.ArrayList<>();
            if (sender.hasPermission("advancedenchantmentsaddon.reload")) {
                options.add("reload");
            }
            if (hasAnyAddonGiveItemPermission(sender)) {
                options.add("giveitem");
            }
            if (sender.hasPermission("advancedenchantmentsaddon.test")) {
                options.add("test");
            }
            return filterCompletions(options, args[0]);
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("giveitem")) {
            return tabCompleteAeaGiveItem(sender, args);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("test")
                && sender.hasPermission("advancedenchantmentsaddon.test")) {
            return filterCompletions(Arrays.asList("dimensionalshift", "wintersmercy"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("test")
                && (normalize(args[1]).equals("dimensionalshift") || normalize(args[1]).equals("wintersmercy"))
                && sender.hasPermission("advancedenchantmentsaddon.test")) {
            List<String> players = new java.util.ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return filterCompletions(players, args[2]);
        }
        return Collections.emptyList();
    }

    private List<String> tabCompleteAeaGiveItem(CommandSender sender, String[] args) {
        if (!hasAnyAddonGiveItemPermission(sender)) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            List<String> players = new java.util.ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return filterCompletions(players, args[1]);
        }

        if (args.length == 3) {
            return filterCompletions(getPermittedAddonGiveItemNames(sender), args[2]);
        }

        if (args.length == 4 && canGiveAddonItem(sender, args[2])) {
            return filterCompletions(GIVE_AMOUNT_COMPLETIONS, args[3]);
        }

        if (args.length == 5 && canGiveAddonItem(sender, args[2])) {
            if (isHeroicUpgradeItemName(args[2]) || isHeroicBlackScrollItemName(args[2])) {
                return filterCompletions(Arrays.asList("success:100", "success:75", "success:50"), args[4]);
            }
            if (isHolyWaterItemName(args[2])) {
                return filterCompletions(Arrays.asList("max:4", "max:5", "max:6"), args[4]);
            }
        }

        return Collections.emptyList();
    }

    private static List<String> getPermittedAddonGiveItemNames(CommandSender sender) {
        List<String> itemNames = new java.util.ArrayList<>();
        for (String itemName : ADDON_GIVE_ITEM_NAMES) {
            if (canGiveAddonItem(sender, itemName)) {
                itemNames.add(itemName);
            }
        }
        return itemNames;
    }

    private static List<String> filterCompletions(List<String> options, String input) {
        String prefix = input == null ? "" : input.toLowerCase(Locale.ROOT);
        List<String> matches = new java.util.ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                matches.add(option);
            }
        }
        return matches;
    }

    private static final class DeathSaveRule {
        private final String rawEnchantName;
        private final String enchantName;
        private final String displayName;
        private final List<RollItemType> slots;
        private final ConfigurationSection section;

        private DeathSaveRule(String rawEnchantName, ConfigurationSection section) {
            this.rawEnchantName = rawEnchantName;
            this.enchantName = normalize(rawEnchantName);
            this.displayName = section.getString("display-name", rawEnchantName);
            this.slots = readSlots(section);
            this.section = section;
        }

        private String rawEnchantName() {
            return rawEnchantName;
        }

        private String enchantName() {
            return enchantName;
        }

        private String displayName() {
            return displayName;
        }

        private boolean appliesTo(RollItemType itemType) {
            return slots.contains(itemType);
        }

        private int resolveSouls(int level) {
            Object value = readLevelValue(level, "souls-per-use", null);
            if (value == null) {
                value = readLevelValue(level, "souls", 0);
            }
            if (value instanceof Number) {
                return Math.max(0, ((Number) value).intValue());
            }
            return parsePositiveInt(String.valueOf(value), 0);
        }

        private String resolveHealth(int level) {
            Object value = readLevelValue(level, "health", "full");
            String text = String.valueOf(value).trim();
            return text.isEmpty() ? "full" : text;
        }

        private double resolveChance(int level, double nativeChance) {
            Object value = readLevelValue(level, "chance", "native");
            if (value instanceof Number) {
                return Math.max(0.0D, ((Number) value).doubleValue());
            }

            String text = String.valueOf(value).trim();
            if (text.equalsIgnoreCase("native")) {
                return Math.max(0.0D, nativeChance);
            }
            return Math.max(0.0D, parseDouble(text, nativeChance));
        }

        private int resolveCooldownSeconds(int level, int nativeCooldown) {
            Object value = readLevelValue(level, "cooldown-seconds", null);
            if (value == null) {
                value = readLevelValue(level, "cooldown", "native");
            }
            if (value instanceof Number) {
                return Math.max(0, ((Number) value).intValue());
            }

            String text = String.valueOf(value).trim();
            if (text.equalsIgnoreCase("native")) {
                return Math.max(0, nativeCooldown);
            }
            return parsePositiveInt(text, nativeCooldown);
        }

        private List<String> resolveEffects(AdvancedAbility ability) {
            List<String> effects = section.getStringList("effects");
            if (!effects.isEmpty() || section.contains("effects")) {
                return effects;
            }
            return ability == null ? Collections.emptyList() : ability.getEffects();
        }

        private Object readLevelValue(int level, String key, Object fallback) {
            ConfigurationSection levelSection = section.getConfigurationSection("levels." + level);
            if (levelSection != null && levelSection.contains(key)) {
                return levelSection.get(key);
            }
            return section.contains(key) ? section.get(key) : fallback;
        }

        private static List<RollItemType> readSlots(ConfigurationSection section) {
            List<String> names = section.getStringList("slots");
            if (names.isEmpty() && !section.contains("slots")) {
                names = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
            }

            List<RollItemType> parsed = new java.util.ArrayList<>();
            for (String name : names) {
                addSlot(parsed, name);
            }

            if (parsed.isEmpty()) {
                addArmorSlots(parsed);
            }
            return parsed;
        }

        private static void addSlot(List<RollItemType> slots, String name) {
            String normalized = normalize(name);
            switch (normalized) {
                case "all":
                    addUnique(slots, RollItemType.HAND);
                    addUnique(slots, RollItemType.OFFHAND);
                    addArmorSlots(slots);
                    break;
                case "armor":
                    addArmorSlots(slots);
                    break;
                case "hand":
                case "hands":
                case "main":
                case "mainhand":
                    addUnique(slots, RollItemType.HAND);
                    break;
                case "offhand":
                    addUnique(slots, RollItemType.OFFHAND);
                    break;
                case "helmet":
                case "head":
                    addUnique(slots, RollItemType.HELMET);
                    break;
                case "chestplate":
                case "chest":
                    addUnique(slots, RollItemType.CHESTPLATE);
                    break;
                case "leggings":
                case "legs":
                    addUnique(slots, RollItemType.LEGGINGS);
                    break;
                case "boots":
                case "feet":
                    addUnique(slots, RollItemType.BOOTS);
                    break;
                default:
                    break;
            }
        }

        private static void addArmorSlots(List<RollItemType> slots) {
            addUnique(slots, RollItemType.HELMET);
            addUnique(slots, RollItemType.CHESTPLATE);
            addUnique(slots, RollItemType.LEGGINGS);
            addUnique(slots, RollItemType.BOOTS);
        }

        private static void addUnique(List<RollItemType> slots, RollItemType itemType) {
            if (!slots.contains(itemType)) {
                slots.add(itemType);
            }
        }
    }

    private static final class DistanceDamageEffect extends AdvancedEffect {
        private static final double NORMAL_DAMAGE_DISTANCE = 2.0D;
        private static final double STRENGTH_PER_DAMAGE_POINT = 0.125D;

        private DistanceDamageEffect(JavaPlugin plugin) {
            super(plugin, "DISTANCE_DAMAGE", "Scale damage by attacker distance", "%e:<MAX_DAMAGE>:<MIN_DAMAGE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (args == null || args.length < 2) {
                return false;
            }

            Event event = task.getBuilder().getEvent();
            if (!(event instanceof EntityDamageEvent)) {
                return false;
            }

            LivingEntity attacker = task.getBuilder().getAttacker();
            LivingEntity victim = task.getBuilder().getVictim();
            if (attacker == null || victim == null) {
                task.reportIssue("DISTANCE_DAMAGE requires an attacker and victim", String.join(":", args));
                return false;
            }
            if (attacker.getWorld() == null || !attacker.getWorld().equals(victim.getWorld())) {
                return false;
            }

            double maxDamage = parseDouble(args[0], Double.NaN);
            double minDamage = parseDouble(args[1], Double.NaN);
            if (!Double.isFinite(maxDamage) || !Double.isFinite(minDamage)) {
                task.reportIssue("Invalid DISTANCE_DAMAGE arguments", String.join(":", args));
                return false;
            }

            double strength = Math.abs(maxDamage - minDamage) * STRENGTH_PER_DAMAGE_POINT;
            if (strength <= 0.0D) {
                return false;
            }

            double distance = attacker.getLocation().distance(victim.getLocation());
            double multiplier = resolveDistanceDamageMultiplier(distance, Math.max(maxDamage, minDamage), strength);
            if (!Double.isFinite(multiplier)) {
                return false;
            }

            EntityDamageEvent damageEvent = (EntityDamageEvent) event;
            double updatedDamage = damageEvent.getDamage() * multiplier;
            if (!Double.isFinite(updatedDamage)) {
                task.reportIssue("DISTANCE_DAMAGE result is too large", String.join(":", args));
                return false;
            }

            damageEvent.setDamage(Math.max(0.0D, updatedDamage));
            return true;
        }

        private static double resolveDistanceDamageMultiplier(double distance, double falloffDistance, double strength) {
            double closeMultiplier = 1.0D + strength;
            double farMultiplier = Math.max(0.0D, 1.0D - strength);

            if (distance <= NORMAL_DAMAGE_DISTANCE) {
                double progress = Math.max(0.0D, distance) / NORMAL_DAMAGE_DISTANCE;
                return closeMultiplier + ((1.0D - closeMultiplier) * progress);
            }

            double effectiveFalloffDistance = Math.max(NORMAL_DAMAGE_DISTANCE + 1.0D, falloffDistance);
            double progress = Math.min(1.0D, (distance - NORMAL_DAMAGE_DISTANCE) / (effectiveFalloffDistance - NORMAL_DAMAGE_DISTANCE));
            return 1.0D + ((farMultiplier - 1.0D) * progress);
        }
    }

    private final class NoKnockbackDamageEffect extends AdvancedEffect {
        private NoKnockbackDamageEffect(JavaPlugin plugin) {
            super(plugin, "DAMAGE_NO_KNOCKBACK", "Damage an entity without applying knockback", "%e:<DAMAGE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (entity == null || args == null || args.length < 1) {
                return false;
            }

            double damage = Math.abs(parseDouble(args[0], Double.NaN));
            if (!Double.isFinite(damage) || damage <= 0.0D) {
                task.reportIssue("Invalid DAMAGE_NO_KNOCKBACK damage", args.length == 0 ? "" : String.join(":", args));
                return false;
            }

            damageWithoutKnockback(entity, getOtherEntity(entity, task), damage);
            return true;
        }
    }

    private final class NoKnockbackBleedEffect extends AdvancedEffect {
        private NoKnockbackBleedEffect(JavaPlugin plugin) {
            super(plugin, "BLEED_NO_KNOCKBACK", "Bleed an entity without applying knockback", "%e:<DAMAGE>:<TICKS>:<INTERVAL_TICKS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (entity == null || args == null || args.length < 1) {
                return false;
            }

            double damage = Math.abs(parseDouble(args[0], 1.0D));
            int pulses = args.length >= 2 ? parsePositiveInt(args[1], 5) : 5;
            int intervalTicks = args.length >= 3 ? parsePositiveInt(args[2], 20) : 20;
            if (!Double.isFinite(damage) || damage <= 0.0D || pulses <= 0 || intervalTicks <= 0) {
                task.reportIssue("Invalid BLEED_NO_KNOCKBACK arguments", String.join(":", args));
                return false;
            }

            new BukkitRunnable() {
                private int pulse;
                private final long createdAtTicksLived = entity.getTicksLived();

                @Override
                public void run() {
                    if (entity.isDead() || !entity.isValid() || entity.getTicksLived() < createdAtTicksLived) {
                        cancel();
                        return;
                    }

                    damageBleedWithoutKnockback(entity, getOtherEntity(entity, task), damage);
                    pulse++;
                    if (pulse >= pulses) {
                        cancel();
                    }
                }
            }.runTaskTimer(AdvancedEnchantmentsAddonPlugin.this, intervalTicks, intervalTicks);
            return true;
        }
    }

    private final class TrueInvisibilityEffect extends AdvancedEffect {
        private TrueInvisibilityEffect(JavaPlugin plugin) {
            super(plugin, "TRUE_INVISIBILITY", "Apply invisibility and hide worn armor from other players", "%e:<AMPLIFIER>:<TICKS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            LivingEntity target = firstValidLivingEntity(entity,
                    task == null || task.getBuilder() == null ? null : task.getBuilder().getVictim(),
                    task == null || task.getBuilder() == null ? null : task.getBuilder().getMain());
            if (!(target instanceof Player)) {
                return false;
            }

            int amplifier = args != null && args.length >= 1 ? parsePositiveInt(args[0], 0) : 0;
            int durationTicks = args != null && args.length >= 2 ? parsePositiveInt(args[1], 20) : 20;
            if (durationTicks <= 0) {
                return false;
            }

            startTrueInvisibility((Player) target, amplifier, durationTicks);
            return true;
        }
    }

    private final class RuseZombiesEffect extends AdvancedEffect {
        private RuseZombiesEffect(JavaPlugin plugin) {
            super(plugin, "RUSE_ZOMBIES", "Spawn hidden-effect baby zombie guards for ruse enchants", "%e:<COUNT>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }

            int count = args != null && args.length >= 1 ? parsePositiveInt(args[0], 1) : 1;
            if (count <= 0) {
                return false;
            }

            LivingEntity owner = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    task.getBuilder().getMain());
            LivingEntity target = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(owner, task));
            if (owner == null) {
                return false;
            }

            String name = args != null && args.length >= 2 ? args[1] : "";
            spawnRuseZombies(owner, target, count, name);
            return true;
        }
    }

    private final class EpidemicCarrierEffect extends AdvancedEffect {
        private EpidemicCarrierEffect(JavaPlugin plugin) {
            super(plugin, "EPIDEMIC_CARRIER",
                    "Spawn controlled charged creeper carriers",
                    "%e:<COUNT>:<BLINDNESS_AMPLIFIER>:<BLINDNESS_TICKS>:<CONFUSION_AMPLIFIER>:<CONFUSION_TICKS>[:<SLOWNESS_AMPLIFIER>:<SLOWNESS_TICKS>][:<NAME>]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null || args == null || args.length < 5) {
                return false;
            }

            LivingEntity owner = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    task.getBuilder().getMain());
            LivingEntity target = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(owner, task));
            if (owner == null || target == null) {
                return false;
            }

            int count = parsePositiveInt(args[0], 1);
            int blindnessAmplifier = parsePositiveInt(args[1], 0);
            int blindnessTicks = parsePositiveInt(args[2], 40);
            int confusionAmplifier = parsePositiveInt(args[3], 0);
            int confusionTicks = parsePositiveInt(args[4], 40);
            int slownessAmplifier = 0;
            int slownessTicks = 60;
            String name = "";
            if (args.length >= 6) {
                if (args[5] != null && args[5].trim().matches("\\d+")) {
                    slownessAmplifier = parsePositiveInt(args[5], 0);
                    slownessTicks = args.length >= 7 ? parsePositiveInt(args[6], 60) : 60;
                    name = args.length >= 8 ? args[7] : "";
                } else {
                    name = args[5];
                }
            }

            spawnEpidemicCarriers(owner, target, count, blindnessAmplifier, blindnessTicks,
                    confusionAmplifier, confusionTicks, slownessAmplifier, slownessTicks, name);
            return true;
        }
    }

    private final class VisualSpiritsEffect extends AdvancedEffect {
        private VisualSpiritsEffect(JavaPlugin plugin) {
            super(plugin, "VISUAL_SPIRITS", "Spawn harmless visual blaze spirits", "%e:<SECONDS>:<AMOUNT>:<NAME>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            LivingEntity target = firstValidLivingEntity(entity,
                    task == null || task.getBuilder() == null ? null : task.getBuilder().getVictim(),
                    task == null || task.getBuilder() == null ? null : task.getBuilder().getMain());
            if (target == null || args == null || args.length < 1) {
                return false;
            }

            int durationSeconds = args.length >= 1 ? parsePositiveInt(args[0], 5) : 5;
            int amount = args.length >= 2 ? parsePositiveInt(args[1], 1) : 1;
            int regenerationAmplifier = 0;
            String name = "";
            if (args.length >= 3) {
                if (args[2] != null && args[2].trim().matches("\\d+")) {
                    regenerationAmplifier = parsePositiveInt(args[2], 0);
                    name = args.length >= 4 ? args[3] : "";
                } else {
                    name = args[2];
                }
            }
            spawnVisualSpirits(target, durationSeconds, amount, regenerationAmplifier, name);
            return true;
        }
    }

    private final class ObsidianGuardiansEffect extends AdvancedEffect {
        private ObsidianGuardiansEffect(JavaPlugin plugin) {
            super(plugin, "OBSIDIAN_GUARDIANS",
                    "Spawn controlled iron golem guardians",
                    "%e:<COUNT>:<DURATION_TICKS>:<DAMAGE>:<PULL_STRENGTH>:<SLOW_AMPLIFIER>:<SLOW_TICKS>:<NAME>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }

            LivingEntity owner = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    task.getBuilder().getMain());
            LivingEntity target = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(owner, task));
            if (owner == null || target == null) {
                return false;
            }

            int count = args != null && args.length >= 1 ? parsePositiveInt(args[0], 1) : 1;
            int durationTicks = args != null && args.length >= 2
                    ? parsePositiveInt(args[1], getEnchantsConfig().getInt("obsidian-guardians.lifetime-ticks", 240))
                    : getEnchantsConfig().getInt("obsidian-guardians.lifetime-ticks", 240);
            double damage = args != null && args.length >= 3 ? parseDouble(args[2], 2.0D) : 2.0D;
            double pullStrength = args != null && args.length >= 4 ? parseDouble(args[3], 1.5D) : 1.5D;
            int slowAmplifier = args != null && args.length >= 5 ? parsePositiveInt(args[4], 0) : 0;
            int slowTicks = args != null && args.length >= 6 ? parsePositiveInt(args[5], 40) : 40;
            String name = args != null && args.length >= 7 ? args[6] : "";

            spawnObsidianGuardians(owner, target, count, durationTicks, damage, pullStrength,
                    slowAmplifier, slowTicks, name);
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private final class DivineImmolationEffect extends AdvancedEffect {
        private DivineImmolationEffect(JavaPlugin plugin) {
            super(plugin, "DIVINE_IMMOLATION", "Apply Divine Immolation AoE damage ticks", "%e");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }

            LivingEntity centerTarget = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task));
            LivingEntity caster = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    task.getBuilder().getMain());
            if (centerTarget == null) {
                return false;
            }

            if (shouldSendDivineImmolationActivationNotice(caster, centerTarget)) {
                playDivineImmolationHitSounds(centerTarget);
                sendDivineImmolationMessages(caster, centerTarget);
            }
            startDivineImmolation(caster, centerTarget, args);
            return true;
        }
    }

    private final class AccurateLifestealEffect extends AdvancedEffect {
        private AccurateLifestealEffect(JavaPlugin plugin) {
            super(plugin, "LIFESTEAL_ACCURATE",
                    "Steal accurate health from the damaged player and report the real amount",
                    "%e:<MIN_DAMAGE>:<MAX_DAMAGE>[:MESSAGE_PREFIX]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null || args == null || args.length < 2) {
                return false;
            }

            LivingEntity victim = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task));
            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(victim, task),
                    task.getBuilder().getMain());
            if (attacker == null || victim == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
                return false;
            }

            double minDamage = Math.abs(parseDouble(args[0], Double.NaN));
            double maxDamage = Math.abs(parseDouble(args[1], Double.NaN));
            if (!Double.isFinite(minDamage) || !Double.isFinite(maxDamage)
                    || minDamage <= 0.0D || maxDamage <= 0.0D) {
                task.reportIssue("Invalid LIFESTEAL_ACCURATE arguments", String.join(":", args));
                return false;
            }

            String prefix = args.length >= 3 ? args[2] : "lifesteal";
            applyAccurateLifesteal(attacker, victim, minDamage, maxDamage, prefix);
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private final class ChainLifestealEffect extends AdvancedEffect {
        private ChainLifestealEffect(JavaPlugin plugin) {
            super(plugin, "CHAIN_LIFESTEAL",
                    "Steal health from players near the damaged target",
                    "%e:<MIN_DAMAGE>:<MAX_DAMAGE>:<RADIUS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null || args == null || args.length < 2) {
                return false;
            }

            LivingEntity centerTarget = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task));
            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(centerTarget, task));
            if (attacker == null || centerTarget == null) {
                return false;
            }

            double minDamage = Math.abs(parseDouble(args[0], Double.NaN));
            double maxDamage = Math.abs(parseDouble(args[1], Double.NaN));
            double radius = args.length >= 3 ? Math.abs(parseDouble(args[2], 6.0D)) : 6.0D;
            if (!Double.isFinite(minDamage) || !Double.isFinite(maxDamage)
                    || minDamage <= 0.0D || maxDamage <= 0.0D) {
                task.reportIssue("Invalid CHAIN_LIFESTEAL arguments", String.join(":", args));
                return false;
            }

            applyChainLifesteal(attacker, centerTarget, minDamage, maxDamage, radius);
            return true;
        }
    }

    private final class ParadoxHealEffect extends AdvancedEffect {
        private ParadoxHealEffect(JavaPlugin plugin) {
            super(plugin, "PARADOX_HEAL",
                    "Heal real nearby players for a portion of the current damage taken",
                    "%e:<DAMAGE_DIVISOR>:<RADIUS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null || args == null || args.length < 2) {
                return false;
            }

            Event event = task.getBuilder().getEvent();
            if (!(event instanceof EntityDamageEvent)) {
                return false;
            }

            LivingEntity defender = firstValidLivingEntity(
                    entity,
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task),
                    task.getBuilder().getMain());
            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getOtherEntity(defender, task));
            if (defender == null) {
                return false;
            }

            double divisor = Math.abs(parseDouble(args[0], Double.NaN));
            double radius = Math.abs(parseDouble(args[1], Double.NaN));
            if (!Double.isFinite(divisor) || !Double.isFinite(radius)
                    || divisor <= 0.0D || radius <= 0.0D) {
                task.reportIssue("Invalid PARADOX_HEAL arguments", String.join(":", args));
                return false;
            }

            applyParadoxHeal(defender, attacker, divisor, radius, (EntityDamageEvent) event);
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private final class DestructionAuraEffect extends AdvancedEffect {
        private DestructionAuraEffect(JavaPlugin plugin) {
            super(plugin, "DESTRUCTION_AURA",
                    "Declare addon Destruction aura settings for this enchant level",
                    "%e:<DAMAGE_PER_TICK>:<RADIUS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private final class RageMultiplierEffect extends AdvancedEffect {
        private RageMultiplierEffect(JavaPlugin plugin) {
            super(plugin, "RAGE_MULTIPLIER",
                    "Multiply the current hit by the attacker's combo stack",
                    "%e:<PER_STACK_MULTIPLIER>:<MAX_STACKS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null || args == null || args.length < 1) {
                return false;
            }

            Event event = task.getBuilder().getEvent();
            if (!(event instanceof EntityDamageEvent)) {
                return false;
            }

            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    task.getBuilder().getMain());
            LivingEntity victim = firstValidLivingEntity(
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task),
                    entity);
            if (attacker == null || victim == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
                return false;
            }

            double perStack = Math.abs(parseDouble(args[0], 0.1D));
            int maxStacks = args.length >= 2 ? parsePositiveInt(args[1], 5) : 5;
            if (!Double.isFinite(perStack) || perStack <= 0.0D || maxStacks <= 0) {
                task.reportIssue("Invalid RAGE_MULTIPLIER arguments", String.join(":", args));
                return false;
            }

            int combo = resolveRageCombo(attacker, victim, maxStacks);
            if (combo <= 0) {
                return false;
            }

            EntityDamageEvent damageEvent = (EntityDamageEvent) event;
            double multiplier = 1.0D + (perStack * combo);
            applyAdditiveDamageMultiplier(damageEvent, multiplier);
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private final class DamageMultiplierEffect extends AdvancedEffect {
        private DamageMultiplierEffect(JavaPlugin plugin) {
            super(plugin, "DAMAGE_MULTIPLIER",
                    "Multiply only the current damage event",
                    "%e:<MULTIPLIER>[:SWORD_ONLY]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (args == null || args.length < 1) {
                return false;
            }

            double multiplier = parseDouble(args[0], Double.NaN);
            if (!Double.isFinite(multiplier) || multiplier <= 0.0D) {
                task.reportIssue("Invalid DAMAGE_MULTIPLIER argument", String.join(":", args));
                return false;
            }

            boolean swordOnly = args.length >= 2 && normalize(args[1]).equals("swordonly");
            return multiplyCurrentDamage(task, multiplier, swordOnly);
        }
    }

    private final class ExecuteDamageEffect extends AdvancedEffect {
        private ExecuteDamageEffect(JavaPlugin plugin) {
            super(plugin, "EXECUTE_DAMAGE",
                    "Multiply the current hit when the victim is under an HP threshold",
                    "%e:<THRESHOLD_HP>:<MULTIPLIER>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }
            LivingEntity victim = firstValidLivingEntity(entity, task.getBuilder().getVictim());
            return applyExecuteDamage(task.getBuilder().getEvent(), victim, args);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private final class SilenceEffect extends AdvancedEffect {
        private SilenceEffect(JavaPlugin plugin) {
            super(plugin, "SILENCE",
                    "Apply addon-controlled Silence with Solitude modifiers",
                    "%e:<BASE_CHANCE>:<BASE_DURATION_SECONDS>:<COOLDOWN_SECONDS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }

            Event sourceEvent = task.getBuilder().getEvent();
            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    task.getBuilder().getMain());
            if (sourceEvent instanceof EntityDamageByEntityEvent) {
                attacker = firstValidLivingEntity(
                        getLivingDamager(((EntityDamageByEntityEvent) sourceEvent).getDamager()), attacker);
            }
            LivingEntity victim = firstValidLivingEntity(entity, task.getBuilder().getVictim());
            return applySilence(sourceEvent, attacker, victim, task.getBuilder().getItem(), args);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private static final class SolitudeEffect extends AdvancedEffect {
        private SolitudeEffect(JavaPlugin plugin) {
            super(plugin, "SOLITUDE",
                    "Declare addon Solitude chance and maximum Silence multiplier",
                    "%e:<PROC_CHANCE>:<MAX_MULTIPLIER>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class PerfectSolitudeEffect extends AdvancedEffect {
        private PerfectSolitudeEffect(JavaPlugin plugin) {
            super(plugin, "PERFECT_SOLITUDE",
                    "Declare addon Perfect Solitude chance and maximum Silence multiplier",
                    "%e:<PROC_CHANCE>:<MAX_MULTIPLIER>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class AlienHungerResistEffect extends AdvancedEffect {
        private AlienHungerResistEffect(JavaPlugin plugin) {
            super(plugin, "ALIEN_HUNGER_RESIST",
                    "Declare addon Alien Implants hunger-loss resistance chance",
                    "%e:<RESIST_CHANCE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private final class EnlightenedHealEffect extends AdvancedEffect {
        private EnlightenedHealEffect(JavaPlugin plugin) {
            super(plugin, "ENLIGHTENED_HEAL",
                    "Heal shortly after confirmed incoming damage",
                    "%e:<MIN_HEAL>:<MAX_HEAL>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return applyDelayedEnlightenedHeal(task, entity, args);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeEffect(task, (LivingEntity) null, args);
        }
    }

    private static final class BloodLustEffect extends AdvancedEffect {
        private BloodLustEffect(JavaPlugin plugin) {
            super(plugin, "BLOOD_LUST",
                    "Declare addon Blood Lust healing behavior for this enchant level",
                    "%e:<CHANCE>:<HEAL_AMOUNT>:<RADIUS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class AegisEffect extends AdvancedEffect {
        private AegisEffect(JavaPlugin plugin) {
            super(plugin, "AEGIS",
                    "Declare addon Aegis group damage reduction for this enchant level",
                    "%e:<INITIAL_ENEMIES>:<WINDOW_TICKS>:<REDUCTION_PERCENT>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class BloodLinkEffect extends AdvancedEffect {
        private BloodLinkEffect(JavaPlugin plugin) {
            super(plugin, "BLOOD_LINK",
                    "Declare addon Blood Link healing behavior for this enchant level",
                    "%e:<CHANCE>:<MIN_HEAL>:<MAX_HEAL>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class ValorEffect extends AdvancedEffect {
        private ValorEffect(JavaPlugin plugin) {
            super(plugin, "VALOR",
                    "Declare addon Valor reduction for this enchant level",
                    "%e:<REDUCTION_PERCENT>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class MartyrValorEffect extends AdvancedEffect {
        private MartyrValorEffect(JavaPlugin plugin) {
            super(plugin, "MARTYR_VALOR",
                    "Declare addon Martyr Valor reduction for this enchant level",
                    "%e:<REDUCTION_PERCENT>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class EnchantReflectEffect extends AdvancedEffect {
        private EnchantReflectEffect(JavaPlugin plugin) {
            super(plugin, "ENCHANT_REFLECT",
                    "Declare addon Enchant Reflect behavior for this enchant level",
                    "%e:<MAX_GROUP>:<CHANCE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class HeroicEnchantReflectEffect extends AdvancedEffect {
        private HeroicEnchantReflectEffect(JavaPlugin plugin) {
            super(plugin, "HEROIC_ENCHANT_REFLECT",
                    "Declare addon Heroic Enchant Reflect behavior for this enchant level",
                    "%e:<MAX_GROUP>:<CHANCE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class SoulHardenedEffect extends AdvancedEffect {
        private SoulHardenedEffect(JavaPlugin plugin) {
            super(plugin, "SOUL_HARDENED",
                    "Declare addon Soul Hardened soul-trap block behavior",
                    "%e[:<REPAIR_AMOUNT>]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class MetaphysicalEffect extends AdvancedEffect {
        private MetaphysicalEffect(JavaPlugin plugin) {
            super(plugin, "METAPHYSICAL",
                    "Declare addon Metaphysical slowness-resist behavior",
                    "%e[:<RESIST_CHANCE>]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class PolymorphicMetaphysicalEffect extends AdvancedEffect {
        private PolymorphicMetaphysicalEffect(JavaPlugin plugin) {
            super(plugin, "POLYMORPHIC_METAPHYSICAL",
                    "Declare addon Polymorphic Metaphysical slowness-resist behavior",
                    "%e[:<RESIST_CHANCE>]");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class CreeperArmorEffect extends AdvancedEffect {
        private CreeperArmorEffect(JavaPlugin plugin) {
            super(plugin, "CREEPER_ARMOR",
                    "Declare addon Creeper Armor protection for this enchant level",
                    "%e:<HEAL_CHANCE>:<MIN_HEAL>:<MAX_HEAL>:<NO_KNOCKBACK>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class CustomCreeperArmorEffect extends AdvancedEffect {
        private CustomCreeperArmorEffect(JavaPlugin plugin) {
            super(plugin, "CUSTOM_CREEPER_ARMOR",
                    "Declare addon Custom Creeper Armor protection for this enchant level",
                    "%e:<EXPLOSION_BLOCK_CHANCE>:<CUSTOM_CREEPER_BLOCK_CHANCE>:<SLOWNESS_BLOCK_CHANCE>:<NO_KNOCKBACK>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }
    }

    private static final class InertiaCleanseEffect extends AdvancedEffect {
        private InertiaCleanseEffect(JavaPlugin plugin) {
            super(plugin, "INERTIA_CLEANSE",
                    "Declare addon Inertia soul cost for this enchant level",
                    "%e:<SOULS_PER_CLEANSE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return true;
        }
    }

    private static final class ImmortalRepairEffect extends AdvancedEffect {
        private ImmortalRepairEffect(JavaPlugin plugin) {
            super(plugin, "IMMORTAL_REPAIR",
                    "Declare addon Immortal repair amount for this enchant level",
                    "%e:<DURABILITY_PER_PIECE>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return true;
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return true;
        }
    }

    private final class DiminishNextEffect extends AdvancedEffect {
        private DiminishNextEffect(JavaPlugin plugin) {
            super(plugin, "DIMINISH_NEXT",
                    "Store a next-hit damage cap from the current damage taken",
                    "%e");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return storeDiminishCap(task, entity, false);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return storeDiminishCap(task, null, false);
        }
    }

    private final class VengefulDiminishNextEffect extends AdvancedEffect {
        private VengefulDiminishNextEffect(JavaPlugin plugin) {
            super(plugin, "VENGEFUL_DIMINISH_NEXT",
                    "Store a next-hit damage cap and reflect overflow",
                    "%e");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return storeDiminishCap(task, entity, true);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return storeDiminishCap(task, null, true);
        }
    }

    private final class DimensionalShiftEffect extends AdvancedEffect {
        private DimensionalShiftEffect(JavaPlugin plugin) {
            super(plugin, "DIMENSIONAL_SHIFT", "Drop unstable dimensional blocks around the set wearer", "%e");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return executeDimensionalShift(task, entity, args);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeDimensionalShift(task, null, args);
        }

        private boolean executeDimensionalShift(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null) {
                return false;
            }

            LivingEntity victim = firstValidLivingEntity(
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task),
                    task.getBuilder().getMain(),
                    entity);
            LivingEntity attacker = firstValidLivingEntity(
                    task.getBuilder().getAttacker(),
                    getDimensionalShiftDamageAttacker(task),
                    getRecentDimensionalShiftDamageAttacker(victim),
                    entity != null && (victim == null || !entity.getUniqueId().equals(victim.getUniqueId())) ? entity : null);
            LivingEntity target = attacker != null && (victim == null || !attacker.equals(victim))
                    ? attacker
                    : entity == null ? null : getOtherEntity(entity, task);
            if (target != null && (target.isDead() || !target.isValid())) {
                target = null;
            }

            LivingEntity caster = firstValidLivingEntity(
                    victim != null && !victim.equals(target) ? victim : null,
                    entity != null && !entity.equals(target) ? entity : null,
                    task.getBuilder().getMain(),
                    victim,
                    target);
            if (caster == null || !caster.isValid() || caster.isDead()) {
                return false;
            }

            List<LivingEntity> targets = resolveDimensionalShiftTargets(caster, target);
            if (targets.isEmpty()) {
                return true;
            }

            String message = args != null && args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()
                    ? args[0]
                    : "default";
            return triggerConfiguredDimensionalShift(caster, targets, message);
        }
    }

    private final class WintersMercyEffect extends AdvancedEffect {
        private WintersMercyEffect(JavaPlugin plugin) {
            super(plugin, "WINTERS_MERCY", "Rain harmless snowballs and grant temporary damage reduction", "%e");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            return executeWinterMercy(task, entity, args);
        }

        @Override
        public boolean executeEffect(ExecutionTask task, Location location, String[] args) {
            return executeWinterMercy(task, null, args);
        }

        private boolean executeWinterMercy(ExecutionTask task, LivingEntity entity, String[] args) {
            if (task == null || task.getBuilder() == null) {
                return false;
            }

            LivingEntity victim = firstValidLivingEntity(
                    task.getBuilder().getVictim(),
                    getDimensionalShiftDamageVictim(task),
                    task.getBuilder().getMain(),
                    entity);
            LivingEntity target = firstValidLivingEntity(entity, victim, task.getBuilder().getMain());
            LivingEntity caster = firstValidLivingEntity(
                    victim != null && target != null && !victim.getUniqueId().equals(target.getUniqueId()) ? victim : null,
                    task.getBuilder().getMain(),
                    target);
            if (target == null || !target.isValid() || target.isDead()) {
                return true;
            }

            long now = System.currentTimeMillis();
            Long cooldownEndsAt = winterMercyCooldowns.get(target.getUniqueId());
            if (cooldownEndsAt != null && cooldownEndsAt > now) {
                return true;
            }
            if (cooldownEndsAt != null) {
                winterMercyCooldowns.remove(target.getUniqueId());
            }

            double chance = Math.max(0.0D, Math.min(100.0D,
                    getSetsConfig().getDouble("winter-mercy.chance-percent", 20.0D)));
            if (chance <= 0.0D || ThreadLocalRandom.current().nextDouble(100.0D) >= chance) {
                return true;
            }

            int cooldownSeconds = Math.max(0, getSetsConfig().getInt("winter-mercy.cooldown-seconds", 10));
            if (cooldownSeconds > 0) {
                winterMercyCooldowns.put(target.getUniqueId(), now + cooldownSeconds * 1000L);
            }

            String message = args != null && args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()
                    ? args[0]
                    : "default";
            return triggerConfiguredWinterMercy(caster, target, message);
        }
    }

    private final class DeathSaveEffect extends AdvancedEffect {
        private DeathSaveEffect(JavaPlugin plugin) {
            super(plugin, "DEATH_SAVE", "Cancel lethal damage and heal to full health", "%e:<HEALTH>:<SOULS>");
        }

        @Override
        public boolean executeEffect(ExecutionTask task, LivingEntity entity, String[] args) {
            Event event = task.getBuilder().getEvent();
            LivingEntity victim = entity;
            if (event instanceof EntityDamageEvent
                    && ((EntityDamageEvent) event).getEntity() instanceof LivingEntity) {
                victim = (LivingEntity) ((EntityDamageEvent) event).getEntity();
            }

            if (!isLethalDamageEvent(event, victim)) {
                return false;
            }

            int souls = resolveDeathSaveSouls(args);
            if (souls > 0) {
                if (!(victim instanceof Player)) {
                    return false;
                }

                if (!payDeathSaveSouls((Player) victim, task.getBuilder().getItem(), task.getBuilder().getItemType(), souls)) {
                    return false;
                }
            }

            EntityDamageEvent damageEvent = (EntityDamageEvent) event;
            damageEvent.setCancelled(true);

            double maxHealth = getMaxHealth(victim);
            if (!Double.isFinite(maxHealth) || maxHealth <= 0.0D) {
                return false;
            }

            double targetHealth = resolveDeathSaveHealth(args, maxHealth);
            victim.setHealth(Math.max(1.0D, Math.min(maxHealth, targetHealth)));
            return true;
        }
    }

    private static final class AdditiveDamageMultiplierState {
        private final double baseDamage;
        private double bonusMultiplier;

        private AdditiveDamageMultiplierState(double baseDamage) {
            this.baseDamage = baseDamage;
        }

        private void addMultiplier(double multiplier) {
            bonusMultiplier += Math.max(-1.0D, multiplier - 1.0D);
        }

        private double damage() {
            return baseDamage * Math.max(0.0D, 1.0D + bonusMultiplier);
        }
    }

    private static final class SilenceModifier {
        private final double multiplier;

        private SilenceModifier(double multiplier) {
            this.multiplier = multiplier;
        }

        private double multiplier() {
            return multiplier;
        }
    }

    private static final class ImmortalSource {
        private final RollItemType itemType;
        private final ItemStack item;
        private final int level;
        private final int repairAmount;

        private ImmortalSource(RollItemType itemType, ItemStack item, int level, int repairAmount) {
            this.itemType = itemType;
            this.item = item;
            this.level = level;
            this.repairAmount = repairAmount;
        }

        private RollItemType itemType() {
            return itemType;
        }

        private ItemStack item() {
            return item;
        }

        private int level() {
            return level;
        }

        private int repairAmount() {
            return repairAmount;
        }
    }

    private static final class BloodLustSettings {
        private final double chance;
        private final double healAmount;
        private final double radius;

        private BloodLustSettings(double chance, double healAmount, double radius) {
            this.chance = chance;
            this.healAmount = healAmount;
            this.radius = radius;
        }

        private double chance() {
            return chance;
        }

        private double healAmount() {
            return healAmount;
        }

        private double radius() {
            return radius;
        }
    }

    private static final class BloodLinkSettings {
        private final double chance;
        private final double minHeal;
        private final double maxHeal;

        private BloodLinkSettings(double chance, double minHeal, double maxHeal) {
            this.chance = chance;
            this.minHeal = minHeal;
            this.maxHeal = maxHeal;
        }

        private double chance() {
            return chance;
        }

        private double minHeal() {
            return minHeal;
        }

        private double maxHeal() {
            return maxHeal;
        }
    }

    private static final class AegisSettings {
        private final int initialEnemies;
        private final int windowTicks;
        private final double reductionPercent;

        private AegisSettings(int initialEnemies, int windowTicks, double reductionPercent) {
            this.initialEnemies = initialEnemies;
            this.windowTicks = windowTicks;
            this.reductionPercent = reductionPercent;
        }

        private int initialEnemies() {
            return initialEnemies;
        }

        private int windowTicks() {
            return windowTicks;
        }

        private double reductionPercent() {
            return reductionPercent;
        }
    }

    private static final class AegisAttackWindow {
        private final java.util.LinkedHashMap<UUID, Long> attackers = new java.util.LinkedHashMap<>();

        private boolean registerAndIsAdditional(UUID attackerId, long currentTick, int initialEnemies, int windowTicks) {
            prune(currentTick, windowTicks);
            if (attackerId == null) {
                return false;
            }

            int index = indexOf(attackerId);
            if (index >= 0) {
                attackers.put(attackerId, currentTick);
                return index >= Math.max(1, initialEnemies);
            }

            boolean additional = attackers.size() >= Math.max(1, initialEnemies);
            attackers.put(attackerId, currentTick);
            return additional;
        }

        private void prune(long currentTick, int windowTicks) {
            long safeWindow = Math.max(1L, windowTicks);
            java.util.Iterator<Map.Entry<UUID, Long>> iterator = attackers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                if (currentTick - entry.getValue() > safeWindow) {
                    iterator.remove();
                }
            }
        }

        private int indexOf(UUID attackerId) {
            int index = 0;
            for (UUID trackedId : attackers.keySet()) {
                if (trackedId.equals(attackerId)) {
                    return index;
                }
                index++;
            }
            return -1;
        }
    }

    private static final class DamageCapState {
        private final double maxDamage;
        private final boolean reflectOverflow;
        private final Event sourceEvent;

        private DamageCapState(double maxDamage, boolean reflectOverflow, Event sourceEvent) {
            this.maxDamage = maxDamage;
            this.reflectOverflow = reflectOverflow;
            this.sourceEvent = sourceEvent;
        }

        private double maxDamage() {
            return maxDamage;
        }

        private boolean reflectOverflow() {
            return reflectOverflow;
        }

        private Event sourceEvent() {
            return sourceEvent;
        }
    }

    private static final class EpidemicCarrierSettings {
        private final UUID ownerId;
        private final UUID targetId;
        private final int blindnessAmplifier;
        private final int blindnessTicks;
        private final int confusionAmplifier;
        private final int confusionTicks;
        private final int slownessAmplifier;
        private final int slownessTicks;

        private EpidemicCarrierSettings(UUID ownerId, UUID targetId,
                                        int blindnessAmplifier, int blindnessTicks,
                                        int confusionAmplifier, int confusionTicks,
                                        int slownessAmplifier, int slownessTicks) {
            this.ownerId = ownerId;
            this.targetId = targetId;
            this.blindnessAmplifier = blindnessAmplifier;
            this.blindnessTicks = blindnessTicks;
            this.confusionAmplifier = confusionAmplifier;
            this.confusionTicks = confusionTicks;
            this.slownessAmplifier = slownessAmplifier;
            this.slownessTicks = slownessTicks;
        }

        private UUID ownerId() {
            return ownerId;
        }

        private UUID targetId() {
            return targetId;
        }

        private int blindnessAmplifier() {
            return blindnessAmplifier;
        }

        private int blindnessTicks() {
            return blindnessTicks;
        }

        private int confusionAmplifier() {
            return confusionAmplifier;
        }

        private int confusionTicks() {
            return confusionTicks;
        }

        private int slownessAmplifier() {
            return slownessAmplifier;
        }

        private int slownessTicks() {
            return slownessTicks;
        }
    }

    private static final class CreeperArmorSettings {
        private final double explosionBlockChance;
        private final double healChance;
        private final double minHeal;
        private final double maxHeal;
        private final boolean noKnockback;

        private CreeperArmorSettings(double explosionBlockChance, double healChance, double minHeal,
                                     double maxHeal, boolean noKnockback) {
            this.explosionBlockChance = explosionBlockChance;
            this.healChance = healChance;
            this.minHeal = minHeal;
            this.maxHeal = maxHeal;
            this.noKnockback = noKnockback;
        }

        private double explosionBlockChance() {
            return explosionBlockChance;
        }

        private double healChance() {
            return healChance;
        }

        private double minHeal() {
            return minHeal;
        }

        private double maxHeal() {
            return maxHeal;
        }

        private boolean noKnockback() {
            return noKnockback;
        }
    }

    private static final class CustomCreeperArmorSettings {
        private final double explosionBlockChance;
        private final double customCreeperBlockChance;
        private final double slownessBlockChance;
        private final boolean noKnockback;

        private CustomCreeperArmorSettings(double explosionBlockChance, double customCreeperBlockChance,
                                           double slownessBlockChance, boolean noKnockback) {
            this.explosionBlockChance = explosionBlockChance;
            this.customCreeperBlockChance = customCreeperBlockChance;
            this.slownessBlockChance = slownessBlockChance;
            this.noKnockback = noKnockback;
        }

        private double explosionBlockChance() {
            return explosionBlockChance;
        }

        private double customCreeperBlockChance() {
            return customCreeperBlockChance;
        }

        private double slownessBlockChance() {
            return slownessBlockChance;
        }

        private boolean noKnockback() {
            return noKnockback;
        }
    }

    private static final class ReflectCandidate {
        private final boolean heroic;
        private final double chance;
        private final int maxRank;
        private final String enchantName;
        private final int cooldownSeconds;

        private ReflectCandidate(boolean heroic, double chance, int maxRank,
                                 String enchantName, int cooldownSeconds) {
            this.heroic = heroic;
            this.chance = chance;
            this.maxRank = maxRank;
            this.enchantName = enchantName;
            this.cooldownSeconds = cooldownSeconds;
        }

        private boolean heroic() {
            return heroic;
        }

        private double chance() {
            return chance;
        }

        private int maxRank() {
            return maxRank;
        }

        private String enchantName() {
            return enchantName;
        }

        private int cooldownSeconds() {
            return cooldownSeconds;
        }
    }

    private static final class ObsidianGuardianSettings {
        private final double damage;
        private final double pullStrength;
        private final int slowAmplifier;
        private final int slowTicks;

        private ObsidianGuardianSettings(double damage, double pullStrength, int slowAmplifier, int slowTicks) {
            this.damage = damage;
            this.pullStrength = pullStrength;
            this.slowAmplifier = slowAmplifier;
            this.slowTicks = slowTicks;
        }

        private static ObsidianGuardianSettings defaults() {
            return new ObsidianGuardianSettings(2.0D, 1.5D, 0, 40);
        }

        private double damage() {
            return damage;
        }

        private double pullStrength() {
            return pullStrength;
        }

        private int slowAmplifier() {
            return slowAmplifier;
        }

        private int slowTicks() {
            return slowTicks;
        }
    }

    private static final class DivineImmolationSession {
        private final UUID targetId;
        private UUID casterId;
        private long expiresAtTick;
        private double radius;
        private double damage;
        private int witherAmplifier;
        private int witherDurationTicks;
        private int burnSeconds;
        private boolean affectSummons;
        private BukkitTask task;

        private DivineImmolationSession(UUID targetId, UUID casterId, long expiresAtTick,
                                        double radius, double damage, int witherAmplifier,
                                        int witherDurationTicks, int burnSeconds, boolean affectSummons) {
            this.targetId = targetId;
            refresh(casterId, expiresAtTick, radius, damage, witherAmplifier,
                    witherDurationTicks, burnSeconds, affectSummons);
        }

        private void refresh(UUID casterId, long expiresAtTick, double radius, double damage,
                             int witherAmplifier, int witherDurationTicks, int burnSeconds,
                             boolean affectSummons) {
            this.casterId = casterId;
            this.expiresAtTick = expiresAtTick;
            this.radius = radius;
            this.damage = damage;
            this.witherAmplifier = witherAmplifier;
            this.witherDurationTicks = witherDurationTicks;
            this.burnSeconds = burnSeconds;
            this.affectSummons = affectSummons;
        }

        private UUID casterId() {
            return casterId;
        }

        private long expiresAtTick() {
            return expiresAtTick;
        }

        private double radius() {
            return radius;
        }

        private double damage() {
            return damage;
        }

        private int witherAmplifier() {
            return witherAmplifier;
        }

        private int witherDurationTicks() {
            return witherDurationTicks;
        }

        private int burnSeconds() {
            return burnSeconds;
        }

        private boolean affectSummons() {
            return affectSummons;
        }

        private void setTask(BukkitTask task) {
            this.task = task;
        }

        private void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }

    private static final class DestructionAuraSession {
        private final UUID wearerId;
        private UUID sourceId;
        private long expiresAtTick;
        private double damagePerTick;
        private double radius;
        private final java.util.Set<UUID> messagedTargets = new java.util.HashSet<>();
        private BukkitTask task;

        private DestructionAuraSession(UUID wearerId, UUID sourceId, long expiresAtTick,
                                       double damagePerTick, double radius) {
            this.wearerId = wearerId;
            refresh(sourceId, expiresAtTick, damagePerTick, radius);
        }

        private void refresh(UUID sourceId, long expiresAtTick, double damagePerTick, double radius) {
            this.sourceId = sourceId;
            this.expiresAtTick = Math.max(this.expiresAtTick, expiresAtTick);
            this.damagePerTick = Math.max(this.damagePerTick, damagePerTick);
            this.radius = Math.max(this.radius, radius);
        }

        private UUID sourceId() {
            return sourceId;
        }

        private long expiresAtTick() {
            return expiresAtTick;
        }

        private double damagePerTick() {
            return damagePerTick;
        }

        private double radius() {
            return radius;
        }

        private java.util.Set<UUID> messagedTargets() {
            return messagedTargets;
        }

        private void setTask(BukkitTask task) {
            this.task = task;
        }

        private void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }

    private static final class RageComboState {
        private final UUID victimId;
        private int count;
        private long lastTick;

        private RageComboState(UUID victimId, int count, long lastTick) {
            this.victimId = victimId;
            this.count = count;
            this.lastTick = lastTick;
        }

        private UUID victimId() {
            return victimId;
        }

        private int count() {
            return count;
        }

        private long lastTick() {
            return lastTick;
        }

        private void increment(int maxStacks, long currentTick) {
            count = Math.min(Math.max(1, maxStacks), count + 1);
            lastTick = currentTick;
        }

        private boolean expired(long currentTick, long resetTicks) {
            return lastTick >= 0L && currentTick - lastTick > resetTicks;
        }
    }

    private final class PendingSummonProtection {
        private final Location center;
        private final java.util.Set<EntityType> entityTypes;
        private final long expiresAtTick;
        private final UUID ownerId;

        private PendingSummonProtection(Location center, java.util.Set<EntityType> entityTypes,
                                        long expiresAtTick, UUID ownerId) {
            this.center = center;
            this.entityTypes = entityTypes == null ? Collections.emptySet() : new java.util.HashSet<>(entityTypes);
            this.expiresAtTick = expiresAtTick;
            this.ownerId = ownerId;
        }

        private boolean expired(long currentTick) {
            return currentTick > expiresAtTick;
        }

        private boolean matches(EntityType entityType, Location location) {
            if (center == null || location == null || center.getWorld() == null
                    || !center.getWorld().equals(location.getWorld())) {
                return false;
            }
            if (!entityTypes.isEmpty() && !entityTypes.contains(entityType)) {
                return false;
            }
            return center.distanceSquared(location) <= 16.0D;
        }

        private UUID ownerId() {
            return ownerId;
        }
    }


}

