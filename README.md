# NSUMedia Bot

![NSUMedia Bot Logo](assets/nsumedia-bot-logo.png)

Добро пожаловать в репозиторий NSUMedia Bot! Это проект Telegram-бота, предоставляющего студентам удобный
доступ к учебным материалам, организованным по специализациям, курсам, семестрам и предметам

## Технологии

- Java 17
- Spring Boot 3

- Telegram Bot API

- SQL
- Spring Data JPA
- PostgreSQL
- Liquibase

- Yandex Disk

- Spring Mail
- Thymeleaf

- Docker Compose

## Установка и настройка

### Вручную

1. Убедитесь, что у вас установлены Java 17 и Apache Maven;
2. Клонируйте репозиторий на свою локальную машину;
3. Настройте переменные среды базы данных:

    + POSTGRES_USER - имя пользователя базы данных
    + POSTGRES_PASSWORD - пароль пользователя базы данных
    + POSTGRES_DB - имя базы данных
    + NSUMEDIA_BOT_DB_URL - URL базы данных
    + NSUMEDIA_BOT_DB_USERNAME - имя пользователя базы данных
    + NSUMEDIA_BOT_DB_PASSWORD - пароль пользователя базы данных

4. Настройте переменные среды почты:

    + NSUMEDIA_BOT_MAIL_PROTOCOL - протокол SMTP сервера
    + NSUMEDIA_BOT_MAIL_HOST - хост SMTP сервера
    + NSUMEDIA_BOT_MAIL_PORT - порт SMTP сервера
    + NSUMEDIA_BOT_MAIL_USERNAME - имя пользователя SMTP сервера
    + NSUMEDIA_BOT_MAIL_PASSWORD - пароль пользователя SMTP сервера

5. Добавьте в переменные среды API токен Яндекс Диска:

    + NSUMEDIA_BOT_YANDEX_DISK_API_TOKEN - API токен Yandex Диска

6. Настройте переменные среды для телеграм бота:

    + NSUMEDIA_BOT_TELEGRAM_BOT_TOKEN - токен телеграм бота
    + NSUMEDIA_BOT_TELEGRAM_BOT_NAME - имя телеграм бота
    + NSUMEDIA_BOT_TELEGRAM_BOT_THREAD_POOL_SIZE - количество потоков, используемых ботом для обработки сообщений
    + NSUMEDIA_BOT_TELEGRAM_BOT_CREATOR_ID - ID создателя телеграм бота

7. Запустите приложение с помощью команды:

   ```bash
    mvn spring-boot:run
   ```

### Docker Compose

1. Скопируйте файлы [docker-compose.yml](./docker-compose.yml) и [sample.env](./sample.env) в локальную директорию;
2. Добавьте значения переменных среды в файл [sample.env](./sample.env) и переименуйте его в .env;
3. Запустите контейнеры с помощью команды:

   ```bash
    sudo docker compose up -d
   ```

## Документация

+ [UML диаграммы](docs/diagrams.asta)
+ [Техническая документация](docs/NSUMedia-bot-Project.Report.pdf)

## Вклад в проект

Если вы хотите внести свой вклад в проект, вы можете следовать этим шагам:

1. Создайте форк этого репозитория.
2. Внесите необходимые изменения.
3. Создайте pull request, описывая ваши изменения.

