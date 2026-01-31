package com.razor.BitBucket.repository;

import com.razor.BitBucket.model.Folder;
import com.razor.BitBucket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    
    List<Folder> findByOwner(User owner);
    
    List<Folder> findByOwnerAndParent(User owner, Folder parent);
    
    List<Folder> findByOwnerAndParentIsNull(User owner);
    
    Optional<Folder> findByIdAndOwner(Long id, User owner);
    
    boolean existsByNameAndOwnerAndParent(String name, User owner, Folder parent);
}
