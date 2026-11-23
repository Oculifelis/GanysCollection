package dev.oculifelis.ganys.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import dev.oculifelis.ganys.GanysCollection;
import dev.oculifelis.ganys.entity.BlazeGolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.util.Map;
import java.util.Optional;

/** Draws the blaze golem from its geometry and animation files, with the glow skin as an always-lit layer. */
public class BlazeGolemRenderer extends MobRenderer<BlazeGolem, BlazeGolemRenderState, BlazeGolemModel> {
    private static final Identifier TEXTURE = GanysCollection.id("textures/entity/blaze_golem.png");
    private static final Identifier GLOW = GanysCollection.id("textures/entity/blaze_golem_glow.png");
    private static final Identifier GEO = GanysCollection.id("geo/blaze_golem.geo.json");
    private static final Identifier ANIMATIONS = GanysCollection.id("animations/blaze_golem.animation.json");
    private static final Identifier ANIM_DATA = GanysCollection.id("animations/blaze_golem.anim_data.json");

    public BlazeGolemRenderer(EntityRendererProvider.Context context) {
        super(context, load(context.getResourceManager()), 1.0F);
        addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderTypes.eyes(GLOW);
            }
        });
    }

    private static BlazeGolemModel load(ResourceManager resources) {
        JsonObject geo = json(resources, GEO), anims = json(resources, ANIMATIONS), data = json(resources, ANIM_DATA);
        GeoModel model = geo != null ? GeoModel.bake(geo) : GeoModel.empty();
        Map<String, MolangClips.Clip> clips = anims != null ? MolangClips.read(anims) : Map.of();
        float bps = 1.0F;
        if (data != null && data.has("animations") && data.getAsJsonObject("animations").has("walk")) {
            JsonObject walk = data.getAsJsonObject("animations").getAsJsonObject("walk");
            if (walk.has("blocks_per_second")) bps = walk.get("blocks_per_second").getAsFloat();
        }
        return new BlazeGolemModel(model, clips, bps);
    }

    private static @Nullable JsonObject json(ResourceManager resources, Identifier id) {
        Optional<Resource> res = resources.getResource(id);
        if (res.isEmpty()) {
            LogUtils.getLogger().error("Missing blaze golem resource {}", id);
            return null;
        }
        try (Reader reader = res.get().openAsReader()) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            LogUtils.getLogger().error("Could not read blaze golem resource {}", id, e);
            return null;
        }
    }

    @Override
    public BlazeGolemRenderState createRenderState() {
        return new BlazeGolemRenderState();
    }

    @Override
    public void extractRenderState(BlazeGolem golem, BlazeGolemRenderState state, float partialTick) {
        super.extractRenderState(golem, state, partialTick);
        state.attackTime = golem.attackStart < 0 ? -1.0F : (golem.tickCount - golem.attackStart + partialTick) / 20.0F;
    }

    @Override
    public Identifier getTextureLocation(BlazeGolemRenderState state) {
        return TEXTURE;
    }
}
