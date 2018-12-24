package nl.juraji.imagemanager.util;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
public final class FileUtils {
    private FileUtils() {
    }

    public static String getFileExtension(Path path) {
        return getFileExtension(path.getFileName().toString());
    }

    public static String getFileExtension(String url) {
        return url.substring(url.lastIndexOf('.') + 1);
    }

    public static List<Path> getDirectoryFiles(Path directory, Collection<String> fileExtensions) {
        final List<Path> files = listDirectoryChildren(directory, Files::isRegularFile);

        if (fileExtensions == null) {
            return files;
        } else {
            return files.stream()
                    .filter(file -> fileExtensions.contains(getFileExtension(file)))
                    .collect(Collectors.toList());
        }
    }

    public static List<Path> getDirectorySubDirectories(Path directory) {
        return listDirectoryChildren(directory, Files::isDirectory);
    }

    private static List<Path> listDirectoryChildren(Path parent, Predicate<Path> filter) {
        if (!Files.isDirectory(parent)) {
            throw new RuntimeException(parent.toString() + " is not a directory");
        }

        try {
            return Files.list(parent)
                    .filter(filter)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static void delete(Path path) throws IOException {
        if (exists(path)) {
            if (Files.isRegularFile(path)) {
                Files.deleteIfExists(path);
            } else {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        if (exc instanceof NoSuchFileException) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        throw exc;
                    }
                });
            }
        }
    }

    public static String getHumanReadableSize(double bytes) {
        final String[] dictionary = {"bytes", "KB", "MB", "GB", "TB"};
        final int stepSize = 1024;

        int index;

        for (index = 0; index < dictionary.length; index++) {
            if (bytes < stepSize) {
                break;
            }

            bytes = bytes / stepSize;
        }

        return String.format("%.1f %s", bytes, dictionary[index]);
    }

    public static boolean exists(Path path) {
        return path != null && Files.exists(path);
    }
}
