package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "file_content")
    private byte[] fileContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Storage(String fileName, Long fileSize, byte[] fileContent, User user) {
        this.date = LocalDateTime.now();
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileContent = fileContent;
        this.user = user;
    }
}
