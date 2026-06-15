package com._jackoboy.otherside.client;

/**
 * Client-side cache for the player's corruption level.
 * Updated by CorruptionSyncPayload from the server.
 * Read by CorruptionOverlay to render the vignette.
 */
public final class CorruptionClientData {
    private CorruptionClientData() {}

    /** Current corruption level (0–100), synced from server. */
    public static float currentCorruption = 0.0F;
}
