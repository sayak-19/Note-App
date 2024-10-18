INSERT INTO roles (role_id, role_name)
VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

INSERT INTO users (
    user_id,
    username,
    email,
    password,
    account_non_locked,
    account_non_expired,
    credentials_non_expired,
    enabled,
    credentials_expiry_date,
    account_expiry_date,
    two_factor_secret,
    is_two_factor_enabled,
    sign_up_method,
    role_id,
    created_date,
    updated_date
)
VALUES (
    1,
    'john',
    'john@example.com',
    '$2a$10$FMcYVPF.aHtcfKNdVjr6OOB0VEhXLs2kHLT83Uh3Zqd2rlGPE4V4S',
    true,
    true,
    true,
    true,
    '2024-12-31',
    '2024-12-31',
    NULL,
    false,
    'SIGNUP_EMAIL',
    1,
    NOW(),
    NOW()
), (
    2,
    'admin',
    'admin@example.com',
    '$2a$10$jGjEWK1XF0FMdwbj.jw0lOQ2/VFpoJEL9Df70XUNEZHfuPYSD7U0C', -- Example hashed password
    true,
    true,
    true,
    true,
    '2024-12-31',
    '2024-12-31',
    NULL,
    false,
    'SIGNUP_GOOGLE',
    2,
    NOW(),
    NOW()
);