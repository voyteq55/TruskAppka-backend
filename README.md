## Local development:
- override `application.properties` with:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/truskappka
spring.datasource.username=postgres
spring.datasource.password=postgres

minio.url=http://localhost:9000
minio.accessKey=minioadmin
minio.secretKey=minioadmin
minio.bucket=images

jwt.secret=3IUIRg1kXZF9DrfhuBKU0+E8IHL+3yVcGVd0aE4z5y8=
google.clientId=1234567890-abcde12345.apps.googleusercontent.com
```

- provide Minio and Postgres containers with respective envs

## Enviromental variables:

```env
SERVICE_DB_URL=jdbc:postgresql://postgresdb:5432/truskappka
SERVICE_DB_LOGIN=postgres
SERVICE_DB_PASSWORD=postgres

MINIO_URL=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=images

JWT_SECRET=3IUIRg1kXZF9DrfhuBKU0+E8IHL+3yVcGVd0aE4z5y8=
GOOGLE_CLIENT_ID=1234567890-abcde12345.apps.googleusercontent.com
```