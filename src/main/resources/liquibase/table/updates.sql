--liquibase formatted sql

--changeset andryss:create-updates-table
create table updates (
    id bigint primary key,
    prev bigint unique not null,
    type int not null,
    data jsonb not null,
    created_at timestamp not null
);

comment on table updates is 'События обновления';

comment on column updates.id is 'Идентификатор';
comment on column updates.prev is 'Идентификатор предыдущего обновления';
comment on column updates.type is 'Тип';
comment on column updates.data is 'Данные о событии';
comment on column updates.created_at is 'Время создания';
