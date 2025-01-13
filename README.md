# Task Management System
RESTful API для Системы Управления Задачами. Сервис поддерживает аутентификацию и авторизацию пользователей по email и паролю. Доступ к API аутентифицирован с помощью JWT. Создана ролевая система администратора и пользователей. Администратор может управлять всеми задачами: создавать новые, редактировать существующие, просматривать и удалять, менять статус и приоритет, назначать исполнителей задачи, оставлять и редактировать комментарии. Пользователи могут управлять своими задачами, если указаны как исполнитель: менять статус, оставлять комментарии и редактировать их. Реализована фильтрация и пагинация вывода задач. Сервис валидирует входящие данные и возвращает понятные сообщения при ошибках. API подробно задокументирован с помощью OpenAPI и Swagger.
### Стек технологий
* Java 17
* Spring Boot
* Spring Web
* Spring Security
* Spring Data
* JWT
* PostgreSQL
* JUnit
* Mockito
* OpenAPI
* Swagger
* Lombok
