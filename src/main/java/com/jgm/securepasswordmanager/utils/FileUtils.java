package com.jgm.securepasswordmanager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileUtils {


    // Helper method to recursively delete a directory and its contents
    public static void recursiveDelete(Path path) {
        if (Files.exists(path)) {
            try {
                // Walk the file tree and delete each path
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}