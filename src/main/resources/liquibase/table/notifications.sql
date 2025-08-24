--liquibase formatted sql

--changeset andryss:create-notifications-table
create table notifications (
    id bigserial primary key,
    user_id bigint not null,
    update_id bigint not null,
    created_at timestamp not null default current_timestamp
);

comment on table notifications is 'События обновления';

comment on column notifications.id is 'Идентификатор';
comment on column notifications.user_id is 'Идентификатор получателя';
comment on column notifications.update_id is 'Идентификатор обновления';
comment on column notifications.created_at is 'Время создания';
