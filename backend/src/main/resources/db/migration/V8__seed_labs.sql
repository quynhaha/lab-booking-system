-- Seed sample labs data

INSERT INTO labs (lab_name, capacity, location, description, status) VALUES
('Computer Lab 1', 30, 'Building A, Room 101', 'Main computer laboratory with Windows workstations', 'ACTIVE'),
('Computer Lab 2', 25, 'Building A, Room 102', 'Computer laboratory with Linux workstations', 'ACTIVE'),
('Network Lab', 20, 'Building B, Room 201', 'Specialized network and security laboratory', 'ACTIVE'),
('Software Development Lab', 35, 'Building C, Room 301', 'Modern development environment with latest tools', 'ACTIVE'),
('Multimedia Lab', 15, 'Building D, Room 401', 'Audio/video production and editing laboratory', 'ACTIVE'),
('Research Lab', 10, 'Building E, Room 501', 'Advanced research and development laboratory', 'ACTIVE'),
('Mobile Development Lab', 20, 'Building F, Room 601', 'Mobile app development with various devices', 'ACTIVE'),
('AI/ML Lab', 12, 'Building G, Room 701', 'Artificial Intelligence and Machine Learning laboratory', 'ACTIVE');

-- Add LECTURER role if it doesn't exist
INSERT INTO roles(role_name)
SELECT 'LECTURER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'LECTURER');
