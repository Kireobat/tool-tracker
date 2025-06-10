create table tool_tracker.users (
    id serial primary key,
    name varchar not null,
    email varchar,
    password_hash varchar
);

create sequence if not exists tool_tracker.users_seq increment by 1 start with 10;