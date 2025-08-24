--liquibase formatted sql

--changeset andryss:create-sessions-table
create table sessions (
    id bigserial primary key,
    user_id bigint not null,
    meta jsonb not null,
    status int not null,
    created_at timestamp not null default current_timestamp
);

comment on table sessions is 'Сессии пользователей';

comment on column sessions.id is 'Идентификатор';
comment on column sessions.user_id is 'Идентификатор пользователя';
comment on column sessions.meta is 'Метаинформация о сессии';
comment on column sessions.status is 'Статус';
comment on column sessions.created_at is 'Время создания';
