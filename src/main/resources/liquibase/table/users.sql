--liquibase formatted sql

--changeset andryss:create-users-table
create table users (
    id bigserial primary key,
    username varchar(32) not null,
    password_hash text not null,
    created_at timestamp not null default current_timestamp
);

comment on table users is 'Пользователи';

comment on column users.id is 'Идентификатор';
comment on column users.username is 'Имя пользователя';
comment on column users.password_hash is 'Хеш пароля';
comment on column users.created_at is 'Время создания';
