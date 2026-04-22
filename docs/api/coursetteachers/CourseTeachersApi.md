# Course-Teacher Assignments API Documentation

## Overview
Complete REST API for managing many-to-many relationships between courses and teachers within the Student REST APIs system. This API enables assigning teachers to courses, removing assignments, querying course rosters, and checking assignment status.

**Base URL:** `http://localhost:8080`  
**Authentication:** Required for POST/DELETE operations (ADMIN role)  
**Response Format:** JSON with `ApiResponse` wrapper

---

## 1. Assign Teacher to Course

### Endpoint
```
POST /courses/{courseId}/teachers/{teacherId}
```

### Description
Assigns an existing teacher to an existing course. Creates a new assignment record linking the teacher and course. Prevents duplicate assignments and validates that both course and teacher exist and are active.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** POST
- **URL:** `/courses/{courseId}/teachers/{teacherId}`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Path Parameters
| Parameter | Type | Required | Description       |
| --------- | ---- | -------- | ----------------- |
| courseId  | Long | Yes      | ID of the course  |
| teacherId | Long | Yes      | ID of the teacher |

### Response

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "assignmentId": 5,
    "courseId": 1,
    "courseName": "Introduction to Data Science",
    "teacherId": 1,
    "teacherName": "Anas Alam",
    "teacherEmail": "anas@example.com",
    "assignedAt": "2026-04-08T14:30:00",
    "assignedBy": "admin@example.com"
  },
  "message": "Teacher successfully assigned to course",
  "timestamp": "2026-04-08T14:30:00Z"
}
```

**Error Response (400 Bad Request - Validation Error):**
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "Cannot assign inactive teacher to this course",
  "timestamp": "2026-04-08T14:30:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course with ID 999 not found",
  "timestamp": "2026-04-08T14:30:00Z"
}
```

**Error Response (409 Conflict - Duplicate):**
```json
{
  "success": false,
  "message": "Conflict",
  "error": "Teacher is already assigned to this course",
  "timestamp": "2026-04-08T14:30:00Z"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Unauthorized",
  "error": "Access denied. ADMIN role required",
  "timestamp": "2026-04-08T14:30:00Z"
}
```

### Status Codes
| Code | Description                                |
| ---- | ------------------------------------------ |
| 201  | Teacher successfully assigned to course    |
| 400  | Validation failed (inactive teacher, etc.) |
| 404  | Course or teacher not found                |
| 409  | Teacher already assigned to this course    |
| 401  | Unauthorized (not ADMIN)                   |
| 500  | Server error while assigning               |

### Validations
- ✓ Course must exist in database
- ✓ Teacher must exist in database
- ✓ Teacher must be active (isActive = true)
- ✓ Teacher must not already be assigned to course (no duplicates)

### cURL Example
```bash
curl -X POST http://localhost:8080/courses/1/teachers/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 2. Remove Teacher from Course

### Endpoint
```
DELETE /courses/{courseId}/teachers/{teacherId}
```

### Description
Removes an existing assignment of a teacher from a course. Deletes the assignment record and validates that the assignment exists before deletion.

### Authorization
- **Required Role:** ADMIN
- **Public Access:** No

### Request
- **Method:** DELETE
- **URL:** `/courses/{courseId}/teachers/{teacherId}`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>` (ADMIN token required)

### Path Parameters
| Parameter | Type | Required | Description       |
| --------- | ---- | -------- | ----------------- |
| courseId  | Long | Yes      | ID of the course  |
| teacherId | Long | Yes      | ID of the teacher |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": null,
  "message": "Teacher successfully removed from course",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "This teacher is not assigned to this course",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Unauthorized",
  "error": "Access denied. ADMIN role required",
  "timestamp": "2026-04-08T14:35:00Z"
}
```

### Status Codes
| Code | Description                              |
| ---- | ---------------------------------------- |
| 200  | Teacher successfully removed from course |
| 404  | Assignment not found                     |
| 401  | Unauthorized (not ADMIN)                 |
| 500  | Server error while removing              |

### cURL Example
```bash
curl -X DELETE http://localhost:8080/courses/1/teachers/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 3. Get All Teachers for Course

### Endpoint
```
GET /courses/{courseId}/teachers
```

### Description
Retrieves all teachers assigned to a specific course. Returns complete teacher information for each assignment including name, email, specialization, and college details. Returns 204 No Content if no teachers are assigned.

### Authorization
- **Required Role:** None (Public)
- **Public Access:** Yes

### Request
- **Method:** GET
- **URL:** `/courses/{courseId}/teachers`
- **Headers:** 
  - `Content-Type: application/json`

### Path Parameters
| Parameter | Type | Required | Description      |
| --------- | ---- | -------- | ---------------- |
| courseId  | Long | Yes      | ID of the course |

### Response

**Success Response (200 OK - With Teachers):**
```json
{
  "success": true,
  "data": [
    {
      "teacherId": 1,
      "firstName": "Anas",
      "lastName": "Alam",
      "email": "anas@example.com",
      "specialization": "Java Development",
      "employeeId": "EMP001",
      "collegeId": 1,
      "collegeName": "IITB"
    },
    {
      "teacherId": 2,
      "firstName": "Saritha",
      "lastName": "Sharma",
      "email": "saritha@example.com",
      "specialization": "Python Development",
      "employeeId": "EMP002",
      "collegeId": 1,
      "collegeName": "IITB"
    }
  ],
  "message": "Teachers retrieved successfully",
  "timestamp": "2026-04-08T14:40:00Z"
}
```

**No Content Response (204 No Content):**
```
(Empty response body)
```
- Returned when no teachers are assigned to the course

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course with ID 999 not found",
  "timestamp": "2026-04-08T14:40:00Z"
}
```

### Status Codes
| Code | Description                     |
| ---- | ------------------------------- |
| 200  | Teachers successfully retrieved |
| 204  | No teachers assigned to course  |
| 404  | Course not found                |
| 500  | Server error while fetching     |

### cURL Example
```bash
curl -X GET http://localhost:8080/courses/1/teachers \
  -H "Content-Type: application/json"
```

---

## 4. Check Teacher Assignment

### Endpoint
```
GET /courses/{courseId}/teachers/{teacherId}
```

### Description
Checks if a specific teacher is assigned to a specific course. Returns assignment status with detailed information if assigned, or just status if not assigned. Uses conditional JSON serialization to exclude null fields.

### Authorization
- **Required Role:** None (Public)
- **Public Access:** Yes

### Request
- **Method:** GET
- **URL:** `/courses/{courseId}/teachers/{teacherId}`
- **Headers:** 
  - `Content-Type: application/json`

### Path Parameters
| Parameter | Type | Required | Description       |
| --------- | ---- | -------- | ----------------- |
| courseId  | Long | Yes      | ID of the course  |
| teacherId | Long | Yes      | ID of the teacher |

### Response

**Success Response (200 OK - Teacher Is Assigned):**
```json
{
  "success": true,
  "data": {
    "isAssigned": true,
    "courseId": 1,
    "teacherId": 1,
    "assignedAt": "2026-04-08T14:30:00",
    "assignedBy": "admin@example.com"
  },
  "message": "Assignment check completed",
  "timestamp": "2026-04-08T14:45:00Z"
}
```

**Success Response (200 OK - Teacher Is NOT Assigned):**
```json
{
  "success": true,
  "data": {
    "isAssigned": false,
    "courseId": 1,
    "teacherId": 5
  },
  "message": "Assignment check completed",
  "timestamp": "2026-04-08T14:45:00Z"
}
```
Note: When not assigned, `assignedAt` and `assignedBy` fields are excluded from response (JSON serialization with NON_NULL include policy).

### Status Codes
| Code | Description                 |
| ---- | --------------------------- |
| 200  | Assignment status retrieved |
| 500  | Server error while checking |

### cURL Example
```bash
curl -X GET http://localhost:8080/courses/1/teachers/1 \
  -H "Content-Type: application/json"
```

---

## 5. Get Course Roster

### Endpoint
```
GET /courses/{courseId}/roster
```

### Description
Retrieves complete course information with all assigned teachers. Generates a comprehensive roster report showing course details, total number of teachers, and all teacher assignments with their information.

### Authorization
- **Required Role:** None (Public)
- **Public Access:** Yes

### Request
- **Method:** GET
- **URL:** `/courses/{courseId}/roster`
- **Headers:** 
  - `Content-Type: application/json`

### Path Parameters
| Parameter | Type | Required | Description      |
| --------- | ---- | -------- | ---------------- |
| courseId  | Long | Yes      | ID of the course |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "courseName": "Introduction to Data Science",
    "courseCode": "CS101",
    "description": "Fundamentals of data analysis",
    "collegeId": 1,
    "collegeName": "IITB",
    "credits": 4.0,
    "teachers": [
      {
        "teacherId": 1,
        "firstName": "Anas",
        "lastName": "Alam",
        "email": "anas@example.com",
        "specialization": "Java Development",
        "employeeId": "EMP001",
        "collegeId": 1,
        "collegeName": "IITB"
      },
      {
        "teacherId": 2,
        "firstName": "Saritha",
        "lastName": "Sharma",
        "email": "saritha@example.com",
        "specialization": "Python Development",
        "employeeId": "EMP002",
        "collegeId": 1,
        "collegeName": "IITB"
      }
    ],
    "totalTeachers": 2,
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-08T14:30:00"
  },
  "message": "Roster retrieved successfully",
  "timestamp": "2026-04-08T14:50:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course with ID 999 not found",
  "timestamp": "2026-04-08T14:50:00Z"
}
```

### Status Codes
| Code | Description                   |
| ---- | ----------------------------- |
| 200  | Roster successfully retrieved |
| 404  | Course not found              |
| 500  | Server error while fetching   |

### Response Fields
| Field         | Type                 | Description                           |
| ------------- | -------------------- | ------------------------------------- |
| courseId      | Long                 | Unique identifier of the course       |
| courseName    | String               | Full name of the course               |
| courseCode    | String               | Unique course code                    |
| description   | String               | Course description                    |
| collegeId     | Long                 | ID of the college offering the course |
| collegeName   | String               | Name of the college                   |
| credits       | Double               | Number of credit hours                |
| teachers      | Array<TeacherObject> | List of assigned teachers             |
| totalTeachers | Integer              | Count of assigned teachers            |
| createdAt     | Timestamp            | Course creation timestamp             |
| updatedAt     | Timestamp            | Course last update timestamp          |

### cURL Example
```bash
curl -X GET http://localhost:8080/courses/1/roster \
  -H "Content-Type: application/json"
```

---

## 6. Get Courses for Teacher

### Endpoint
```
GET /teachers/{teacherId}/courses
```

### Description
Retrieves all courses assigned to a specific teacher. Returns complete course information with assignment timestamps. Returns 204 No Content if the teacher has no assigned courses.

### Authorization
- **Required Role:** None (Public)
- **Public Access:** Yes

### Request
- **Method:** GET
- **URL:** `/teachers/{teacherId}/courses`
- **Headers:** 
  - `Content-Type: application/json`

### Path Parameters
| Parameter | Type | Required | Description       |
| --------- | ---- | -------- | ----------------- |
| teacherId | Long | Yes      | ID of the teacher |

### Response

**Success Response (200 OK - With Courses):**
```json
{
  "success": true,
  "data": [
    {
      "courseId": 1,
      "courseName": "Introduction to Data Science",
      "courseCode": "CS101",
      "description": "Fundamentals of data analysis",
      "collegeId": 1,
      "collegeName": "IITB",
      "credits": 4.0,
      "assignedAt": "2026-04-08T14:30:00"
    },
    {
      "courseId": 3,
      "courseName": "Web Development Fundamentals",
      "courseCode": "CS103",
      "description": "Learn web technologies",
      "collegeId": 1,
      "collegeName": "IITB",
      "credits": 3.5,
      "assignedAt": "2026-04-08T15:00:00"
    }
  ],
  "message": "Courses retrieved successfully",
  "timestamp": "2026-04-08T15:05:00Z"
}
```

**No Content Response (204 No Content):**
```
(Empty response body)
```
- Returned when teacher has no assigned courses

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Teacher with ID 999 not found",
  "timestamp": "2026-04-08T15:05:00Z"
}
```

### Status Codes
| Code | Description                    |
| ---- | ------------------------------ |
| 200  | Courses successfully retrieved |
| 204  | No courses assigned to teacher |
| 404  | Teacher not found              |
| 500  | Server error while fetching    |

### Response Fields
| Field       | Type      | Description                           |
| ----------- | --------- | ------------------------------------- |
| courseId    | Long      | Unique identifier of the course       |
| courseName  | String    | Full name of the course               |
| courseCode  | String    | Unique course code                    |
| description | String    | Course description                    |
| collegeId   | Long      | ID of the college offering the course |
| collegeName | String    | Name of the college                   |
| credits     | Double    | Number of credit hours                |
| assignedAt  | Timestamp | When the teacher was assigned         |

### cURL Example
```bash
curl -X GET http://localhost:8080/teachers/1/courses \
  -H "Content-Type: application/json"
```

---

## Error Handling

All endpoints return errors in a standardized format with appropriate HTTP status codes:

### Common Error Responses

**400 Bad Request - Validation Error:**
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "Detailed validation error message",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**401 Unauthorized:**
```json
{
  "success": false,
  "message": "Unauthorized",
  "error": "Access denied. ADMIN role required",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**404 Not Found:**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Resource with ID not found",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**409 Conflict:**
```json
{
  "success": false,
  "message": "Conflict",
  "error": "Resource already exists or operation conflicts",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

**500 Internal Server Error:**
```json
{
  "success": false,
  "message": "Failed to process request",
  "error": "An unexpected error occurred",
  "timestamp": "2026-04-08T15:00:00Z"
}
```

---

## Authentication & Authorization

### Bearer Token
All protected endpoints require a valid JWT Bearer token in the Authorization header:
```
Authorization: Bearer <JWT_TOKEN>
```

### Role-Based Access Control (RBAC)
- **ADMIN Role:** Can assign/remove teachers from courses (POST/DELETE operations)
- **TEACHER Role:** Can view course and teacher information (GET operations)
- **STUDENT Role:** Can view course and teacher information (GET operations)
- **Public Access:** All GET endpoints are accessible without authentication

### Example with Bearer Token
```bash
curl -X POST http://localhost:8080/courses/1/teachers/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Business Rules

1. **Duplicate Prevention:** A teacher cannot be assigned to the same course twice
2. **Active Teachers Only:** Only active teachers (isActive=true) can be assigned to courses
3. **Referential Integrity:** Both course and teacher must exist before creating an assignment
4. **Cascade Delete:** If a course or teacher is deleted, associated assignments are automatically deleted
5. **Audit Trail:** Each assignment records who assigned the teacher (assignedBy field)
6. **Timestamps:** All assignments include assignment timestamp (assignedAt)

---

## Response Schema

### CourseTeacherAssignmentDto (Assignment Response)
```json
{
  "assignmentId": "Long",
  "courseId": "Long",
  "courseName": "String",
  "teacherId": "Long",
  "teacherName": "String",
  "teacherEmail": "String",
  "assignedAt": "Timestamp",
  "assignedBy": "String"
}
```

### TeacherAssignmentDto (Teacher in Assignment Context)
```json
{
  "teacherId": "Long",
  "firstName": "String",
  "lastName": "String",
  "email": "String",
  "specialization": "String",
  "employeeId": "String",
  "collegeId": "Long",
  "collegeName": "String"
}
```

### CoursesForTeacherDto (Course in Assignment Context)
```json
{
  "courseId": "Long",
  "courseName": "String",
  "courseCode": "String",
  "description": "String",
  "collegeId": "Long",
  "collegeName": "String",
  "credits": "Double",
  "assignedAt": "Timestamp"
}
```

### AssignmentCheckDto (Assignment Status Response)
```json
{
  "isAssigned": "Boolean",
  "courseId": "Long",
  "teacherId": "Long",
  "assignedAt": "Timestamp (optional, null if not assigned)",
  "assignedBy": "String (optional, null if not assigned)"
}
```

### CourseRosterDto (Complete Course with Teachers)
```json
{
  "courseId": "Long",
  "courseName": "String",
  "courseCode": "String",
  "description": "String",
  "collegeId": "Long",
  "collegeName": "String",
  "credits": "Double",
  "teachers": "Array<TeacherAssignmentDto>",
  "totalTeachers": "Integer",
  "createdAt": "Timestamp",
  "updatedAt": "Timestamp"
}
```

---

## Summary Table

| Operation               | Method | Endpoint                      | Auth   | Returns                    | Status  |
| ----------------------- | ------ | ----------------------------- | ------ | -------------------------- | ------- |
| Assign Teacher          | POST   | `/courses/{id}/teachers/{id}` | ADMIN  | CourseTeacherAssignmentDto | 201     |
| Remove Teacher          | DELETE | `/courses/{id}/teachers/{id}` | ADMIN  | Void                       | 200     |
| Get Teachers            | GET    | `/courses/{id}/teachers`      | Public | List<TeacherAssignmentDto> | 200/204 |
| Check Assignment        | GET    | `/courses/{id}/teachers/{id}` | Public | AssignmentCheckDto         | 200     |
| Get Roster              | GET    | `/courses/{id}/roster`        | Public | CourseRosterDto            | 200     |
| Get Courses for Teacher | GET    | `/teachers/{id}/courses`      | Public | List<CoursesForTeacherDto> | 200/204 |
