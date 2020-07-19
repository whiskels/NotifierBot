# Telegram bot for internal use inside company.
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/31c586b264204eef98c12a700563b089)](https://www.codacy.com/manual/whiskels/TelegramNotifierBot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whiskels/TelegramNotifierBot&amp;utm_campaign=Badge_Grade)
## PLEASE NOTE THAT THIS PROJECT WON'T COMPILE
Due to exception restrictions this repository doesn't contain pom.xml, tokens and user config.
In order to run this bot:
- pom_example.xml - Change YOUR_APP_NAME to your app name on Heroku and rename file to pom.xml
- Set up BOT_NAME, BOT_TOKEN, DATABASE_URL and JSON_URL environment variables on Heroku;

### Features:
- Bot is based on Long Polling Bot;
- Authorization based on predefined green list of tokens stored in Postgres DB;
- Bot admins are notified if unauthorized user tries to use bot;
- Authorized users can get information about customer debts based on their role (manager, head);
- Information is cached and updated daily by deserialization from JSON;
- Scheduling of individual notification time for each user;
- Several levels of logging;
- Bot is deployed to Heroku.

Used technologies: Java 8, Maven, Postgres, JDBC, Logback, SLF4J, TelegramBots, TelegramBotsExtensions, Jackson.

Deployment (via Heroku CLI):

<code>mvn clean heroku:deploy</code>

Bot start up

<code>heroku ps:scale worker=1 -a *app_name*</code>

Logging

<code>heroku logs -a *app_name*</code>
