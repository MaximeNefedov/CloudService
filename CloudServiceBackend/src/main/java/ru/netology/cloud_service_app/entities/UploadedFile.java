package ru.netology.cloud_service_app.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "files")
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private long size;
    @Column(nullable = false)
    private LocalDateTime changeTime;
    @Column(nullable = false)
    private byte[] fileBody;
    @Column(nullable = false)
    private String hash;
    @Enumerated(EnumType.STRING)
    private UploadedFileStatus status;
    private LocalDateTime removalTime;
    @ManyToOne
    @ToString.Exclude
    private User user;
}
