# bank-api

REST API на Java 11 / Spring Boot 2.7.

## Запуск из IDE

1. Поднять Postgres и Redis:

```bash
docker compose up -d
```

2. Открыть проект как Maven, запустить `com.bank.BankApplication`.

3. Swagger: http://localhost:8080/swagger-ui.html

## Тестовые пользователи

Пароль у всех: `password123`

| id | email | phone |
|----|-------|-------|
| 1 | ivan@example.com | 79201111111 |
| 2 | petr@example.com | 79202222222 |
| 3 | anna@example.com | 79203333333 |

Логин: `POST /auth/login` с `email` **или** `phone` + `password`. Дальше заголовок `Authorization: Bearer <token>`.
