# Wallet

## Summary
Wallet is a Java Spring Boot application for basic wallet and bank-related operations.  
From the current codebase structure, it includes REST endpoints, service-layer business logic, persistence via Spring Data, DTO/entity mapping, and centralized exception handling.

---

## What Is Included
- REST controller layer in `src/main/java/com/duikt/wallet/controller`
- Service layer in `src/main/java/com/duikt/wallet/service`
- Persistence layer in `src/main/java/com/duikt/wallet/repository`
- Domain entities and DTOs in `src/main/java/com/duikt/wallet/entity` and `src/main/java/com/duikt/wallet/dto`
- Global exception handling in `src/main/java/com/duikt/wallet/exception`
- Unit/integration tests under `src/test/java/com/duikt/wallet`

---

## Tech Stack
- Java 21+
- Spring Boot 3.5+
- Maven (`mvnw` wrapper included)
- H2 (inferred from `data/wallet.mv.db`)

---

## Run Locally
```bash
cd /home/user/IdeaProjects/wallet
./mvnw spring-boot:run
```

## Run Tests
```bash
cd /home/user/IdeaProjects/wallet
./mvnw test
```

---
