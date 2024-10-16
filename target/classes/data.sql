-- data.sql

-- Insert initial roles
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Insert users with hashed passwords (passwords are 'password123' hashed with BCrypt)
INSERT INTO users (username, password) VALUES
                                           ('admin', '$2a$12$E9h5S/9Y2/1vH6M8v9QOHe7VhR3Jk2C6fZ7hXdu1nE8xXWJ1FqU4S'), -- admin123
                                           ('johndoe', '$2a$12$E6sQ1hObF6POeKx/9NlZae4q0dXx0o7/SNClLLAM1OkzP64O.5E1y'), -- password123
                                           ('janedoe', '$2a$12$E6sQ1hObF6POeKx/9NlZae4q0dXx0o7/SNClLLAM1OkzP64O.5E1y'); -- password123

-- Assign roles to users
-- Assign ROLE_ADMIN to admin user
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE username = 'admin'),
           (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
       );

-- Assign ROLE_USER to johndoe and janedoe
INSERT INTO user_roles (user_id, role_id)
VALUES
    (
        (SELECT id FROM users WHERE username = 'johndoe'),
        (SELECT id FROM roles WHERE name = 'ROLE_USER')
    ),
    (
        (SELECT id FROM users WHERE username = 'janedoe'),
        (SELECT id FROM roles WHERE name = 'ROLE_USER')
    );

-- Insert borrowers
INSERT INTO borrowers (borrower_id, name, email, user_id) VALUES
                                                              ('b1a2c3d4-e5f6-7890-abcd-ef1234567890', 'John Doe', 'john.doe@example.com', (SELECT id FROM users WHERE username = 'johndoe')),
                                                              ('a1b2c3d4-e5f6-7890-abcd-ef0987654321', 'Jane Doe', 'jane.doe@example.com', (SELECT id FROM users WHERE username = 'janedoe'));

-- Insert books
INSERT INTO books (book_id, isbn, title, author, borrower_id) VALUES
                                                                  ('book1-uuid-1234-5678-abcdefabcdef', '978-0134685991', 'Effective Java', 'Joshua Bloch', NULL),
                                                                  ('book2-uuid-1234-5678-abcdefabcdef', '978-0596009205', 'Head First Design Patterns', 'Eric Freeman', NULL),
                                                                  ('book3-uuid-1234-5678-abcdefabcdef', '978-0201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma', 'b1a2c3d4-e5f6-7890-abcd-ef1234567890'), -- Borrowed by John Doe
                                                                  ('book4-uuid-1234-5678-abcdefabcdef', '978-0132350884', 'Clean Code', 'Robert C. Martin', 'a1b2c3d4-e5f6-7890-abcd-ef0987654321'); -- Borrowed by Jane Doe
