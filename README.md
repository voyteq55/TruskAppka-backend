## Local development:
- override `application.properties` with:

```properties

spring.application.name=truskappka_backend
spring.datasource.url=jdbc:postgresql://localhost:5432/truskappka
spring.datasource.username=postgres
spring.datasource.password=postgres
minio.url:http://localhost:9000
minio.accessKey:minioadmin
minio.secretKey:minioadmin
minio.bucket:images
```

- provide Minio and Postgres containers with respective envs

## Enviromental variables:

```env
SERVICE_DB_URL=jdbc:postgresql://postgresdb:5432/truskappka
SERVICE_DB_LOGIN=postgres
SERVICE_DB_PASSWORD=postgres
SERVICE_DB_NAME=truskappka

MINIO_URL=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=images
```