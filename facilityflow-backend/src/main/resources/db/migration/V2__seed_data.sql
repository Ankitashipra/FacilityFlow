-- ============================================================================
-- FacilityFlow — Seed Data
-- All seeded users share the password: Password123
-- (BCrypt hash below was generated with strength 12, matching SecurityConfig)
-- ============================================================================

INSERT INTO users (full_name, email, password, phone_number, designation, department, role, enabled, account_locked, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
('Ananya Sharma', 'admin@facilityflow.com', '$2b$12$mJmsrvSolqORFfwK4HmhxO0m54eWhCmXpxfYnD8auiJK8FQ0u2Wiy', '9800000001', 'System Administrator', 'IT', 'ADMIN', TRUE, FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0),
('Rohan Mehta', 'manager@facilityflow.com', '$2b$12$mJmsrvSolqORFfwK4HmhxO0m54eWhCmXpxfYnD8auiJK8FQ0u2Wiy', '9800000002', 'Facility Manager', 'Operations', 'FACILITY_MANAGER', TRUE, FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0),
('Priya Nair', 'technician@facilityflow.com', '$2b$12$mJmsrvSolqORFfwK4HmhxO0m54eWhCmXpxfYnD8auiJK8FQ0u2Wiy', '9800000003', 'Maintenance Technician', 'Operations', 'EMPLOYEE', TRUE, FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0),
('Karan Verma', 'employee@facilityflow.com', '$2b$12$mJmsrvSolqORFfwK4HmhxO0m54eWhCmXpxfYnD8auiJK8FQ0u2Wiy', '9800000004', 'Software Engineer', 'Engineering', 'EMPLOYEE', TRUE, FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0),
('Sneha Iyer', 'sneha.iyer@facilityflow.com', '$2b$12$mJmsrvSolqORFfwK4HmhxO0m54eWhCmXpxfYnD8auiJK8FQ0u2Wiy', '9800000005', 'HR Executive', 'Human Resources', 'EMPLOYEE', TRUE, FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO buildings (name, code, address, city, total_floors, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
('Innovation Tower', 'BLD-A', 'Plot 14, Tech Park Road', 'Bhubaneswar', 8, NOW(), NOW(), 'system', 'system', FALSE, 0),
('Skyline Annex', 'BLD-B', 'Sector 5, Business District', 'Bhubaneswar', 5, NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO floors (building_id, floor_number, name, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
(1, 1, 'Ground Floor', NOW(), NOW(), 'system', 'system', FALSE, 0),
(1, 2, 'Second Floor', NOW(), NOW(), 'system', 'system', FALSE, 0),
(1, 3, 'Third Floor', NOW(), NOW(), 'system', 'system', FALSE, 0),
(2, 1, 'Ground Floor', NOW(), NOW(), 'system', 'system', FALSE, 0),
(2, 2, 'Second Floor', NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO rooms (floor_id, name, code, type, status, capacity, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
(1, 'Reception Lounge', 'A1-R01', 'UTILITY', 'AVAILABLE', 15, NOW(), NOW(), 'system', 'system', FALSE, 0),
(2, 'Falcon Meeting Room', 'A2-R01', 'MEETING_ROOM', 'AVAILABLE', 8, NOW(), NOW(), 'system', 'system', FALSE, 0),
(2, 'Engineering Bay', 'A2-R02', 'WORKSTATION_AREA', 'OCCUPIED', 40, NOW(), NOW(), 'system', 'system', FALSE, 0),
(3, 'Everest Conference Hall', 'A3-R01', 'CONFERENCE_HALL', 'AVAILABLE', 60, NOW(), NOW(), 'system', 'system', FALSE, 0),
(3, 'Server Room A3', 'A3-R02', 'SERVER_ROOM', 'AVAILABLE', 2, NOW(), NOW(), 'system', 'system', FALSE, 0),
(4, 'Titan Cabin', 'B1-R01', 'CABIN', 'AVAILABLE', 4, NOW(), NOW(), 'system', 'system', FALSE, 0),
(5, 'Zenith Meeting Room', 'B2-R01', 'MEETING_ROOM', 'AVAILABLE', 10, NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO assets (asset_tag, name, type, status, room_id, purchase_date, warranty_expiry_date, serial_number, vendor, purchase_cost, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
('AST-CMP-0001', 'Dell OptiPlex 7090', 'COMPUTER', 'ACTIVE', 3, '2023-06-15', '2026-06-15', 'DL7090-9981', 'Dell Technologies', 68000.00, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AST-CMP-0002', 'HP EliteDesk 800', 'COMPUTER', 'ACTIVE', 3, '2023-06-15', '2026-06-15', 'HP800-4471', 'HP Inc.', 62000.00, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AST-CHR-0001', 'Herman Miller Aeron', 'CHAIR', 'ACTIVE', 3, '2022-11-01', '2027-11-01', 'HM-AER-2201', 'Herman Miller', 45000.00, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AST-AC-0001', 'Daikin Split AC 2T', 'AC', 'ACTIVE', 4, '2021-03-20', '2026-03-20', 'DKN-2T-8871', 'Daikin', 55000.00, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AST-PRJ-0001', 'Epson EB-2250U Projector', 'PROJECTOR', 'ACTIVE', 4, '2022-08-10', '2025-08-10', 'EPS-2250-331', 'Epson', 78000.00, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AST-PRN-0001', 'Canon imageRUNNER 2630', 'PRINTER', 'IN_MAINTENANCE', 1, '2020-01-05', '2025-01-05', 'CNN-2630-119', 'Canon', 95000.00, NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO maintenance_tickets (title, description, asset_id, room_id, reported_by_id, assigned_to_id, priority, status, escalated, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
('Printer jamming repeatedly', 'The Canon imageRUNNER in the reception lounge jams on every duplex print job.', 6, 1, 4, 3, 'HIGH', 'IN_PROGRESS', FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0),
('AC not cooling in conference hall', 'Everest Conference Hall AC blows warm air; likely low refrigerant.', 4, 4, 5, NULL, 'MEDIUM', 'OPEN', FALSE, NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO ticket_comments (ticket_id, author_id, content, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
(1, 3, 'Inspected the fuser unit — ordering a replacement roller.', NOW(), NOW(), 'system', 'system', FALSE, 0);

INSERT INTO reservations (room_id, requested_by_id, purpose, start_time, end_time, status, attendee_count, created_at, updated_at, created_by, updated_by, is_deleted, version) VALUES
(2, 4, 'Sprint planning', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 1 HOUR, 'APPROVED', 6, NOW(), NOW(), 'system', 'system', FALSE, 0),
(7, 5, 'HR policy review', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 2 HOUR, 'PENDING', 4, NOW(), NOW(), 'system', 'system', FALSE, 0);
