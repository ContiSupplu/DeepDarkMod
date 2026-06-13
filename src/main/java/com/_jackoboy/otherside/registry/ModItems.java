package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.item.IgnitionSparkItem;
import com._jackoboy.otherside.item.ResonantCatalystItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OthersideMod.MOD_ID);

    // Echo materials
    public static final DeferredItem<Item> ECHO_DUST = ITEMS.register("echo_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ECHO_SHARD_CLUSTER = ITEMS.register("echo_shard_cluster",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> RESONANT_CORE = ITEMS.register("resonant_core",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> STALKER_SINEW = ITEMS.register("stalker_sinew",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TAINTED_FLESH = ITEMS.register("tainted_flesh",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.1F)
                            .build())));

    // Portal items
    public static final DeferredItem<Item> RESONANT_CATALYST = ITEMS.register("resonant_catalyst",
            () -> new ResonantCatalystItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredItem<Item> IGNITION_SPARK = ITEMS.register("ignition_spark",
            () -> new IgnitionSparkItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final DeferredItem<Item> WARDEN_HEARTCATALYST = ITEMS.register("warden_heartcatalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> PURGE_CHARGE = ITEMS.register("purge_charge",
            () -> new Item(new Item.Properties().stacksTo(16)));
}

