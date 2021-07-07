package ru.netology.cloud_service_app.configs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private String mimeTypesFilename;
    private char mimeTypesDelimiter;
    private Map<String, String> mimeTypesMap;
    private Set<String> dirNames;

    public Map<String, String> getMimeTypesMap() {
        if (mimeTypesMap == null || mimeTypesMap.isEmpty()) {
            try (val in = new FileReader(mimeTypesFilename)) {
                Iterable<CSVRecord> records = CSVFormat.newFormat(mimeTypesDelimiter).parse(in);
                mimeTypesMap = new HashMap<>();
                for (CSVRecord record : records) {
                    val fileType = record.get(0);
                    val contentType = record.get(1);
                    mimeTypesMap.put(contentType, fileType);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mimeTypesMap;
    }

    public Set<String> getInnerDirNames() {
        return Set.of(imageDirName, textDirName, audioDirName, videoDirName, applicationDirName);
    }
}
