DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS users;
CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id      INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    user_id VARCHAR UNIQUE                    NOT NULL,
    name    VARCHAR                           NOT NULL,
    manager BOOL                DEFAULT FALSE NOT NULL,
    admin   BOOL                DEFAULT FALSE NOT NULL,
    head    BOOL                DEFAULT FALSE NOT NULL
);
CREATE UNIQUE INDEX users_unique_userid_idx ON users (user_id);

CREATE TABLE schedule
(
    id      INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    user_id VARCHAR                       NOT NULL,
    hour    INTEGER                       NOT NULL,
    minutes INTEGER             DEFAULT 0 NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX schedule_unique_user_time_idx ON schedule (user_id, hour, minutes);
