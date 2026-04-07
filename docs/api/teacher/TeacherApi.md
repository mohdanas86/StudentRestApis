# Teacher API Documentation

## Overview
Complete REST API for managing teachers within the Student REST APIs system. All endpoints support full CRUD operations with comprehensive error handling and validation.

**Base URL:** `http://localhost:8080`  
**Authentication:** Not required  
**Response Format:** JSON with `ApiResponse` wrapper

---

## 1. Get All Teachers

### Endpoint
```
GET /teachers
```

### Description
Retrieves all active teachers with their complete details including college and course assignments.

### Request
- **Method:** GET
- **URL:** `/teachers`
- **Headers:** 
  - `Content-Type: application/json`

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "teacherId": 1,
      "employeeId": "EMP001",
      "firstName": "Anas",
      "lastName": "Alam",
      "email": "anas@example.com",
      "specialization": "Java Development",
      "collegeId": 1,
      "collegeName": "IITB",
      "courseNames": ["CS101", "CS102"],
      "isActive": true,
      "createdAt": "2026-04-07T10:30:00",
      "updatedAt": "2026-04-07T10:30:00"
    },
    {
      "teacherId": 2,
      "employeeId": "EMP002",
      "firstName": "Saritha",
      "lastName": "Sharma",
      "email": "saritha@example.com",
      "specialization": "Python Development",
      "collegeId": 2,
      "collegeName": "BITS",
      "courseNames": ["CS103"],
      "isActive": true,
      "createdAt": "2026-04-07T10:30:00",
      "updatedAt": "2026-04-07T10:30:00"
    }
  ],
  "message": "Teachers fetched successfully"
}
```

**No Content Response (204 No Content):**
```
(Empty response body)
```
- Returned when no active teachers are found

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch teachers",
  "error": "Database connection error"
}
```

### Status Codes
| Code | Description                                |
| ---- | ------------------------------------------ |
| 200  | Successfully retrieved all active teachers |
| 204  | No active teachers found                   |
| 500  | Server error while fetching teachers       |

### cURL Example
```bash
curl -X GET http://localhost:8080/teachers \
  -H "Content-Type: application/json"
```

---

## 2. Create a New Teacher

### Endpoint
```
POST /teachers
```

### Description
Creates a new teacher record. Teacher must be assigned to an existing college. Newly created teachers are active by default.

### Request
- **Method:** POST
- **URL:** `/teachers`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "employeeId": "EMP006",
  "firstName": "Vikram",
  "lastName": "Patel",
  "email": "vikram@example.com",
  "specialization": "Web Development",
  "collegeId": 1
}
```

### Request Fields
| Field          | Type   | Required | Validation   | Description                 |
| -------------- | ------ | -------- | ------------ | --------------------------- |
| employeeId     | String | Yes      | Min 1 char   | Unique employee identifier  |
| firstName      | String | Yes      | Min 1 char   | Teacher's first name        |
| lastName       | String | Yes      | Min 1 char   | Teacher's last name         |
| email          | String | Yes      | Valid email  | Must be valid email format  |
| specialization | String | Yes      | Min 1 char   | Area of expertise           |
| collegeId      | Long   | Yes      | Exists in DB | ID of existing college (FK) |

### Response

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "teacherId": 6,
    "employeeId": "EMP006",
    "firstName": "Vikram",
    "lastName": "Patel",
    "email": "vikram@example.com",
    "specialization": "Web Development",
    "collegeId": 1,
    "collegeName": "IITB",
    "courseNames": [],
    "isActive": true,
    "createdAt": "2026-04-07T14:25:30",
    "updatedAt": "2026-04-07T14:25:30"
  },
  "message": "Teacher created successfully"
}
```

**Error Response (400 Bad Request - College Not Found):**
```json
{
  "success": false,
  "message": "Failed to create teacher",
  "error": "College not found with ID: 999"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Failed to create teacher",
  "error": "Email is invalid"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to create teacher",
  "error": "Database error occurred"
}
```

### Status Codes
| Code | Description                           |
| ---- | ------------------------------------- |
| 201  | Teacher successfully created          |
| 400  | Validation error or college not found |
| 500  | Server error while creating teacher   |

### cURL Example
```bash
curl -X POST http://localhost:8080/teachers \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP006",
    "firstName": "Vikram",
    "lastName": "Patel",
    "email": "vikram@example.com",
    "specialization": "Web Development",
    "collegeId": 1
  }'
```

---

## 3. Get Teacher by ID

### Endpoint
```
GET /teachers/{id}
```

### Description
Retrieves a specific teacher by their ID with complete details including college name and assigned courses.

### Request
- **Method:** GET
- **URL:** `/teachers/{id}`
- **Path Parameters:**
  - `id` (Long): TeacherId to fetch

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "teacherId": 1,
    "employeeId": "EMP001",
    "firstName": "Anas",
    "lastName": "Alam",
    "email": "anas@example.com",
    "specialization": "Java Development",
    "collegeId": 1,
    "collegeName": "IITB",
    "courseNames": ["CS101", "CS102"],
    "isActive": true,
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T10:30:00"
  },
  "message": "Teacher fetched successfully"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to fetch teacher",
  "error": "Teacher not found with ID: 999"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch teacher",
  "error": "Database error occurred"
}
```

### Status Codes
| Code | Description                         |
| ---- | ----------------------------------- |
| 200  | Teacher successfully retrieved      |
| 404  | Teacher not found                   |
| 500  | Server error while fetching teacher |

### cURL Example
```bash
curl -X GET http://localhost:8080/teachers/1 \
  -H "Content-Type: application/json"
```

---

## 4. Update Teacher

### Endpoint
```
PUT /teachers/{id}
```

### Description
Updates an existing teacher's information. Only provided fields will be updated (partial update). If isActive is not provided, current state is retained.

### Request
- **Method:** PUT
- **URL:** `/teachers/{id}`
- **Path Parameters:**
  - `id` (Long): TeacherId to update
- **Headers:** 
  - `Content-Type: application/json`

### Request Body (All fields optional)
```json
{
  "employeeId": "EMP001_UPDATED",
  "firstName": "Anas",
  "lastName": "Khan",
  "email": "anas.khan@example.com",
  "specialization": "Advanced Java",
  "isActive": true
}
```

### Request Fields
| Field          | Type    | Required | Validation  | Description                        |
| -------------- | ------- | -------- | ----------- | ---------------------------------- |
| employeeId     | String  | No       | Min 1 char  | Updated employee identifier        |
| firstName      | String  | No       | Min 1 char  | Updated first name                 |
| lastName       | String  | No       | Min 1 char  | Updated last name                  |
| email          | String  | No       | Valid email | Updated email address              |
| specialization | String  | No       | Min 1 char  | Updated specialization             |
| isActive       | Boolean | No       | true/false  | Set teacher active/inactive status |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "teacherId": 1,
    "employeeId": "EMP001_UPDATED",
    "firstName": "Anas",
    "lastName": "Khan",
    "email": "anas.khan@example.com",
    "specialization": "Advanced Java",
    "collegeId": 1,
    "collegeName": "IITB",
    "courseNames": ["CS101", "CS102"],
    "isActive": true,
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T14:35:00"
  },
  "message": "Teacher updated successfully"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to update teacher",
  "error": "Teacher not found with ID: 999"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Failed to update teacher",
  "error": "Email is invalid"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to update teacher",
  "error": "Database error occurred"
}
```

### Status Codes
| Code | Description                         |
| ---- | ----------------------------------- |
| 200  | Teacher successfully updated        |
| 404  | Teacher not found                   |
| 400  | Validation error                    |
| 500  | Server error while updating teacher |

### cURL Example
```bash
curl -X PUT http://localhost:8080/teachers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anas",
    "lastName": "Khan",
    "email": "anas.khan@example.com",
    "specialization": "Advanced Java"
  }'
```

---

## 5. Delete Teacher

### Endpoint
```
DELETE /teachers/{id}
```

### Description
Soft-deletes a teacher by setting their `isActive` status to `false`. The teacher record remains in the database but is no longer considered "active" and won't appear in GET all teachers endpoint.

### Request
- **Method:** DELETE
- **URL:** `/teachers/{id}`
- **Path Parameters:**
  - `id` (Long): TeacherId to delete

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": "",
  "message": "Teacher deleted successfully"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Failed to delete teacher",
  "error": "Teacher not found with ID: 999"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to delete teacher",
  "error": "Database error occurred"
}
```

### Status Codes
| Code | Description                                |
| ---- | ------------------------------------------ |
| 200  | Teacher successfully deleted (soft delete) |
| 404  | Teacher not found                          |
| 500  | Server error while deleting teacher        |

### Notes
- This is a **soft delete** - teacher record is retained but marked as inactive
- Deleted teachers will not appear in GET /teachers endpoint (only fetched with GET /teachers/{id})
- To restore a teacher, use PUT /teachers/{id} with `isActive: true`

### cURL Example
```bash
curl -X DELETE http://localhost:8080/teachers/1 \
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
  "error": string (only in failure responses)
}
```

### Data Integrity
- Teachers are linked to colleges via Foreign Key relationship
- College must exist before creating a teacher
- Teacher records are soft-deleted (marked inactive) rather than hard-deleted
- Timestamps (`createdAt`, `updatedAt`) are automatically managed

### Filtering
- GET /teachers returns **only active teachers** (`isActive = true`)
- GET /teachers/{id} returns **all teachers** regardless of active status
- Deleted teachers can only be retrieved by ID

### Course Assignments
- `courseNames` is populated based on Many-to-Many relationships
- New teachers have empty `courseNames` array by default
- Course assignments are managed through separate course endpoints

---

## Error Handling

### Common Error Scenarios

| Error             | Code | Cause                                     | Solution                    |
| ----------------- | ---- | ----------------------------------------- | --------------------------- |
| Invalid Email     | 400  | Email format validation fails             | Provide valid email format  |
| College Not Found | 400  | collegeId doesn't exist in database       | Verify college ID exists    |
| Teacher Not Found | 404  | teacherId doesn't exist                   | Verify teacher ID exists    |
| Validation Failed | 400  | Missing required fields or invalid values | Check all required fields   |
| Database Error    | 500  | Connection or query error                 | Check database connectivity |

---

## Testing

### Test Workflow
```
1. Create College (if not exists)
2. Create Teacher (POST /teachers)
3. Get Teacher by ID (GET /teachers/{id})
4. Update Teacher (PUT /teachers/{id})
5. Get All Teachers (GET /teachers)
6. Delete Teacher (DELETE /teachers/{id})
7. Verify deletion (GET /teachers - should not appear)
```

### Sample Test Data
```json
{
  "employeeId": "TEST_EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "specialization": "Testing",
  "collegeId": 1
}
```