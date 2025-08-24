--liquibase formatted sql

--changeset andryss:create-updates-table
create table updates (
    id bigserial primary key,
    type int not null,
    data jsonb not null,
    created_at timestamp not null default current_timestamp
);

comment on table updates is 'Обновления';

comment on column updates.id is 'Идентификатор';
comment on column updates.type is 'Тип';
comment on column updates.data is 'Данные об обновлении';
comment on column updates.created_at is 'Время создания';
