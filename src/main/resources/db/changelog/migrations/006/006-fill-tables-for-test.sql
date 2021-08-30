insert into users (login, password)
VALUES ('max@mail.ru', crypt('12345', gen_salt('bf', 12)));

insert into users (login, password)
VALUES ('kate@mail.ru', crypt('123', gen_salt('bf', 12)));