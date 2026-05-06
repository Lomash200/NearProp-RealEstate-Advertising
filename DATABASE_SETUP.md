# NearProp Database Setup Guide

This guide explains how to set up the NearProp database from scratch using the consolidated SQL files.

## Prerequisites

- PostgreSQL 12 or later
- Database user with privileges to create tables and schemas

## Setup Steps

### 1. Create Database

First, create a new database for NearProp:

```bash
sudo -u postgres psql
```

In the PostgreSQL prompt:

```sql
CREATE DATABASE nearprop;
CREATE USER nearpropadmin WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE nearprop TO nearpropadmin;
\q
```

### 2. Apply Database Schema

Execute the complete schema SQL file to create all tables and relationships:

```bash
psql -U nearpropadmin -d nearprop -a -f complete_schema.sql
```

### 3. Load Initial Data

Execute the initial data SQL file to populate essential data:

```bash
psql -U nearpropadmin -d nearprop -a -f initial_data.sql
```

## Schema Structure

The database schema is structured around the following main entities:

1. **User Management**
   - Users, roles, sessions, OTPs for authentication
   - User preferences and profile management

2. **Property Management**
   - Properties, amenities, images
   - Property reviews, favorites, visits
   - Property update requests

3. **Subscription System**
   - Subscription plans and features
   - User subscriptions

4. **Franchisee System**
   - Districts and franchisee management
   - Revenue tracking and withdrawal requests

5. **Social Features**
   - Chat rooms and messages
   - Reels and interactions
   - User following

6. **Payment System**
   - Payment transactions
   - Coupon system

7. **Advertisement System**
   - Advertisement management and analytics

## Custom ID Generation

The database implements a custom ID generation system for the `permanent_id` field in the properties table. IDs are generated in the format:

```
RNPUYYYYMMDDTTTT### 
```

Where:
- `RNPU`: Fixed prefix
- `YYYYMMDD`: Date part (year, month, day)
- `TTTT`: Time part in hours and minutes
- `###`: Random 3-digit suffix to ensure uniqueness

This custom ID generation uses PostgreSQL functions and triggers to automatically assign a unique ID on property creation. The system guarantees uniqueness even if multiple properties are created simultaneously (in the same millisecond).

## Connection Configuration

Update the `application.yml` file in your Spring Boot application with the following database configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nearprop
    username: nearpropadmin
    password: your_secure_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    baseline-on-migrate: true
    baseline-version: 2
```

## Flyway Migration Consolidation

If you're switching from multiple migrations to the consolidated schema approach:

1. Run the `cleanup_migrations.sh` script to:
   - Back up existing migration files to `migration_backup/` directory
   - Remove old migration files
   - Install the consolidated schema files as V1 and V2 migrations

2. Update your application.yml to use the new baseline:
   ```yaml
   spring.flyway.baseline-on-migrate: true
   spring.flyway.baseline-version: 2
   ```

## Troubleshooting

### Missing Sequences

If you encounter errors related to missing sequences for ID generation, run the following SQL for each affected table:

```sql
CREATE SEQUENCE IF NOT EXISTS table_name_id_seq;
ALTER TABLE table_name ALTER COLUMN id SET DEFAULT nextval('table_name_id_seq');
ALTER SEQUENCE table_name_id_seq OWNED BY table_name.id;
SELECT setval('table_name_id_seq', COALESCE((SELECT MAX(id) FROM table_name), 1));
```

### Permission Issues

If you encounter permission issues, ensure the database user has appropriate privileges:

```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO nearpropadmin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO nearpropadmin;
```

### Custom ID Format Issues

If you encounter issues with the custom ID generation:

```sql
-- Check if the function exists
SELECT proname, prosrc FROM pg_proc WHERE proname = 'generate_nearprop_id';

-- Recreate the function if needed
CREATE OR REPLACE FUNCTION generate_nearprop_id() 
RETURNS TEXT AS $$
DECLARE
    date_part TEXT;
    time_part TEXT;
    random_suffix TEXT;
    complete_id TEXT;
BEGIN
    date_part := TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD');
    time_part := TO_CHAR(CURRENT_TIMESTAMP, 'HH24MISSMS');
    random_suffix := LPAD(FLOOR(RANDOM() * 1000)::TEXT, 3, '0');
    complete_id := 'RNPU' || date_part || SUBSTRING(time_part, 1, 4) || random_suffix;
    RETURN complete_id;
END;
$$ LANGUAGE plpgsql;
``` 