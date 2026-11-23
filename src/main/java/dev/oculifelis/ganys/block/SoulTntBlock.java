package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.SoulTnt;
import dev.oculifelis.ganys.entity.SoulPrimedTnt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

/** TNT that primes into {@link SoulPrimedTnt}; every vanilla way of lighting TNT goes through {@link #onCaughtFire}. */
public class SoulTntBlock extends TntBlock {
    public SoulTntBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (!(level instanceof ServerLevel server) || !server.getGameRules().get(GameRules.TNT_EXPLODES)) return false;
        SoulPrimedTnt tnt = SoulPrimedTnt.create(server, pos, state, igniter);
        tnt.setFuse(SoulTnt.of(this).fuse());
        server.addFreshEntity(tnt);
        server.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        server.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        return true;
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        if (!level.getGameRules().get(GameRules.TNT_EXPLODES)) return;
        SoulPrimedTnt tnt = SoulPrimedTnt.create(level, pos, defaultBlockState(), explosion.getIndirectSourceEntity());
        int fuse = SoulTnt.of(this).fuse();
        tnt.setFuse(level.getRandom().nextInt(Math.max(1, fuse / 4)) + fuse / 8);
        level.addFreshEntity(tnt);
    }
}
