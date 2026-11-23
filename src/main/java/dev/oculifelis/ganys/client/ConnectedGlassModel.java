package dev.oculifelis.ganys.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.oculifelis.ganys.GanysCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

/**
 * Connected glass, a block model with {@code "loader": "ganyscollection:connected"} and the five sprites of
 * {@link ConnectedGlass#VARIANTS} as textures (plus "particle"), and {@code "pattern": n} for glass whose picture spans
 * n x n blocks. Every quadrant of every face is baked once per sprite; drawing a block only picks from them.
 */
public final class ConnectedGlassModel {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GanysCollection.MOD_ID, "connected");

    /** Per face and quadrant, the index into that quadrant's baked quads. */
    static final ModelProperty<int[]> PICKS = new ModelProperty<>();

    private ConnectedGlassModel() {
    }

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ID, Loader.INSTANCE);
    }

    enum Loader implements IGeometryLoader<Unbaked> {
        INSTANCE;

        @Override
        public Unbaked read(JsonObject json, JsonDeserializationContext context) {
            return new Unbaked(GsonHelper.getAsInt(json, "pattern", 1));
        }
    }

    record Unbaked(int pattern) implements IUnbakedGeometry<Unbaked> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> sprites,
                               ModelState state, ItemOverrides overrides) {
            TextureAtlasSprite[] mats = new TextureAtlasSprite[ConnectedGlass.VARIANTS.length];
            for (int i = 0; i < mats.length; i++) {
                mats[i] = sprites.apply(context.getMaterial(ConnectedGlass.VARIANTS[i]));
            }
            FaceBakery bakery = new FaceBakery();
            // quads[face][quadrant][sprite + 5 * tile]
            int n = Math.max(1, pattern), tiles = n * n;
            BakedQuad[][][] quads = new BakedQuad[6][4][mats.length * tiles];
            for (Direction face : Direction.values()) {
                for (int sv = -1; sv <= 1; sv += 2) {
                    for (int su = -1; su <= 1; su += 2) {
                        float[] b = ConnectedGlass.box(face, su, sv);
                        for (int t = 0; t < tiles; t++) {
                            float[] uv = ConnectedGlass.uv(face, b, n, t);
                            for (int s = 0; s < mats.length; s++) {
                                BlockElementFace ef = new BlockElementFace(face, -1, "", new BlockFaceUV(uv.clone(), 0));
                                quads[face.get3DDataValue()][ConnectedGlass.quadrant(su, sv)][s + mats.length * t] = bakery.bakeQuad(
                                        new Vector3f(b[0], b[1], b[2]), new Vector3f(b[3], b[4], b[5]), ef, mats[s], face,
                                        BlockModelRotation.X0_Y0, null, true);
                            }
                        }
                    }
                }
            }
            TextureAtlasSprite particle = context.hasMaterial("particle")
                    ? sprites.apply(context.getMaterial("particle")) : mats[ConnectedGlass.ISOLATED];
            ResourceLocation hint = context.getRenderTypeHint();
            RenderType type = hint == null ? null : context.getRenderType(hint).block();
            ChunkRenderTypeSet types = type == null ? null : ChunkRenderTypeSet.of(type);
            return new Baked(quads, n, particle, types, context.getTransforms());
        }
    }

    record Baked(BakedQuad[][][] quads, int n, TextureAtlasSprite particle, @Nullable ChunkRenderTypeSet types,
                 ItemTransforms transforms) implements IDynamicBakedModel {
        @Override
        public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
            int[] picks = new int[24];
            for (Direction face : Direction.values()) {
                int tile = ConnectedGlass.VARIANTS.length * ConnectedGlass.tile(pos, face, n);
                for (int sv = -1; sv <= 1; sv += 2) {
                    for (int su = -1; su <= 1; su += 2) {
                        picks[face.get3DDataValue() * 4 + ConnectedGlass.quadrant(su, sv)] =
                                tile + ConnectedGlass.pick(level::getBlockState, pos, state, face, su, sv);
                    }
                }
            }
            return data.derive().with(PICKS, picks).build();
        }

        /** Without a world (item frames, falling blocks) the plain look. */
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data,
                                        @Nullable RenderType renderType) {
            if (side == null) return List.of();
            int[] picks = data.get(PICKS);
            List<BakedQuad> list = new ArrayList<>(4);
            for (int q = 0; q < 4; q++) {
                int s = picks == null ? ConnectedGlass.ISOLATED : picks[side.get3DDataValue() * 4 + q];
                list.add(quads[side.get3DDataValue()][q][s]);
            }
            return list;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            return types != null ? types : IDynamicBakedModel.super.getRenderTypes(state, rand, data);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return true;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }
}
