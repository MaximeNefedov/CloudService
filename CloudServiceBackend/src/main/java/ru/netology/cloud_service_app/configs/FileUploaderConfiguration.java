package ru.netology.cloud_service_app.configs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "application.file-uploader")
public class FileUploaderConfiguration {
    private String mainDirName;
    private String imageDirName;
    private String textDirName;
    private String audioDirName;
    private String videoDirName;
    private String applicationDirName;

    private Set<String> dirNames;

    public Set<String> getInnerDirNames() {
        return Set.of(imageDirName, textDirName, audioDirName, videoDirName, applicationDirName);
    }
}
