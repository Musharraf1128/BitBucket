package com.razor.BitBucket.repository;

import com.razor.BitBucket.model.FileMetadata;
import com.razor.BitBucket.model.Folder;
import com.razor.BitBucket.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    Page<FileMetadata> findByOwner(User owner, Pageable pageable);
    
    Page<FileMetadata> findByOwnerAndFolder(User owner, Folder folder, Pageable pageable);
    
    Page<FileMetadata> findByOwnerAndFolderIsNull(User owner, Pageable pageable);
    
    Optional<FileMetadata> findByIdAndOwner(Long id, User owner);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.owner = :owner AND LOWER(f.fileName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<FileMetadata> searchByFileName(@Param("owner") User owner, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT SUM(f.fileSize) FROM FileMetadata f WHERE f.owner = :owner")
    Long getTotalStorageUsedByOwner(@Param("owner") User owner);
    
    Long countByOwner(User owner);
}
