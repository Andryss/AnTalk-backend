--liquibase formatted sql

--changeset andryss:create-notifications-table
create table notifications (
    id bigserial primary key,
    user_id bigint not null,
    type int not null,
    data jsonb not null,
    created_at timestamp not null default current_timestamp
);

comment on table notifications is 'События обновления';

comment on column notifications.id is 'Идентификатор';
comment on column notifications.user_id is 'Идентификатор получателя';
comment on column notifications.type is 'Тип';
comment on column notifications.data is 'Данные о событии';
comment on column notifications.created_at is 'Время создания';
