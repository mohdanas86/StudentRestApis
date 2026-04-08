# College API Documentation

## Overview
Complete REST API for managing colleges within the Student REST APIs system. All endpoints support full CRUD operations with comprehensive error handling and validation.

**Base URL:** `http://localhost:8080`  
**Authentication:** Not required  
**Response Format:** JSON with `ApiResponse` wrapper

---

## 1. Get All Colleges

### Endpoint
```
GET /colleges
```

### Description
Retrieves all colleges with their complete details including code, name, dean information, and timestamps.

### Request
- **Method:** GET
- **URL:** `/colleges`
- **Headers:** 
  - `Content-Type: application/json`

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "collegeId": 1,
      "collegeCode": "IITB",
      "collegeName": "Indian Institute of Technology Bombay",
      "deanName": "Dr. Subir Gokarn",
      "createdAt": "2026-04-07T10:30:00",
      "updatedAt": "2026-04-07T10:30:00"
    }
  ],
  "message": "Colleges fetched successfully",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**No Content Response (204 No Content):**
```
(Empty response body)
```
- Returned when no colleges are found in the system

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch colleges",
  "error": "An error occurred while fetching colleges",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

### Status Codes
| Code | Description                     |
| ---- | ------------------------------- |
| 200  | Successfully retrieved colleges |
| 204  | No colleges found               |
| 500  | Server error while fetching     |

### cURL Example
```bash
curl -X GET http://localhost:8080/colleges \
  -H "Content-Type: application/json"
```

---

## 2. Get College by ID

### Endpoint
```
GET /colleges/{collegeId}
```

### Description
Retrieves a specific college by their ID with complete details.

### Request
- **Method:** GET
- **URL:** `/colleges/{collegeId}`
- **Path Parameters:**
  - `collegeId` (Long): College ID to fetch

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "collegeId": 1,
    "collegeCode": "IITB",
    "collegeName": "Indian Institute of Technology Bombay",
    "deanName": "Dr. Subir Gokarn",
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T10:30:00"
  },
  "message": "College fetched successfully",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to fetch college",
  "error": "College not found with ID: 999",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch college",
  "error": "An error occurred while fetching college",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

### Status Codes
| Code | Description                    |
| ---- | ------------------------------ |
| 200  | College successfully retrieved |
| 404  | College not found              |
| 500  | Server error while fetching    |

### cURL Example
```bash
curl -X GET http://localhost:8080/colleges/1 \
  -H "Content-Type: application/json"
```

---

## 3. Create a New College

### Endpoint
```
POST /colleges
```

### Description
Creates a new college record in the system. College code must be unique across all colleges.

### Request
- **Method:** POST
- **URL:** `/colleges`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "collegeCode": "VIT",
  "collegeName": "Vellore Institute of Technology",
  "deanName": "Dr. Prashanth Reddy"
}
```

### Request Fields
| Field       | Type   | Required | Validation | Description          |
| ----------- | ------ | -------- | ---------- | -------------------- |
| collegeCode | String | Yes      | Min 1 char | Unique college code  |
| collegeName | String | Yes      | Min 1 char | Full college name    |
| deanName    | String | Yes      | Min 1 char | Name of college dean |

### Response

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "collegeId": 4,
    "collegeCode": "VIT",
    "collegeName": "Vellore Institute of Technology",
    "deanName": "Dr. Prashanth Reddy",
    "createdAt": "2026-04-08T14:25:30",
    "updatedAt": "2026-04-08T14:25:30"
  },
  "message": "College created successfully",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (409 Conflict - Duplicate College Code):**
```json
{
  "success": false,
  "message": "Failed to create college",
  "error": "College with code 'IITB' already exists",
  "code": "DUPLICATE_RESOURCE",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "collegeCode": "College code is required",
    "collegeName": "College name is required"
  },
  "code": "VALIDATION_ERROR",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to create college",
  "error": "An error occurred while creating college",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

### Status Codes
| Code | Description                                |
| ---- | ------------------------------------------ |
| 201  | College successfully created               |
| 400  | Validation error or duplicate college code |
| 409  | Duplicate resource (college code exists)   |
| 500  | Server error while creating college        |

### cURL Example
```bash
curl -X POST http://localhost:8080/colleges \
  -H "Content-Type: application/json" \
  -d '{
    "collegeCode": "VIT",
    "collegeName": "Vellore Institute of Technology",
    "deanName": "Dr. Prashanth Reddy"
  }'
```

---

## 4. Update College

### Endpoint
```
PUT /colleges/{collegeId}
```

### Description
Updates an existing college's information. Only provided fields will be updated (partial update). Timestamps are automatically managed by the system.

### Request
- **Method:** PUT
- **URL:** `/colleges/{collegeId}`
- **Path Parameters:**
  - `collegeId` (Long): College ID to update
- **Headers:** 
  - `Content-Type: application/json`

### Request Body (All fields optional)
```json
{
  "collegeCode": "IITB",
  "collegeName": "IIT Bombay (Updated)",
  "deanName": "Dr. New Dean"
}
```

### Request Fields
| Field       | Type   | Required | Validation | Description          |
| ----------- | ------ | -------- | ---------- | -------------------- |
| collegeCode | String | No       | Min 1 char | Updated college code |
| collegeName | String | No       | Min 1 char | Updated college name |
| deanName    | String | No       | Min 1 char | Updated dean name    |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "collegeId": 1,
    "collegeCode": "IITB",
    "collegeName": "IIT Bombay (Updated)",
    "deanName": "Dr. New Dean",
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-08T14:35:00"
  },
  "message": "College updated successfully",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to update college",
  "error": "College not found with ID: 999",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (409 Conflict - Duplicate College Code):**
```json
{
  "success": false,
  "message": "Failed to update college",
  "error": "College with code 'BITS' already exists",
  "code": "DUPLICATE_RESOURCE",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "collegeCode": "College code is required"
  },
  "code": "VALIDATION_ERROR",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to update college",
  "error": "An error occurred while updating college",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

### Status Codes
| Code | Description                              |
| ---- | ---------------------------------------- |
| 200  | College successfully updated             |
| 404  | College not found                        |
| 409  | Duplicate resource (college code exists) |
| 400  | Validation error                         |
| 500  | Server error while updating college      |

### cURL Example
```bash
curl -X PUT http://localhost:8080/colleges/1 \
  -H "Content-Type: application/json" \
  -d '{
    "collegeName": "IIT Bombay (Updated)",
    "deanName": "Dr. New Dean"
  }'
```

---

## 5. Delete College

### Endpoint
```
DELETE /colleges/{collegeId}
```

### Description
Deletes a college record from the system. Due to cascade constraints, deleting a college will also delete all associated courses. Teachers associated with the college will have their college_id set to null.

### Request
- **Method:** DELETE
- **URL:** `/colleges/{collegeId}`
- **Path Parameters:**
  - `collegeId` (Long): College ID to delete

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": null,
  "message": "College deleted successfully",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to delete college",
  "error": "College not found with ID: 999",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to delete college",
  "error": "An error occurred while deleting college",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

### Status Codes
| Code | Description                  |
| ---- | ---------------------------- |
| 200  | College successfully deleted |
| 404  | College not found            |
| 500  | Server error while deleting  |

### Important Notes
- **Cascade Delete:** All courses associated with this college will be deleted
- **Teachers:** Teachers will become orphaned (college_id = null) - they won't be deleted
- **Permanent:** This is a hard delete - the college cannot be recovered

### cURL Example
```bash
curl -X DELETE http://localhost:8080/colleges/1 \
  -H "Content-Type: application/json"
```

---

## General Notes

### Response Structure
All endpoints follow a consistent response wrapper format:
```json
{
  "success": boolean,
  "data": object|array|null,
  "message": string,
  "error": string (only in failure responses),
  "code": string (only in failure responses),
  "errors": object (only in validation failure responses),
  "timestamp": ISO8601 timestamp
}
```

### Data Integrity
- Each college is uniquely identified by `collegeId`
- `collegeCode` is unique and serves as a short identifier
- Timestamps (`createdAt`, `updatedAt`) are automatically managed by the system
- Colleges can have multiple courses and teachers associated with them

### Relationship Management
- **One-to-Many with Courses:** Each college can offer multiple courses
  - Cascade: DELETE - Deleting a college will delete all associated courses
- **One-to-Many with Teachers:** Each college can have multiple teachers
  - Cascade: PERSIST - Teachers are created with college but protected from deletion

---

## Error Handling

### Common Error Scenarios

| Error                  | Code | Cause                              | Solution                        |
| ---------------------- | ---- | ---------------------------------- | ------------------------------- |
| Duplicate College Code | 409  | College code already exists        | Use unique college code         |
| College Not Found      | 404  | collegeId doesn't exist            | Verify college ID exists        |
| Validation Failed      | 400  | Missing required fields or invalid | Check all required fields       |
| Database Error         | 500  | Connection or query error          | Check database connectivity     |
| Constraint Violation   | 409  | Unique constraint violated         | Verify unique fields are unique |

### Error Code Reference
| Code               | Meaning                         |
| ------------------ | ------------------------------- |
| DUPLICATE_RESOURCE | Resource already exists (409)   |
| RESOURCE_NOT_FOUND | Resource not found (404)        |
| VALIDATION_ERROR   | Request validation failed (400) |
| INTERNAL_ERROR     | Server error (500)              |

---

## Testing Workflow

### Test Sequence
```
1. GET All Colleges - Verify initial data
2. Create College - Test with valid data
3. Get College by ID - Verify creation
4. Update College - Modify college details
5. Verify Update - Confirm changes
6. Test Duplicate Prevention - Try creating with same code
7. Delete College - Remove the college
8. Verify Deletion - Confirm college is removed
```

### Sample Test Data
```json
{
  "collegeCode": "NSA",
  "collegeName": "New Sample Academy",
  "deanName": "Dr. Test Dean"
}
```

### Testing Duplicate Prevention
```bash
# First create - should succeed
curl -X POST http://localhost:8080/colleges \
  -H "Content-Type: application/json" \
  -d '{"collegeCode": "NSA", "collegeName": "New Sample Academy", "deanName": "Dr. Test Dean"}'

# Second create with same code - should fail with 409 CONFLICT
curl -X POST http://localhost:8080/colleges \
  -H "Content-Type: application/json" \
  -d '{"collegeCode": "NSA", "collegeName": "Another Academy", "deanName": "Dr. Another"}'
```

---

## Security & Error Handling Best Practices

### Security
- Error messages are generic and do not expose database details or SQL queries
- All database errors are caught and converted to appropriate HTTP status codes
- Sensitive information is not exposed in API responses
- Input validation is enforced on all endpoints

### Error Message Examples
- **Before Fix:** `ERROR: duplicate key value violates unique constraint "colleges_college_code_key"` (SQL exposed ❌)
- **After Fix:** `College with code 'IITB' already exists` (Generic and safe ✅)

### Benefits of Proper Error Handling
1. **Security:** No database details leaked
2. **User Experience:** Clear, actionable error messages
3. **Debugging:** Server logs contain full error details
4. **API Consistency:** All endpoints follow same error format
