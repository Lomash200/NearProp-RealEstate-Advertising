# NearProp Subscription Management Testing Guide

This document provides step-by-step instructions for testing the subscription management system in NearProp using Postman.

## Setup

1. Import the `subscription_management.postman_collection.json` file into Postman
2. Set up two environment variables:
   - `admin_token`: JWT token for an admin user
   - `user_token`: JWT token for a regular user
   - `base_url`: Set to `http://localhost:8081` or your deployment URL

## Getting Authentication Tokens

### Admin Token
1. Send a POST request to `{{base_url}}/api/v1/auth/login` with:
   ```json
   {
     "mobileNumber": "admin_phone_number"
   }
   ```
2. Verify the OTP (for development, check logs or use mock OTP)
3. Send a POST request to `{{base_url}}/api/v1/auth/verify-otp` with:
   ```json
   {
     "mobileNumber": "admin_phone_number",
     "otp": "123456"
   }
   ```
4. Copy the JWT token from the response and set it as `admin_token` in your environment

### User Token
Follow the same procedure for a non-admin user to get the `user_token`.

## Testing Workflow

### 1. Admin: Plan Management

#### Create Subscription Plans
1. In Postman, select the "Admin - Subscription Plan Management" folder
2. Run "Create Subscription Plan" request with basic plan details
   ```json
   {
     "name": "Basic Plan",
     "description": "Entry-level plan for property listings",
     "type": "BASIC",
     "price": 300.00,
     "durationDays": 30,
     "maxProperties": 1,
     "maxReelsPerProperty": 3,
     "maxTotalReels": 3,
     "contentHideAfterDays": 30,
     "contentDeleteAfterDays": 60,
     "active": true
   }
   ```
3. Run it again with premium plan details
   ```json
   {
     "name": "Premium Plan",
     "description": "Advanced plan for property listings",
     "type": "PREMIUM",
     "price": 1500.00,
     "durationDays": 30,
     "maxProperties": 10,
     "maxReelsPerProperty": 5,
     "maxTotalReels": 50,
     "contentHideAfterDays": 30,
     "contentDeleteAfterDays": 60,
     "active": true
   }
   ```
4. Create a franchise plan with marketing fee
   ```json
   {
     "name": "Franchise Plan - Delhi",
     "description": "Franchise rights for Delhi region",
     "type": "FRANCHISEE",
     "price": 50000.00,
     "marketingFee": 25000.00,
     "durationDays": 365,
     "active": true
   }
   ```

#### Verify All Plans
1. Run the "Get All Subscription Plans" request
2. Verify that all created plans appear in the response

### 2. User: Subscription Purchase

#### Browse Available Plans
1. In Postman, select the "User - Subscription Management" folder
2. Run "Get Available Plans" request to view all active plans
3. Run "Get Plans by Type" request with different plan types (BASIC, PREMIUM, FRANCHISEE)

#### Purchase Subscriptions
1. Run "Create Subscription" request with a property-specific plan
   ```json
   {
     "planId": 1,  // Use actual ID from the plans list
     "propertyId": 123,  // Use actual property ID
     "autoRenew": true,
     "paymentMethod": "CREDIT_CARD",
     "paymentReferenceId": "PAY-123456789"
   }
   ```
2. Run "Create Franchise Subscription" request for a franchise plan
   ```json
   {
     "planId": 3,  // Use ID of the franchise plan
     "autoRenew": false,
     "paymentMethod": "BANK_TRANSFER",
     "paymentReferenceId": "BANK-987654321"
   }
   ```

#### Manage Subscriptions
1. Run "Get User's Subscriptions" to view all user subscriptions
2. Note the subscription ID and run "Get Subscription by ID" to view specific details
3. Test "Cancel Subscription" on one of your subscriptions
4. Test "Renew Subscription" on another subscription

### 3. Testing Content Visibility Logic

This requires modifying subscription data in the database or waiting for expirations, but here's how to verify:

1. Expired subscriptions (non-franchise) should have content hidden after the content hide period
2. Hidden content should be deleted after the content delete period
3. Franchise plan content remains visible and is never deleted, regardless of expiry

You can verify this by:
1. Modifying subscription end dates in the database to simulate expiry
2. Running the scheduled task manually or waiting for it to run
3. Checking property and reel visibility through their respective APIs

## Common Issues and Troubleshooting

### Authentication Issues
- Check that your token is valid and not expired
- Ensure you're using the correct role (admin for plan management)

### Plan Creation Failures
- Check for duplicate plan names
- Ensure required fields are provided
- Verify valid values for numeric fields (no negatives for prices)

### Subscription Purchase Failures
- Verify the plan is active
- Ensure the property belongs to the user
- Check that property doesn't already have an active subscription
- Verify user hasn't reached the subscription limit for their role

## Testing Content Management via Database

To test the content hiding and deletion logic without waiting, you can:

1. Get the ID of an active subscription
2. Update its end date to be in the past:
   ```sql
   UPDATE subscriptions SET end_date = NOW() - INTERVAL '32 days' WHERE id = <subscription_id>;
   ```
3. Trigger the scheduled task manually or wait for it to run
4. Verify the subscription status and content visibility through API calls 