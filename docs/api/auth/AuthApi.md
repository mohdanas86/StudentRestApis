# Authentication API Documentation

## Overview
Complete REST API for user authentication within the Student REST APIs system. Provides endpoints for user registration, login, token refresh, and token validation. These endpoints do NOT require authentication.

**Base URL:** `http://localhost:8000/api/v1`  
**Authentication:** Not required (Public endpoints)  
**Response Format:** JSON with `ApiResponse` wrapper  
**Token Type:** JWT (Bearer)

---

## 1. Register User

### Endpoint
```
POST /auth/register
```

### Description
Registers a new user in the system. Users can register with ADMIN or TEACHER roles. STUDENT registration is blocked by design (must be registered by administrators). Returns user details on successful registration.

### Request
- **Method:** POST
- **URL:** `/auth/register`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "username": "teacher001",
  "email": "teacher001@college.ac.in",
  "password": "SecurePass@123",
  "role": "TEACHER"
}
```

### Request Fields
| Field    | Type   | Required | Validation                                                               | Description                 |
| -------- | ------ | -------- | ------------------------------------------------------------------------ | --------------------------- |
| username | String | Yes      | 3-50 chars, unique, alphanumeric + underscore                            | Unique login username       |
| email    | String | Yes      | Valid email format, unique                                               | User email address          |
| password | String | Yes      | Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char (@$!%*?&) | Strong password requirement |
| role     | String | Yes      | ADMIN or TEACHER only                                                    | User role in system         |

### Response

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "userId": 5,
    "username": "teacher001",
    "email": "teacher001@college.ac.in",
    "role": "TEACHER",
    "createdAt": "2026-04-21T18:30:00"
  },
  "message": "User registered successfully",
  "timestamp": "2026-04-21T18:30:00Z"
}
```

**Error Response (400 Bad Request - Student Registration Blocked):**
```json
{
  "success": false,
  "message": "Failed to register",
  "error": "Student must be registered by administrators",
  "timestamp": "2026-04-21T18:30:00Z"
}
```

**Error Response (400 Bad Request - Duplicate Username):**
```json
{
  "success": false,
  "message": "Failed to register",
  "error": "Username already exists",
  "timestamp": "2026-04-21T18:30:00Z"
}
```

**Error Response (400 Bad Request - Invalid Password):**
```json
{
  "success": false,
  "message": "Failed to register",
  "error": "Password must contain uppercase, lowercase, digit, and special character (@$!%*?&)",
  "timestamp": "2026-04-21T18:30:00Z"
}
```

**Error Response (400 Bad Request - Validation Error):**
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "Email is invalid",
  "timestamp": "2026-04-21T18:30:00Z"
}
```

### Status Codes
| Code | Description                             |
| ---- | --------------------------------------- |
| 201  | User successfully registered            |
| 400  | Validation failed or duplicate resource |
| 500  | Server error while registering          |

### cURL Example
```bash
curl -X POST http://localhost:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher001",
    "email": "teacher001@college.ac.in",
    "password": "SecurePass@123",
    "role": "TEACHER"
  }'
```

### Notes
- **STUDENT role is blocked** - Students must be registered by administrators through a separate endpoint
- **Unique constraints:** Username and email must be unique across all users
- **Password requirements:** Must be 8-128 characters with mixed case, digit, and special character

---

## 2. User Login

### Endpoint
```
POST /auth/login
```

### Description
Authenticates a user and returns JWT access and refresh tokens. These tokens are required for accessing protected endpoints.

### Request
- **Method:** POST
- **URL:** `/auth/login`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "username": "admin001",
  "password": "AdminPass@123"
}
```

### Request Fields
| Field    | Type   | Required | Description         |
| -------- | ------ | -------- | ------------------- |
| username | String | Yes      | Registered username |
| password | String | Yes      | User password       |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "userId": 1,
      "username": "admin001",
      "email": "admin@example.com",
      "role": "ADMIN",
      "createdAt": "2026-04-20T10:00:00"
    }
  },
  "message": "Login successful",
  "timestamp": "2026-04-21T18:35:00Z"
}
```

**Error Response (400 Bad Request - Invalid Credentials):**
```json
{
  "success": false,
  "message": "Failed to login",
  "error": "Invalid username or password",
  "timestamp": "2026-04-21T18:35:00Z"
}
```

**Error Response (400 Bad Request - User Not Found):**
```json
{
  "success": false,
  "message": "Failed to login",
  "error": "User not found",
  "timestamp": "2026-04-21T18:35:00Z"
}
```

**Error Response (500 Internal Server Error - JWT Generation Failed):**
```json
{
  "success": false,
  "message": "Failed to login",
  "error": "Token generation failed",
  "timestamp": "2026-04-21T18:35:00Z"
}
```

### Status Codes
| Code | Description                           |
| ---- | ------------------------------------- |
| 200  | Login successful, tokens returned     |
| 400  | Invalid credentials or user not found |
| 500  | Server error during login             |

### Response Fields
| Field        | Type   | Description                                             |
| ------------ | ------ | ------------------------------------------------------- |
| accessToken  | String | JWT access token for API requests                       |
| refreshToken | String | JWT refresh token for obtaining new access token        |
| tokenType    | String | Always "Bearer" for JWT tokens                          |
| expiresIn    | Long   | Access token expiration time in seconds                 |
| user         | Object | User details (userId, username, email, role, createdAt) |

### Token Usage
Add the access token to subsequent API requests:
```bash
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...
```

### cURL Example
```bash
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin001",
    "password": "AdminPass@123"
  }'
```

### Notes
- **Access token expiration:** 24 hours (86400 seconds)
- **Refresh token expiration:** 7 days
- **Token algorithm:** HS512 (HMAC-SHA512)
- **Security:** Always transmit tokens over HTTPS in production

---

## 3. Refresh Access Token

### Endpoint
```
POST /auth/refresh
```

### Description
Generates a new access token using a valid refresh token. Refresh tokens have a longer lifespan (7 days) to allow users to get new access tokens without re-logging in.

### Request
- **Method:** POST
- **URL:** `/auth/refresh`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
}
```

### Request Fields
| Field        | Type   | Required | Description                    |
| ------------ | ------ | -------- | ------------------------------ |
| refreshToken | String | Yes      | Valid refresh token from login |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "token_type": "Bearer",
    "expires_in": 86400
  },
  "message": "Token refreshed successfully",
  "timestamp": "2026-04-21T18:40:00Z"
}
```

**Error Response (400 Bad Request - Invalid Refresh Token):**
```json
{
  "success": false,
  "message": "Failed to refresh token",
  "error": "Invalid or expired refresh token",
  "timestamp": "2026-04-21T18:40:00Z"
}
```

**Error Response (400 Bad Request - Expired Refresh Token):**
```json
{
  "success": false,
  "message": "Failed to refresh token",
  "error": "Refresh token has expired. Please login again.",
  "timestamp": "2026-04-21T18:40:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to refresh token",
  "error": "Token generation failed",
  "timestamp": "2026-04-21T18:40:00Z"
}
```

### Status Codes
| Code | Description                       |
| ---- | --------------------------------- |
| 200  | New access token generated        |
| 400  | Invalid or expired refresh token  |
| 500  | Server error during token refresh |

### cURL Example
```bash
curl -X POST http://localhost:8000/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
  }'
```

### Notes
- **When to use:** Call this endpoint when access token is about to expire
- **Refresh token lifespan:** 7 days
- **New access token:** Valid for 24 hours

---

## 4. Validate Token

### Endpoint
```
POST /auth/validate
```

### Description
Validates a JWT token without making any state changes. Useful for debugging and verifying token validity. This is a debug endpoint and may not be available in production.

### Request
- **Method:** POST
- **URL:** `/auth/validate`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
}
```

### Request Fields
| Field | Type   | Required | Description           |
| ----- | ------ | -------- | --------------------- |
| token | String | Yes      | JWT token to validate |

### Response

**Success Response (200 OK - Valid Token):**
```json
{
  "success": true,
  "data": {
    "valid": true,
    "username": "admin001",
    "role": "ADMIN",
    "expiresAt": "2026-04-22T18:35:00Z"
  },
  "message": "Token is valid",
  "timestamp": "2026-04-21T18:45:00Z"
}
```

**Error Response (400 Bad Request - Invalid Token):**
```json
{
  "success": false,
  "data": {
    "valid": false,
    "reason": "Invalid JWT signature"
  },
  "message": "Token validation failed",
  "timestamp": "2026-04-21T18:45:00Z"
}
```

**Error Response (400 Bad Request - Expired Token):**
```json
{
  "success": false,
  "data": {
    "valid": false,
    "reason": "Token has expired"
  },
  "message": "Token validation failed",
  "timestamp": "2026-04-21T18:45:00Z"
}
```

### Status Codes
| Code | Description                 |
| ---- | --------------------------- |
| 200  | Token validation completed  |
| 400  | Token is invalid or expired |

### cURL Example
```bash
curl -X POST http://localhost:8000/api/v1/auth/validate \
  -H "Content-Type: application/json" \
  -d '{
    "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
  }'
```

### Notes
- **Debug only:** This endpoint is primarily for debugging and testing
- **No state change:** This request does not modify any data
- **Production note:** May be disabled in production environments

---

## Authentication Flow

### Complete Login & Use Flow
```
1. User registers or logs in
   POST /auth/register → Returns userId
   POST /auth/login → Returns accessToken & refreshToken

2. User accesses protected endpoints with accessToken
   GET /colleges
   Authorization: Bearer {accessToken}

3. When accessToken expires (24 hours)
   POST /auth/refresh → Returns new accessToken

4. When refreshToken expires (7 days)
   User must login again via POST /auth/login
```

### Token Structure (JWT)
```
Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "userId": 1,
  "role": "ADMIN",
  "email": "admin@example.com",
  "type": "access",
  "iss": "StudentRestAPIs",
  "aud": "web-client",
  "iat": 1713705000,
  "exp": 1713791400
}

Signature: HMAC_SHA512(header.payload, secret)
```

---

## Password Requirements

| Requirement      | Details                                |
| ---------------- | -------------------------------------- |
| **Length**       | 8-128 characters                       |
| **Uppercase**    | At least 1 uppercase letter (A-Z)      |
| **Lowercase**    | At least 1 lowercase letter (a-z)      |
| **Digit**        | At least 1 digit (0-9)                 |
| **Special char** | At least 1 special character (@$!%*?&) |

### Valid Password Examples
✅ `AdminPass@123`  
✅ `SecurePass@456`  
✅ `Teacher@2026`  

### Invalid Password Examples
❌ `password123` (no uppercase, no special char)  
❌ `Pass@` (too short)  
❌ `PASSWORD123@` (no lowercase)  
❌ `Pass123` (no special char)

---

## Common Errors

| Error                                        | Cause                            | Solution                                         |
| -------------------------------------------- | -------------------------------- | ------------------------------------------------ |
| Invalid username or password                 | Wrong credentials provided       | Verify username/password or register new account |
| Username already exists                      | Username taken                   | Choose a different username                      |
| Student must be registered by administrators | Trying to register as STUDENT    | Contact admin for student registration           |
| Invalid or expired refresh token             | Refresh token is invalid/expired | Login again to get new tokens                    |
| Token has expired                            | Access token expired             | Use refresh token to get new access token        |
| JWT secret is invalid                        | Server configuration issue       | Contact system administrator                     |

---

## Security Best Practices

1. **Store tokens securely**
   - Use httpOnly cookies in browsers (prevent XSS)
   - Avoid storing in localStorage (vulnerable to XSS)
   - Never log or transmit tokens in plain text

2. **Token rotation**
   - Always refresh tokens before expiration
   - Invalidate old tokens on logout
   - Use refresh tokens only once

3. **HTTPS only**
   - Always use HTTPS in production
   - Never transmit tokens over HTTP

4. **Timeouts**
   - Access token: 24 hours (short-lived)
   - Refresh token: 7 days (longer-lived)
   - Set reasonable session timeouts

5. **Password security**
   - Enforce strong password requirements
   - Use bcrypt/scrypt hashing
   - Never store plain text passwords
