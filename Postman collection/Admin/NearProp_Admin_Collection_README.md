# NearProp Admin Unified Collection

This Postman collection provides a comprehensive set of API endpoints specifically designed for administrators of the NearProp platform. It includes all essential administrative functions organized logically by category.

## Collection Structure

The collection is organized into the following sections:

1. **Authentication**
   - Endpoints for admin login and authentication

2. **Dashboard Overview**
   - Dashboard analytics and statistics
   - System-wide metrics and performance trends

3. **User Management**
   - User retrieval and creation
   - Role management
   - Role request processing

4. **Property Management**
   - Property approval workflows
   - Property analytics

5. **Advertisement Management**
   - Advertisement listing and analytics
   - District-specific advertisement insights
   - Social media engagement metrics

6. **Revenue & Payments**
   - Payment transaction details
   - Payment processing
   - District revenue statistics

7. **Chat Management**
   - Chat room and message monitoring
   - Chat analytics and statistics
   - Message search functionality

## Setup Instructions

1. Import the `NearProp_Admin_Unified.json` file into Postman
2. Ensure you have the appropriate environment variables set:
   - `baseUrl`: Base URL of the API (e.g., "http://localhost:8080")
   - `apiPrefix`: API prefix (if any)
   - `adminToken`: Admin JWT token (obtained after login)

3. Use the "Admin Login" endpoint to obtain a valid token before using other endpoints

## Usage Notes

- All endpoints (except authentication) require a valid admin token
- Most endpoints include test scripts to validate responses
- Variable references (like `{{userId}}`) should be updated with actual values for your environment
- Default pagination parameters can be modified as needed (page, size, etc.)

## Troubleshooting

- If authentication fails, ensure your admin credentials are correct
- For any 403 errors, verify that your account has the ADMIN role
- For search and analytics endpoints, adjust date ranges if needed

## Maintenance

This collection consolidates all admin-related endpoints into a single, organized file. It replaces several partial collections that previously existed. 