-- Delete the problematic migration records
DELETE FROM flyway_schema_history WHERE version = '7';
DELETE FROM flyway_schema_history WHERE version = '15';
DELETE FROM flyway_schema_history WHERE version = '16';

-- Mark the migrations as executed with the correct checksums
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES 
  ((SELECT COALESCE(MAX(installed_rank), 0) + 1 FROM flyway_schema_history), '15', 'create visits table', 'SQL', 'V15__create_visits_table.sql', 1402347559, 'postgres', 0, true),
  ((SELECT COALESCE(MAX(installed_rank), 0) + 2 FROM flyway_schema_history), '16', 'create reel tables', 'SQL', 'V16__create_reel_tables.sql', 885985409, 'postgres', 0, true); 