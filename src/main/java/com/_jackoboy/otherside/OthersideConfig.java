package com._jackoboy.otherside;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class OthersideConfig {
    public static final ServerConfig SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();
    }

    public static class ServerConfig {
        // Infection settings
        public final ModConfigSpec.IntValue spreadCycleTicks;
        public final ModConfigSpec.IntValue spreadBudgetPerCycle;
        public final ModConfigSpec.IntValue globalSpreadBudget;
        public final ModConfigSpec.DoubleValue lightResistChance;
        public final ModConfigSpec.DoubleValue deathConversionChance;
        public final ModConfigSpec.BooleanValue simulateUnloadedSpread;

        // Beast organism (W1 Worldbeast Rework — replaces phases)
        public final ModConfigSpec.ConfigValue<String> beastMassRails;
        public final ModConfigSpec.DoubleValue beastHungerBaseRate;
        public final ModConfigSpec.DoubleValue beastNutritionScale;
        public final ModConfigSpec.DoubleValue beastAttentionDecayRate;
        public final ModConfigSpec.IntValue beastAttentionDecayMinDist;
        public final ModConfigSpec.ConfigValue<String> beastTierThresholds;
        public final ModConfigSpec.DoubleValue beastPlayerBlockResistance;
        public final ModConfigSpec.IntValue beastPlayerBlockResistanceMassGate;
        public final ModConfigSpec.DoubleValue beastClaimThreshold;
        public final ModConfigSpec.IntValue beastSatedDurationTicks;
        public final ModConfigSpec.DoubleValue beastSatedSpreadMultiplier;
        public final ModConfigSpec.DoubleValue beastRailThrottleMultiplier;

        // W2 — Veins, orders, sores (§3.2, §3.3, §6)
        public final ModConfigSpec.IntValue veinGrowthIntervalTicks;
        public final ModConfigSpec.IntValue surgeCooldownTicks;
        public final ModConfigSpec.IntValue surgeLeadMaxSeconds;
        public final ModConfigSpec.IntValue surgeLeadMinSeconds;
        public final ModConfigSpec.IntValue surgeRadiusBlocks;
        public final ModConfigSpec.DoubleValue surgeMultiplier;
        public final ModConfigSpec.IntValue surgeDurationTicks;
        public final ModConfigSpec.IntValue breakoutLeadMaxSeconds;
        public final ModConfigSpec.IntValue breakoutLeadMinSeconds;
        public final ModConfigSpec.IntValue retaliationLeadSeconds;
        public final ModConfigSpec.DoubleValue retaliationMultiplier;
        public final ModConfigSpec.IntValue retaliationDurationTicks;
        public final ModConfigSpec.IntValue soreThreshold;
        public final ModConfigSpec.IntValue soreMinDistance;
        public final ModConfigSpec.DoubleValue soreSpreadBudgetFraction;
        public final ModConfigSpec.IntValue maxActiveSores;
        public final ModConfigSpec.IntValue severThreshold;
        public final ModConfigSpec.IntValue severDelayTicks;
        public final ModConfigSpec.IntValue painFlinchRadius;
        public final ModConfigSpec.IntValue trainRepeatIntervalTicks;

        // Director
        public final ModConfigSpec.BooleanValue directorChatFeed;
        public final ModConfigSpec.BooleanValue directorLogEnabled;

        // Mobs
        public final ModConfigSpec.BooleanValue stalkerEnabled;

        // Otherside
        public final ModConfigSpec.IntValue lanternFuelSeconds;
        public final ModConfigSpec.DoubleValue wardenDensity;

        // Portal Guardian
        public final ModConfigSpec.BooleanValue guardianEnabled;
        public final ModConfigSpec.BooleanValue guardianEveryIgnition;
        public final ModConfigSpec.IntValue leashRadius;
        public final ModConfigSpec.BooleanValue sealEnabled;
        public final ModConfigSpec.BooleanValue cinematicBlackout;
        public final ModConfigSpec.DoubleValue knockbackStrengthClose;
        public final ModConfigSpec.DoubleValue knockbackStrengthFar;
        public final ModConfigSpec.DoubleValue knockbackVertical;
        public final ModConfigSpec.DoubleValue knockbackRadius;

        // Boss
        public final ModConfigSpec.IntValue originHPPool;
        public final ModConfigSpec.IntValue blindnessPulseDuration;
        public final ModConfigSpec.IntValue blindnessSightWindow;

        // Endings
        public final ModConfigSpec.IntValue cleansingWaveSpeed;
        public final ModConfigSpec.BooleanValue whisperEpilogue;

        ServerConfig(ModConfigSpec.Builder builder) {
            builder.push("infection");
            spreadCycleTicks = builder.comment("Ticks between spread cycles (20 ticks = 1 second)")
                    .defineInRange("spreadCycleTicks", 60, 20, 1000);
            spreadBudgetPerCycle = builder.comment("Max blocks converted per breach per cycle")
                    .defineInRange("spreadBudgetPerCycle", 128, 1, 512);
            globalSpreadBudget = builder.comment("Global max blocks converted per cycle across all breaches")
                    .defineInRange("globalSpreadBudget", 512, 1, 2048);
            lightResistChance = builder.comment("Chance (0.0-1.0) that conversion fails in block-light >= 12")
                    .defineInRange("lightResistChance", 0.85, 0.0, 1.0);
            deathConversionChance = builder.comment("Chance a mob dying on sculk becomes corrupted")
                    .defineInRange("deathConversionChance", 0.4, 0.0, 1.0);
            simulateUnloadedSpread = builder.comment("Apply catch-up spread when unloaded chunks reload")
                    .define("simulateUnloadedSpread", false);
            builder.pop();

            // Beast organism — replaces old phases section
            builder.push("beast");
            beastMassRails = builder.comment(
                    "Weekly MASS soft caps (in-game weeks). Format: 'wk1,wk2,wk3,wk4,perWeekAfter'.",
                    "Default: 3% week 1, 7% week 2, 12% week 3, 18% week 4, +6% per week after.")
                    .define("massRails", "3,7,12,18,6");
            beastHungerBaseRate = builder.comment("Base hunger gain per minute (§2.2)")
                    .defineInRange("hungerBaseRate", 0.2, 0.0, 1.0);
            beastNutritionScale = builder.comment("Global multiplier for all nutrition values")
                    .defineInRange("nutritionScale", 1.0, 0.1, 5.0);
            beastAttentionDecayRate = builder.comment("Attention decay per minute when quiet and far from body (§2.4)")
                    .defineInRange("attentionDecayRate", 0.5, 0.0, 5.0);
            beastAttentionDecayMinDist = builder.comment("Min distance from body for attention decay (§2.4)")
                    .defineInRange("attentionDecayMinDist", 64, 16, 256);
            beastTierThresholds = builder.comment("Attention tier thresholds: HEARD,KNOWN,HUNTED (§2.4)")
                    .define("tierThresholds", "25,55,80");
            beastPlayerBlockResistance = builder.comment("Conversion chance for player-placed blocks when MASS >= gate (§3.1)")
                    .defineInRange("playerBlockResistance", 0.25, 0.0, 1.0);
            beastPlayerBlockResistanceMassGate = builder.comment("MASS threshold to arm player-block resistance (below = immune) (§3.1)")
                    .defineInRange("playerBlockResistanceMassGate", 12, 0, 100);
            beastClaimThreshold = builder.comment("Fraction of 16-point sample that must be beast_body for a chunk to count as claimed (§2.1)")
                    .defineInRange("claimThreshold", 0.6, 0.1, 1.0);
            beastSatedDurationTicks = builder.comment("Duration of SATED state in ticks (24000 = 1 in-game day) (§2.2)")
                    .defineInRange("satedDurationTicks", 36000, 1000, 96000);
            beastSatedSpreadMultiplier = builder.comment("Spread budget multiplier during SATED state (§2.2)")
                    .defineInRange("satedSpreadMultiplier", 0.35, 0.0, 1.0);
            beastRailThrottleMultiplier = builder.comment("Spread budget multiplier when rail-throttled (§2.1)")
                    .defineInRange("railThrottleMultiplier", 0.15, 0.0, 1.0);

            // W2 — Veins, orders, sores
            veinGrowthIntervalTicks = builder.comment("Base ticks between vein growth steps (1 block per interval) (§3.2)")
                    .defineInRange("veinGrowthIntervalTicks", 500, 100, 2400);
            surgeCooldownTicks = builder.comment("Minimum ticks between SURGE order issuance")
                    .defineInRange("surgeCooldownTicks", 6000, 1200, 24000);
            surgeLeadMaxSeconds = builder.comment("Max telegraph lead time for SURGE at ACUITY 0 (§3.2)")
                    .defineInRange("surgeLeadMaxSeconds", 180, 30, 600);
            surgeLeadMinSeconds = builder.comment("Min telegraph lead time for SURGE at ACUITY 100 (§3.2)")
                    .defineInRange("surgeLeadMinSeconds", 60, 10, 300);
            surgeRadiusBlocks = builder.comment("Radius around SURGE target for ×3 frontier scoring boost (§3.2)")
                    .defineInRange("surgeRadiusBlocks", 48, 16, 128);
            surgeMultiplier = builder.comment("Frontier score multiplier during active SURGE (§3.2)")
                    .defineInRange("surgeMultiplier", 3.0, 1.0, 10.0);
            surgeDurationTicks = builder.comment("Duration of SURGE regional boost in ticks (90s default) (§3.2)")
                    .defineInRange("surgeDurationTicks", 1800, 400, 6000);
            breakoutLeadMaxSeconds = builder.comment("Max telegraph lead time for BREAKOUT at ACUITY 0 (§3.2)")
                    .defineInRange("breakoutLeadMaxSeconds", 120, 30, 600);
            breakoutLeadMinSeconds = builder.comment("Min telegraph lead time for BREAKOUT at ACUITY 100 (§3.2)")
                    .defineInRange("breakoutLeadMinSeconds", 45, 10, 300);
            retaliationLeadSeconds = builder.comment("Fixed telegraph lead time for RETALIATION (§6)")
                    .defineInRange("retaliationLeadSeconds", 60, 10, 300);
            retaliationMultiplier = builder.comment("Frontier multiplier during RETALIATION (§6)")
                    .defineInRange("retaliationMultiplier", 2.0, 1.0, 10.0);
            retaliationDurationTicks = builder.comment("Duration of RETALIATION front boost (1 day default) (§6)")
                    .defineInRange("retaliationDurationTicks", 24000, 1200, 96000);
            soreThreshold = builder.comment("Minimum trigger score for a Sore eruption (§3.3)")
                    .defineInRange("soreThreshold", 40, 10, 200);
            soreMinDistance = builder.comment("Minimum blocks from existing sites for a new Sore (§3.3)")
                    .defineInRange("soreMinDistance", 160, 32, 512);
            soreSpreadBudgetFraction = builder.comment("Fraction of main spread budget for Sore breaches (§3.3)")
                    .defineInRange("soreSpreadBudgetFraction", 0.25, 0.05, 1.0);
            maxActiveSores = builder.comment("Maximum concurrent active Sores")
                    .defineInRange("maxActiveSores", 6, 1, 20);
            severThreshold = builder.comment("Cord blocks broken to trigger order severance (§3.2)")
                    .defineInRange("severThreshold", 6, 1, 20);
            severDelayTicks = builder.comment("Delay in ticks before severed order reroutes (5 min default) (§3.2)")
                    .defineInRange("severDelayTicks", 6000, 1200, 24000);
            painFlinchRadius = builder.comment("Radius for FLINCH pulseOmni on pain events (§6)")
                    .defineInRange("painFlinchRadius", 64, 16, 256);
            trainRepeatIntervalTicks = builder.comment("Ticks between pulse train repeats during order lead time (§3.2)")
                    .defineInRange("trainRepeatIntervalTicks", 600, 200, 2400);
            builder.pop();

            builder.push("director");
            directorChatFeed = builder.comment("Send director events to ops in chat")
                    .define("directorChatFeed", true);
            directorLogEnabled = builder.comment("Write director events to CSV log file")
                    .define("directorLogEnabled", true);
            builder.pop();

            builder.push("mobs");
            stalkerEnabled = builder.comment("Enable the Sculk Stalker entity")
                    .define("stalkerEnabled", true);
            builder.pop();

            builder.push("otherside");
            lanternFuelSeconds = builder.comment("Seconds of fuel per echo dust in the Echo Lantern")
                    .defineInRange("lanternFuelSeconds", 90, 10, 600);
            wardenDensity = builder.comment("Target Wardens per loaded chunk column in the Otherside")
                    .defineInRange("wardenDensity", 0.17, 0.01, 1.0);
            builder.pop();

            builder.push("portal");
            builder.push("guardian");
            guardianEnabled = builder.comment("Enable the Portal Guardian mini-boss on first ignition")
                    .define("guardianEnabled", true);
            guardianEveryIgnition = builder.comment("Spawn a guardian on every portal ignition, not just the first")
                    .define("guardianEveryIgnition", false);
            leashRadius = builder.comment("Max distance (blocks) the guardian will stray from the portal")
                    .defineInRange("leashRadius", 24, 8, 64);
            sealEnabled = builder.comment("Seal the portal frame during the guardian fight")
                    .define("sealEnabled", true);
            cinematicBlackout = builder.comment("Play cinematic blackout effect when the guardian spawns")
                    .define("cinematicBlackout", true);
            knockbackStrengthClose = builder.comment("Knockback strength for players near the portal")
                    .defineInRange("knockbackStrengthClose", 1.6, 0.0, 10.0);
            knockbackStrengthFar = builder.comment("Knockback strength for players far from the portal")
                    .defineInRange("knockbackStrengthFar", 0.3, 0.0, 10.0);
            knockbackVertical = builder.comment("Vertical knockback component")
                    .defineInRange("knockbackVertical", 0.35, 0.0, 5.0);
            knockbackRadius = builder.comment("Radius (blocks) of the spawn knockback wave")
                    .defineInRange("knockbackRadius", 12.0, 1.0, 64.0);
            builder.pop();
            builder.pop();

            builder.push("boss");
            originHPPool = builder.comment("HP per phase pool for the Origin boss (3 pools total)")
                    .defineInRange("originHPPool", 200, 50, 1000);
            blindnessPulseDuration = builder.comment("Duration in ticks of blindness pulses in Phase C")
                    .defineInRange("blindnessPulseDuration", 240, 40, 600);
            blindnessSightWindow = builder.comment("Duration in ticks of sight windows between blindness pulses")
                    .defineInRange("blindnessSightWindow", 160, 40, 600);
            builder.pop();

            builder.push("endings");
            cleansingWaveSpeed = builder.comment("Blocks per second for the Cleansing wave")
                    .defineInRange("cleansingWaveSpeed", 8, 1, 100);
            whisperEpilogue = builder.comment("Keep Whisper ambience active after Cleansing for filming")
                    .define("whisperEpilogue", false);
            builder.pop();
        }
    }
}
