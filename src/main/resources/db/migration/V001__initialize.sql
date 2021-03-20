CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id      INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    chat_id INTEGER UNIQUE                    NOT NULL,
    name    VARCHAR                           NOT NULL
);
CREATE UNIQUE INDEX users_unique_chatid_idx ON users (chat_id);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE schedule
(
    id      INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    user_id INTEGER                       NOT NULL,
    hour    INTEGER                       NOT NULL,
    minutes INTEGER             DEFAULT 0 NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX schedule_unique_user_time_idx ON schedule (user_id, hour, minutes);
