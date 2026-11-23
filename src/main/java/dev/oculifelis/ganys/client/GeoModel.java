package dev.oculifelis.ganys.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bakes a Bedrock geometry file (format 1.12, box UV, no per-cube rotation) into a vanilla {@link ModelPart} tree and
 * keeps each bone's part by name. Bedrock pivots and cubes are absolute and x-mirrored against Java models, so each bone
 * gets offset {@code P - parent P} with {@code P = (-px, 24 - py, pz)}, and bone rotations flip the sign of x and y.
 */
public final class GeoModel {
    public final ModelPart root;
    public final Map<String, ModelPart> parts;

    private GeoModel(ModelPart root, Map<String, ModelPart> parts) {
        this.root = root;
        this.parts = parts;
    }

    public static GeoModel bake(JsonObject json) {
        JsonObject geo = json.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
        JsonObject desc = geo.getAsJsonObject("description");
        int tw = desc.get("texture_width").getAsInt(), th = desc.get("texture_height").getAsInt();
        MeshDefinition mesh = new MeshDefinition();
        Map<String, PartDefinition> defs = new HashMap<>();
        Map<String, float[]> pivots = new HashMap<>();
        Map<String, List<String>> paths = new LinkedHashMap<>();
        for (JsonElement el : geo.getAsJsonArray("bones")) {
            JsonObject bone = el.getAsJsonObject();
            String name = bone.get("name").getAsString();
            String parent = bone.has("parent") ? bone.get("parent").getAsString() : null;
            float[] pv = vec(bone, "pivot", 0.0F);
            float[] p = {-pv[0], 24.0F - pv[1], pv[2]};
            float[] pp = parent != null && pivots.containsKey(parent) ? pivots.get(parent) : new float[3];
            float[] rot = vec(bone, "rotation", 0.0F);
            CubeListBuilder cubes = CubeListBuilder.create();
            if (bone.has("cubes")) for (JsonElement ce : bone.getAsJsonArray("cubes")) {
                JsonObject c = ce.getAsJsonObject();
                float[] o = vec(c, "origin", 0.0F), s = vec(c, "size", 0.0F);
                JsonArray uv = c.getAsJsonArray("uv");
                float inflate = c.has("inflate") ? c.get("inflate").getAsFloat() : 0.0F;
                cubes.texOffs(uv.get(0).getAsInt(), uv.get(1).getAsInt()).mirror(c.has("mirror") && c.get("mirror").getAsBoolean())
                        .addBox(-(o[0] + s[0]) - p[0], 24.0F - (o[1] + s[1]) - p[1], o[2] - p[2], s[0], s[1], s[2], new CubeDeformation(inflate));
            }
            PartDefinition parentDef = parent != null && defs.containsKey(parent) ? defs.get(parent) : mesh.getRoot();
            PartPose pose = PartPose.offsetAndRotation(p[0] - pp[0], p[1] - pp[1], p[2] - pp[2],
                    (float) Math.toRadians(-rot[0]), (float) Math.toRadians(-rot[1]), (float) Math.toRadians(rot[2]));
            defs.put(name, parentDef.addOrReplaceChild(name, cubes, pose));
            pivots.put(name, p);
            List<String> path = new ArrayList<>(parent != null && paths.containsKey(parent) ? paths.get(parent) : List.of());
            path.add(name);
            paths.put(name, path);
        }
        ModelPart root = LayerDefinition.create(mesh, tw, th).bakeRoot();
        Map<String, ModelPart> parts = new LinkedHashMap<>();
        paths.forEach((name, path) -> {
            ModelPart part = root;
            for (String step : path) part = part.getChild(step);
            parts.put(name, part);
        });
        return new GeoModel(root, parts);
    }

    /** A model with no bones, used when the geometry file is missing. */
    public static GeoModel empty() {
        return new GeoModel(LayerDefinition.create(new MeshDefinition(), 16, 16).bakeRoot(), Map.of());
    }

    private static float[] vec(JsonObject o, String key, float fallback) {
        float[] v = {fallback, fallback, fallback};
        if (o.has(key)) {
            JsonArray a = o.getAsJsonArray(key);
            for (int i = 0; i < 3 && i < a.size(); i++) v[i] = a.get(i).getAsFloat();
        }
        return v;
    }
}
