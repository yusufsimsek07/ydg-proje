-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(50) PRIMARY KEY
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);

-- Create facilities table
CREATE TABLE IF NOT EXISTS facilities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    contact_name VARCHAR(255),
    contact_phone VARCHAR(50)
);

-- Create checklist_templates table
CREATE TABLE IF NOT EXISTS checklist_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create checklist_items table
CREATE TABLE IF NOT EXISTS checklist_items (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    section VARCHAR(255) NOT NULL,
    question_text TEXT NOT NULL,
    critical BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (template_id) REFERENCES checklist_templates(id) ON DELETE CASCADE
);

-- Create audits table
CREATE TABLE IF NOT EXISTS audits (
    id BIGSERIAL PRIMARY KEY,
    facility_id BIGINT NOT NULL,
    audit_date DATE NOT NULL,
    created_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    FOREIGN KEY (facility_id) REFERENCES facilities(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create audit_responses table
CREATE TABLE IF NOT EXISTS audit_responses (
    id BIGSERIAL PRIMARY KEY,
    audit_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    result VARCHAR(10) NOT NULL DEFAULT 'NA',
    comment TEXT,
    FOREIGN KEY (audit_id) REFERENCES audits(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES checklist_items(id),
    UNIQUE(audit_id, item_id)
);

-- Create non_conformities table
CREATE TABLE IF NOT EXISTS non_conformities (
    id BIGSERIAL PRIMARY KEY,
    audit_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (audit_id) REFERENCES audits(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES checklist_items(id)
);

-- Create corrective_actions table
CREATE TABLE IF NOT EXISTS corrective_actions (
    id BIGSERIAL PRIMARY KEY,
    non_conformity_id BIGINT NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    due_date DATE NOT NULL,
    action_text TEXT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'OPEN',
    closed_at TIMESTAMP,
    FOREIGN KEY (non_conformity_id) REFERENCES non_conformities(id) ON DELETE CASCADE
);
