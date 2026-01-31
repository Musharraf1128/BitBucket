package com.razor.BitBucket.service;

import com.razor.BitBucket.dto.CreateFolderRequest;
import com.razor.BitBucket.dto.FolderDTO;
import com.razor.BitBucket.model.Folder;
import com.razor.BitBucket.model.User;
import com.razor.BitBucket.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public FolderDTO createFolder(CreateFolderRequest request, User owner) {
        Folder parent = null;
        if (request.getParentId() != null) {
            parent = folderRepository.findByIdAndOwner(request.getParentId(), owner)
                    .orElseThrow(() -> new IllegalArgumentException("Parent folder not found"));
        }

        if (folderRepository.existsByNameAndOwnerAndParent(request.getName(), owner, parent)) {
            throw new IllegalStateException("Folder with this name already exists in this location");
        }

        Folder folder = new Folder(request.getName(), parent, owner);
        folder = folderRepository.save(folder);

        return convertToDTO(folder);
    }

    public List<FolderDTO> listFolders(User owner, Long parentId) {
        List<Folder> folders;

        if (parentId == null) {
            folders = folderRepository.findByOwnerAndParentIsNull(owner);
        } else {
            Folder parent = folderRepository.findByIdAndOwner(parentId, owner)
                    .orElseThrow(() -> new IllegalArgumentException("Parent folder not found"));
            folders = folderRepository.findByOwnerAndParent(owner, parent);
        }

        return folders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FolderDTO getFolder(Long folderId, User owner) {
        Folder folder = folderRepository.findByIdAndOwner(folderId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        return convertToDTO(folder);
    }

    public void deleteFolder(Long folderId, User owner) {
        Folder folder = folderRepository.findByIdAndOwner(folderId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        folderRepository.delete(folder);
    }

    private FolderDTO convertToDTO(Folder folder) {
        return new FolderDTO(
                folder.getId(),
                folder.getName(),
                folder.getParent() != null ? folder.getParent().getId() : null,
                folder.getParent() != null ? folder.getParent().getName() : null,
                folder.getCreatedAt()
        );
    }
}
