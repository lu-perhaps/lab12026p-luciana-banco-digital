# Banco digital - Backend

[![CI/CD Pipeline](https://github.com/lu-perhaps/lab12026p-luciana-banco-digital/actions/workflows/build.yml/badge.svg)](https://github.com/lu-perhaps/lab12026p-luciana-banco-digital/actions/workflows/build.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=lu-perhaps_lab12026p-luciana-banco-digital&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lu-perhaps_lab12026p-luciana-banco-digital)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=lu-perhaps_lab12026p-luciana-banco-digital&metric=coverage)](https://sonarcloud.io/summary/new_code?id=lu-perhaps_lab12026p-luciana-banco-digital)

API REST de un banco digital hecha con Spring Boot 3.5, Java 17 y MySQL.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Estado del servicio |
| GET | `/api/customers` | Listar clientes |
| GET | `/api/customers/{id}` | Buscar un cliente |
| POST | `/api/customers` | Crear un cliente |
| PUT | `/api/customers/{id}` | Actualizar un cliente |
| DELETE | `/api/customers/{id}` | Eliminar un cliente |
| POST | `/api/transactions` | Realizar una transferencia |
| GET | `/api/transactions/{cuenta}` | Historial de una cuenta |

## Pipeline

`tests` → `sonar` → `build` (JAR e imagen en Docker Hub) → `deploy` (Render, solo en `main`).

## Despliegue

- Backend: https://banco-back.onrender.com
- Base de datos: MySQL en Aiven
- Imagen: `luperhaps/banco-back`

## Ejecutar en local

Crea un archivo `.env` con `DB_URL`, `DB_USER` y `DB_PASSWORD`, y ejecuta:

    ./mvnw spring-boot:run
