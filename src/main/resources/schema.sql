-- Schema for account
CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    role VARCHAR(50)
);

-- Schema for menu
CREATE TABLE IF NOT EXISTS menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_id VARCHAR(50) NOT NULL,
    screen_name VARCHAR(255) NOT NULL,
    button_name VARCHAR(255) NOT NULL,
    display_order INT
);
