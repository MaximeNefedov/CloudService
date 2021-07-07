package ru.netology.cloud_service_app.controllers;

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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/list")
    public ResponseEntity<List<FileData>> getAllFiles(@RequestParam("limit") @Min(1) int limit, Principal principal) {
        val allFiles = fileService.getAllFiles(limit, principal.getName());
        return new ResponseEntity<>(allFiles, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") @Pattern(regexp = "\\w+\\..+") String filename,
                                                 Principal principal) {
        val file = fileService.downloadFile(filename, principal.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + file.getName())
                .body(new ByteArrayResource(file.getFileBody()));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping("/file")
    public void saveFile(@NotNull MultipartFile file, Principal principal) {
        fileService.saveFile(file, principal.getName());
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") @Pattern(regexp = "\\w+\\..+") String filename,
                                             Principal principal) {
        fileService.deleteFile(filename, principal.getName());
        return new ResponseEntity<>("Success deleted", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PutMapping("/file")
    public ResponseEntity<String> editFilename(@RequestParam("filename") @Pattern(regexp = "\\w+\\..+") String oldFilename,
                                               @Valid @RequestBody NewFilename newFilename,
                                               Principal principal) {
        fileService.editFilename(oldFilename, newFilename.getFilename(), principal.getName());
        return new ResponseEntity<>("Success upload", HttpStatus.OK);
    }
}
