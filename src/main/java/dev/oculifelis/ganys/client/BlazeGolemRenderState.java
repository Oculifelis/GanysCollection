package dev.oculifelis.ganys.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class BlazeGolemRenderState extends LivingEntityRenderState {
    /** Seconds since the last throw started, or -1. */
    public float attackTime = -1.0F;
}
