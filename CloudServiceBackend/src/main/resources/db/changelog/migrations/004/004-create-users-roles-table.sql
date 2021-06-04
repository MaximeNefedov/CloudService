create table users_roles
(
    user_id bigint check ( user_id > 0 ) not null,
    role_id bigint check ( role_id > 0 ) not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
)