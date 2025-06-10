create table tool_tracker.tool_services (
    id serial primary key,
    damage_report_id integer references tool_tracker.damage_reports(id),
    service_start_time timestamptz not null,
    service_stop_time timestamptz,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.tool_services_seq increment by 1 start with 1;