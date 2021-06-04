create table files
(
    id           bigserial primary key,
    name         varchar(255) not null,
    content_type varchar(255) not null,
    size         bigint       not null,
    date         timestamp    not null,
    file_body    bytea        not null,
    user_id      bigint       not null,
    foreign key (user_id) references users (id),
    unique (name, content_type, user_id)
);