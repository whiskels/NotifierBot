### Telegram Notifier Bot
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/31c586b264204eef98c12a700563b089)](https://www.codacy.com/manual/whiskels/TelegramNotifierBot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whiskels/TelegramNotifierBot&amp;utm_campaign=Badge_Grade)
___
Bot is used by account managers and their heads in order to get information about overdue debts daily.
Designed for internal use inside company.

___
#### PLEASE NOTE THAT THIS PROJECT WON'T COMPILE
Due to security restrictions this repository doesn't contain tokens.
In order to run this bot:
- application.yaml - add bot name, token and admin chat token, add JSON url.

___
### Technology stack: 
Java 8, TelegramBots API, TelegramBotsExtensions, Spring Boot, Spring Data JPA, Hibernate, Lombok, Maven, Postgres, Logback, SLF4J, Jackson.

___
#### Features:
- Bot is based on Long Polling Bot;
- Authorization based on predefined green list of tokens stored in Postgres DB;
- Bot admin is notified if unauthorized user tries to use bot;
- Authorized users can get information about customer debts based on their role (manager, head);
- Information is cached and updated daily by deserialization from JSON;
- Scheduling of individual notification time for each user;
- Several levels of logging;
- Bot is deployed to Heroku.

___
#### Screenshots:
Message about successful initialization:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/start_report.png)

Unauthorized user available commands:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/user_unauthorized_commands.png)

Authorized user (manager, head) available commands:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/user_authorized_commands.png)

Schedule management:


![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/schedule_managing.png)



___
### Deployment
Deployment via Heroku CLI:

<code>mvn clean heroku:deploy</code>

Starting up:

<code>heroku ps:scale worker=1 -a *app_name*</code>

Retrieving logs:

<code>heroku logs -a *app_name*</code>
