# Note-App (Spring Boot Backend)

Backend for a simple **Notes** application built with **Spring Boot**.  
Provides secure REST APIs for user authentication and CRUD operations on notes.

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://adoptium.net/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot) [![Build](https://img.shields.io/badge/build-Maven-informational.svg)](https://maven.apache.org/) [![Docker](https://img.shields.io/badge/Docker-ready-2496ED.svg)](https://www.docker.com/)  

Deployed: `https://note-app-10sb.onrender.com/`

---

## ✨ Features

- JWT-based authentication (register, login, refresh)
- CRUD for Notes (create, read, update, delete)
- Optional fields: tags, archived, pinned
- Pagination + filtering (e.g., by tag or search query)
- CORS enabled for a separate frontend
- Ready for Docker & docker-compose (app + database)

---

## 🏗️ Tech Stack

- **Java 17+**, **Spring Boot 3.x**
- Spring Web, Spring Data JPA, Spring Security + JWT
- Database: PostgreSQL or MySQL (configurable)
- Build: Maven
- Containerization: Docker (`Dockerfile`, `docker-compose.yml`)

---

## 🔧 Getting Started (Local)

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL/MySQL running locally

### Configuration

Create `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notes
    username: notes
    password: notes
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: changeme-super-secret
  expiration: 3600000  # 1 hour in ms
cors:
  allowed-origins: "http://localhost:5173,http://localhost:3000"
