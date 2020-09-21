# Telegram bot for internal use inside company.
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/31c586b264204eef98c12a700563b089)](https://www.codacy.com/manual/whiskels/TelegramNotifierBot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whiskels/TelegramNotifierBot&amp;utm_campaign=Badge_Grade)
## PLEASE NOTE THAT THIS PROJECT WON'T COMPILE
Due to security restrictions this repository doesn't contain pom.xml, tokens and user config.
In order to run this bot:
- pom_example.xml - Change YOUR_APP_NAME to your app name on Heroku, rename file to pom.xml
- application_example.yaml - Add database credentials, rename to application.yaml (default spring boot config file)
- bot_example.properties - Add bot name, token and admin chat token, rename file.
- json.example.properties - Add JSON url, rename.

### Features:
- Bot is based on Long Polling Bot;
- Authorization based on predefined green list of tokens stored in Postgres DB;
- Bot admins are notified if unauthorized user tries to use bot;
- Authorized users can get information about customer debts based on their role (manager, head);
- Information is cached and updated daily by deserialization from JSON;
- Scheduling of individual notification time for each user;
- Several levels of logging;
- Bot is deployed to Heroku.

Used technologies: Java 8, TelegramBots API, TelegramBotsExtensions, Spring Boot, Spring Data JPA, Hibernate, Lombok, Maven, Postgres, Logback, SLF4J, Jackson.

Deployment (via Heroku CLI):

<code>mvn clean heroku:deploy</code>

Bot start up

<code>heroku ps:scale worker=1 -a *app_name*</code>

Logging

<code>heroku logs -a *app_name*</code>
