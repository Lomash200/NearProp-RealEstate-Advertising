# Database Migration Update

This document explains the changes made to the database migration system to simplify setup on new machines.

## Changes Made

1. **Consolidated Migration Files**:
   - All previous migration files have been consolidated into two files:
     - `V1__schema.sql`: Contains all table definitions with `IF NOT EXISTS` checks
     - `V2__initial_data.sql`: Contains essential initial data with safe inserts

2. **Custom ID Generation**:
   - Added custom ID generation for property permanent IDs
   - Format: `RNPUYYYYMMDDTTTT###` (RNPU prefix + date + time + random suffix)
   - Ensures uniqueness even with high concurrency

3. **Configuration Updates**:
   - Updated Flyway configuration in `application.yml`:
     - Set `out-of-order: true` to handle migrations flexibly
     - Set `clean-disabled: true` to prevent accidental data loss
     - Updated Redis configuration to use newer Spring Boot syntax

4. **Hibernate Changes**:
   - Changed `ddl-auto` from `update` to `validate`
   - This ensures Hibernate doesn't try to modify the schema, only validates it

## How It Works

When the application starts:

1. Flyway will run the consolidated migration files in order
2. The `IF NOT EXISTS` checks ensure tables are only created if they don't already exist
3. The initial data script adds essential data without duplicates
4. Hibernate will validate the schema matches the entity definitions

## Backup

A backup of all original migration files has been created in the `migration_backup` directory.

## Running on a New Machine

When running on a new Ubuntu machine with a fresh database:

1. Create the database:
   ```sql
   CREATE DATABASE nearprop;
   CREATE USER nearprop WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE nearprop TO nearprop;
   ```

2. Update database credentials in `application.yml`

3. Start the application - all tables will be created automatically

## Troubleshooting

If you encounter issues:

1. **Database Connection**: Ensure the database exists and credentials are correct

2. **Schema Validation**: If Hibernate validation fails, check entity definitions against the schema

3. **Missing Tables**: If tables are missing, you can manually run the migration scripts:
   ```bash
   psql -U username -d nearprop -f src/main/resources/db/migration/V1__schema.sql
   psql -U username -d nearprop -f src/main/resources/db/migration/V2__initial_data.sql
   ```

4. **Flyway Issues**: You can repair the Flyway metadata table:
   ```sql
   DELETE FROM flyway_schema_history WHERE success = false;
   ``` 