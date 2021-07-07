package ru.netology.cloud_service_app.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileData implements Serializable {
    @JsonProperty(value = "filename")
    private String fileName;
    private long size;
}