CREATE TABLE user_roles(
    user_id BIGINT NOT NULL,
    role ENUM('ADMIN','USER') NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_account_role FOREIGN KEY (user_id) REFERENCES users(id)
);