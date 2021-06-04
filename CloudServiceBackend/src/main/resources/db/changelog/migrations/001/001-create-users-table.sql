create extension if not exists pgcrypto;

create table users
(
    id bigserial primary key,
    login varchar(255) unique not null,
    password varchar(255) not null
)