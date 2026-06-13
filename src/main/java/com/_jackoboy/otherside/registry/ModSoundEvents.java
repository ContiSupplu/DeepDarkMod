package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, OthersideMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PHASE_ADVANCE =
            registerSound("phase_advance");
    public static final DeferredHolder<SoundEvent, SoundEvent> WHISPER_MESSAGE =
            registerSound("whisper_message");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_CHARGE =
            registerSound("portal_charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_IGNITE =
            registerSound("portal_ignite");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_AMBIENT_LOOP =
            registerSound("portal_ambient_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_HEARTBEAT =
            registerSound("stalker_heartbeat");
    public static final DeferredHolder<SoundEvent, SoundEvent> LANTERN_FLICKER =
            registerSound("lantern_flicker");
    public static final DeferredHolder<SoundEvent, SoundEvent> LANTERN_DIE =
            registerSound("lantern_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPIRE_DEATH =
            registerSound("spire_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORIGIN_HEARTBEAT_LOOP =
            registerSound("origin_heartbeat_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEANSING_CHIME =
            registerSound("cleansing_chime");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_CHARGE_SWELL =
            registerSound("portal_charge_swell");
    public static final DeferredHolder<SoundEvent, SoundEvent> OTHERSIDE_AMBIENT_LOOP =
            registerSound("otherside_ambient_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARRIVAL_WHISPER =
            registerSound("arrival_whisper");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTRY_MUSIC =
            registerSound("entry_music");

    // W4: Listening Bloom sounds
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_UNFURL =
            registerSound("entity.bloom.unfurl");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_FOLD =
            registerSound("entity.bloom.fold");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_TWITCH =
            registerSound("entity.bloom.twitch");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_ALERT =
            registerSound("entity.bloom.alert");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_TREMOR =
            registerSound("entity.bloom.tremor");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOM_PRESENCE =
            registerSound("entity.bloom.presence");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
