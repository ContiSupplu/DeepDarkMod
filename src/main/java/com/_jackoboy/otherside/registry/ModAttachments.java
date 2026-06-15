package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * NeoForge Data Attachments for the Otherside mod.
 * Attachments are per-entity persistent data that survives relog and (optionally) death.
 */
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, OthersideMod.MOD_ID);

    /**
     * Per-player corruption level (0–100). Rises from Echo dimension exposure,
     * suppressed by light wards, cured only by golden apples.
     * Persistent (survives relog) and copyOnDeath (survives death).
     */
    public static final java.util.function.Supplier<AttachmentType<Float>> CORRUPTION =
            ATTACHMENT_TYPES.register("corruption", () ->
                    AttachmentType.builder(() -> 0.0F)
                            .serialize(Codec.FLOAT)
                            .copyOnDeath()
                            .build());
}
