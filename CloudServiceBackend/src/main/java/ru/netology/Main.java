package ru.netology;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.util.DigestUtils;
import ru.netology.cloud_service_app.utils.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        val filename = "1.jpeg";
        val login = "max@mail.ru";

        System.out.println(DigestUtils.md5DigestAsHex(filename.concat(login).getBytes(StandardCharsets.UTF_8)));

        final byte[] bytes = new StringBuilder(filename).append(login).toString().getBytes(StandardCharsets.UTF_8);

        System.out.println(DigestUtils.md5DigestAsHex(bytes));
//        Files.write(Path.of("mime_types.txt"))
    }
}
