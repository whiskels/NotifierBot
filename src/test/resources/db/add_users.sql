insert into users (id, chat_id, name)
values (1, 1, 'Test user 1'),
       (2, 2, 'Test user 2'),
       (3, 3, 'Test user 3');

insert into user_roles(user_id, role)
values (1, 'ADMIN'),
       (2, 'UNAUTHORIZED');