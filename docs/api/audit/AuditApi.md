# Audit Logs API Documentation

## Overview
Complete REST API for retrieving and analyzing audit logs within the Student REST APIs system. This API supports filtered audit log search, entity history tracking, user activity history, and statistics reporting.

**Base URL:** `http://localhost:8000`  
**Authentication:** Required (ADMIN role only)  
**Response Format:** JSON with `ApiResponse` wrapper

---

## 1. Get Audit Logs (Paginated + Filters)

### Endpoint
```
GET /api/v1/audit-logs
```

### Description
Retrieves paginated audit logs with optional filters for entity type, action, user, and date range.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** GET
- **URL:** `/api/v1/audit-logs`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Query Parameters
| Parameter  | Type          | Required | Description                                                                                                      |
| ---------- | ------------- | -------- | ---------------------------------------------------------------------------------------------------------------- |
| entityType | String        | No       | Filter by entity type (example: `TEACHER`, `COURSE`, `COLLEGE`, `USER`)                                          |
| action     | String (Enum) | No       | Filter by action (`CREATE`, `READ`, `UPDATE`, `DELETE`, `LOGIN`, `LOGOUT`, `PERMISSION_DENIED`, `INVALID_LOGIN`) |
| userId     | Long          | No       | Filter by user ID                                                                                                |
| startDate  | LocalDateTime | No       | Start datetime (ISO format)                                                                                      |
| endDate    | LocalDateTime | No       | End datetime (ISO format)                                                                                        |
| page       | Integer       | No       | Page index (default: `0`)                                                                                        |
| size       | Integer       | No       | Page size (default: `10`)                                                                                        |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "auditId": 101,
        "user": {
          "userId": 1,
          "username": "admin001"
        },
        "entityType": "TEACHER",
        "entityId": 5,
        "action": "UPDATE",
        "oldValues": "{\"email\":\"old@example.com\"}",
        "newValues": "{\"email\":\"new@example.com\"}",
        "ipAddress": "192.168.1.10",
        "userAgent": "Mozilla/5.0",
        "status": "SUCCESS",
        "errorMessage": null,
        "requestPath": "/api/v1/teachers/5",
        "requestMethod": "PUT",
        "createdAt": "2026-04-26T23:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  },
  "message": "Audit logs retrieved successfully",
  "timestamp": "2026-04-26T23:10:00Z"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Invalid filter parameters",
  "error": "Failed to convert value for action",
  "timestamp": "2026-04-26T23:10:00Z"
}
```

**Error Response (401 Unauthorized / 403 Forbidden):**
```json
{
  "success": false,
  "message": "Unauthorized",
  "error": "Access denied",
  "timestamp": "2026-04-26T23:10:00Z"
}
```

### Status Codes
| Code | Description                          |
| ---- | ------------------------------------ |
| 200  | Audit logs retrieved successfully    |
| 400  | Invalid query/filter parameters      |
| 401  | Unauthorized (missing/invalid token) |
| 403  | Forbidden (not ADMIN role)           |
| 500  | Server error while fetching logs     |

### cURL Example
```bash
curl -X GET "http://localhost:8000/api/v1/audit-logs?entityType=TEACHER&action=UPDATE&page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 2. Get Audit History by Entity

### Endpoint
```
GET /api/v1/audit-logs/entity/{entityType}/{entityId}
```

### Description
Retrieves complete audit history for a specific entity.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** GET
- **URL:** `/api/v1/audit-logs/entity/{entityType}/{entityId}`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Path Parameters
| Parameter  | Type   | Required | Description                                |
| ---------- | ------ | -------- | ------------------------------------------ |
| entityType | String | Yes      | Entity type (example: `TEACHER`, `COURSE`) |
| entityId   | Long   | Yes      | Target entity ID                           |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "auditId": 210,
      "entityType": "COURSE",
      "entityId": 7,
      "action": "CREATE",
      "status": "SUCCESS",
      "createdAt": "2026-04-24T08:00:00"
    },
    {
      "auditId": 211,
      "entityType": "COURSE",
      "entityId": 7,
      "action": "UPDATE",
      "status": "SUCCESS",
      "createdAt": "2026-04-25T10:15:00"
    }
  ],
  "message": "Entity audit history retrieved successfully",
  "timestamp": "2026-04-26T23:15:00Z"
}
```

**Success Response (200 OK - No Records):**
```json
{
  "success": true,
  "data": [],
  "message": "No audit history found for entity",
  "timestamp": "2026-04-26T23:15:00Z"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Invalid entity ID",
  "error": "Entity ID must be positive",
  "timestamp": "2026-04-26T23:15:00Z"
}
```

### Status Codes
| Code | Description                          |
| ---- | ------------------------------------ |
| 200  | Entity audit history retrieved       |
| 400  | Invalid path parameters              |
| 401  | Unauthorized (missing/invalid token) |
| 403  | Forbidden (not ADMIN role)           |
| 500  | Server error while fetching history  |

### cURL Example
```bash
curl -X GET http://localhost:8000/api/v1/audit-logs/entity/COURSE/7 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 3. Get Audit Logs by User

### Endpoint
```
GET /api/v1/audit-logs/user/{userId}
```

### Description
Retrieves audit logs created by a specific user.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** GET
- **URL:** `/api/v1/audit-logs/user/{userId}`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Path Parameters
| Parameter | Type | Required | Description |
| --------- | ---- | -------- | ----------- |
| userId    | Long | Yes      | User ID     |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "auditId": 300,
      "entityType": "TEACHER",
      "entityId": 4,
      "action": "UPDATE",
      "status": "SUCCESS",
      "createdAt": "2026-04-26T20:20:00"
    }
  ],
  "message": "User audit logs retrieved successfully",
  "timestamp": "2026-04-26T23:20:00Z"
}
```

**Success Response (200 OK - No Records):**
```json
{
  "success": true,
  "data": [],
  "message": "No audit activity found for user",
  "timestamp": "2026-04-26T23:20:00Z"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Invalid user ID",
  "error": "User ID must be positive",
  "timestamp": "2026-04-26T23:20:00Z"
}
```

### Status Codes
| Code | Description                           |
| ---- | ------------------------------------- |
| 200  | User audit logs retrieved             |
| 400  | Invalid user ID                       |
| 401  | Unauthorized (missing/invalid token)  |
| 403  | Forbidden (not ADMIN role)            |
| 500  | Server error while fetching user logs |

### cURL Example
```bash
curl -X GET http://localhost:8000/api/v1/audit-logs/user/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 4. Get Audit Statistics

### Endpoint
```
GET /api/v1/audit-logs/statistics
```

### Description
Retrieves overall audit statistics. If `startDate` and `endDate` are provided, returns statistics for that date range.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** GET
- **URL:** `/api/v1/audit-logs/statistics`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Query Parameters
| Parameter | Type          | Required | Description                  |
| --------- | ------------- | -------- | ---------------------------- |
| startDate | LocalDateTime | No       | Start datetime in ISO format |
| endDate   | LocalDateTime | No       | End datetime in ISO format   |

### Response

**Success Response (200 OK - Overall Stats):**
```json
{
  "success": true,
  "data": {
    "totalAuditLogs": 120,
    "successfulOperations": 115,
    "failedOperations": 5,
    "operationsByAction": {
      "CREATE": 30,
      "UPDATE": 50,
      "DELETE": 10,
      "LOGIN": 30
    },
    "operationsByEntity": {
      "TEACHER": 40,
      "COURSE": 35,
      "COLLEGE": 25,
      "USER": 20
    }
  },
  "message": "Audit statistics retrieved successfully",
  "timestamp": "2026-04-26T23:25:00Z"
}
```

**Success Response (200 OK - Date Range Stats):**
```json
{
  "success": true,
  "data": {
    "totalLogs": 35,
    "successCount": 33,
    "failedCount": 2,
    "dateRange": {
      "start": "2026-04-20T00:00:00",
      "end": "2026-04-26T23:59:59"
    }
  },
  "message": "Audit statistics retrieved successfully",
  "timestamp": "2026-04-26T23:25:00Z"
}
```

**Error Response (400 Bad Request - Invalid Date Range):**
```json
{
  "success": false,
  "message": "Invalid date range",
  "error": "Start date must be before end date",
  "timestamp": "2026-04-26T23:25:00Z"
}
```

### Status Codes
| Code | Description                            |
| ---- | -------------------------------------- |
| 200  | Statistics retrieved successfully      |
| 400  | Invalid date range                     |
| 401  | Unauthorized (missing/invalid token)   |
| 403  | Forbidden (not ADMIN role)             |
| 500  | Server error while fetching statistics |

### cURL Examples
```bash
# Overall statistics
curl -X GET http://localhost:8000/api/v1/audit-logs/statistics \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Date range statistics
curl -X GET "http://localhost:8000/api/v1/audit-logs/statistics?startDate=2026-04-20T00:00:00&endDate=2026-04-26T23:59:59" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## Authentication & Authorization

All audit endpoints are protected using role-based access control.

- **Required Header:**
```bash
Authorization: Bearer <JWT_TOKEN>
```

- **Required Role:** `ADMIN`
- **Non-ADMIN users:** Receive `403 Forbidden`
- **Missing/invalid token:** Receive `401 Unauthorized`

---

## Common Error Response Format

```json
{
  "success": false,
  "message": "Error summary",
  "error": "Detailed error message",
  "timestamp": "2026-04-26T23:30:00Z"
}
```

---

## Notes

- Date parameters must use ISO LocalDateTime format.
- Audit logs are sorted by `createdAt` in descending order (latest first).
- Empty results are returned as `200 OK` with an empty list for entity/user history endpoints.
- Statistics endpoint supports both full-system and date-range reporting.
