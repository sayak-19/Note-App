CREATE TABLE note (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TEXT,
    owner_username VARCHAR(255),
    created_at DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB;


CREATE TABLE roles (
    role_id INTEGER NOT NULL AUTO_INCREMENT,
    role_name ENUM ('ROLE_ADMIN', 'ROLE_USER'),
    PRIMARY KEY (role_id)
) ENGINE=InnoDB;

CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    account_expiry_date DATE,
    account_non_expired BIT,
    account_non_locked BIT,
    created_date DATETIME(6),
    credentials_expiry_date DATE,
    credentials_non_expired BIT,
    email VARCHAR(50) NOT NULL,
    enabled BIT,
    is_two_factor_enabled BIT,
    password VARCHAR(120),
    sign_up_method VARCHAR(255),
    two_factor_secret VARCHAR(255),
    updated_date DATETIME(6),
    username VARCHAR(20) NOT NULL,
    role_id INTEGER,
    PRIMARY KEY (user_id),
    UNIQUE (username),             -- Unique constraint on username
    UNIQUE (email),                -- Unique constraint on email
    FOREIGN KEY (role_id) REFERENCES roles (role_id)  -- Foreign key constraint
) ENGINE=InnoDB;

ALTER TABLE users
    ADD CONSTRAINT FKp56c1712k691lhsyewcssf40f
    FOREIGN KEY (role_id)
    REFERENCES roles (role_id);

CREATE TABLE audit_log (
     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     action VARCHAR(20) NOT NULL,
     username VARCHAR(20) NOT NULL,
     note_id BIGINT,
     time DATETIME
);

CREATE TABLE password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);