package com.razor.BitBucket.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String storedFileName; // UUID-based filename on disk

    @Column(nullable = false)
    private String filePath; // Full path on disk

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Column(nullable = false)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt = Instant.now();

    protected FileMetadata() {
        // JPA only
    }

    public FileMetadata(String fileName, String storedFileName, String filePath, 
                       Long fileSize, String contentType, Folder folder, User owner) {
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.folder = folder;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public User getOwner() {
        return owner;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
