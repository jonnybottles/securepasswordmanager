package com.jgm.securepasswordmanager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

// Single method file utilities class.
// Will combine UserDataServices at later point
public class FileUtils {


    // Helper method to recursively delete a directory and its contents
    public static void recursiveDelete(String path) {
        Path filePath = new File(path).toPath();
        if (Files.exists(filePath)) {
            try {
                // Walk the file tree and delete each path
                Files.walk(filePath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
