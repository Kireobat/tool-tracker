create table tool_tracker.roles (
    id serial primary key,
    name varchar not null ,
    description varchar,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null ,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.roles_seq increment by 1 start with 10;