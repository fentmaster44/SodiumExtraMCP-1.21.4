package net.caffeinemc.sodium.client.util;

import org.joml.Vector3dc;
import org.joml.Vector3fc;

import static org.joml.Math.fma;

public class MathUtil {
    /**
     * @return True if the specified number is greater than zero and is a power of two, otherwise false
     */
    public static boolean isPowerOfTwo(int n) {
        return ((n & (n - 1)) == 0);
    }

    public static long toMib(long bytes) {
        return bytes / (1024L * 1024L); // 1 MiB = 1048576 (2^20) bytes
    }

    /**
     * <p>Rounds the integer {@param num} up to the next multiple of {@param alignment}. This multiple *MUST* be
     * a power-of-two, or undefined behavior will occur.</p>
     *
     * @param num The number to round up
     * @param alignment The power-of-two multiple to round to
     * @return The rounded number
     */
    public static int align(int num, int alignment) {
        int additive = alignment - 1;
        int mask = ~additive;
        return (num + additive) & mask;
    }

    /**
     * Converts a float to a comparable integer value. This is used to compare
     * floating point values by their int bits (for example packed in a long).
     * <p>
     * The resulting integer can be treated as if it's unsigned and numbers the
     * floats from the smallest negative to the largest positive value.
     * <p>
     * Reference: <a href="https://stackoverflow.com/questions/23900328/are-floats-bit-patterns-ordered">StackOverflow Answer</a>
     */
    public static int floatToComparableInt(float f) {
        var bits = Float.floatToRawIntBits(f);
        return bits ^ ((bits >> 31) & 0x7FFFFFFF);
    }

    public static float comparableIntToFloat(int i) {
        return Float.intBitsToFloat(i ^ ((i >> 31) & 0x7FFFFFFF));
    }

    public static double floatDoubleDot(Vector3fc a, Vector3dc b) {
        return fma(a.x(), b.x(), fma(a.y(), b.y(), a.z() * b.z()));
    }

    public static double floatDoubleDot(Vector3fc a, double bx, double by, double bz) {
        return fma(a.x(), bx, fma(a.y(), by, a.z() * bz));
    }
}
