# NearProp Application Running Guide

This guide provides step-by-step instructions for running the NearProp application and accessing the mock data through the existing monthly report APIs.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL database (configured in application properties)

## Running the Application

### Step 1: Compile the application

```bash
mvn clean install
```

This will compile the application and run any tests. If you want to skip tests, use:

```bash
mvn clean install -DskipTests
```

### Step 2: Run the application

```bash
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080` (or the port configured in your application properties).

## Generating and Accessing Mock Data

### Step 1: Generate Mock Monthly Report Data

You can generate mock monthly revenue report data using one of the following API endpoints:

#### For Admin Users:

1. Generate mock report for a specific franchisee:
   ```
   POST /api/mock-reports/franchisee/{franchiseeId}?year=2023&month=7
   ```

2. Generate mock reports for all franchisees:
   ```
   POST /api/mock-reports/all-franchisees?year=2023&month=7
   ```

3. Generate mock report for a specific district:
   ```
   POST /api/mock-reports/district/{districtId}?year=2023&month=7
   ```

4. Generate mock reports for all districts of a franchisee:
   ```
   POST /api/mock-reports/franchisee/{franchiseeId}/districts?year=2023&month=7
   ```

#### For Franchisee Users:

1. Generate mock reports for your own districts:
   ```
   POST /api/mock-reports/my-districts?year=2023&month=7
   ```

2. Generate mock report for a specific district (that belongs to you):
   ```
   POST /api/mock-reports/district/{districtId}?year=2023&month=7
   ```

### Step 2: Access the Generated Mock Data through Existing APIs

Once you've generated the mock data, you can access it through the existing monthly report APIs:

#### For Admin Users:

1. Get all monthly reports:
   ```
   GET /api/admin/monthly-reports?year=2023&month=7
   ```

2. Get monthly report for a specific franchisee:
   ```
   GET /api/admin/monthly-reports/franchisee/{franchiseeId}?year=2023&month=7
   ```

3. Get monthly report for a specific district:
   ```
   GET /api/admin/monthly-reports/district/{districtId}?year=2023&month=7
   ```

#### For Franchisee Users:

1. Get your monthly reports:
   ```
   GET /api/franchisee/monthly-reports?year=2023&month=7
   ```

2. Get monthly report for a specific district (that belongs to you):
   ```
   GET /api/franchisee/monthly-reports/district/{districtId}?year=2023&month=7
   ```

## Accessing Franchisee Dashboard

### For Franchisee Users:

1. Get your dashboard data:
   ```
   GET /api/franchisee/dashboard?startDate=2023-06-01&endDate=2023-07-31
   ```

2. Get performance data for all your districts:
   ```
   GET /api/franchisee/dashboard/districts?startDate=2023-06-01&endDate=2023-07-31
   ```

3. Get performance data for a specific district:
   ```
   GET /api/franchisee/dashboard/districts/{districtId}?startDate=2023-06-01&endDate=2023-07-31
   ```

### For Admin Users:

1. Get dashboard data for a specific franchisee:
   ```
   GET /api/franchisee/dashboard/admin/franchisee/{franchiseeId}?startDate=2023-06-01&endDate=2023-07-31
   ```

2. Get district performance data for a specific franchisee:
   ```
   GET /api/franchisee/dashboard/admin/franchisee/{franchiseeId}/districts?startDate=2023-06-01&endDate=2023-07-31
   ```

3. Get performance data for a specific district:
   ```
   GET /api/franchisee/dashboard/admin/districts/{districtId}?startDate=2023-06-01&endDate=2023-07-31
   ```

## Accessing Child Safety Standards API

The child safety standards API is a public endpoint that doesn't require authentication:

```
GET /api/safety-standards
```

This will return the safety standards as plain text. If you want the response in JSON format, set the Accept header to `application/json`.

## Troubleshooting

### Common Issues

1. **Database Connection Issues**:
   - Check your database configuration in `application.properties` or `application.yml`
   - Ensure your MySQL server is running

2. **Authentication Issues**:
   - Make sure you're using the correct JWT token for authentication
   - Verify that your user has the required role (ADMIN or FRANCHISEE)

3. **Mock Data Generation Errors**:
   - If you get an error saying a report already exists, try using a different year/month combination
   - Ensure the franchisee or district ID exists in the database

### Logs

Check the application logs for detailed error information. By default, logs are written to the console, but they may also be configured to write to a file depending on your application's configuration. 