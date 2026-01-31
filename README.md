# BitBucket - File Storage System

A comprehensive file storage system with React frontend and Spring Boot backend, featuring JWT authentication, file/folder management, caching, rate limiting, and complete Docker support.

## 🚀 Features

### Frontend
- **React + TypeScript** - Modern, type-safe frontend
- **Clean UI** - Google-like minimal design with white background
- **Responsive** - Works on desktop and mobile devices
- **File Upload** - Simple drag-and-drop file upload
- **Folder Navigation** - Intuitive folder structure browsing
- **Search** - Real-time file search functionality

### Backend
- **JWT Authentication** - Secure user registration and login with role-based access control
- **File Management** - Upload, download, delete files with metadata tracking
- **Folder Structure** - Organize files in nested folders
- **Search & Filter** - Search files by name with pagination and sorting
- **Caching** - Redis-based caching for improved performance
- **Rate Limiting** - Bucket4j-based rate limiting to prevent abuse
- **Email Notifications** - SMTP integration for notifications
- **API Documentation** - Swagger/OpenAPI documentation
- **Docker Support** - Complete containerization with one-command startup
- **Data Persistence** - PostgreSQL database with volume mounts

## 📋 Prerequisites

- Docker and Docker Compose
- (Optional) Java 17+ and Maven 3.9+ for backend local development
- (Optional) Node.js 20+ and npm for frontend local development

## 🏃 Quick Start

### One-Command Docker Startup

**Important**: Create a `.env` file first with your credentials:

```bash
cp .env.template .env
# Edit .env and add your JWT_SECRET, MAIL_USERNAME, and MAIL_PASSWORD
```

Then start the application:

```bash
# Navigate to the project root directory
cd BitBucket
docker-compose up --build
```

The application will be available at:
- **Frontend**: http://localhost:3000 (Main application UI)
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/api/health

### Services

- **Frontend**: Port 3000 (Nginx serving React app)
- **Backend**: Port 8080 (Spring Boot API)
- **PostgreSQL**: Internal (not exposed)
- **Redis**: Port 6379

## 🖥️ Using the Frontend

### Access the Application

Open your browser and navigate to **http://localhost:3000**

### First Time Setup

1. **Create an Account**
   - Click "Create account" on the login page
   - Enter your email and password
   - Click "Create account"
   - You'll be automatically logged in and redirected to the dashboard

2. **Login**
   - Enter your email and password
   - Click "Sign in"
   - You'll be redirected to the dashboard

### Dashboard Features

**File Management:**
- **Upload Files**: Click "Upload File" button and select a file
- **Download Files**: Click "Download" next to any file
- **Delete Files**: Click "Delete" next to any file (confirmation required)
- **Search Files**: Use the search bar at the top to find files by name

**Folder Management:**
- **Create Folder**: Click "New Folder", enter a name, and click "Create"
- **Navigate Folders**: Click on any folder name to enter it
- **Delete Folder**: Click "Delete" next to any folder (confirmation required)
- **Back to Root**: Click "← Back to root" when inside a folder

**User Account:**
- **View Email**: Your email is displayed in the top right corner
- **Logout**: Click "Logout" to sign out

## 🔐 Authentication

### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com",
  "role": "USER",
  "message": "Registration successful"
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

## 📁 File Operations

### Upload a File

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/file.pdf"
```

### Upload to a Folder

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/file.pdf" \
  -F "folderId=1"
```

### List Files

```bash
curl http://localhost:8080/api/files?page=0&size=20&sort=uploadedAt,desc \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Download a File

```bash
curl http://localhost:8080/api/files/1/download \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -O -J
```

### Search Files

```bash
curl "http://localhost:8080/api/files/search?q=document&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete a File

```bash
curl -X DELETE http://localhost:8080/api/files/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📂 Folder Operations

### Create a Folder

```bash
curl -X POST http://localhost:8080/api/folders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Documents"
  }'
```

### Create a Subfolder

```bash
curl -X POST http://localhost:8080/api/folders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Work Files",
    "parentId": 1
  }'
```

### List Folders

```bash
curl http://localhost:8080/api/folders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete a Folder

```bash
curl -X DELETE http://localhost:8080/api/folders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🛠️ Configuration

### Environment Variables

Create a `.env` file from the template:

```bash
cp .env.template .env
```

Key variables:
- `JWT_SECRET`: Secret key for JWT token generation (min 256 bits recommended)
- `MAIL_USERNAME`: SMTP email username
- `MAIL_PASSWORD`: SMTP app password
- `CACHE_TYPE`: Set to `redis` to enable caching, `none` to disable

See [MANUAL_CONFIGURATION.md](MANUAL_CONFIGURATION.md) for detailed configuration instructions.

## 🏗️ Architecture

### Technology Stack

- **Backend**: Spring Boot 3.5.10
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Authentication**: JWT (JJWT 0.12.3)
- **API Docs**: SpringDoc OpenAPI 2.3.0
- **Rate Limiting**: Bucket4j 8.7.0
- **Build**: Maven 3.9
- **Runtime**: Java 17

### Project Structure

```
backend/
├── src/main/java/com/razor/BitBucket/
│   ├── config/          # Security, JWT, Cache, Rate Limiting
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # JPA Entities
│   ├── repository/      # Data Access Layer
│   ├── service/         # Business Logic
│   └── util/            # JWT Utilities
└── src/main/resources/
    └── application.yaml # Application configuration
```

## 🐳 Docker

### Build and Run

```bash
docker-compose up --build
```

### Stop Services

```bash
docker-compose down
```

### View Logs

```bash
docker logs bitbucket-backend -f
```

### Rebuild After Code Changes

```bash
docker-compose up --build -d
```

## 📊 Database

### Access PostgreSQL (from host)

```bash
docker exec -it bitbucket-db psql -U postgres -d filestorage_db
```

### Tables

- `users` - User accounts
- `folders` - Folder structure
- `file_metadata` - File information

## 🧪 Testing

### Health Check

```bash
curl http://localhost:8080/api/health
```

### Swagger UI

Navigate to http://localhost:8080/swagger-ui.html to explore and test all API endpoints interactively.

## 🔧 Troubleshooting

### Port Already in Use

If port 8080 is already in use, modify `docker-compose.yml`:

```yaml
ports:
  - "8081:8080"  # Change 8080 to 8081
```

### Database Connection Issues

Check if PostgreSQL is healthy:

```bash
docker-compose ps
```

### File Upload Issues

Ensure the upload directory has proper permissions:

```bash
docker exec -it bitbucket-backend ls -la /app/uploads
```

### Clear All Data

```bash
docker-compose down -v  # Removes volumes
docker-compose up --build
```

## 📝 API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login user | No |
| GET | `/api/health` | Health check | No |
| POST | `/api/files/upload` | Upload file | Yes |
| GET | `/api/files/{id}/download` | Download file | Yes |
| GET | `/api/files` | List files | Yes |
| GET | `/api/files/search` | Search files | Yes |
| DELETE | `/api/files/{id}` | Delete file | Yes |
| POST | `/api/folders` | Create folder | Yes |
| GET | `/api/folders` | List folders | Yes |
| GET | `/api/folders/{id}` | Get folder | Yes |
| DELETE | `/api/folders/{id}` | Delete folder | Yes |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 👤 Author

Musharraf1128

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL and Redis communities
- Docker for containerization support
