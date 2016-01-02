package ch.puzzle.jee.userauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Helper class that simplifies file handling in tests.
 */
public class FileHelper {
    private FileHelper() {
    }

    public static String readFile(String path) throws IOException {
        try (InputStream is = getFileAsStream(path)) {
            return toString(is);
        }
    }

    private static String toString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static InputStream getFileAsStream(String path) {
        return FileHelper.class.getResourceAsStream(path);
    }
}
