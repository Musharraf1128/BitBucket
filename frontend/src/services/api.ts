import axios from 'axios';
import type {
    LoginRequest,
    RegisterRequest,
    AuthResponse,
    FileDTO,
    FileUploadResponse,
    FolderDTO,
    CreateFolderRequest,
    Page,
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor to handle errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Auth API
export const authAPI = {
    login: async (data: LoginRequest): Promise<AuthResponse> => {
        const response = await api.post<AuthResponse>('/api/auth/login', data);
        return response.data;
    },

    register: async (data: RegisterRequest): Promise<AuthResponse> => {
        const response = await api.post<AuthResponse>('/api/auth/register', data);
        return response.data;
    },
};

// File API
export const fileAPI = {
    uploadFile: async (file: File, folderId?: number): Promise<FileUploadResponse> => {
        const formData = new FormData();
        formData.append('file', file);
        if (folderId) {
            formData.append('folderId', folderId.toString());
        }

        const response = await api.post<FileUploadResponse>('/api/files/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    },

    listFiles: async (
        folderId?: number,
        page = 0,
        size = 20,
        sort = 'uploadedAt,desc'
    ): Promise<Page<FileDTO>> => {
        const params: any = { page, size, sort };
        if (folderId) {
            params.folderId = folderId;
        }
        const response = await api.get<Page<FileDTO>>('/api/files', { params });
        return response.data;
    },

    searchFiles: async (query: string, page = 0, size = 20): Promise<Page<FileDTO>> => {
        const response = await api.get<Page<FileDTO>>('/api/files/search', {
            params: { q: query, page, size },
        });
        return response.data;
    },

    downloadFile: async (fileId: number): Promise<Blob> => {
        const response = await api.get(`/api/files/${fileId}/download`, {
            responseType: 'blob',
        });
        return response.data;
    },

    deleteFile: async (fileId: number): Promise<void> => {
        await api.delete(`/api/files/${fileId}`);
    },
};

// Folder API
export const folderAPI = {
    createFolder: async (data: CreateFolderRequest): Promise<FolderDTO> => {
        const response = await api.post<FolderDTO>('/api/folders', data);
        return response.data;
    },

    listFolders: async (parentId?: number): Promise<FolderDTO[]> => {
        const params = parentId ? { parentId } : {};
        const response = await api.get<FolderDTO[]>('/api/folders', { params });
        return response.data;
    },

    getFolder: async (folderId: number): Promise<FolderDTO> => {
        const response = await api.get<FolderDTO>(`/api/folders/${folderId}`);
        return response.data;
    },

    deleteFolder: async (folderId: number): Promise<void> => {
        await api.delete(`/api/folders/${folderId}`);
    },
};

export default api;
