# bank-api

REST API на Java 11 / Spring Boot 2.7.

Репозиторий: https://github.com/srgbr/test-task

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

## Тесты

Нужен Docker (Testcontainers поднимает `postgres:13.7-alpine`).
Для Docker 29+ в тестах зафиксирован Docker API `1.44` (иначе клиент 1.32 отклоняется).

```bash
mvn test
```

- `TransferServiceTest` — unit на перевод денег  
- `TransferApiTest` — MockMvc + Testcontainers (login + transfer)
