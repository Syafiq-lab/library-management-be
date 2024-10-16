-- schema.sql

-- Drop tables in the correct order to respect foreign key constraints
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS borrowers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Table: roles
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL
);

-- Table: user_roles (Join table for users and roles)
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                          FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Table: borrowers
CREATE TABLE IF NOT EXISTS borrowers (
                                         borrower_id VARCHAR(36) PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         email VARCHAR(255) NOT NULL UNIQUE,
                                         user_id BIGINT UNIQUE,
                                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Table: books
CREATE TABLE IF NOT EXISTS books (
                                     book_id VARCHAR(36) PRIMARY KEY,
                                     isbn VARCHAR(255) NOT NULL,
                                     title VARCHAR(255) NOT NULL,
                                     author VARCHAR(255) NOT NULL,
                                     borrower_id VARCHAR(36),
                                     FOREIGN KEY (borrower_id) REFERENCES borrowers(borrower_id) ON DELETE SET NULL
);
