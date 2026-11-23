package dev.oculifelis.ganys.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads GeckoLib-format animation clips whose channels are plain vectors of numbers or Molang expressions, and samples
 * them. The expressions may use numbers, + - * /, brackets, math.cos, math.sin (degrees), math.pi and query.anim_time.
 */
public final class MolangClips {
    /** Channel order in a sample: rotation x y z, position x y z, scale x y z. */
    public static final String[] CHANNELS = {"rotation", "position", "scale"};

    private interface Expr {
        double eval(double t);
    }

    /** One clip: its length in seconds, whether it loops, and per bone up to three channels of three expressions. */
    public record Clip(float length, boolean loop, Map<String, Expr[][]> bones) {
        /** Blends this clip's channels for {@code bone} at time {@code seconds} into {@code out} with {@code weight}. */
        public void blend(String bone, float seconds, float weight, float[] out) {
            Expr[][] ch = bones.get(bone);
            if (ch == null || weight <= 0.0F) return;
            double t = loop && length > 0.0F ? Math.floorMod((long) Math.floor(seconds * 1000.0), (long) Math.round(length * 1000.0)) / 1000.0
                    : Math.min(Math.max(seconds, 0.0F), length);
            for (int c = 0; c < 3; c++) {
                if (ch[c] == null) continue;
                for (int i = 0; i < 3; i++) {
                    float v = (float) ch[c][i].eval(t);
                    out[c * 3 + i] += (v - out[c * 3 + i]) * weight;
                }
            }
        }
    }

    /** Clips by short name (the part after the last dot of the animation id). */
    public static Map<String, Clip> read(JsonObject root) {
        Map<String, Clip> clips = new HashMap<>();
        JsonObject anims = root.getAsJsonObject("animations");
        for (Map.Entry<String, JsonElement> a : anims.entrySet()) {
            JsonObject anim = a.getValue().getAsJsonObject();
            float length = anim.has("animation_length") ? anim.get("animation_length").getAsFloat() : 0.0F;
            boolean loop = anim.has("loop") && anim.get("loop").isJsonPrimitive() && anim.get("loop").getAsBoolean();
            Map<String, Expr[][]> bones = new HashMap<>();
            if (anim.has("bones")) for (Map.Entry<String, JsonElement> b : anim.getAsJsonObject("bones").entrySet()) {
                JsonObject chans = b.getValue().getAsJsonObject();
                Expr[][] out = new Expr[3][];
                for (int c = 0; c < 3; c++) {
                    if (!chans.has(CHANNELS[c])) continue;
                    JsonElement e = chans.get(CHANNELS[c]);
                    JsonArray vec = e.isJsonObject() ? e.getAsJsonObject().getAsJsonArray("vector") : e.isJsonArray() ? e.getAsJsonArray() : null;
                    if (vec == null) continue;
                    out[c] = new Expr[3];
                    for (int i = 0; i < 3; i++) out[c][i] = parse(vec.get(Math.min(i, vec.size() - 1)));
                }
                bones.put(b.getKey(), out);
            }
            String name = a.getKey().substring(a.getKey().lastIndexOf('.') + 1);
            clips.put(name, new Clip(length, loop, bones));
        }
        return clips;
    }

    private static Expr parse(JsonElement e) {
        if (e.getAsJsonPrimitive().isNumber()) {
            double v = e.getAsDouble();
            return t -> v;
        }
        Parser p = new Parser(e.getAsString());
        Expr x = p.sum();
        p.skip();
        if (p.pos != p.s.length()) throw new IllegalArgumentException("Unexpected text in Molang: " + p.s.substring(p.pos));
        return x;
    }

    private static final class Parser {
        final String s;
        int pos;

        Parser(String s) {
            this.s = s.toLowerCase(java.util.Locale.ROOT);
        }

        void skip() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }

        boolean eat(char c) {
            skip();
            if (pos < s.length() && s.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        Expr sum() {
            Expr x = product();
            while (true) {
                if (eat('+')) {
                    Expr a = x, b = product();
                    x = t -> a.eval(t) + b.eval(t);
                } else if (eat('-')) {
                    Expr a = x, b = product();
                    x = t -> a.eval(t) - b.eval(t);
                } else return x;
            }
        }

        Expr product() {
            Expr x = unary();
            while (true) {
                if (eat('*')) {
                    Expr a = x, b = unary();
                    x = t -> a.eval(t) * b.eval(t);
                } else if (eat('/')) {
                    Expr a = x, b = unary();
                    x = t -> {
                        double d = b.eval(t);
                        return d == 0.0 ? 0.0 : a.eval(t) / d;
                    };
                } else return x;
            }
        }

        Expr unary() {
            if (eat('-')) {
                Expr a = unary();
                return t -> -a.eval(t);
            }
            if (eat('+')) return unary();
            return primary();
        }

        Expr primary() {
            if (eat('(')) {
                Expr x = sum();
                if (!eat(')')) throw new IllegalArgumentException("Missing ) in Molang: " + s);
                return x;
            }
            skip();
            int start = pos;
            if (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) {
                while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
                if (pos < s.length() && s.charAt(pos) == 'e') {
                    pos++;
                    if (pos < s.length() && (s.charAt(pos) == '-' || s.charAt(pos) == '+')) pos++;
                    while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
                }
                double v = Double.parseDouble(s.substring(start, pos));
                return t -> v;
            }
            while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_' || s.charAt(pos) == '.')) pos++;
            String id = s.substring(start, pos);
            switch (id) {
                case "query.anim_time", "q.anim_time" -> {
                    return t -> t;
                }
                case "math.pi" -> {
                    return t -> Math.PI;
                }
                case "math.cos", "math.sin" -> {
                    if (!eat('(')) throw new IllegalArgumentException("Missing ( after " + id);
                    Expr a = sum();
                    if (!eat(')')) throw new IllegalArgumentException("Missing ) after " + id);
                    return id.equals("math.cos") ? t -> Math.cos(Math.toRadians(a.eval(t))) : t -> Math.sin(Math.toRadians(a.eval(t)));
                }
                default -> throw new IllegalArgumentException("Unknown Molang term '" + id + "' in " + s);
            }
        }
    }

    private MolangClips() {
    }
}
