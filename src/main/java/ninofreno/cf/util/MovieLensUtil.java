package ninofreno.cf.util;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import info.debatty.java.lsh.LSHSuperBit;
import ninofreno.cf.core.Event;;

public final class MovieLensUtil {

    private MovieLensUtil() {
        // static methods only
    }

    public static Event fromLine(final String line) {

        final String[] fields = line.split("::");
        return new Event(fields[0], fields[1], Integer.parseInt(fields[2]));
    }

    public static double[] toDenseVector(final Map<String, Integer> ratings, final int dimensionality) {

        final double[] vector = new double[dimensionality];
        ratings.entrySet().stream()
                .forEach(rating -> vector[Math.abs(rating.getKey().hashCode()) % dimensionality] = rating.getValue());

        return vector;
    }

    public static void main(String[] args) {

        int dimensionality = 100;

        int stages = 10;
        int buckets = 10;

        System.err.println("S");
        final LSHSuperBit lsh = new LSHSuperBit(stages, buckets, dimensionality);
        System.err.println("E");

        Map<String, Integer> map = ImmutableMap.of("98", 4, "ghostbusters", 2, "970", 3);
        double[] vector = toDenseVector(map, dimensionality);
        int[] lsHashes = lsh.hash(vector);
        System.err.println(Arrays.toString(lsHashes));

        map = ImmutableMap.of("98", 5, "ghostbusters", 2, "970", 3);
        vector = toDenseVector(map, dimensionality);
        lsHashes = lsh.hash(vector);
        System.err.println(Arrays.toString(lsHashes));

        map = ImmutableMap.of("98", 1, "ghostbusters", 5);
        vector = toDenseVector(map, dimensionality);
        lsHashes = lsh.hash(vector);
        System.err.println(Arrays.toString(lsHashes));

    }
}
