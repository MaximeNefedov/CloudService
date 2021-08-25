create table users_roles
(
    user_name varchar(255) not null,
    role_id bigint check ( role_id > 0 ) not null,
    primary key (user_name, role_id),
    foreign key (user_name) references users (login),
    foreign key (role_id) references roles (id)
)