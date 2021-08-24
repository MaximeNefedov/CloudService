package ru.netology;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.util.DigestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        val pattern = "\\w+\\..+";
        val input = "1jpeg";
        System.out.println(input.matches(pattern));
    }
}
