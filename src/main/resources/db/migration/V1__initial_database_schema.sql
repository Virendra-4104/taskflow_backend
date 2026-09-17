-- user table --
CREATE TABLE users(
    id BIGINT AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    gender ENUM('FEMALE','MALE','OTHER') NOT NULL,
    profile_img_url varchar(255),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
);

-- project table --
CREATE TABLE projects(
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    created_by_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- task table --
CREATE TABLE tasks(
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    created_by_id BIGINT NOT NULL,
    status ENUM('TODO','IN_PROGRESS','DONE') NOT NULL,
    priority ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    project_id BIGINT DEFAULT NULL,
    assigned_to_id BIGINT DEFAULT NULL,
    due_date DATE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_task_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_task_created_by_user_id FOREIGN KEY (created_by_id) REFERENCES users(id),
    CONSTRAINT fk_task_assigned_to_user_id FOREIGN KEY (assigned_to_id) REFERENCES users(id)
);

-- project members table --
CREATE TABLE project_members(
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT  uk_project_member UNIQUE (project_id,user_id),
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

-- members role table --
CREATE TABLE project_roles(
    member_id BIGINT NOT NULL,
    roles ENUM('PROJECT_OWNER','PROJECT_ADMIN','PROJECT_MEMBER') NOT NULL,
    PRIMARY KEY (member_id,roles),
    CONSTRAINT fk_members_role FOREIGN KEY (member_id) REFERENCES project_members(id)
);

-- history table --
CREATE TABLE histories(
    id BIGINT AUTO_INCREMENT,
    entity_type ENUM('COMMENT','PROJECT','TASK','TEAM','USER') NOT NULL,
    entity_id BIGINT NOT NULL,
    action ENUM('CREATED','UPDATED','REMOVED','DELETED','TITLE_UPDATED','DESCRIPTION_UPDATED','STATUS_UPDATED','PRIORITY_UPDATED','DUE_DATE_UPDATED','MEMBER_JOINED','MEMBER_REMOVED') NOT NULL,
    old_value TEXT,
    new_value TEXT,
    created_by_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    Index idx_history_entity (entity_type,entity_id,created_at),
    CONSTRAINT fk_history_created_by FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- comment table --
CREATE TABLE comments(
    id BIGINT AUTO_INCREMENT,
    content TEXT NOT NULL,
    task_id BIGINT NOT NULL,
    created_by_id BIGINT NOT NULL,
    created_at datetime(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comment_on_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_created_by_id FOREIGN KEY (created_by_id) REFERENCES users(id)
);
