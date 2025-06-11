SELECT setval('tool_tracker.tool_services_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tool_services), 0) + 1);
SELECT setval('tool_tracker.tool_services_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tool_services), 0) + 1);

SELECT setval('tool_tracker.damage_reports_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.damage_reports), 0) + 1);
SELECT setval('tool_tracker.damage_reports_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.damage_reports), 0) + 1);


SELECT setval('tool_tracker.lending_agreements_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.lending_agreements), 0) + 1);
SELECT setval('tool_tracker.lending_agreements_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.lending_agreements), 0) + 1);


SELECT setval('tool_tracker.tool_types_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tool_types), 0) + 1);
SELECT setval('tool_tracker.tool_types_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tool_types), 0) + 1);


SELECT setval('tool_tracker.tools_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tools), 0) + 1);
SELECT setval('tool_tracker.tools_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.tools), 0) + 1);


SELECT setval('tool_tracker.users_map_roles_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.users_map_roles), 0) + 1);

SELECT setval('tool_tracker.roles_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.roles), 0) + 1);
SELECT setval('tool_tracker.roles_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.roles), 0) + 1);


SELECT setval('tool_tracker.users_id_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.users), 0) + 1);
SELECT setval('tool_tracker.users_seq', COALESCE((SELECT MAX(id) FROM tool_tracker.users), 0) + 1);