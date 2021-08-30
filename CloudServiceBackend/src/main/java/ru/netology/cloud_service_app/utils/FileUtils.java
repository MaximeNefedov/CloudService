package ru.netology.cloud_service_app.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class FileUtils {
    public static String getFileHash(String filename, String login) {
        return getHash(filename.concat(login).getBytes(StandardCharsets.UTF_8));
    }

    public static String getFullFileHash(String filename, String login, byte[] fileBody) {
        val fileBodyHash = getHash(fileBody);
        val bytesForHashCalc = (filename + login + fileBodyHash).getBytes(StandardCharsets.UTF_8);
        return getHash(bytesForHashCalc);
    }

    private static String getHash(byte[] bytesForHashing) {
        return DigestUtils.md5DigestAsHex(bytesForHashing);
    }
}
