package mekanism.common;

import javax.annotation.Nonnull;
import mekanism.api.IItemProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.item.IItemMekanism;
import mekanism.common.item.ItemAlloy;
import mekanism.common.item.ItemBalloon;
import mekanism.common.item.ItemConfigurationCard;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemControlCircuit;
import mekanism.common.item.ItemCraftingFormula;
import mekanism.common.item.ItemDictionary;
import mekanism.common.item.ItemEnergized;
import mekanism.common.item.ItemGaugeDropper;
import mekanism.common.item.ItemMekanism;
import mekanism.common.item.ItemNetworkReader;
import mekanism.common.item.ItemPortableTeleporter;
import mekanism.common.item.ItemProxy;
import mekanism.common.item.ItemResource;
import mekanism.common.item.ItemRobit;
import mekanism.common.item.ItemSeismicReader;
import mekanism.common.item.ItemTierInstaller;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.item.ItemWalkieTalkie;
import mekanism.common.item.gear.ItemArmoredJetpack;
import mekanism.common.item.gear.ItemAtomicDisassembler;
import mekanism.common.item.gear.ItemElectricBow;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemFreeRunners;
import mekanism.common.item.gear.ItemGasMask;
import mekanism.common.item.gear.ItemJetpack;
import mekanism.common.item.gear.ItemScubaTank;
import mekanism.common.resource.MiscResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tier.AlloyTier;
import mekanism.common.tier.BaseTier;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public enum MekanismItem implements IItemProvider {
    ITEM_PROXY(new ItemProxy()),

    ELECTRIC_BOW(new ItemElectricBow()),
    ROBIT(new ItemRobit()),
    ATOMIC_DISASSEMBLER(new ItemAtomicDisassembler()),
    ENERGY_TABLET(new ItemEnergized("energy_tablet", 1_000_000)),
    CONFIGURATOR(new ItemConfigurator()),
    NETWORK_READER(new ItemNetworkReader()),
    WALKIE_TALKIE(new ItemWalkieTalkie()),
    DICTIONARY(new ItemDictionary()),
    GAS_MASK(new ItemGasMask()),
    SCUBA_TANK(new ItemScubaTank()),
    PORTABLE_TELEPORTER(new ItemPortableTeleporter()),
    FREE_RUNNERS(new ItemFreeRunners()),
    CONFIGURATION_CARD(new ItemConfigurationCard()),
    CRAFTING_FORMULA(new ItemCraftingFormula()),
    SEISMIC_READER(new ItemSeismicReader()),
    FLAMETHROWER(new ItemFlamethrower()),
    GAUGE_DROPPER(new ItemGaugeDropper()),
    TELEPORTATION_CORE("teleportation_core"),
    ENRICHED_IRON("enriched_iron"),
    ELECTROLYTIC_CORE("electrolytic_core"),
    //TODO: pulps/wood? Doesn't really make sense so not adding it unless it is one of the standards
    SAWDUST("sawdust"),
    //TODO: Once more mods are on 1.14 see what standard for salt is, if we should have forge:foods/salt etc
    SALT("salt"),
    SUBSTRATE("substrate"),
    //TODO: Make sure we match the common spec
    BIO_FUEL("bio_fuel"),

    ENRICHED_CARBON(new ItemResource(ResourceType.ENRICHED, MiscResource.CARBON)),
    ENRICHED_REDSTONE(new ItemResource(ResourceType.ENRICHED, MiscResource.REDSTONE)),
    ENRICHED_DIAMOND(new ItemResource(ResourceType.ENRICHED, MiscResource.DIAMOND)),
    ENRICHED_OBSIDIAN(new ItemResource(ResourceType.ENRICHED, MiscResource.REFINED_OBSIDIAN)),

    SPEED_UPGRADE(new ItemUpgrade(Upgrade.SPEED)),
    ENERGY_UPGRADE(new ItemUpgrade(Upgrade.ENERGY)),
    FILTER_UPGRADE(new ItemUpgrade(Upgrade.FILTER)),
    MUFFLING_UPGRADE(new ItemUpgrade(Upgrade.MUFFLING)),
    GAS_UPGRADE(new ItemUpgrade(Upgrade.GAS)),
    ANCHOR_UPGRADE(new ItemUpgrade(Upgrade.ANCHOR)),

    //Alloy names are alloy_type for purposes of tab complete
    INFUSED_ALLOY(new ItemAlloy(AlloyTier.INFUSED)),
    REINFORCED_ALLOY(new ItemAlloy(AlloyTier.REINFORCED)),
    ATOMIC_ALLOY(new ItemAlloy(AlloyTier.ATOMIC)),

    BASIC_CONTROL_CIRCUIT(new ItemControlCircuit(BaseTier.BASIC)),
    ADVANCED_CONTROL_CIRCUIT(new ItemControlCircuit(BaseTier.ADVANCED)),
    ELITE_CONTROL_CIRCUIT(new ItemControlCircuit(BaseTier.ELITE)),
    ULTIMATE_CONTROL_CIRCUIT(new ItemControlCircuit(BaseTier.ULTIMATE)),

    JETPACK(new ItemJetpack()),
    ARMORED_JETPACK(new ItemArmoredJetpack()),

    BLACK_BALLOON(new ItemBalloon(EnumColor.BLACK)),
    RED_BALLOON(new ItemBalloon(EnumColor.RED)),
    GREEN_BALLOON(new ItemBalloon(EnumColor.DARK_GREEN)),
    BROWN_BALLOON(new ItemBalloon(EnumColor.BROWN)),
    BLUE_BALLOON(new ItemBalloon(EnumColor.DARK_BLUE)),
    PURPLE_BALLOON(new ItemBalloon(EnumColor.PURPLE)),
    CYAN_BALLOON(new ItemBalloon(EnumColor.DARK_AQUA)),
    LIGHT_GRAY_BALLOON(new ItemBalloon(EnumColor.GRAY)),
    GRAY_BALLOON(new ItemBalloon(EnumColor.DARK_GRAY)),
    PINK_BALLOON(new ItemBalloon(EnumColor.BRIGHT_PINK)),
    LIME_BALLOON(new ItemBalloon(EnumColor.BRIGHT_GREEN)),
    YELLOW_BALLOON(new ItemBalloon(EnumColor.YELLOW)),
    LIGHT_BLUE_BALLOON(new ItemBalloon(EnumColor.INDIGO)),
    MAGENTA_BALLOON(new ItemBalloon(EnumColor.PINK)),
    ORANGE_BALLOON(new ItemBalloon(EnumColor.ORANGE)),
    WHITE_BALLOON(new ItemBalloon(EnumColor.WHITE)),

    HDPE_PELLET("hdpe_pellet"),
    HDPE_ROD("hdpe_rod"),
    HDPE_SHEET("hdpe_sheet"),
    HDPE_STICK("hdpe_stick"),

    BASIC_TIER_INSTALLER(new ItemTierInstaller(BaseTier.BASIC)),
    ADVANCED_TIER_INSTALLER(new ItemTierInstaller(BaseTier.ADVANCED)),
    ELITE_TIER_INSTALLER(new ItemTierInstaller(BaseTier.ELITE)),
    ULTIMATE_TIER_INSTALLER(new ItemTierInstaller(BaseTier.ULTIMATE)),

    IRON_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.IRON)),
    GOLD_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.GOLD)),
    OSMIUM_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.OSMIUM)),
    COPPER_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.COPPER)),
    TIN_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.TIN)),
    SILVER_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.SILVER)),
    LEAD_CRYSTAL(new ItemResource(ResourceType.CRYSTAL, Resource.LEAD)),

    IRON_SHARD(new ItemResource(ResourceType.SHARD, Resource.IRON)),
    GOLD_SHARD(new ItemResource(ResourceType.SHARD, Resource.GOLD)),
    OSMIUM_SHARD(new ItemResource(ResourceType.SHARD, Resource.OSMIUM)),
    COPPER_SHARD(new ItemResource(ResourceType.SHARD, Resource.COPPER)),
    TIN_SHARD(new ItemResource(ResourceType.SHARD, Resource.TIN)),
    SILVER_SHARD(new ItemResource(ResourceType.SHARD, Resource.SILVER)),
    LEAD_SHARD(new ItemResource(ResourceType.SHARD, Resource.LEAD)),

    IRON_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.IRON)),
    GOLD_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.GOLD)),
    OSMIUM_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.OSMIUM)),
    COPPER_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.COPPER)),
    TIN_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.TIN)),
    SILVER_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.SILVER)),
    LEAD_CLUMP(new ItemResource(ResourceType.CLUMP, Resource.LEAD)),

    DIRTY_IRON_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.IRON)),
    DIRTY_GOLD_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.GOLD)),
    DIRTY_OSMIUM_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.OSMIUM)),
    DIRTY_COPPER_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.COPPER)),
    DIRTY_TIN_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.TIN)),
    DIRTY_SILVER_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.SILVER)),
    DIRTY_LEAD_DUST(new ItemResource(ResourceType.DIRTY_DUST, Resource.LEAD)),

    IRON_DUST(new ItemResource(ResourceType.DUST, Resource.IRON)),
    GOLD_DUST(new ItemResource(ResourceType.DUST, Resource.GOLD)),
    OSMIUM_DUST(new ItemResource(ResourceType.DUST, Resource.OSMIUM)),
    COPPER_DUST(new ItemResource(ResourceType.DUST, Resource.COPPER)),
    TIN_DUST(new ItemResource(ResourceType.DUST, Resource.TIN)),
    SILVER_DUST(new ItemResource(ResourceType.DUST, Resource.SILVER)),
    LEAD_DUST(new ItemResource(ResourceType.DUST, Resource.LEAD)),

    DIAMOND_DUST(new ItemResource(ResourceType.DUST, MiscResource.DIAMOND)),
    STEEL_DUST(new ItemResource(ResourceType.DUST, MiscResource.STEEL)),
    SULFUR_DUST(new ItemResource(ResourceType.DUST, MiscResource.SULFUR)),
    LITHIUM_DUST(new ItemResource(ResourceType.DUST, MiscResource.LITHIUM)),
    REFINED_OBSIDIAN_DUST(new ItemResource(ResourceType.DUST, MiscResource.REFINED_OBSIDIAN)),
    OBSIDIAN_DUST(new ItemResource(ResourceType.DUST, MiscResource.OBSIDIAN)),

    REFINED_OBSIDIAN_INGOT(new ItemResource(ResourceType.INGOT, MiscResource.REFINED_OBSIDIAN)),
    OSMIUM_INGOT(new ItemResource(ResourceType.INGOT, Resource.OSMIUM)),
    BRONZE_INGOT(new ItemResource(ResourceType.INGOT, MiscResource.BRONZE)),
    REFINED_GLOWSTONE_INGOT(new ItemResource(ResourceType.INGOT, MiscResource.REFINED_GLOWSTONE)),
    STEEL_INGOT(new ItemResource(ResourceType.INGOT, MiscResource.STEEL)),
    COPPER_INGOT(new ItemResource(ResourceType.INGOT, Resource.COPPER)),
    TIN_INGOT(new ItemResource(ResourceType.INGOT, Resource.TIN)),

    REFINED_OBSIDIAN_NUGGET(new ItemResource(ResourceType.NUGGET, MiscResource.REFINED_OBSIDIAN)),
    OSMIUM_NUGGET(new ItemResource(ResourceType.NUGGET, Resource.OSMIUM)),
    BRONZE_NUGGET(new ItemResource(ResourceType.NUGGET, MiscResource.BRONZE)),
    REFINED_GLOWSTONE_NUGGET(new ItemResource(ResourceType.NUGGET, MiscResource.REFINED_GLOWSTONE)),
    STEEL_NUGGET(new ItemResource(ResourceType.NUGGET, MiscResource.STEEL)),
    COPPER_NUGGET(new ItemResource(ResourceType.NUGGET, Resource.COPPER)),
    TIN_NUGGET(new ItemResource(ResourceType.NUGGET, Resource.TIN));

    @Nonnull
    private final Item item;

    MekanismItem(String name) {
        this(new ItemMekanism(name));
    }

    <ITEM extends Item & IItemMekanism> MekanismItem(@Nonnull ITEM item) {
        //TODO: Should item be a consumer for registration that takes a ItemProperties?
        // If it works would make it easier to ensure creative tab is set properly
        this.item = item;
    }

    @Override
    @Nonnull
    public Item getItem() {
        return item;
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        for (MekanismItem mekanismItem : values()) {
            registry.register(mekanismItem.getItem());
        }
    }
}