--liquibase formatted sql

--changeset bank:002-seed
-- password for all users: password123
INSERT INTO users (id, name, date_of_birth, password) VALUES
    (1, 'Ivan Ivanov', DATE '1990-01-15', '$2b$10$K3.jTlysK5GK3ABEEkfsSO6RuRD2BTMZa1fOXClvpLTFO1zGTLpSu'),
    (2, 'Petr Petrov', DATE '1993-05-01', '$2b$10$K3.jTlysK5GK3ABEEkfsSO6RuRD2BTMZa1fOXClvpLTFO1zGTLpSu'),
    (3, 'Anna Sidorova', DATE '1985-12-20', '$2b$10$K3.jTlysK5GK3ABEEkfsSO6RuRD2BTMZa1fOXClvpLTFO1zGTLpSu');

SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));

INSERT INTO account (user_id, balance, initial_balance) VALUES
    (1, 100.00, 100.00),
    (2, 200.00, 200.00),
    (3, 50.00, 50.00);

INSERT INTO email_data (user_id, email) VALUES
    (1, 'ivan@example.com'),
    (2, 'petr@example.com'),
    (3, 'anna@example.com');

INSERT INTO phone_data (user_id, phone) VALUES
    (1, '79201111111'),
    (2, '79202222222'),
    (3, '79203333333');
