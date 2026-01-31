package com.razor.BitBucket.controller;

import com.razor.BitBucket.dto.CreateFolderRequest;
import com.razor.BitBucket.dto.FolderDTO;
import com.razor.BitBucket.model.User;
import com.razor.BitBucket.repository.UserRepository;
import com.razor.BitBucket.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserRepository userRepository;

    public FolderController(FolderService folderService, UserRepository userRepository) {
        this.folderService = folderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<FolderDTO> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        FolderDTO folder = folderService.createFolder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> listFolders(
            @RequestParam(value = "parentId", required = false) Long parentId,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        List<FolderDTO> folders = folderService.listFolders(user, parentId);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderDTO> getFolder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        FolderDTO folder = folderService.getFolder(id, user);
        return ResponseEntity.ok(folder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        folderService.deleteFolder(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
