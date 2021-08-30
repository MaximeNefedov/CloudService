package ru.netology.cloud_service_app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Validated
public class NewFilename {
    @Pattern(regexp = "\\w+\\..+")
    private String filename;
}
