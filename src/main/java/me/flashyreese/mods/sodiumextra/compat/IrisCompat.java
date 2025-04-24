package me.flashyreese.mods.sodiumextra.compat;

import com.mojang.blaze3d.vertex.VertexFormat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class IrisCompat {
    private static VertexFormat terrainFormat;

//    static {
//        try {
//            apiInstance = api.cast(api.getDeclaredMethod("getInstance").invoke(null));
//            handleRenderingShadowPass = MethodHandles.lookup().findVirtual(api, "isRenderingShadowPass", MethodType.methodType(boolean.class));
//
//            Class<?> irisVertexFormatsClass = Class.forName("net.irisshaders.iris.vertices.IrisVertexFormats");
//            Field terrainField = irisVertexFormatsClass.getDeclaredField("TERRAIN");
//            terrainFormat = (VertexFormat) terrainField.get(null);
//
//            irisPresent = true;
//        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException |
//                 InvocationTargetException e) {
//            irisPresent = false;
//        }
//    }

    public static VertexFormat getTerrainFormat() {
        return terrainFormat;
    }

    // TODO IRIS
    public static boolean isRenderingShadowPass() {
        return false;
    }

}