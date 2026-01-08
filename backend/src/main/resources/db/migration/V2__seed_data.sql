-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('AUDITOR') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('MANAGER') ON CONFLICT (name) DO NOTHING;

-- Insert users (passwords are bcrypt hashed: Admin123!, Auditor123!, Manager123!)
INSERT INTO users (id, username, password_hash, full_name, enabled) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy7qK8O', 'Administrator', TRUE),
(2, 'auditor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy7qK8O', 'Auditor User', TRUE),
(3, 'manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy7qK8O', 'Manager User', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Insert user roles
INSERT INTO user_roles (user_id, role_name) VALUES
(1, 'ADMIN'),
(2, 'AUDITOR'),
(3, 'MANAGER')
ON CONFLICT (user_id, role_name) DO NOTHING;

-- Insert facility
INSERT INTO facilities (id, name, address, city, contact_name, contact_phone) VALUES
(1, 'Sample Food Processing Facility', '123 Main Street', 'Istanbul', 'John Doe', '+90-555-123-4567')
ON CONFLICT DO NOTHING;

-- Insert checklist template
INSERT INTO checklist_templates (id, name, version, active) VALUES
(1, 'HACCP Standard Checklist', '1.0', TRUE)
ON CONFLICT DO NOTHING;

-- Insert checklist items (12 items)
INSERT INTO checklist_items (id, template_id, section, question_text, critical) VALUES
(1, 1, 'Documentation', 'Are all HACCP documents up to date and accessible?', TRUE),
(2, 1, 'Documentation', 'Is the HACCP plan reviewed and approved by management?', FALSE),
(3, 1, 'Hygiene', 'Are all personnel trained in food safety and hygiene?', TRUE),
(4, 1, 'Hygiene', 'Are handwashing facilities properly maintained and accessible?', TRUE),
(5, 1, 'Hygiene', 'Are protective clothing and equipment used correctly?', FALSE),
(6, 1, 'Storage', 'Are storage temperatures monitored and recorded?', TRUE),
(7, 1, 'Storage', 'Are raw and cooked foods stored separately?', TRUE),
(8, 1, 'Storage', 'Is the storage area clean and organized?', FALSE),
(9, 1, 'Processing', 'Are critical control points monitored as per HACCP plan?', TRUE),
(10, 1, 'Processing', 'Are processing equipment cleaned and sanitized regularly?', TRUE),
(11, 1, 'Processing', 'Are batch records maintained for all products?', FALSE),
(12, 1, 'Quality Control', 'Is product testing conducted as per schedule?', TRUE)
ON CONFLICT DO NOTHING;
