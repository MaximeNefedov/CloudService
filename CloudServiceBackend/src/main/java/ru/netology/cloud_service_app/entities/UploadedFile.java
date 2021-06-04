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
    private LocalDateTime date;
//    @Lob
    private byte[] fileBody;
    @ManyToOne
    @ToString.Exclude
    private User user;
}
