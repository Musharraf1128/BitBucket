package com.razor.BitBucket.dto;

import java.time.Instant;

public class FileDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private Long folderId;
    private String folderName;
    private Instant uploadedAt;

    public FileDTO(Long id, String fileName, Long fileSize, String contentType, 
                  Long folderId, String folderName, Instant uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.folderId = folderId;
        this.folderName = folderName;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
