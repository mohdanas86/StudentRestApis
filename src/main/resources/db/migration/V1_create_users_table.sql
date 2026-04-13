-- ============================================================
-- Create USERS table for authentication & authorization
-- ============================================================

CREATE TABLE IF NOT EXISTS Users(
   user_id BIGSERIAL PRIMARY KEY,

    -- Authentication Credentials
                                    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,  -- BCrypt: always 60 characters

   -- Authorization
    role VARCHAR(50) NOT NULL DEFAULT 'STUDENT' CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Relationship to Teacher (optional - user can be admin without being teacher)
    teacher_id BIGINT UNIQUE REFERENCES teachers(teacher_id) ON DELETE SET NULL,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for fast lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_teacher_id ON users(teacher_id);

-- ============================================================
-- Add audit trigger for updated_at
-- ============================================================
CREATE OR REPLACE FUNCTION update_users_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_updated_at_trigger
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_users_updated_at();

-- ============================================================
-- Comments for documentation
-- ============================================================
COMMENT ON TABLE users IS 'Authentication and authorization data. Every authenticated user has a record here.';
COMMENT ON COLUMN users.user_id IS 'Primary key - unique user identifier';
COMMENT ON COLUMN users.email IS 'Unique email - used for password recovery, notifications';
COMMENT ON COLUMN users.username IS 'Unique username - required for login';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password - NEVER store plain text';
COMMENT ON COLUMN users.role IS 'Authorization role: ADMIN (full access), TEACHER (manage courses), STUDENT (view only)';
COMMENT ON COLUMN users.teacher_id IS 'Foreign key to teachers table - null if user is not a teacher';