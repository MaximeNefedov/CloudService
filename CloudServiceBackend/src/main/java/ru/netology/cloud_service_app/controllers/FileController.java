package ru.netology.cloud_service_app.controllers;

import lombok.val;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.services.FileService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public List<FileData> getAllFiles(@RequestParam("limit") int limit,
                                      Principal principal) {
        return fileService.getAllFiles(limit, principal.getName());
    }

    @PostMapping("/file")
    public void saveFile(MultipartFile file, Principal principal) {
        fileService.saveFile(file, principal.getName());
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename,
                                                 Principal principal) {
        val file = fileService.downloadFile(filename, principal.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + file.getName())
                .body(new ByteArrayResource(file.getFileBody()));
    }
}
