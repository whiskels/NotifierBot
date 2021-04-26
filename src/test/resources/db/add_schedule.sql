insert into users (id, chat_id, name)
values (1, 1, 'Test user 1'),
       (2, 2, 'Test user 2'),
       (3, 3, 'Test user 3');

insert into schedule(id, user_id, hour, minutes)
values (4, 1, 10, 0),
       (5, 1, 15, 0),
       (6, 2, 23, 59);