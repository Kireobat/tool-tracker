create table tool_tracker.damage_reports (
    id serial primary key,
    lending_agreement_id integer references tool_tracker.lending_agreements(id),
    tool_id integer references tool_tracker.tools(id),
    description varchar,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.damage_reports_seq increment by 1 start with 1;