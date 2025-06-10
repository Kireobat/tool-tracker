create table tool_tracker.tools (
    id serial primary key,
    name varchar not null,
    serial varchar not null,
    type integer references tool_tracker.tool_types(id),
    status varchar not null,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.tools_seq increment by 1 start with 1;