ALTER TABLE tool_tracker.users ADD COLUMN created_by integer references tool_tracker.users(id);
ALTER TABLE tool_tracker.users ADD COLUMN created_time timestamptz not null;
ALTER TABLE tool_tracker.users ADD COLUMN modified_by integer references tool_tracker.users(id);
ALTER TABLE tool_tracker.users ADD COLUMN modified_time timestamptz;
