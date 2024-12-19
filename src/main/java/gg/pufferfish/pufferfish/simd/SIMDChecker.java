package gg.pufferfish.pufferfish.simd;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * Basically, java is annoying and we have to push this out to its own class.
 */
public class SIMDChecker {

    public static void initialize() {
        if (SIMDDetection.isInitialized()) {
            return;
        }
        SIMDDetection.setInitialized();
        try {
            int javaVersion = SIMDDetection.getJavaVersion();
            if (javaVersion < 17) {
                return;
            }
            SIMDDetection.supportingJavaVersion = true;
            SIMDDetection.testRunStarted = true;

            VectorSpecies<Integer> ISPEC = IntVector.SPECIES_PREFERRED;
            VectorSpecies<Float> FSPEC = FloatVector.SPECIES_PREFERRED;

            SIMDDetection.intVectorBitSize = ISPEC.vectorBitSize();
            SIMDDetection.floatVectorBitSize = FSPEC.vectorBitSize();

            SIMDDetection.intElementSize = ISPEC.elementSize();
            SIMDDetection.floatElementSize = FSPEC.elementSize();

            SIMDDetection.testRunCompleted = true;

            if (ISPEC.elementSize() < 2 || FSPEC.elementSize() < 2) {
                SIMDDetection.unsupportingLaneSize = true;
                return;
            }

            SIMDDetection.isEnabled = true;
        } catch (Throwable ignored) {} // Basically, we don't do anything. This lets us detect if it's not functional and disable it.
    }

}
