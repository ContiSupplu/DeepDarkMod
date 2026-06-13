package com._jackoboy.otherside.infection;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import java.util.*;

public class BreachData {
    private final BlockPos cityOrigin;
    private BlockPos surfaceBreakout;
    private int columnProgress; // 0 to max height
    private int columnMaxHeight;
    private boolean columnComplete;
    private final Set<Long> frontier = new LinkedHashSet<>(); // packed BlockPos
    private int noiseCharge;
    private boolean active;
    private float budgetFraction = 1.0f; // W2: Sores use 0.25 of main budget

    public BreachData(BlockPos cityOrigin) {
        this.cityOrigin = cityOrigin;
        this.columnProgress = 0;
        this.columnMaxHeight = 0;
        this.columnComplete = false;
        this.noiseCharge = 0;
        this.active = true;
    }

    // Getters/setters
    public BlockPos getCityOrigin() { return cityOrigin; }
    public BlockPos getSurfaceBreakout() { return surfaceBreakout; }
    public void setSurfaceBreakout(BlockPos pos) { this.surfaceBreakout = pos; }
    public int getColumnProgress() { return columnProgress; }
    public void setColumnProgress(int p) { this.columnProgress = p; }
    public int getColumnMaxHeight() { return columnMaxHeight; }
    public void setColumnMaxHeight(int h) { this.columnMaxHeight = h; }
    public boolean isColumnComplete() { return columnComplete; }
    public void setColumnComplete(boolean c) { this.columnComplete = c; }
    public Set<Long> getFrontier() { return frontier; }
    public int getNoiseCharge() { return noiseCharge; }
    public void addNoiseCharge(int amount) { this.noiseCharge = Math.min(noiseCharge + amount, 3200); }
    public void decayNoiseCharge(int amount) { this.noiseCharge = Math.max(0, noiseCharge - amount); }
    public int consumeNoiseBonus() { int bonus = Math.min(noiseCharge / 100, 32); noiseCharge %= 100; return bonus; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }
    public float getBudgetFraction() { return budgetFraction; }
    public void setBudgetFraction(float f) { this.budgetFraction = f; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("cityOrigin", cityOrigin.asLong());
        if (surfaceBreakout != null) tag.putLong("surfaceBreakout", surfaceBreakout.asLong());
        tag.putInt("columnProgress", columnProgress);
        tag.putInt("columnMaxHeight", columnMaxHeight);
        tag.putBoolean("columnComplete", columnComplete);
        tag.putLongArray("frontier", new ArrayList<>(frontier).stream().mapToLong(Long::longValue).toArray());
        tag.putInt("noiseCharge", noiseCharge);
        tag.putBoolean("active", active);
        tag.putFloat("budgetFraction", budgetFraction);
        return tag;
    }

    public static BreachData load(CompoundTag tag) {
        BreachData data = new BreachData(BlockPos.of(tag.getLong("cityOrigin")));
        if (tag.contains("surfaceBreakout")) data.surfaceBreakout = BlockPos.of(tag.getLong("surfaceBreakout"));
        data.columnProgress = tag.getInt("columnProgress");
        data.columnMaxHeight = tag.getInt("columnMaxHeight");
        data.columnComplete = tag.getBoolean("columnComplete");
        for (long l : tag.getLongArray("frontier")) data.frontier.add(l);
        data.noiseCharge = tag.getInt("noiseCharge");
        data.active = tag.getBoolean("active");
        data.budgetFraction = tag.contains("budgetFraction") ? tag.getFloat("budgetFraction") : 1.0f;
        return data;
    }
}
