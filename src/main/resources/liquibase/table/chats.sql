--liquibase formatted sql

--changeset andryss:create-chats-table
create table chats (
    id bigserial primary key,
    type int not null,
    user_ids jsonb not null,
    created_at timestamp not null default current_timestamp
);

comment on table chats is 'Чаты';

comment on column chats.id is 'Идентификатор';
comment on column chats.type is 'Тип';
comment on column chats.user_ids is 'Идентификаторы пользователей участников';
comment on column chats.created_at is 'Время создания';
