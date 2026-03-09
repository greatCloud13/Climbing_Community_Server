package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orgName;

    @Column(nullable = false, unique = true)
    private String savedName;

    @Column(nullable = false)
    private String savedPath;

    private String ext;

    private Long size;

    @Builder
    public FileEntity(String orgName, String savedName, String savedPath, String ext, Long size) {
        this.orgName = orgName;
        this.savedName = savedName;
        this.savedPath = savedPath;
        this.ext = ext;
        this.size = size;
    }
}
