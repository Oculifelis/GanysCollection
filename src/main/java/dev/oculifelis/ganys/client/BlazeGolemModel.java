package dev.oculifelis.ganys.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Map;

/**
 * The blaze golem's geometry model, posed each frame from its idle, walk and attack clips. Idle always plays, walk fades
 * in with walking speed, and attack plays over both for its length after each throw. The head also follows where the
 * golem looks.
 */
public class BlazeGolemModel extends EntityModel<BlazeGolemRenderState> {
    private static final float ATTACK_FADE = 0.25F;
    private final Map<String, ModelPart> parts;
    private final Map<String, MolangClips.Clip> clips;
    private final float walkBlocksPerSecond;
    private final ModelPart head;
    private final float[] pose = new float[9];

    public BlazeGolemModel(GeoModel geo, Map<String, MolangClips.Clip> clips, float walkBlocksPerSecond) {
        super(geo.root);
        this.parts = geo.parts;
        this.clips = clips;
        this.walkBlocksPerSecond = walkBlocksPerSecond > 0.0F ? walkBlocksPerSecond : 1.0F;
        this.head = geo.parts.get("head");
    }

    @Override
    public void setupAnim(BlazeGolemRenderState state) {
        super.setupAnim(state);
        MolangClips.Clip idle = clips.get("idle"), walk = clips.get("walk"), attack = clips.get("attack");
        float idleTime = state.ageInTicks / 20.0F;
        float walkTime = state.walkAnimationPos / 4.0F / walkBlocksPerSecond;
        float walkWeight = Mth.clamp((state.walkAnimationSpeed - 0.05F) / 0.25F, 0.0F, 1.0F);
        float attackWeight = 0.0F;
        if (attack != null && state.attackTime >= 0.0F && state.attackTime <= attack.length())
            attackWeight = Mth.clamp(Math.min(state.attackTime, attack.length() - state.attackTime) / ATTACK_FADE, 0.0F, 1.0F);
        for (Map.Entry<String, ModelPart> e : parts.entrySet()) {
            String bone = e.getKey();
            pose[0] = pose[1] = pose[2] = pose[3] = pose[4] = pose[5] = 0.0F;
            pose[6] = pose[7] = pose[8] = 1.0F;
            if (idle != null) idle.blend(bone, idleTime, 1.0F, pose);
            if (walk != null) walk.blend(bone, walkTime, walkWeight, pose);
            if (attack != null) attack.blend(bone, state.attackTime, attackWeight, pose);
            ModelPart part = e.getValue();
            part.offsetRotation(new Vector3f(rad(-pose[0]), rad(-pose[1]), rad(pose[2])));
            part.offsetPos(new Vector3f(-pose[3], -pose[4], pose[5]));
            part.offsetScale(new Vector3f(pose[6] - 1.0F, pose[7] - 1.0F, pose[8] - 1.0F));
        }
        if (head != null) {
            head.yRot += rad(state.yRot);
            head.xRot += rad(state.xRot);
        }
    }

    private static float rad(float degrees) {
        return degrees * Mth.DEG_TO_RAD;
    }
}
