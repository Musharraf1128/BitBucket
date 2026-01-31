package com.razor.BitBucket.dto;

public class FileUploadResponse {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String message;

    public FileUploadResponse(Long id, String fileName, Long fileSize, String contentType, String message) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.message = message;
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

    public String getMessage() {
        return message;
    }
}
