package ru.netology.cloud_service_app.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileData {
    @JsonProperty(value = "filename")
    private String fileName;
    private final long size;
}