create table tool_tracker.fees (
    id serial primary key,
    lending_agreement_id integer references tool_tracker.lending_agreements(id),
    reason varchar not null,
    fee_amount integer not null,
    created_by integer references tool_tracker.users(id) not null,
    created_time timestamptz not null,
    modified_by integer references tool_tracker.users(id),
    modified_time timestamptz
);

create sequence if not exists tool_tracker.fees_seq increment by 1 start with 1;