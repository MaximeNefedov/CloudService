create extension if not exists pgcrypto;

create table users
(
    login varchar(255) primary key not null,
    password varchar(255) not null
)