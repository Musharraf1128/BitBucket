// Authentication types
export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    email: string;
    role: string;
    message: string;
}

// File types
export interface FileDTO {
    id: number;
    fileName: string;
    fileSize: number;
    contentType: string;
    uploadedAt: string;
    folderId?: number;
}

export interface FileUploadResponse {
    id: number;
    fileName: string;
    fileSize: number;
    message: string;
}

// Folder types
export interface FolderDTO {
    id: number;
    name: string;
    parentId?: number;
    createdAt: string;
    fileCount: number;
    subfolderCount: number;
}

export interface CreateFolderRequest {
    name: string;
    parentId?: number;
}

// Pagination
export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

// User context
export interface User {
    email: string;
    token: string;
    role: string;
}
