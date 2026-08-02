-- ============================================================================
-- FacilityFlow — Initial Schema
-- ============================================================================

CREATE TABLE users (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name         VARCHAR(100)  NOT NULL,
    email             VARCHAR(150)  NOT NULL,
    password          VARCHAR(255)  NOT NULL,
    phone_number      VARCHAR(20),
    designation       VARCHAR(100),
    department        VARCHAR(100),
    role              VARCHAR(30)   NOT NULL,
    enabled           BOOLEAN       NOT NULL DEFAULT TRUE,
    account_locked    BOOLEAN       NOT NULL DEFAULT FALSE,
    profile_image_url VARCHAR(500),
    created_at        DATETIME      NOT NULL,
    updated_at        DATETIME      NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    is_deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    version           BIGINT,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_role ON users (role);

CREATE TABLE refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(512) NOT NULL,
    user_id     BIGINT       NOT NULL,
    expiry_date DATETIME     NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL,
    CONSTRAINT uq_refresh_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_refresh_token_value ON refresh_tokens (token);

CREATE TABLE buildings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    code          VARCHAR(20)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    city          VARCHAR(100),
    total_floors  INT,
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    version       BIGINT,
    CONSTRAINT uq_building_code UNIQUE (code)
) ENGINE=InnoDB;

CREATE INDEX idx_building_code ON buildings (code);

CREATE TABLE floors (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    building_id   BIGINT NOT NULL,
    floor_number  INT    NOT NULL,
    name          VARCHAR(100),
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    version       BIGINT,
    CONSTRAINT fk_floor_building FOREIGN KEY (building_id) REFERENCES buildings (id) ON DELETE CASCADE,
    CONSTRAINT uq_floor_building_number UNIQUE (building_id, floor_number)
) ENGINE=InnoDB;

CREATE INDEX idx_floor_building ON floors (building_id);

CREATE TABLE rooms (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    floor_id    BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(30)  NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    status      VARCHAR(30)  NOT NULL DEFAULT 'AVAILABLE',
    capacity    INT          NOT NULL,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    version     BIGINT,
    CONSTRAINT fk_room_floor FOREIGN KEY (floor_id) REFERENCES floors (id) ON DELETE CASCADE,
    CONSTRAINT uq_room_code UNIQUE (code)
) ENGINE=InnoDB;

CREATE INDEX idx_room_floor ON rooms (floor_id);
CREATE INDEX idx_room_status ON rooms (status);

CREATE TABLE assets (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag             VARCHAR(40)  NOT NULL,
    name                  VARCHAR(150) NOT NULL,
    type                  VARCHAR(30)  NOT NULL,
    status                VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    room_id               BIGINT,
    purchase_date         DATE NOT NULL,
    warranty_expiry_date  DATE,
    qr_code_url           VARCHAR(500),
    serial_number         VARCHAR(100),
    vendor                VARCHAR(100),
    purchase_cost         DOUBLE,
    created_at            DATETIME NOT NULL,
    updated_at            DATETIME NOT NULL,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    is_deleted            BOOLEAN NOT NULL DEFAULT FALSE,
    version               BIGINT,
    CONSTRAINT fk_asset_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE SET NULL,
    CONSTRAINT uq_asset_tag UNIQUE (asset_tag)
) ENGINE=InnoDB;

CREATE INDEX idx_asset_room ON assets (room_id);
CREATE INDEX idx_asset_status ON assets (status);

CREATE TABLE maintenance_tickets (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200)  NOT NULL,
    description      VARCHAR(2000) NOT NULL,
    asset_id         BIGINT,
    room_id          BIGINT,
    reported_by_id   BIGINT NOT NULL,
    assigned_to_id   BIGINT,
    priority         VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status           VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    completion_date  DATETIME,
    escalated        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    is_deleted       BOOLEAN NOT NULL DEFAULT FALSE,
    version          BIGINT,
    CONSTRAINT fk_ticket_asset FOREIGN KEY (asset_id) REFERENCES assets (id) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_reporter FOREIGN KEY (reported_by_id) REFERENCES users (id),
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assigned_to_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_ticket_status ON maintenance_tickets (status);
CREATE INDEX idx_ticket_priority ON maintenance_tickets (priority);
CREATE INDEX idx_ticket_asset ON maintenance_tickets (asset_id);
CREATE INDEX idx_ticket_assignee ON maintenance_tickets (assigned_to_id);

CREATE TABLE ticket_comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id   BIGINT NOT NULL,
    author_id   BIGINT NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    version     BIGINT,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES maintenance_tickets (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE INDEX idx_comment_ticket ON ticket_comments (ticket_id);

CREATE TABLE reservations (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id           BIGINT NOT NULL,
    requested_by_id   BIGINT NOT NULL,
    approved_by_id    BIGINT,
    purpose           VARCHAR(200) NOT NULL,
    start_time        DATETIME NOT NULL,
    end_time          DATETIME NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attendee_count    INT,
    rejection_reason  VARCHAR(500),
    created_at        DATETIME NOT NULL,
    updated_at        DATETIME NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    is_deleted        BOOLEAN NOT NULL DEFAULT FALSE,
    version           BIGINT,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_requester FOREIGN KEY (requested_by_id) REFERENCES users (id),
    CONSTRAINT fk_reservation_approver FOREIGN KEY (approved_by_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_reservation_room ON reservations (room_id);
CREATE INDEX idx_reservation_status ON reservations (status);
CREATE INDEX idx_reservation_time_range ON reservations (room_id, start_time, end_time);

CREATE TABLE notifications (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id   BIGINT NOT NULL,
    type           VARCHAR(40) NOT NULL,
    title          VARCHAR(200) NOT NULL,
    message        VARCHAR(1000) NOT NULL,
    is_read        BOOLEAN NOT NULL DEFAULT FALSE,
    reference_url  VARCHAR(255),
    created_at     DATETIME NOT NULL,
    updated_at     DATETIME NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    version        BIGINT,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_notification_recipient ON notifications (recipient_id);
CREATE INDEX idx_notification_read ON notifications (is_read);

CREATE TABLE audit_logs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT,
    action       VARCHAR(30)  NOT NULL,
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    BIGINT,
    details      VARCHAR(2000),
    ip_address   VARCHAR(45),
    timestamp    DATETIME NOT NULL,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_audit_user ON audit_logs (user_id);
CREATE INDEX idx_audit_action ON audit_logs (action);
CREATE INDEX idx_audit_entity ON audit_logs (entity_name, entity_id);
CREATE INDEX idx_audit_timestamp ON audit_logs (timestamp);
