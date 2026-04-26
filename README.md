# Wallet

<a href="https://github.com/RomanGulevatiy/wallet/actions/workflows/ci.yml">
  <img src="https://github.com/RomanGulevatiy/wallet/actions/workflows/tests.yml/badge.svg" alt="CI Tests"/>
</a>

## Summary
Wallet is a Java Spring Boot application for basic wallet and bank-related operations.  
It includes a REST API and a Telegram bot for user interaction. From the current codebase structure, it includes REST endpoints, service-layer business logic, persistence via Spring Data, DTO/entity mapping, centralized exception handling, and a Telegram long-polling bot built with TelegramBots Spring Starter v9.5.

---

## Features
- Create users with a wallet
- Add and withdraw money with validation
- View balance and user list
- Telegram bot for all wallet operations
- Global error handling

---

## Tech Stack
- Java 21+
- Spring Boot 3.5+
- Spring Data JPA
- TelegramBots Spring Starter 9.5.0
- Lombok
- Maven
- H2 Database

---

## REST API
Base URL: `http://localhost:8080/api`

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/user/create?name={name}` | Create a user |
| `GET` | `/user/{id}` | Get user by ID |
| `GET` | `/users` | Get all users |

### Wallet
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/user/{id}/deposit?amount={amount}` | Add money |
| `POST` | `/user/{id}/withdraw?amount={amount}` | Withdraw money |

---

## Telegram Bot
Find your bot in Telegram and use these commands:

| Command | Description | Example |
|---------|-------------|---------|
| `/start` | Welcome message | `/start` |
| `/help` | Show all commands | `/help` |
| `/users` | List all users | `/users` |
| `/profile <id>` | Show user profile | `/profile 1` |
| `/deposit <id> <amount>` | Add money | `/deposit 1 200.50` |
| `/withdraw <id> <amount>` | Withdraw money | `/withdraw 1 50.00` |

---

## Quick Start
### Run Locally
```bash
cd /home/user/IdeaProjects/wallet
./mvnw spring-boot:run
```

### Run Tests
```bash
cd /home/user/IdeaProjects/wallet
./mvnw test
```
