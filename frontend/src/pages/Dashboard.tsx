import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { fileAPI, folderAPI } from '../services/api';
import type { FileDTO, FolderDTO } from '../types';

const Dashboard: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [files, setFiles] = useState<FileDTO[]>([]);
    const [folders, setFolders] = useState<FolderDTO[]>([]);
    const [currentFolderId, setCurrentFolderId] = useState<number | undefined>();
    const [searchQuery, setSearchQuery] = useState('');
    const [loading, setLoading] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [newFolderName, setNewFolderName] = useState('');
    const [showCreateFolder, setShowCreateFolder] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user) {
            navigate('/login');
        } else {
            loadData();
        }
    }, [user, currentFolderId, navigate]);

    const loadData = async () => {
        setLoading(true);
        setError('');
        try {
            const [filesData, foldersData] = await Promise.all([
                fileAPI.listFiles(currentFolderId),
                folderAPI.listFolders(currentFolderId),
            ]);
            setFiles(filesData.content);
            setFolders(foldersData);
        } catch (err: any) {
            setError('Failed to load data');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!searchQuery.trim()) {
            loadData();
            return;
        }

        setLoading(true);
        try {
            const result = await fileAPI.searchFiles(searchQuery);
            setFiles(result.content);
            setFolders([]);
        } catch (err) {
            setError('Search failed');
        } finally {
            setLoading(false);
        }
    };

    const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        setUploading(true);
        setError('');
        try {
            await fileAPI.uploadFile(file, currentFolderId);
            loadData();
        } catch (err: any) {
            setError('Upload failed');
        } finally {
            setUploading(false);
        }
    };

    const handleDownload = async (fileId: number, fileName: string) => {
        try {
            const blob = await fileAPI.downloadFile(fileId);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } catch (err) {
            setError('Download failed');
        }
    };

    const handleDeleteFile = async (fileId: number) => {
        if (!confirm('Delete this file?')) return;

        try {
            await fileAPI.deleteFile(fileId);
            loadData();
        } catch (err) {
            setError('Delete failed');
        }
    };

    const handleCreateFolder = async () => {
        if (!newFolderName.trim()) return;

        try {
            await folderAPI.createFolder({
                name: newFolderName,
                parentId: currentFolderId,
            });
            setNewFolderName('');
            setShowCreateFolder(false);
            loadData();
        } catch (err) {
            setError('Failed to create folder');
        }
    };

    const handleDeleteFolder = async (folderId: number) => {
        if (!confirm('Delete this folder and all its contents?')) return;

        try {
            await folderAPI.deleteFolder(folderId);
            loadData();
        } catch (err) {
            setError('Delete failed');
        }
    };

    const formatFileSize = (bytes: number): string => {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
    };

    const formatDate = (dateString: string): string => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        });
    };

    return (
        <div className="min-h-screen bg-white">
            {/* Header */}
            <header className="border-b border-google-gray-300 bg-white">
                <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
                    <h1 className="text-xl font-normal text-google-gray-900">BitBucket</h1>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-google-gray-700">{user?.email}</span>
                        <button
                            onClick={logout}
                            className="text-sm text-google-blue hover:underline"
                        >
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            <div className="max-w-7xl mx-auto px-4 py-6">
                {/* Search and Actions */}
                <div className="mb-6 flex gap-3">
                    <div className="flex-1 flex gap-2">
                        <input
                            type="text"
                            placeholder="Search files..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                            className="flex-1 px-3 py-2 border border-google-gray-300 rounded focus:outline-none focus:border-google-blue text-google-gray-900"
                        />
                        <button
                            onClick={handleSearch}
                            className="px-4 py-2 bg-google-blue text-white rounded hover:bg-google-blue-hover"
                        >
                            Search
                        </button>
                    </div>
                    <button
                        onClick={() => setShowCreateFolder(!showCreateFolder)}
                        className="px-4 py-2 border border-google-gray-300 rounded hover:bg-google-gray-50 text-google-gray-900"
                    >
                        New Folder
                    </button>
                    <label className="px-4 py-2 bg-google-blue text-white rounded hover:bg-google-blue-hover cursor-pointer">
                        {uploading ? 'Uploading...' : 'Upload File'}
                        <input
                            type="file"
                            onChange={handleFileUpload}
                            disabled={uploading}
                            className="hidden"
                        />
                    </label>
                </div>

                {/* Create Folder Form */}
                {showCreateFolder && (
                    <div className="mb-4 p-4 border border-google-gray-300 rounded bg-google-gray-50">
                        <div className="flex gap-2">
                            <input
                                type="text"
                                placeholder="Folder name"
                                value={newFolderName}
                                onChange={(e) => setNewFolderName(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleCreateFolder()}
                                className="flex-1 px-3 py-2 border border-google-gray-300 rounded focus:outline-none focus:border-google-blue text-google-gray-900"
                            />
                            <button
                                onClick={handleCreateFolder}
                                className="px-4 py-2 bg-google-blue text-white rounded hover:bg-google-blue-hover"
                            >
                                Create
                            </button>
                            <button
                                onClick={() => setShowCreateFolder(false)}
                                className="px-4 py-2 border border-google-gray-300 rounded hover:bg-google-gray-100 text-google-gray-900"
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                )}

                {/* Error Message */}
                {error && (
                    <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded text-sm text-red-800">
                        {error}
                    </div>
                )}

                {/* Back Button */}
                {currentFolderId && (
                    <button
                        onClick={() => setCurrentFolderId(undefined)}
                        className="mb-4 text-sm text-google-blue hover:underline"
                    >
                        ← Back to root
                    </button>
                )}

                {/* Loading State */}
                {loading ? (
                    <div className="text-center py-8 text-google-gray-700">Loading...</div>
                ) : (
                    <>
                        {/* Folders */}
                        {folders.length > 0 && (
                            <div className="mb-6">
                                <h2 className="text-sm font-medium text-google-gray-900 mb-2">Folders</h2>
                                <div className="border border-google-gray-300 rounded">
                                    <table className="w-full">
                                        <thead className="bg-google-gray-50 border-b border-google-gray-300">
                                            <tr>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Name</th>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Created</th>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Items</th>
                                                <th className="px-4 py-2 text-right text-sm font-medium text-google-gray-900">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {folders.map((folder) => (
                                                <tr key={folder.id} className="border-b border-google-gray-200 hover:bg-google-gray-50">
                                                    <td className="px-4 py-3">
                                                        <button
                                                            onClick={() => setCurrentFolderId(folder.id)}
                                                            className="text-google-blue hover:underline text-sm"
                                                        >
                                                            📁 {folder.name}
                                                        </button>
                                                    </td>
                                                    <td className="px-4 py-3 text-sm text-google-gray-700">
                                                        {formatDate(folder.createdAt)}
                                                    </td>
                                                    <td className="px-4 py-3 text-sm text-google-gray-700">
                                                        {folder.fileCount} files, {folder.subfolderCount} folders
                                                    </td>
                                                    <td className="px-4 py-3 text-right">
                                                        <button
                                                            onClick={() => handleDeleteFolder(folder.id)}
                                                            className="text-sm text-red-600 hover:underline"
                                                        >
                                                            Delete
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}

                        {/* Files */}
                        {files.length > 0 ? (
                            <div>
                                <h2 className="text-sm font-medium text-google-gray-900 mb-2">Files</h2>
                                <div className="border border-google-gray-300 rounded">
                                    <table className="w-full">
                                        <thead className="bg-google-gray-50 border-b border-google-gray-300">
                                            <tr>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Name</th>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Size</th>
                                                <th className="px-4 py-2 text-left text-sm font-medium text-google-gray-900">Uploaded</th>
                                                <th className="px-4 py-2 text-right text-sm font-medium text-google-gray-900">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {files.map((file) => (
                                                <tr key={file.id} className="border-b border-google-gray-200 hover:bg-google-gray-50">
                                                    <td className="px-4 py-3 text-sm text-google-gray-900">{file.fileName}</td>
                                                    <td className="px-4 py-3 text-sm text-google-gray-700">
                                                        {formatFileSize(file.fileSize)}
                                                    </td>
                                                    <td className="px-4 py-3 text-sm text-google-gray-700">
                                                        {formatDate(file.uploadedAt)}
                                                    </td>
                                                    <td className="px-4 py-3 text-right space-x-3">
                                                        <button
                                                            onClick={() => handleDownload(file.id, file.fileName)}
                                                            className="text-sm text-google-blue hover:underline"
                                                        >
                                                            Download
                                                        </button>
                                                        <button
                                                            onClick={() => handleDeleteFile(file.id)}
                                                            className="text-sm text-red-600 hover:underline"
                                                        >
                                                            Delete
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        ) : folders.length === 0 ? (
                            <div className="text-center py-12 text-google-gray-700">
                                <p className="mb-2">No files or folders yet</p>
                                <p className="text-sm">Upload a file or create a folder to get started</p>
                            </div>
                        ) : null}
                    </>
                )}
            </div>
        </div>
    );
};

export default Dashboard;
