# Quick Start - Development Setup

## Fix Applied ✅

### 1. ModelMapper Configuration Fixed
- **Issue:** Conflict in nested college property mapping
- **Solution:** Added custom mapping configuration in `ModelMapperConfig.java`
- **Result:** Skips automatic nested mapping, allows manual mapping in service layer

### 2. Dev Profile Created
- **File:** `application-dev.properties`
- **Features:**
  - ✅ No authentication required
  - ✅ Debug logging enabled
  - ✅ Auto-schema creation (create-drop)
  - ✅ All endpoints accessible without login

---

## How to Run in Development

### Option 1: Maven Command Line
```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Option 2: IDE Run Configuration (IntelliJ)
1. Edit Run Configurations
2. Add VM Option: `-Dspring.profiles.active=dev`
3. Or set Environment variable: `SPRING_PROFILES_ACTIVE=dev`
4. Click Run

### Option 3: IDE Run Configuration (Eclipse)
1. Run Configurations → Arguments
2. Program arguments: `--spring.profiles.active=dev`
3. Click Run

---

## What Changes in Dev Profile

| Feature        | Production               | Development        |
| -------------- | ------------------------ | ------------------ |
| Authentication | HTTP Basic Auth required | ✅ No auth needed   |
| All endpoints  | Protected                | ✅ Open access      |
| Logging        | INFO level               | DEBUG level        |
| Database       | Persistent               | Auto drop-recreate |

---

## Test the API

### Without Auth (Dev Profile)
```bash
# No credentials needed
curl http://localhost:8080/api/v1/teachers

# Response: 200 OK with teachers list
```

### With Auth (Production Profile)
```bash
# Requires credentials
curl -u postgres:mohdanas1234 http://localhost:8080/api/v1/teachers

# Response: 200 OK with teachers list
```

---

## Database Auto-Setup

On every application start (dev profile):
1. Database schema DROPPED
2. Tables RECREATED
3. Sample data (data.sql) RELOADED
4. Application READY TO USE

No manual setup needed!

---

## Next Run

Simply run:
```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

✅ No login required  
✅ API accessible immediately  
✅ Debug logs visible  
✅ All endpoints open  

---

**Status: READY FOR DEVELOPMENT** 🚀
