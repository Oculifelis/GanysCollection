package dev.oculifelis.ganys.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * Gany's Surface coloured redstone dust. It works like vanilla dust but only links up with dust and blocks of its own
 * colour: it never connects to, powers or takes power from vanilla dust, redstone blocks or other colours, so wires of
 * different colours can run side by side. Repeaters, torches, levers and the rest work with it as with vanilla dust.
 */
public class ColouredRedstoneWireBlock extends RedStoneWireBlock {
    private final DyeColor colour;
    private final BlockState crossState;
    private boolean shouldSignal = true;

    public ColouredRedstoneWireBlock(DyeColor colour, Properties properties) {
        super(properties);
        this.colour = colour;
        this.crossState = this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE).setValue(EAST, RedstoneSide.SIDE)
                .setValue(SOUTH, RedstoneSide.SIDE).setValue(WEST, RedstoneSide.SIDE);
    }

    public DyeColor colour() {
        return colour;
    }

    /** True for redstone that must stay apart from this colour: vanilla dust and blocks, and other colours. */
    public boolean foreign(BlockState state) {
        if (state.is(Blocks.REDSTONE_WIRE) || state.is(Blocks.REDSTONE_BLOCK)) return true;
        if (state.getBlock() instanceof ColouredRedstoneWireBlock w) return w.colour != colour;
        if (state.getBlock() instanceof ColouredRedstoneBlock b) return b.colour() != colour;
        return false;
    }

    /** True for wire that must not link to the given colour (null = vanilla dust). */
    static boolean wireApart(BlockState state, @Nullable DyeColor colour) {
        if (state.is(Blocks.REDSTONE_WIRE)) return colour != null;
        if (state.getBlock() instanceof ColouredRedstoneWireBlock w) return w.colour != colour;
        return false;
    }

    /** Asked by a wire next to this one whether to link; a null direction is a diagonal (up or down a step) check. */
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (direction != null) return !wireApart(level.getBlockState(pos.relative(direction.getOpposite())), colour);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (wireApart(level.getBlockState(pos.relative(d).above()), colour)) return false;
            if (wireApart(level.getBlockState(pos.relative(d).below()), colour)) return false;
        }
        return true;
    }

    // connection shape: vanilla's logic, with foreign redstone treated as nothing

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connectionState(context.getLevel(), crossState, context.getClickedPos());
    }

    private BlockState connectionState(BlockGetter level, BlockState state, BlockPos pos) {
        boolean wasDot = isDot(state);
        state = missingConnections(level, defaultBlockState().setValue(POWER, state.getValue(POWER)), pos);
        if (wasDot && isDot(state)) return state;
        boolean north = state.getValue(NORTH).isConnected();
        boolean south = state.getValue(SOUTH).isConnected();
        boolean east = state.getValue(EAST).isConnected();
        boolean west = state.getValue(WEST).isConnected();
        boolean nsEmpty = !north && !south;
        boolean ewEmpty = !east && !west;
        if (!west && nsEmpty) state = state.setValue(WEST, RedstoneSide.SIDE);
        if (!east && nsEmpty) state = state.setValue(EAST, RedstoneSide.SIDE);
        if (!north && ewEmpty) state = state.setValue(NORTH, RedstoneSide.SIDE);
        if (!south && ewEmpty) state = state.setValue(SOUTH, RedstoneSide.SIDE);
        return state;
    }

    private BlockState missingConnections(BlockGetter level, BlockState state, BlockPos pos) {
        boolean canConnectUp = !level.getBlockState(pos.above()).isRedstoneConductor(level, pos);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(PROPERTY_BY_DIRECTION.get(d)).isConnected()) {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(d), connectingSide(level, pos, d, canConnectUp));
            }
        }
        return state;
    }

    private RedstoneSide connectingSide(BlockGetter level, BlockPos pos, Direction direction, boolean canConnectUp) {
        BlockPos rel = pos.relative(direction);
        BlockState relState = level.getBlockState(rel);
        if (canConnectUp) {
            boolean placeableAbove = relState.getBlock() instanceof TrapDoorBlock || canSurviveOn(level, rel, relState);
            BlockState above = level.getBlockState(rel.above());
            if (placeableAbove && !foreign(above) && above.canRedstoneConnectTo(level, rel.above(), null)) {
                return relState.isFaceSturdy(level, rel, direction.getOpposite()) ? RedstoneSide.UP : RedstoneSide.SIDE;
            }
        }
        if (!foreign(relState) && relState.canRedstoneConnectTo(level, rel, direction)) return RedstoneSide.SIDE;
        if (relState.isRedstoneConductor(level, rel)) return RedstoneSide.NONE;
        BlockState below = level.getBlockState(rel.below());
        return !foreign(below) && below.canRedstoneConnectTo(level, rel.below(), null) ? RedstoneSide.SIDE : RedstoneSide.NONE;
    }

    private static boolean isCross(BlockState s) {
        return s.getValue(NORTH).isConnected() && s.getValue(SOUTH).isConnected() && s.getValue(EAST).isConnected() && s.getValue(WEST).isConnected();
    }

    private static boolean isDot(BlockState s) {
        return !s.getValue(NORTH).isConnected() && !s.getValue(SOUTH).isConnected() && !s.getValue(EAST).isConnected() && !s.getValue(WEST).isConnected();
    }

    /** Sits on any sturdy top, a hopper or glowstone, as the original did. */
    private static boolean canSurviveOn(BlockGetter level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.UP) || state.is(Blocks.HOPPER) || state.is(Blocks.GLOWSTONE);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveOn(level, pos.below(), level.getBlockState(pos.below()));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction dir,
                                     BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (dir == Direction.DOWN) return canSurviveOn(level, neighbourPos, neighbourState) ? state : Blocks.AIR.defaultBlockState();
        if (dir == Direction.UP) return connectionState(level, state, pos);
        RedstoneSide side = connectingSide(level, pos, dir, !level.getBlockState(pos.above()).isRedstoneConductor(level, pos));
        return side.isConnected() == state.getValue(PROPERTY_BY_DIRECTION.get(dir)).isConnected() && !isCross(state)
                ? state.setValue(PROPERTY_BY_DIRECTION.get(dir), side)
                : connectionState(level, crossState.setValue(POWER, state.getValue(POWER)).setValue(PROPERTY_BY_DIRECTION.get(dir), side), pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild || !(isCross(state) || isDot(state))) return InteractionResult.PASS;
        BlockState next = (isCross(state) ? defaultBlockState() : crossState).setValue(POWER, state.getValue(POWER));
        next = connectionState(level, next, pos);
        if (next == state) return InteractionResult.PASS;
        level.setBlock(pos, next, 3);
        Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, null, Direction.UP);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos rel = pos.relative(d);
            if (state.getValue(PROPERTY_BY_DIRECTION.get(d)).isConnected() != next.getValue(PROPERTY_BY_DIRECTION.get(d)).isConnected()
                    && level.getBlockState(rel).isRedstoneConductor(level, rel)) {
                level.updateNeighborsAtExceptFromFacing(rel, next.getBlock(), d.getOpposite(), ExperimentalRedstoneUtils.withFront(orientation, d));
            }
        }
        return InteractionResult.SUCCESS;
    }

    // power: same-colour wire passes power through the vanilla evaluator (it matches on this block only)

    /** Power from neighbours, skipping vanilla dust, redstone blocks and other colours next to this wire. */
    @Override
    public int getBlockSignal(Level level, BlockPos pos) {
        shouldSignal = false;
        int best = 0;
        for (Direction d : Direction.values()) {
            BlockPos rel = pos.relative(d);
            if (foreign(level.getBlockState(rel))) continue;
            best = Math.max(best, level.getSignal(rel, d));
            if (best >= 15) break;
        }
        shouldSignal = true;
        return best;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return shouldSignal;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return shouldSignal ? state.getSignal(level, pos, direction) : 0;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!shouldSignal || direction == Direction.DOWN) return 0;
        int power = state.getValue(POWER);
        if (power == 0) return 0;
        if (foreign(level.getBlockState(pos.relative(direction.getOpposite())))) return 0;
        if (direction != Direction.UP && !connectionState(level, state, pos).getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite())).isConnected()) return 0;
        return power;
    }

    // looks

    /** The dye colour, darker at low power, like vanilla dust. */
    public int colourForPower(int power) {
        float f = power == 0 ? 0.3F : 0.4F + 0.6F * power / 15.0F;
        int rgb = colour.getTextureDiffuseColor();
        return ARGB.color(255, (int) (ARGB.red(rgb) * f), (int) (ARGB.green(rgb) * f), (int) (ARGB.blue(rgb) * f));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int power = state.getValue(POWER);
        if (power == 0) return;
        int c = colourForPower(power);
        for (Direction h : Direction.Plane.HORIZONTAL) {
            switch (state.getValue(PROPERTY_BY_DIRECTION.get(h))) {
                case UP:
                    particles(level, random, pos, c, h, Direction.UP, -0.5F, 0.5F);
                case SIDE:
                    particles(level, random, pos, c, Direction.DOWN, h, 0.0F, 0.5F);
                    break;
                default:
                    particles(level, random, pos, c, Direction.DOWN, h, 0.0F, 0.3F);
            }
        }
    }

    private static void particles(Level level, RandomSource random, BlockPos pos, int colour, Direction side, Direction along, float from, float to) {
        float span = to - from;
        if (random.nextFloat() >= 0.2F * span) return;
        float at = from + span * random.nextFloat();
        double x = 0.5 + 0.4375F * side.getStepX() + at * along.getStepX();
        double y = 0.5 + 0.4375F * side.getStepY() + at * along.getStepY();
        double z = 0.5 + 0.4375F * side.getStepZ() + at * along.getStepZ();
        level.addParticle(new DustParticleOptions(colour, 1.0F), pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0.0, 0.0, 0.0);
    }
}
