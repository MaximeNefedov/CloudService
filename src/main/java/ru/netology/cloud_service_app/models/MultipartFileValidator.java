package ru.netology.cloud_service_app.models;

import lombok.val;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.function.Function;

@FunctionalInterface
public interface MultipartFileValidator extends Function<MultipartFile, Boolean> {
    static MultipartFileValidator isOriginalFilenameValid() {
        return multipartFile -> {
            val originalFilename = multipartFile.getOriginalFilename();
            return originalFilename != null && !originalFilename.isEmpty();
        };
    }

    static MultipartFileValidator isFileBodyValid() {
        return multipartFile -> {
            try {
                return multipartFile.getBytes().length > 0 && multipartFile.getSize() > 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    default MultipartFileValidator and(MultipartFileValidator other) {
        return multipartFile -> {
            val operationStatus = this.apply(multipartFile);
            return operationStatus ? other.apply(multipartFile) : false;
        };
    }
}
