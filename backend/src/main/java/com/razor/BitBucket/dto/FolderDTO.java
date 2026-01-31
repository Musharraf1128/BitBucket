package com.razor.BitBucket.dto;

import java.time.Instant;

public class FolderDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private Instant createdAt;

    public FolderDTO(Long id, String name, Long parentId, String parentName, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
