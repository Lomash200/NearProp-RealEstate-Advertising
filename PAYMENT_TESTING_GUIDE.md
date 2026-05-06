# NearProp Payment System Testing Guide

This document provides step-by-step instructions for testing the payment integration with Razorpay in the NearProp application.

## Prerequisites

1. A Razorpay test account (available at [dashboard.razorpay.com](https://dashboard.razorpay.com))
2. API Key and Secret from Razorpay test account
3. Postman for API testing
4. Valid user authentication token for NearProp

## Environment Setup

1. Configure the following properties in your `application.properties` or environment variables:

```properties
payment.gateway.razorpay.key=rzp_test_XXXXXXXXXX
payment.gateway.razorpay.secret=XXXXXXXXXXXX
payment.gateway.razorpay.api.url=https://api.razorpay.com/v1
```

2. Set up Postman environment variables:
   - `base_url`: Your NearProp API URL (e.g., `http://localhost:8081`)
   - `user_token`: JWT token for an authenticated user
   - `admin_token`: JWT token for an admin user

## Test Cases

### 1. Initiate a Subscription Payment

**Request:**
```http
POST {{base_url}}/api/payments/initiate
Content-Type: application/json
Authorization: Bearer {{user_token}}

{
  "amount": 999.00,
  "currency": "INR",
  "paymentType": "SUBSCRIPTION",
  "subscriptionPlanId": 1,
  "propertyId": 101,
  "autoRenew": true,
  "customerName": "Test User",
  "customerEmail": "test@example.com",
  "customerPhone": "9876543210"
}
```

**Expected Response:**
- Status: 200 OK
- Response contains `referenceId`, `gatewayOrderId`, `paymentToken`, and other payment details
- Verify the payment is recorded in the database with status `INITIATED`

### 2. Verify a Completed Payment

**Request:**
```http
POST {{base_url}}/api/payments/verify
Content-Type: application/json
Authorization: Bearer {{user_token}}

{
  "referenceId": "NP-PAY-XXXXXXXX",
  "gatewayTransactionId": "pay_XXXXXXXXXX",
  "gatewayOrderId": "order_XXXXXXXXXX",
  "paymentSignature": "XXXXXXXXXXXX"
}
```

**Expected Response:**
- Status: 200 OK
- Response indicates successful verification
- Verify the payment status is updated to `COMPLETED` in the database
- Verify the subscription is created/activated

### 3. Check Payment Status

**Request:**
```http
GET {{base_url}}/api/payments/NP-PAY-XXXXXXXX/status
Authorization: Bearer {{user_token}}
```

**Expected Response:**
- Status: 200 OK
- Response contains the current payment status

### 4. Generate Receipt

**Request:**
```http
GET {{base_url}}/api/payments/NP-PAY-XXXXXXXX/receipt
Authorization: Bearer {{user_token}}
```

**Expected Response:**
- Status: 200 OK
- Response contains a URL to download the receipt

### 5. Cancel a Payment

**Request:**
```http
POST {{base_url}}/api/payments/NP-PAY-XXXXXXXX/cancel
Authorization: Bearer {{user_token}}
```

**Expected Response:**
- Status: 200 OK
- Response indicates successful cancellation
- Verify the payment status is updated to `CANCELLED` in the database

### 6. Process a Refund

**Request:**
```http
POST {{base_url}}/api/payments/NP-PAY-XXXXXXXX/refund?amount=999.00&reason=Customer requested
Authorization: Bearer {{user_token}}
```

**Expected Response:**
- Status: 200 OK
- Response contains refund reference ID
- Verify the payment status is updated to `REFUNDED` in the database

### 7. Admin: View Payment Details

**Request:**
```http
GET {{base_url}}/api/payments/admin/NP-PAY-XXXXXXXX
Authorization: Bearer {{admin_token}}
```

**Expected Response:**
- Status: 200 OK
- Response contains complete payment transaction details

## Webhook Testing

To test Razorpay webhooks:

1. Configure a webhook URL in your Razorpay dashboard (e.g., `https://your-api.com/api/payments/webhook`)
2. Use a tool like ngrok to expose your local server to the internet
3. Trigger events in the Razorpay dashboard and verify the webhook handling

## Common Issues and Troubleshooting

1. **Signature Verification Failure**
   - Ensure the correct API secret is being used
   - Check that the signature is being calculated correctly

2. **Payment Status Not Updating**
   - Check database connection and transaction management
   - Verify webhook URL is accessible to Razorpay

3. **Refund Failure**
   - Ensure the payment is in a refundable state
   - Check that the refund amount is valid

## Integration with Subscription System

After a successful payment verification:

1. The system should automatically create or renew a subscription
2. The subscription should be linked to the payment via `payment_transaction_reference`
3. The receipt URL should be stored in the subscription record

## Security Considerations

1. Always verify payment signatures to prevent tampering
2. Use HTTPS for all payment-related API calls
3. Never expose API keys or secrets in client-side code
4. Implement rate limiting to prevent abuse

## End-to-End Testing Workflow

1. Create a subscription plan (as admin)
2. Initiate a payment for the subscription (as user)
3. Complete the payment using Razorpay test cards
4. Verify the payment was processed correctly
5. Check that the subscription is active
6. Test subscription content access based on payment status 