# College API Documentation

## Overview
REST API for managing colleges within the Student REST APIs system. Provides endpoints to retrieve college information with comprehensive error handling and validation.

**Base URL:** `http://localhost:8080/api/v1`  
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
  "message": "Colleges fetched successfully"
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
  "error": "Database connection error"
}
```

### Status Codes
| Code | Description                     |
| ---- | ------------------------------- |
| 200  | Successfully retrieved colleges |
| 204  | No colleges found               |
| 500  | Server error while fetching     |

### Response Fields
| Field       | Type     | Description                        |
| ----------- | -------- | ---------------------------------- |
| collegeId   | Long     | Unique identifier for the college  |
| collegeCode | String   | Unique code for the college        |
| collegeName | String   | Full name of the college           |
| deanName    | String   | Name of the college dean           |
| createdAt   | DateTime | Timestamp when college was created |
| updatedAt   | DateTime | Timestamp of last update           |

### cURL Example
```bash
curl -X GET http://localhost:8080/colleges \
  -H "Content-Type: application/json"
```

### JavaScript/Fetch Example
```javascript
fetch('http://localhost:8080/colleges', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    console.log('Colleges:', data.data);
  } else {
    console.error('Error:', data.error);
  }
})
.catch(error => console.error('Request failed:', error));
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
  "timestamp": ISO8601 timestamp
}
```

### Data Integrity
- Each college is uniquely identified by `collegeId`
- `collegeCode` is unique and serves as a short identifier
- Timestamps (`createdAt`, `updatedAt`) are automatically managed by the system
- Colleges can have multiple courses and teachers associated with them

### Relationships
- **One-to-Many with Courses:** Each college can offer multiple courses
  - Cascade: DELETE - Deleting a college will delete all associated courses
- **One-to-Many with Teachers:** Each college can have multiple teachers
  - Cascade: PERSIST - Teachers are created with college but protected from deletion

---

## Error Handling

### Common Error Scenarios

| Error             | Code | Cause                        | Solution                    |
| ----------------- | ---- | ---------------------------- | --------------------------- |
| Database Error    | 500  | Connection or query error    | Check database connectivity |
| No Colleges Found | 204  | No colleges in the system    | Add colleges first          |
| Server Error      | 500  | Unexpected application error | Check server logs           |

---

## Testing

### Test Workflow
```
1. Ensure Colleges Exist (check with GET /colleges)
2. Verify College Data Structure
3. Verify All Required Fields Present
4. Test Error Scenarios (no data, server error)
```

### Sample Response Data
```json
{
  "collegeId": 1,
  "collegeCode": "IITB",
  "collegeName": "Indian Institute of Technology Bombay",
  "deanName": "Dr. Subir Gokarn",
  "createdAt": "2026-04-07T10:30:00",
  "updatedAt": "2026-04-07T10:30:00"
}
```

---

## Future Endpoints (Planned)

The following College API endpoints are planned for future implementation:
- `POST /colleges` - Create a new college
- `GET /colleges/{id}` - Get college by ID  
- `PUT /colleges/{id}` - Update college information
- `DELETE /colleges/{id}` - Delete a college
