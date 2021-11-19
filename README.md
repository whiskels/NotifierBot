### Telegram/Slack Notifier Bot
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/307f26a176cd4488b19db004f6705254)](https://www.codacy.com/gh/whiskels/TelegramNotifierBot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whiskels/TelegramNotifierBot&amp;utm_campaign=Badge_Grade)
[![Build Status](https://app.travis-ci.com/whiskels/TelegramNotifierBot.svg?branch=master)](https://app.travis-ci.com/whiskels/TelegramNotifierBot)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fwhiskels%2FTelegramNotifierBot&count_bg=%2379C83D&title_bg=%23555555&icon=telegram.svg&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
[![Known Vulnerabilities](https://snyk.io/test/github/whiskels/telegramnotifierbot/badge.svg)](https://snyk.io/test/github/whiskels/telegramnotifierbot)
___
Heroku-hosted application mostly used to create various Reports for Slack. Telegram bot that duplicates the reports and has admin panel that is used to handle situations where reports are incorrect.

Designed for internal use inside company.

___
#### PLEASE NOTE THAT THIS PROJECT WON'T COMPILE (as I am too lazy to create a self-sufficient working demo profile)
Due to security restrictions this repository doesn't contain tokens.

If you would like to run this code:
 - 'dev' profile by default does not include any Slack reporters - they can be enabled by adding 'slack-dev' profile to active profiles
 - Telegram bot can be enabled by adding telegram.bot.token property value in telegram-dev config
 - Bot admin token 'telegram.bot.admin' is required to send start-up report and unauthorized request messages

Feature toggles can be discovered by looking out for @ConditionalOnProperty annotation

___
### Technologies used: 
Java 14, Spring (Boot, Data JPA), Hibernate, Postgres, H2, Flyway and a bunch of APIs (Slack, Telegram, Google Sheets).

___
#### Features:
- Separate profiles (dev, prod)
- CI/CD in Travis with automatic deployment of the latest version of master branch to Heroku

Telegram bot:
- Authentication is based on green list of tokens stored in Postgres DB
- Role-based authorization
- Bot admin is notified if user calls a command he is unauthorized to use
- Help message is generated dynamically (based on user roles)
- Unauthorized users can get their token
- Different reports for authorized users - upcoming birthdays, customer debts, recent payments
- Scheduling of individual notification time for each user
- Admin panel that enables data loading audition, data reloading and manual slack payload execution directly from the bot

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
