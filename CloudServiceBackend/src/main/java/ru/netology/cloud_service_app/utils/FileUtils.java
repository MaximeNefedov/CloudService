package ru.netology.cloud_service_app.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtils {
    private static final Map<String, String> CASHED_DELETED_FILES = new HashMap<>();
    private static final Set<String> FILE_EXTENSIONS = new HashSet<>();
    private static final String FILENAME = "mime_types.txt";

    static {
        if (Files.exists(Path.of(FILENAME))) {
            try {
                val readFile = new String(Files.readAllBytes(Path.of(FILENAME)));
                FILE_EXTENSIONS.addAll(Arrays.stream(readFile.split("\n")).collect(Collectors.toSet()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else throw new IllegalArgumentException("Файл с расширениями отсутствует");
    }

    public boolean isFileHasValidExtension(String filename) {
        val extension = filename.substring(filename.indexOf(".") + 1);
        return FILE_EXTENSIONS.contains(extension);
    }

    public boolean isFileCashed(String hash, byte[] fileBytes) {
        val cashedFileHash = CASHED_DELETED_FILES.get(hash);
        if (cashedFileHash != null && !cashedFileHash.isEmpty()) {
            val fileToUploadHash = getHash(fileBytes);
            return fileToUploadHash.equals(cashedFileHash);
        } else return false;
    }

    public void removeFromCashStorage(String hash) {
        CASHED_DELETED_FILES.remove(hash);
    }

    private static String getHash(byte[] bytes) {
        return DigestUtils.md5DigestAsHex(bytes);
    }

    public static void cashDeletedFile(String hash, byte[] fileBody) {
        val fileBodyHash = getHash(fileBody);
        CASHED_DELETED_FILES.put(hash, fileBodyHash);
    }

    public static String getFileHash(String filename, String login) {
        return getHash(filename.concat(login).getBytes(StandardCharsets.UTF_8));
    }
}
