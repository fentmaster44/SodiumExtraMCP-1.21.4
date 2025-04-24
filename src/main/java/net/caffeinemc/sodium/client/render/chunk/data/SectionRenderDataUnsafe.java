package net.caffeinemc.sodium.client.render.chunk.data;

import net.caffeinemc.sodium.client.util.UInt32;
import net.caffeinemc.sodium.client.render.chunk.SharedQuadIndexBuffer;
import org.lwjgl.system.MemoryUtil;

// This code is a terrible hack to get around the fact that we are so incredibly memory bound, and that we
// have no control over memory layout. The chunk rendering code spends an astronomical amount of time chasing
// object pointers that are scattered across the heap. Worse yet, because render state is initialized over a long
// period of time as the level loads, those objects are never even remotely close to one another in heap, so
// you also have to pay the penalty of a DTLB miss on every other access.
//
// Unfortunately, Hotspot *still* produces abysmal machine code for the chunk rendering code paths, since any usage of
// unsafe memory intrinsics seems to cause it to become paranoid about memory aliasing. Well, that, and it just produces
// terrible machine code in pretty much every critical code path we seem to have...
//
// Please never try to write performance critical code in Java. This is what it will do to you. And you will still be
// three times slower than the most naive solution in literally any other language that LLVM can compile.

// struct SectionRenderData { // 48 bytes
//   base_element: u32,
//   base_vertex: u32,
//   (is_local_index | facing_list): (u8 | u56),
//   slice_mask: u32,
//   vertex_count: [u32; 7]
// }

public class SectionRenderDataUnsafe {
    /**
     * When the "base element" field is not specified (indicated by setting the MSB to 0), the indices for the geometry set
     * should be sourced from a monotonic sequence (see {@link SharedQuadIndexBuffer}).
     *
     * Otherwise, indices should be sourced from the index buffer for the render region using the specified offset.
     */
    private static final long OFFSET_BASE_ELEMENT = 0;
    private static final long OFFSET_BASE_VERTEX = 4;
    private static final long OFFSET_FACING_LIST = 8;
    private static final long OFFSET_IS_LOCAL_INDEX = 15;
    private static final long OFFSET_SLICE_MASK = 16;
    private static final long OFFSET_ELEMENT_COUNTS = 20;

    private static final long ALIGNMENT = 8; // 8 byte (64 bit) alignment for reading longs
    private static final long STRIDE = 48;

    public static long allocateHeap(int count) {
        final var bytes = STRIDE * count;

        final var ptr = MemoryUtil.nmemAlignedAlloc(ALIGNMENT, bytes);
        MemoryUtil.memSet(ptr, 0, bytes);

        return ptr;
    }

    public static void freeHeap(long pointer) {
        MemoryUtil.nmemAlignedFree(pointer);
    }

    public static void clearFull(long pointer) {
        MemoryUtil.memSet(pointer, 0x0, STRIDE);
    }

    public static void clearVertexData(long ptr) {
        // save the base element and the local indexing flag
        int baseElement = MemoryUtil.memGetInt(ptr + OFFSET_BASE_ELEMENT);
        byte isLocalIndexByte = MemoryUtil.memGetByte(ptr + OFFSET_IS_LOCAL_INDEX);

        clearFull(ptr);

        // restore the base element and the local indexing flag
        MemoryUtil.memPutInt(ptr + OFFSET_BASE_ELEMENT, baseElement);
        MemoryUtil.memPutByte(ptr + OFFSET_IS_LOCAL_INDEX, isLocalIndexByte);
    }

    public static void clearIndexData(long ptr) {
        MemoryUtil.memPutInt(ptr + OFFSET_BASE_ELEMENT, 0);
        MemoryUtil.memPutByte(ptr + OFFSET_IS_LOCAL_INDEX, (byte)0);
    }

    public static long heapPointer(long ptr, int index) {
        return ptr + (index * STRIDE);
    }

    public static void setLocalBaseElement(long ptr, long value /* Uint32 */) {
        MemoryUtil.memPutInt(ptr + OFFSET_BASE_ELEMENT, UInt32.downcast(value));
        MemoryUtil.memPutByte(ptr + OFFSET_IS_LOCAL_INDEX, (byte) 1);
    }

    public static void setSharedBaseElement(long ptr, long value /* Uint32 */) {
        MemoryUtil.memPutInt(ptr + OFFSET_BASE_ELEMENT, UInt32.downcast(value));
        MemoryUtil.memPutByte(ptr + OFFSET_IS_LOCAL_INDEX, (byte) 0);
    }

    public static long getBaseElement(long ptr) {
        return Integer.toUnsignedLong(MemoryUtil.memGetInt(ptr + OFFSET_BASE_ELEMENT));
    }

    public static void setSliceMask(long ptr, int value) {
        MemoryUtil.memPutInt(ptr + OFFSET_SLICE_MASK, value);
    }

    public static int getSliceMask(long ptr) {
        return MemoryUtil.memGetInt(ptr + OFFSET_SLICE_MASK);
    }

    public static void setFacingList(long ptr, long facingList) {
        // rescue and re-write the local indexing flag to prevent it from being lost when a whole long is written for the facing list
        byte isLocalIndexByte = MemoryUtil.memGetByte(ptr + OFFSET_IS_LOCAL_INDEX);
        MemoryUtil.memPutLong(ptr + OFFSET_FACING_LIST, facingList);
        MemoryUtil.memPutByte(ptr + OFFSET_IS_LOCAL_INDEX, isLocalIndexByte);
    }

    public static long getFacingList(long ptr) {
        return MemoryUtil.memGetLong(ptr + OFFSET_FACING_LIST);
    }

    public static boolean isLocalIndex(long ptr) {
        return MemoryUtil.memGetByte(ptr + OFFSET_IS_LOCAL_INDEX) != 0;
    }

    public static void setBaseVertex(long ptr, long value /* Uint32 */) {
        MemoryUtil.memPutInt(ptr + OFFSET_BASE_VERTEX, UInt32.downcast(value));
    }

    public static long /* Uint32 */ getBaseVertex(long ptr) {
        return UInt32.upcast(MemoryUtil.memGetInt(ptr + OFFSET_BASE_VERTEX));
    }

    public static void setVertexCount(long ptr, int index, long count /* Uint32 */) {
        MemoryUtil.memPutInt(ptr + OFFSET_ELEMENT_COUNTS + (index * 4), UInt32.downcast(count));
    }

    public static long /* Uint32 */ getVertexCount(long ptr, int index) {
        return UInt32.upcast(MemoryUtil.memGetInt(ptr + OFFSET_ELEMENT_COUNTS + (index * 4)));
    }
}