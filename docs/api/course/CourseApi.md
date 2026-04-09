# Course API Documentation

## Overview
Complete REST API for managing courses within the Student REST APIs system. All endpoints support full CRUD operations with comprehensive error handling and validation.

**Base URL:** `http://localhost:8080`  
**Authentication:** Not required  
**Response Format:** JSON with `ApiResponse` wrapper

---

## 1. Get All Courses

### Endpoint
```
GET /courses
```

### Description
Retrieves all courses with their complete details including course code, name, credits, college information, and timestamps.

### Request
- **Method:** GET
- **URL:** `/courses`
- **Headers:** 
  - `Content-Type: application/json`

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "courseId": 1,
      "courseCode": "CS101",
      "courseName": "Introduction to Data Science",
      "credits": 4.0,
      "createdAt": "2026-04-07T10:30:00",
      "updatedAt": "2026-04-07T10:30:00"
    }
  ],
  "message": "Courses fetched successfully",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**No Content Response (204 No Content):**
```
(Empty response body)
```
- Returned when no courses are found in the system

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch courses",
  "error": "An error occurred while fetching courses",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

### Status Codes
| Code | Description                    |
| ---- | ------------------------------ |
| 200  | Successfully retrieved courses |
| 204  | No courses found               |
| 500  | Server error while fetching    |

### cURL Example
```bash
curl -X GET http://localhost:8080/courses \
  -H "Content-Type: application/json"
```

---

## 2. Get Course by ID

### Endpoint
```
GET /courses/{courseId}
```

### Description
Retrieves a specific course by its ID with complete details including credit information and timestamps.

### Request
- **Method:** GET
- **URL:** `/courses/{courseId}`
- **Path Parameters:**
  - `courseId` (Long): Course ID to fetch

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Data Science",
    "credits": 4.0,
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T10:30:00"
  },
  "message": "Course fetched successfully",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course not found with id: 999",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to fetch course",
  "error": "An error occurred while fetching course",
  "timestamp": "2026-04-08T10:30:00Z"
}
```

### Status Codes
| Code | Description                   |
| ---- | ----------------------------- |
| 200  | Course successfully retrieved |
| 404  | Course not found              |
| 500  | Server error while fetching   |

### cURL Example
```bash
curl -X GET http://localhost:8080/courses/1 \
  -H "Content-Type: application/json"
```

---

## 3. Create a New Course

### Endpoint
```
POST /courses
```

### Description
Creates a new course record in the system. The course must be assigned to an existing college. Course code must be unique across all courses.

### Request
- **Method:** POST
- **URL:** `/courses`
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "courseCode": "CS109",
  "courseName": "Advanced Java Programming",
  "credits": 4.0,
  "collegeId": 1
}
```

### Request Fields
| Field      | Type   | Required | Validation   | Description                 |
| ---------- | ------ | -------- | ------------ | --------------------------- |
| courseCode | String | Yes      | Min 1 char   | Unique course code          |
| courseName | String | Yes      | Min 1 char   | Full course name            |
| credits    | Double | Yes      | Min 0.0      | Course credit hours         |
| collegeId  | Long   | Yes      | Exists in DB | ID of existing college (FK) |

### Response

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "courseId": 9,
    "courseCode": "CS109",
    "courseName": "Advanced Java Programming",
    "credits": 4.0,
    "createdAt": "2026-04-08T14:25:30",
    "updatedAt": "2026-04-08T14:25:30"
  },
  "message": "Course created successfully",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (400 Bad Request - College Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "College not found with id: 999",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (400 Bad Request - Duplicate Course Code):**
```json
{
  "success": false,
  "message": "Duplicate Resource",
  "error": "Course code already exists: CS101",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Validation Error",
  "error": "Course code is required",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to create course",
  "error": "An error occurred while creating course",
  "timestamp": "2026-04-08T14:25:30Z"
}
```

### Status Codes
| Code | Description                                            |
| ---- | ------------------------------------------------------ |
| 201  | Course successfully created                            |
| 400  | Validation error, duplicate code, or college not found |
| 404  | College not found                                      |
| 500  | Server error while creating course                     |

### cURL Example
```bash
curl -X POST http://localhost:8080/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS109",
    "courseName": "Advanced Java Programming",
    "credits": 4.0,
    "collegeId": 1
  }'
```

---

## 4. Update a Course

### Endpoint
```
PUT /courses/{courseId}
```

### Description
Updates an existing course. All fields are optional, only provided fields will be updated. If college is updated, the course will be reassigned to the new college.

### Request
- **Method:** PUT
- **URL:** `/courses/{courseId}`
- **Path Parameters:**
  - `courseId` (Long): Course ID to update
- **Headers:** 
  - `Content-Type: application/json`

### Request Body
```json
{
  "courseCode": "CS109",
  "courseName": "Advanced Java Programming with OOP",
  "credits": 4.5,
  "collegeId": 2
}
```

### Request Fields (All Optional)
| Field      | Type   | Required | Validation   | Description          |
| ---------- | ------ | -------- | ------------ | -------------------- |
| courseCode | String | No       | Min 3 chars  | Updated course code  |
| courseName | String | No       | Min 3 chars  | Updated course name  |
| credits    | Double | No       | Min 0.0      | Updated credit hours |
| collegeId  | Long   | No       | Exists in DB | New college ID (FK)  |

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "courseCode": "CS109",
    "courseName": "Advanced Java Programming with OOP",
    "credits": 4.5,
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-08T15:30:00"
  },
  "message": "Course updated successfully",
  "timestamp": "2026-04-08T15:30:00Z"
}
```

**Error Response (404 Not Found - Course Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course not found with id: 999",
  "timestamp": "2026-04-08T15:30:00Z"
}
```

**Error Response (404 Not Found - College Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "College not found with id: 999",
  "timestamp": "2026-04-08T15:30:00Z"
}
```

**Error Response (400 Bad Request - Validation Failed):**
```json
{
  "success": false,
  "message": "Validation Error",
  "error": "Course code must have at least 3 characters",
  "timestamp": "2026-04-08T15:30:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to update course",
  "error": "An error occurred while updating course",
  "timestamp": "2026-04-08T15:30:00Z"
}
```

### Status Codes
| Code | Description                        |
| ---- | ---------------------------------- |
| 200  | Course successfully updated        |
| 400  | Validation error                   |
| 404  | Course or College not found        |
| 500  | Server error while updating course |

### cURL Example
```bash
curl -X PUT http://localhost:8080/courses/1 \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Advanced Java Programming with OOP",
    "credits": 4.5
  }'
```

---

## 5. Delete a Course

### Endpoint
```
DELETE /courses/{courseId}
```

### Description
Deletes a specific course by its ID. Once deleted, the course cannot be recovered.

### Request
- **Method:** DELETE
- **URL:** `/courses/{courseId}`
- **Path Parameters:**
  - `courseId` (Long): Course ID to delete

### Response

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": null,
  "message": "Course deleted successfully",
  "timestamp": "2026-04-08T16:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Not Found",
  "error": "Course not found with id: 999",
  "timestamp": "2026-04-08T16:00:00Z"
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Failed to delete course",
  "error": "An error occurred while deleting course",
  "timestamp": "2026-04-08T16:00:00Z"
}
```

### Status Codes
| Code | Description                 |
| ---- | --------------------------- |
| 200  | Course successfully deleted |
| 404  | Course not found            |
| 500  | Server error while deleting |

### cURL Example
```bash
curl -X DELETE http://localhost:8080/courses/1 \
  -H "Content-Type: application/json"
```

---

## Common Response Fields

All API responses include the following wrapper fields:

| Field     | Type    | Description                                            |
| --------- | ------- | ------------------------------------------------------ |
| success   | Boolean | Indicates if the request was successful (true/false)   |
| data      | Object  | The actual response data (varies based on endpoint)    |
| message   | String  | Success or error message                               |
| error     | String  | Detailed error description (only in error responses)   |
| timestamp | String  | ISO 8601 formatted timestamp of when response was sent |

---

## Error Handling

### Common Error Scenarios

**Validation Error (400):**
- Missing required fields
- Invalid field values (e.g., negative credits)
- String length validation failures

**Not Found Error (404):**
- Requested course doesn't exist
- Referenced college doesn't exist
- Course has been deleted

**Duplicate Error (400):**
- Duplicate course code attempted on create

**Server Error (500):**
- Database connection issues
- Unexpected system errors
- Transaction failures

---

## Data Models

### CourseEntityDto (Response)
```json
{
  "courseId": 1,
  "courseCode": "CS101",
  "courseName": "Introduction to Data Science",
  "credits": 4.0,
  "createdAt": "2026-04-07T10:30:00",
  "updatedAt": "2026-04-07T10:30:00"
}
```

### CreateCourseRequestDto (Request)
```json
{
  "courseCode": "CS109",
  "courseName": "Advanced Java Programming",
  "credits": 4.0,
  "collegeId": 1
}
```

### UpdateCourseRequestDto (Request)
```json
{
  "courseCode": "CS109",
  "courseName": "Advanced Java Programming",
  "credits": 4.5,
  "collegeId": 1
}
```

---

## Notes & Best Practices

1. **College Association:** Every course must belong to exactly one college. Ensure the `collegeId` exists before creating or updating a course.

2. **Unique Course Code:** Course codes must be unique across the system. Attempting to create a course with a duplicate code will result in a 400 error.

3. **Credits Validation:** Credits must be a positive number (>= 0.0). Fractional credits are allowed (e.g., 3.5).

4. **Partial Updates:** When updating a course, you only need to provide the fields you want to change. Omitted fields will retain their current values.

5. **Timestamps:** `createdAt` is set when the course is created and never changes. `updatedAt` is updated every time the course is modified.

6. **Response Codes:** Always check the HTTP status code. A successful operation may return 200 or 201 depending on the endpoint.

7. **Error Details:** Check the `error` field in error responses for specific details about what went wrong.

---

## Sample Integration Workflow

```javascript
// Example: Create a course, then update it
const collegeId = 1;

// Step 1: Create a course
const createResponse = await fetch('http://localhost:8080/courses', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    courseCode: 'CS109',
    courseName: 'Advanced Java',
    credits: 4.0,
    collegeId: collegeId
  })
});

const course = await createResponse.json();

// Step 2: Update the course
const updateResponse = await fetch(`http://localhost:8080/courses/${course.data.courseId}`, {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    courseName: 'Advanced Java with Microservices',
    credits: 4.5
  })
});

const updatedCourse = await updateResponse.json();

// Step 3: Delete the course
await fetch(`http://localhost:8080/courses/${course.data.courseId}`, {
  method: 'DELETE'
});
```

