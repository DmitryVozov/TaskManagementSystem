# Task Management System
## 📖Описание проекта
Task Management System - это RESTful API приложение для управления задачами. Сервис позволяет получать, создавать, редактировать и удалять задачи. Создана ролевая система администратора и пользователей. Администратор может управлять всеми задачами: создавать, редактировать, удалять и оставлять комментарии. Пользователь может управлять только задачами, где он является исполнителем: обновлять статус задачи и оставлять комментарии. Основной функционал системы покрыт модульными и интеграционными тестами.
## 💻Стек технологий
* Java 17
* Spring Boot
* Spring Web
* Spring Security
* Spring Data
* JWT
* PostgreSQL
* Docker
* JUnit
* Mockito
* OpenAPI
* Swagger
* Lombok
## 🔧Функциональность
* Аутентификация и авторизация пользователей по email и паролю с использованием JWT
* Ролевая система администратора и пользователей
* Фильтрация и пагинация вывода задач
* Валидация входящих данных и возврат понятных сообщений об ошибках
* Контейнеризация с помощью Docker Compose
## 🚀Запуск проекта
### Системные требования
* Maven 
* Java 17+
* Docker
### Установка
1. Клонируйте репозиторий проекта:  
   ```
   git clone https://github.com/DmitryVozov/TaskManagementSystem
   cd TaskManagementSystem
   ```
2. Изменить настройки файла [.env](https://github.com/DmitryVozov/TaskManagementSystem/blob/main/.env) по необходимости:
   * URL базы данных  
     ```
     DB_URL=jdbc:postgresql://db:5432/task_manager
     ```
   * Логин пользователя в PostgreSQL  
     ```
     DB_USERNAME=postgres
     ```
   * Пароль пользователя в PostgreSQL
     ```
     DB_PASSWORD=86040716-3BF1-4537-95B0-54F018CC6
     ```
   * Название базы данных (должно быть такое же, как в URL)
     ```
     DB_NAME=task_manager
     ```
3. Соберите проект:
   ```
   mvn package
   ```
4. Запустите проект:
   ```
   docker compose up -d
   ``` 
5. API будет доступно по адресу:
   ```
   http://localhost:8080
   ```
## ❓Работа с API
### Тестовые данные
При запуске проекта SQL скрипты заполняют БД необходимыми данными для работы. Создаются две роли: ROLE_ADMIN и ROLE_USER.  
Создается администратор:  
* email: admin@gmail.com
* пароль: admin
### Получение доступа к API
Для неавторизованных пользователей доступно два эндпоинта:
*  Регистрация: 
   ```
   http://localhost:8080/api/auth/sign-up
   ```
   Ожидает POST запрос со входящими данными формата json:
   ```
   {
        "email" : "test123@gmail.com",
        "password" : "test",  
        "username": "test" 
   }
   ```  
*  Аутентификация 
   ```
   http://localhost:8080/api/auth/sign-in
   ```
   Ожидает POST запрос со входящими данными формата json:
   ```
   {
        "email" : "admin@gmail.com",  
        "password": "admin"
   }
   ``` 
В случае успешной регистрации/аутентификации будет возвращен токен доступа к API:
```
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTczNzQ1MDQ1OCwiZXhwIjoxNzM3NDU0MDU4fQ.dYp2zIAurpTbw46rGFzpX8IjhmeMpQexdeSvsPvllXk"
}
```
Для получения доступа к остальным эндпоинтам необходимо полученное значение передать в заголовке Authorization с префиксом "Bearer ".
## 📝Документация API
API подробно задокументирован с помощью OpenAPI и Swagger. Посмотреть документацию можно по адресу:
```
http://localhost:8080/swagger-ui/index.html
```
## 👦💻Автор
Возов Дмитрий
* Telegram: [@mintl0l](https://t.me/mintl0l)
* GitHub: [DmitryVozov](https://github.com/DmitryVozov)
