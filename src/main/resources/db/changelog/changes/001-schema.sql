--liquibase formatted sql

--changeset bank:001-schema
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(500) NOT NULL,
    date_of_birth   DATE NOT NULL,
    password        VARCHAR(500) NOT NULL
);

CREATE TABLE account (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL UNIQUE REFERENCES users (id),
    balance          NUMERIC(19, 2) NOT NULL,
    initial_balance  NUMERIC(19, 2) NOT NULL,
    CONSTRAINT account_balance_non_negative CHECK (balance >= 0)
);

CREATE TABLE email_data (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL REFERENCES users (id),
    email    VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE phone_data (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL REFERENCES users (id),
    phone    VARCHAR(13) NOT NULL UNIQUE
);
