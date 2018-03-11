package ninofreno.cf.util;

import java.util.Map;

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

}
