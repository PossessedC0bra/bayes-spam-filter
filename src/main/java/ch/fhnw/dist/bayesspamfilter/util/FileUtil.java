package ch.fhnw.dist.bayesspamfilter.util;

import ch.fhnw.dist.bayesspamfilter.ResourceBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FileUtil {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private FileUtil() {
    }

    public static File getResourceFile(String filePath) {
        URL hamTrainSetUrl = ResourceBase.class.getResource(filePath);
        if (hamTrainSetUrl == null) {
            return null;
        }

        String absoluteFilePath = hamTrainSetUrl.getPath();
        if (absoluteFilePath == null) {
            return null;
        }

        return new File(absoluteFilePath);
    }

    public static Set<String> getUniqueWords(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .flatMap(WHITESPACE_PATTERN::splitAsStream)
                    .filter(word -> word != null && !word.isEmpty())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println("Could not read line from file");
        }

        return Collections.emptySet();
    }
}