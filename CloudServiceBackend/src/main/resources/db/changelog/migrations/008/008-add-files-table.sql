create table files
(
    id           bigserial primary key,
    name         varchar(255) not null,
    content_type varchar(255) not null,
    size         bigint       not null,
    change_time  timestamp    not null,
    file_body    bytea        not null,
    hash         varchar(255) not null,
    status       varchar(255) not null,
    removal_time timestamp,
    user_id      bigint       not null,
    foreign key (user_id) references users (id),
    unique (name, content_type, user_id, hash)
);