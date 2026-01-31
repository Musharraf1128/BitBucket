# Manual Configuration Guide

This guide provides step-by-step instructions for manually configuring the BitBucket file storage system.

## Table of Contents

1. [JWT Secret Configuration](#jwt-secret-configuration)
2. [Email SMTP Setup](#email-smtp-setup)
3. [Redis Configuration](#redis-configuration)
4. [Database Configuration](#database-configuration)
5. [File Storage Configuration](#file-storage-configuration)

## JWT Secret Configuration

### Why It's Important

The JWT secret is used to sign and verify authentication tokens. A strong secret is critical for security.

### Generating a Secure JWT Secret

#### Option 1: Using OpenSSL (Recommended)

```bash
openssl rand -base64 64
```

This generates a 512-bit (64-byte) random string encoded in base64.

#### Option 2: Using Python

```python
import secrets
print(secrets.token_urlsafe(64))
```

#### Option 3: Online Generator

Visit https://randomkeygen.com/ and use the "Fort Knox Passwords" section.

### Setting the JWT Secret

#### For Docker:

Edit `docker-compose.yml` and update the `JWT_SECRET` environment variable:

```yaml
environment:
  JWT_SECRET: your-generated-secret-here
```

#### For Local Development:

Edit `backend/src/main/resources/application.yaml`:

```yaml
jwt:
  secret: your-generated-secret-here
```

Or set an environment variable:

```bash
export JWT_SECRET=your-generated-secret-here
```

## Email SMTP Setup

### Gmail Setup (Recommended for Testing)

#### Step 1: Enable 2-Factor Authentication

1. Go to your Google Account settings
2. Navigate to Security
3. Enable 2-Step Verification

#### Step 2: Generate App Password

1. Go to https://myaccount.google.com/apppasswords
2. Select "Mail" and your device
3. Click "Generate"
4. Copy the 16-character password

#### Step 3: Configure Application

Edit `docker-compose.yml`:

```yaml
environment:
  MAIL_HOST: smtp.gmail.com
  MAIL_PORT: 587
  MAIL_USERNAME: your-email@gmail.com
  MAIL_PASSWORD: your-16-char-app-password
```

### Other SMTP Providers

#### Outlook/Hotmail

```yaml
MAIL_HOST: smtp-mail.outlook.com
MAIL_PORT: 587
MAIL_USERNAME: your-email@outlook.com
MAIL_PASSWORD: your-password
```

#### Yahoo Mail

```yaml
MAIL_HOST: smtp.mail.yahoo.com
MAIL_PORT: 587
MAIL_USERNAME: your-email@yahoo.com
MAIL_PASSWORD: your-app-password
```

#### SendGrid

```yaml
MAIL_HOST: smtp.sendgrid.net
MAIL_PORT: 587
MAIL_USERNAME: apikey
MAIL_PASSWORD: your-sendgrid-api-key
```

### Testing Email Configuration

After configuration, you can test email functionality by triggering a notification event in the application.

## Redis Configuration

### Using Docker (Default)

Redis is automatically configured when using `docker-compose up`. No manual configuration needed.

### Local Redis Installation

#### Install Redis

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install redis-server
sudo systemctl start redis-server
```

**macOS:**
```bash
brew install redis
brew services start redis
```

#### Configure Application

Edit `application.yaml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # Leave empty if no password
```

### Disabling Cache

If you don't want to use Redis caching:

```yaml
spring:
  cache:
    type: none
```

Or set environment variable:

```bash
export CACHE_TYPE=none
```

## Database Configuration

### Using Docker (Default)

PostgreSQL is automatically configured when using `docker-compose up`.

### Local PostgreSQL Installation

#### Install PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**macOS:**
```bash
brew install postgresql
brew services start postgresql
```

#### Create Database

```bash
sudo -u postgres psql
```

In PostgreSQL shell:

```sql
CREATE DATABASE filestorage_db;
CREATE USER bitbucket_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE filestorage_db TO bitbucket_user;
\q
```

#### Configure Application

Edit `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/filestorage_db
    username: bitbucket_user
    password: your_password
```

## File Storage Configuration

### Default Configuration

Files are stored in `/app/uploads` inside the Docker container, which is mapped to a Docker volume for persistence.

### Custom Upload Directory

#### For Docker:

Edit `docker-compose.yml`:

```yaml
environment:
  FILE_UPLOAD_DIR: /app/uploads
volumes:
  - /path/on/host:/app/uploads  # Map to specific host directory
```

#### For Local Development:

Edit `application.yaml`:

```yaml
file:
  upload-dir: /path/to/your/upload/directory
```

Ensure the directory exists and has proper permissions:

```bash
mkdir -p /path/to/your/upload/directory
chmod 755 /path/to/your/upload/directory
```

### Maximum File Size

Edit `application.yaml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

file:
  max-size: 104857600  # 100MB in bytes
```

## Environment Variables Summary

Create a `.env` file in the project root:

```bash
# JWT
JWT_SECRET=your-generated-secret-here

# Database (if not using Docker)
DB_HOST=localhost
DB_USER=postgres
DB_PASSWORD=postgres

# Redis (if not using Docker)
REDIS_HOST=localhost
REDIS_PASSWORD=

# Cache
CACHE_TYPE=redis

# File Storage
FILE_UPLOAD_DIR=/app/uploads

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Verification

### Test JWT Configuration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

If successful, you'll receive a JWT token.

### Test Database Connection

Check application logs:

```bash
docker logs bitbucket-backend | grep "HikariPool"
```

You should see successful connection messages.

### Test Redis Connection

```bash
docker exec -it bitbucket-redis redis-cli ping
```

Should return `PONG`.

### Test File Upload

```bash
TOKEN="your-jwt-token"
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@testfile.txt"
```

## Troubleshooting

### JWT Token Invalid

- Ensure JWT_SECRET is at least 256 bits (32 characters)
- Check that the secret matches between application instances
- Verify token hasn't expired (default: 1 hour)

### Email Not Sending

- Verify SMTP credentials are correct
- Check if 2FA is enabled and app password is used
- Ensure firewall allows outbound connections on port 587
- Check application logs for detailed error messages

### Redis Connection Failed

- Verify Redis is running: `redis-cli ping`
- Check Redis host and port configuration
- If using password, ensure it matches
- Consider disabling cache with `CACHE_TYPE=none` for testing

### Database Connection Failed

- Verify PostgreSQL is running
- Check database name, username, and password
- Ensure database exists
- Check firewall rules for port 5432

### File Upload Fails

- Check upload directory permissions
- Verify max file size configuration
- Ensure sufficient disk space
- Check application logs for detailed errors

## Security Best Practices

1. **Never commit secrets** to version control
2. **Use strong JWT secrets** (minimum 256 bits)
3. **Use app passwords** for email, not account passwords
4. **Rotate secrets regularly** in production
5. **Use environment variables** for sensitive configuration
6. **Enable HTTPS** in production
7. **Restrict file upload types** if needed
8. **Implement file size limits** to prevent abuse
9. **Regular security updates** for dependencies
10. **Monitor logs** for suspicious activity
