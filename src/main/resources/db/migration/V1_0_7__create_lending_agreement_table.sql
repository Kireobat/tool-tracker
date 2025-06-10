create table tool_tracker.lending_agreements (
    id serial primary key,
    tool_id integer references tool_tracker.tools(id) not null,
    borrower_id integer references tool_tracker.users(id) not null,
    lending_start_time timestamptz not null,
    expected_return_time timestamptz not null,
    return_time timestamptz,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.lending_agreements_seq increment by 1 start with 1;