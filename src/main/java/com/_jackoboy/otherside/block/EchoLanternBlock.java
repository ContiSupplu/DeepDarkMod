package com._jackoboy.otherside.block;

import com._jackoboy.otherside.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com._jackoboy.otherside.registry.ModBlockEntityTypes;

import javax.annotation.Nullable;

/**
 * Echo Lantern — a fuel-based lantern that only burns in the Otherside dimension.
 *
 * Blockstate properties:
 *   - HANGING (boolean): whether the lantern hangs from above
 *   - LIT (enum: out, low, full): current light state
 */
public class EchoLanternBlock extends BaseEntityBlock {
    public static final MapCodec<EchoLanternBlock> CODEC = simpleCodec(EchoLanternBlock::new);

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final EnumProperty<LitState> LIT = EnumProperty.create("lit", LitState.class);

    // Vanilla lantern shapes
    private static final VoxelShape STANDING_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 9.0, 11.0);
    private static final VoxelShape HANGING_SHAPE  = Block.box(5.0, 1.0, 5.0, 11.0, 10.0, 11.0);

    /** Max fuel ticks the block entity can hold (15 minutes). */
    public static final int MAX_FUEL = 18000;
    /** Fuel added per echo_dust refuel interaction. */
    public static final int REFUEL_AMOUNT = 1800;

    /**
     * Enum representing the three lit states of the Echo Lantern.
     * Serialized as "out", "low", "full" to match blockstate JSON.
     */
    public enum LitState implements StringRepresentable {
        OUT("out"),
        LOW("low"),
        FULL("full");

        private final String name;

        LitState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public EchoLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HANGING, false)
                .setValue(LIT, LitState.FULL));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // ── Block-state definition ──────────────────────────────────────────────────

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean hanging = false;
        Direction clickedFace = ctx.getClickedFace();
        if (clickedFace == Direction.UP) {
            hanging = false;
        } else if (clickedFace == Direction.DOWN) {
            hanging = true;
        } else {
            BlockPos above = ctx.getClickedPos().above();
            if (ctx.getLevel().getBlockState(above).isFaceSturdy(ctx.getLevel(), above, Direction.DOWN)) {
                hanging = true;
            }
        }
        return this.defaultBlockState()
                .setValue(HANGING, hanging)
                .setValue(LIT, LitState.FULL);
    }

    // ── Shape / survival ────────────────────────────────────────────────────────

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction support = state.getValue(HANGING) ? Direction.UP : Direction.DOWN;
        return canSupportCenter(level, pos.relative(support), support.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction support = state.getValue(HANGING) ? Direction.UP : Direction.DOWN;
        if (direction == support && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    // ── Light level ─────────────────────────────────────────────────────────────

    /**
     * Returns light emission based on LitState.
     * Called from BlockBehaviour.Properties.lightLevel() lambda — see ModBlocks.
     */
    public static int getLightEmission(BlockState state) {
        return switch (state.getValue(LIT)) {
            case FULL -> 14;
            case LOW  -> 8;
            case OUT  -> 0;
        };
    }

    // ── Rendering ───────────────────────────────────────────────────────────────

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // ── Block entity ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EchoLanternBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntityTypes.ECHO_LANTERN.get(), EchoLanternBlockEntity::serverTick);
    }

    // ── Interaction: refuel with echo_dust ───────────────────────────────────────

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        if (stack.is(ModItems.ECHO_DUST.get())) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof EchoLanternBlockEntity lanternBE) {
                    if (lanternBE.getFuel() < MAX_FUEL) {
                        lanternBE.refuel(REFUEL_AMOUNT);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                                SoundSource.BLOCKS, 1.0F, 1.4F);
                    }
                }
            }
            if (level.isClientSide()) {
                double cx = pos.getX() + 0.5;
                double cy = pos.getY() + 0.5;
                double cz = pos.getZ() + 0.5;
                for (int i = 0; i < 8; i++) {
                    level.addParticle(ParticleTypes.SCULK_SOUL,
                            cx + (level.random.nextDouble() - 0.5) * 0.6,
                            cy + level.random.nextDouble() * 0.4,
                            cz + (level.random.nextDouble() - 0.5) * 0.6,
                            0.0, 0.04, 0.0);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
