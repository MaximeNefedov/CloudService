create table authorities
(
    id bigserial primary key,
    name varchar(255) unique not null
)