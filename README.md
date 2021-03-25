### Telegram Notifier Bot
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/307f26a176cd4488b19db004f6705254)](https://www.codacy.com/gh/whiskels/TelegramNotifierBot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whiskels/TelegramNotifierBot&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.com/whiskels/TelegramNotifierBot.svg?branch=master)](https://travis-ci.com/whiskels/TelegramNotifierBot)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fwhiskels%2FTelegramNotifierBot&count_bg=%2379C83D&title_bg=%23555555&icon=telegram.svg&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
[![Known Vulnerabilities](https://snyk.io/test/github/whiskels/telegramnotifierbot/badge.svg)](https://snyk.io/test/github/whiskels/telegramnotifierbot)
___
Bot is used by account managers and their heads in order to get information about overdue debts daily.
Designed for internal use inside company.

___
#### PLEASE NOTE THAT THIS PROJECT WON'T COMPILE
Due to security restrictions this repository doesn't contain tokens.
In order to run this bot:
 - add bot token and name to application.yaml (required minimum to compile the project and run bot);
 - add bot admin token to application.yaml (to get start up report and receive unauthorized messages);
 - make changes to services that use JSON data as those defined in this project are built in order to work with API given by the customer;
 - add slack webhook URL's to receive scheduled messages to designated slack channel.

___
### Technology stack: 
Java 14, TelegramBots API, TelegramBotsExtensions, Slack API, Spring Boot, Spring Data JPA, Hibernate, Flyway, Lombok, Maven, Postgres, SLF4J, Jackson.

___
#### Features:
- Bot is based on Long Polling Bot;
- Separate profiles (war, test);
- Authentication is based on green list of tokens stored in Postgres DB;
- Role-based authorization;
- Bot admin is notified if user calls command he is unauthorized to use;
- Help message is generated dynamically (based on user roles);
- Unauthorized users can get their token;
- Authorized users can get information about upcoming birthdays;
- Authorized users can get information about customer debts based on their role (manager, head);
- Admins can get bot server time, notify all users, update users using chat commands;
- Scheduling of individual notification time for each user;
- Several levels of logging;
- Bot is deployed to Heroku.

___
#### Screenshots:
Message about successful initialization:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/start_report.png)

Unauthorized user available commands:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/user_unauthorized_commands.png)

Authorized user (employee) available commands:

![ScreenShot](https://raw.github.com/whiskels/TelegramNotifierBot/master/screenshots/user_employee_commands.png)

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
