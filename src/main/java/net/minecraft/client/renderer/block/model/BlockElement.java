package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BlockElement {
    private static final boolean DEFAULT_RESCALE = false;
    private static final float MIN_EXTENT = -16.0F;
    private static final float MAX_EXTENT = 32.0F;
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, BlockElementFace> faces;
    @Nullable
    public final BlockElementRotation rotation;
    public final boolean shade;
    public final int lightEmission;

    public BlockElement(Vector3f p_253626_, Vector3f p_254426_, Map<Direction, BlockElementFace> p_254454_) {
        this(p_253626_, p_254426_, p_254454_, null, true, 0);
    }

    public BlockElement(
        Vector3f p_363160_,
        Vector3f p_363775_,
        Map<Direction, BlockElementFace> p_363694_,
        @Nullable BlockElementRotation p_365319_,
        boolean p_363760_,
        int p_366108_
    ) {
        this.from = p_363160_;
        this.to = p_363775_;
        this.faces = p_363694_;
        this.rotation = p_365319_;
        this.shade = p_363760_;
        this.lightEmission = p_366108_;
        this.fillUvs();
    }

    private void fillUvs() {
        for (Entry<Direction, BlockElementFace> entry : this.faces.entrySet()) {
            float[] afloat = this.uvsByFace(entry.getKey());
            entry.getValue().uv().setMissingUv(afloat);
        }
    }

    private float[] uvsByFace(Direction p_111321_) {
        return switch (p_111321_) {
            case DOWN -> new float[]{this.from.x(), 16.0F - this.to.z(), this.to.x(), 16.0F - this.from.z()};
            case UP -> new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
            case NORTH -> new float[]{16.0F - this.to.x(), 16.0F - this.to.y(), 16.0F - this.from.x(), 16.0F - this.from.y()};
            case SOUTH -> new float[]{this.from.x(), 16.0F - this.to.y(), this.to.x(), 16.0F - this.from.y()};
            case WEST -> new float[]{this.from.z(), 16.0F - this.to.y(), this.to.z(), 16.0F - this.from.y()};
            case EAST -> new float[]{16.0F - this.to.z(), 16.0F - this.to.y(), 16.0F - this.from.z(), 16.0F - this.from.y()};
        };
    }

    @OnlyIn(Dist.CLIENT)
    protected static class Deserializer implements JsonDeserializer<BlockElement> {
        private static final boolean DEFAULT_SHADE = true;
        private static final int DEFAULT_LIGHT_EMISSION = 0;

        public BlockElement deserialize(JsonElement p_111329_, Type p_111330_, JsonDeserializationContext p_111331_) throws JsonParseException {
            JsonObject jsonobject = p_111329_.getAsJsonObject();
            Vector3f vector3f = this.getFrom(jsonobject);
            Vector3f vector3f1 = this.getTo(jsonobject);
            BlockElementRotation blockelementrotation = this.getRotation(jsonobject);
            Map<Direction, BlockElementFace> map = this.getFaces(p_111331_, jsonobject);
            if (jsonobject.has("shade") && !GsonHelper.isBooleanValue(jsonobject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            } else {
                boolean flag = GsonHelper.getAsBoolean(jsonobject, "shade", true);
                int i = 0;
                if (jsonobject.has("light_emission")) {
                    boolean flag1 = GsonHelper.isNumberValue(jsonobject, "light_emission");
                    if (flag1) {
                        i = GsonHelper.getAsInt(jsonobject, "light_emission");
                    }

                    if (!flag1 || i < 0 || i > 15) {
                        throw new JsonParseException("Expected light_emission to be an Integer between (inclusive) 0 and 15");
                    }
                }

                return new BlockElement(vector3f, vector3f1, map, blockelementrotation, flag, i);
            }
        }

        @Nullable
        private BlockElementRotation getRotation(JsonObject p_111333_) {
            BlockElementRotation blockelementrotation = null;
            if (p_111333_.has("rotation")) {
                JsonObject jsonobject = GsonHelper.getAsJsonObject(p_111333_, "rotation");
                Vector3f vector3f = this.getVector3f(jsonobject, "origin");
                vector3f.mul(0.0625F);
                Direction.Axis direction$axis = this.getAxis(jsonobject);
                float f = this.getAngle(jsonobject);
                boolean flag = GsonHelper.getAsBoolean(jsonobject, "rescale", false);
                blockelementrotation = new BlockElementRotation(vector3f, direction$axis, f, flag);
            }

            return blockelementrotation;
        }

        private float getAngle(JsonObject p_111343_) {
            float f = GsonHelper.getAsFloat(p_111343_, "angle");
            if (f != 0.0F && Mth.abs(f) != 22.5F && Mth.abs(f) != 45.0F) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            } else {
                return f;
            }
        }

        private Direction.Axis getAxis(JsonObject p_111345_) {
            String s = GsonHelper.getAsString(p_111345_, "axis");
            Direction.Axis direction$axis = Direction.Axis.byName(s.toLowerCase(Locale.ROOT));
            if (direction$axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + s);
            } else {
                return direction$axis;
            }
        }

        private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext p_111326_, JsonObject p_111327_) {
            Map<Direction, BlockElementFace> map = this.filterNullFromFaces(p_111326_, p_111327_);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            } else {
                return map;
            }
        }

        private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext p_111340_, JsonObject p_111341_) {
            Map<Direction, BlockElementFace> map = Maps.newEnumMap(Direction.class);
            JsonObject jsonobject = GsonHelper.getAsJsonObject(p_111341_, "faces");

            for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                Direction direction = this.getFacing(entry.getKey());
                map.put(direction, p_111340_.deserialize(entry.getValue(), BlockElementFace.class));
            }

            return map;
        }

        private Direction getFacing(String p_111338_) {
            Direction direction = Direction.byName(p_111338_);
            if (direction == null) {
                throw new JsonParseException("Unknown facing: " + p_111338_);
            } else {
                return direction;
            }
        }

        private Vector3f getTo(JsonObject p_111353_) {
            Vector3f vector3f = this.getVector3f(p_111353_, "to");
            if (!(vector3f.x() < -16.0F)
                && !(vector3f.y() < -16.0F)
                && !(vector3f.z() < -16.0F)
                && !(vector3f.x() > 32.0F)
                && !(vector3f.y() > 32.0F)
                && !(vector3f.z() > 32.0F)) {
                return vector3f;
            } else {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f getFrom(JsonObject p_111347_) {
            Vector3f vector3f = this.getVector3f(p_111347_, "from");
            if (!(vector3f.x() < -16.0F)
                && !(vector3f.y() < -16.0F)
                && !(vector3f.z() < -16.0F)
                && !(vector3f.x() > 32.0F)
                && !(vector3f.y() > 32.0F)
                && !(vector3f.z() > 32.0F)) {
                return vector3f;
            } else {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f getVector3f(JsonObject p_111335_, String p_111336_) {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111335_, p_111336_);
            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + p_111336_ + " values, found: " + jsonarray.size());
            } else {
                float[] afloat = new float[3];

                for (int i = 0; i < afloat.length; i++) {
                    afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), p_111336_ + "[" + i + "]");
                }

                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
    }
}