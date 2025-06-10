create table tool_tracker.users_map_roles (
    id serial primary key,
    user_id integer references tool_tracker.users(id),
    role_id integer references tool_tracker.roles(id),
    created_by integer references tool_tracker.users(id),
    created_time timestamptz not null ,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.roles_seq increment by 1 start with 10;