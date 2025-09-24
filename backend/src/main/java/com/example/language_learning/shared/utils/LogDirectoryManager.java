package com.example.language_learning.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class LogDirectoryManager {

    private final String logsPath;
    private final int maxDirectoriesToKeep;

    // Regex to match the timestamped directory format: yyyy-MM-dd_HH-mm-ss
    private static final Pattern TIMESTAMP_DIR_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}");

    public LogDirectoryManager(
            @Value("${app.logging.path}") String logsPath,
            @Value("2") int maxDirectoriesToKeep
    ) {
        this.logsPath = logsPath;
        this.maxDirectoriesToKeep = maxDirectoriesToKeep;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void cleanupOldLogDirectories() {
        log.info("Checking for old log directories to clean up in '{}'...", logsPath);
        Path rootPath = Paths.get(logsPath);
        if (!Files.isDirectory(rootPath)) {
            log.warn("Log directory '{}' does not exist. Skipping cleanup.", logsPath);
            return;
        }

        try (Stream<Path> paths = Files.list(rootPath)) {
            List<Path> timestampedDirs = paths
                    .filter(Files::isDirectory)
                    .filter(path -> TIMESTAMP_DIR_PATTERN.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.reverseOrder()) // Sorts newest first (lexicographically)
                    .collect(Collectors.toList());

            if (timestampedDirs.size() > maxDirectoriesToKeep) {
                log.info("Found {} log directories. Keeping the newest {} and deleting the rest.", timestampedDirs.size(), maxDirectoriesToKeep);
                List<Path> dirsToDelete = timestampedDirs.subList(maxDirectoriesToKeep, timestampedDirs.size());

                for (Path dir : dirsToDelete) {
                    log.info("Deleting old log directory: {}", dir);
                    FileSystemUtils.deleteRecursively(dir);
                }
            }
            else {
                log.info("Found {} log directories. No cleanup needed (retention count is {}).", timestampedDirs.size(), maxDirectoriesToKeep);
            }
        }
        catch (IOException e) {
            log.error("Error while scanning log directory for cleanup: {}", logsPath, e);
        }
    }
}