--liquibase formatted sql

--changeset andryss:create-chats-table
create table messages (
    id bigserial primary key,
    chat_id bigint not null,
    sender_id bigint not null,
    text varchar(4096) not null,
    created_at timestamp not null default current_timestamp
);

comment on table messages is 'Сообщения';

comment on column messages.id is 'Идентификатор';
comment on column messages.chat_id is 'Идентификатор чата';
comment on column messages.sender_id is 'Идентификатор получателя';
comment on column messages.text is 'Текст сообщения';
comment on column messages.created_at is 'Время создания';
