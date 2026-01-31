package com.razor.BitBucket.service;

import com.razor.BitBucket.dto.FileDTO;
import com.razor.BitBucket.dto.FileUploadResponse;
import com.razor.BitBucket.model.FileMetadata;
import com.razor.BitBucket.model.Folder;
import com.razor.BitBucket.model.User;
import com.razor.BitBucket.repository.FileMetadataRepository;
import com.razor.BitBucket.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;
    private final FolderRepository folderRepository;

    public FileStorageService(
            @Value("${file.upload-dir}") String uploadDir,
            FileMetadataRepository fileMetadataRepository,
            FolderRepository folderRepository
    ) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileMetadataRepository = fileMetadataRepository;
        this.folderRepository = folderRepository;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public FileUploadResponse uploadFile(MultipartFile file, Long folderId, User owner) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findByIdAndOwner(folderId, owner)
                    .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        }

        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = new FileMetadata(
                    originalFilename,
                    storedFileName,
                    targetLocation.toString(),
                    file.getSize(),
                    file.getContentType(),
                    folder,
                    owner
            );

            metadata = fileMetadataRepository.save(metadata);

            return new FileUploadResponse(
                    metadata.getId(),
                    metadata.getFileName(),
                    metadata.getFileSize(),
                    metadata.getContentType(),
                    "File uploaded successfully"
            );

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    public Resource downloadFile(Long fileId, User owner) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndOwner(fileId, owner)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        try {
            Path filePath = Paths.get(metadata.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error downloading file", ex);
        }
    }

    public String getFileName(Long fileId, User owner) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndOwner(fileId, owner)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return metadata.getFileName();
    }

    public String getContentType(Long fileId, User owner) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndOwner(fileId, owner)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return metadata.getContentType();
    }

    public void deleteFile(Long fileId, User owner) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndOwner(fileId, owner)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        try {
            Path filePath = Paths.get(metadata.getFilePath());
            Files.deleteIfExists(filePath);
            fileMetadataRepository.delete(metadata);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete file", ex);
        }
    }

    public Page<FileDTO> listFiles(User owner, Long folderId, Pageable pageable) {
        Page<FileMetadata> files;

        if (folderId == null) {
            files = fileMetadataRepository.findByOwnerAndFolderIsNull(owner, pageable);
        } else {
            Folder folder = folderRepository.findByIdAndOwner(folderId, owner)
                    .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
            files = fileMetadataRepository.findByOwnerAndFolder(owner, folder, pageable);
        }

        return files.map(this::convertToDTO);
    }

    public Page<FileDTO> searchFiles(User owner, String searchTerm, Pageable pageable) {
        return fileMetadataRepository.searchByFileName(owner, searchTerm, pageable)
                .map(this::convertToDTO);
    }

    private FileDTO convertToDTO(FileMetadata metadata) {
        return new FileDTO(
                metadata.getId(),
                metadata.getFileName(),
                metadata.getFileSize(),
                metadata.getContentType(),
                metadata.getFolder() != null ? metadata.getFolder().getId() : null,
                metadata.getFolder() != null ? metadata.getFolder().getName() : null,
                metadata.getUploadedAt()
        );
    }
}
