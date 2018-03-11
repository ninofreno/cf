package ninofreno.cf.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class FileUtil {

    private FileUtil() {
        // static methods only
    }

    public static List<String> asLines(final String file) throws IOException {

        final List<String> list = new LinkedList<>();

        try (final BufferedReader reader = new BufferedReader(
                file.endsWith(".gz") ? new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))
                        : new FileReader(file))) {

            String line = reader.readLine();
            while (line != null) {

                list.add(line);
                line = reader.readLine();
            }
        }

        return list;
    }

}
