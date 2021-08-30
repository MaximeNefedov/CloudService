package ru.netology.cloud_service_app.controllers;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.models.NewFilename;
import ru.netology.cloud_service_app.services.FileService;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping
@Slf4j
public class FileController {
    private final FileService fileService;
    private static final String REGEXP = ".+\\..+";
    private static final String EXTENSIONS_FILENAME = "mime_types.txt";
    private static final Set<String> EXTENSIONS_STORAGE = new HashSet<>();

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    public void setExtensions() {
        if (Files.exists(Path.of(EXTENSIONS_FILENAME))) {
            try {
                val readFile = new String(Files.readAllBytes(Path.of(EXTENSIONS_FILENAME)));
                EXTENSIONS_STORAGE.addAll(Arrays.stream(readFile.split("\n")).collect(Collectors.toSet()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            throw new IllegalArgumentException(String.format("Файл с расширениями \"%s\" отсутствует", EXTENSIONS_FILENAME));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/list")
    public ResponseEntity<List<FileData>> getAllFiles(@RequestParam("limit") @Min(1) int limit, Principal principal) {
        val login = principal.getName();
        log.info(String.format("Принят запрос от пользоваетеля %s на просмотр всех файлов", login));
        val allFiles = fileService.getAllFiles(limit, login);
        log.info(String.format("Пользователь %s получил список файлов: %s", login, allFiles));
        return new ResponseEntity<>(allFiles, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") @Pattern(regexp = REGEXP) String filename, Principal principal) {
        val login = principal.getName();
        log.info(String.format("Принят запрос от пользователя %s на скачивание файла %s", login, filename));
        val file = fileService.downloadFile(filename, principal.getName());
        log.info(String.format("Пользователь %s успешно скачал файл %s", login, filename));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + file.getName())
                .body(new ByteArrayResource(file.getFileBody()));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping("/file")
    public ResponseEntity<String> saveFile(@NotNull MultipartFile file, @RequestParam String filename, Principal principal) {
        val login = principal.getName();
        log.info(String.format("Принят запрос от пользоваетеля %s на сохранение файла %s", login, filename));
        if (filename != null && filename.matches(REGEXP) && EXTENSIONS_STORAGE.contains(filename.substring(filename.lastIndexOf(".") + 1))) {
            fileService.saveFile(file, principal.getName());
            log.info(String.format("Пользователь %s успешно сохранил файл", login));
            return new ResponseEntity<>("Success save", HttpStatus.OK);
        } else return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") @Pattern(regexp = REGEXP) String filename,
                                             Principal principal) {
        val login = principal.getName();
        log.info(String.format("Принят запрос от пользоваетеля %s на удаление файла %s", login, filename));
        fileService.deleteFile(filename, principal.getName());
        log.info(String.format("Пользоваетель %s успешно удалил файл %s", login, filename));
        return new ResponseEntity<>("Success deleted", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PutMapping("/file")
    public ResponseEntity<String> editFilename(@RequestParam("filename") @Pattern(regexp = REGEXP) String oldFilename,
                                               @Valid @RequestBody NewFilename newFilename,
                                               Principal principal) {
        val login = principal.getName();
        log.info(String.format("Принят запрос от пользователя %s на изменение названия файла %s, на новое: %s", login, oldFilename, newFilename.getFilename()));
        fileService.editFilename(oldFilename, newFilename.getFilename(), principal.getName());
        log.info(String.format("Пользоваетель %s успешно изменил файл %s, новое название файла: %s", login, oldFilename, newFilename.getFilename()));
        return new ResponseEntity<>("Success upload", HttpStatus.OK);
    }
}
