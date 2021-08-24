package ru.netology.cloud_service_app.repositories;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class FileUtils {
    private static final Map<String, String> CASHED_DELETED_FILES = new HashMap<>();
    private static final String MIME_TYPES_FILENAME = "mime_types.csv";
    private static final char MIME_TYPES_DELIMITER = '/';
//    private static final Map<String, String> MIME_TYPES_MAP;
//    private static final Map<String, String> MIME_TYPES_MAP;
//
//    static {
//        MIME_TYPES_MAP = new HashMap<>();
//        try (val in = new FileReader(MIME_TYPES_FILENAME)) {
//            Iterable<CSVRecord> records = CSVFormat.newFormat(MIME_TYPES_DELIMITER).parse(in);
//            for (CSVRecord record : records) {
//                val fileType = record.get(0);
//                val contentType = record.get(1);
//                MIME_TYPES_MAP.put(contentType, fileType);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
//        val contentType = getContentType(filename);
        val stringBuilder = new StringBuilder();
        val bytesForHashing = stringBuilder.append(login)
                .append(filename)
//                .append(contentType)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
        return getHash(bytesForHashing);
    }

//    private static String getContentType(String filename) {
//        val fileType = filename.substring(filename.indexOf(".") + 1);
//        val contentTypePart = MIME_TYPES_MAP.get(fileType);
//        return contentTypePart + MIME_TYPES_DELIMITER + fileType;
//    }
}
