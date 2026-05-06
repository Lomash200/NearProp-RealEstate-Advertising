# SMS Integration with Digital SMS API

## Overview

This integration replaces Twilio SMS with Digital SMS API for sending OTPs and verifications in the NearProp application. The implementation includes:

1. SMS API for sending OTPs
2. Weekly balance checking with automated email alerts
3. Manual balance checking via admin endpoint

## Configuration

The following properties are available in `application-secrets.yml`:

```yaml
# Digital SMS API Configuration
digital-sms:
  api-key: ${DIGITAL_SMS_API_KEY}
  sender-id: ${DIGITAL_SMS_SENDER_ID}
  entity-id: ${DIGITAL_SMS_ENTITY_ID}
  template-id: ${DIGITAL_SMS_TEMPLATE_ID}
  api-url: ${DIGITAL_SMS_API_URL}
  balance-url: ${DIGITAL_SMS_BALANCE_URL}
  low-balance-threshold: ${DIGITAL_SMS_LOW_BALANCE_THRESHOLD:500}
  notification-emails: ${DIGITAL_SMS_NOTIFICATION_EMAILS}
```

## Features

### OTP Sending

OTPs are sent via the Digital SMS API for:
- Login requests (`/v1/auth/login`)
- Registration (`/api/v1/auth/register`)
- Resending mobile OTP (`/api/v1/auth/resend-mobile-otp`)

Email OTPs still use SMTP.

### Balance Checking

- Weekly check on Saturdays at 9:00 AM
- If balance is low, daily checks until recharged
- Email notifications sent to configured addresses
- Escalating notifications for different balance levels (500, 400, 300, 200, below 200)

### Authentication

The Digital SMS API uses a token-based authentication approach:
- The API token should be set directly in the Authorization header (without "Bearer " prefix)
- This token is stored in application-secrets.yml as `digital-sms.api-key`
- The same token is used for both SMS sending and balance checking operations

### Balance Check Admin Endpoint

- Admin endpoint to check balance: `/api/v1/admin/sms/balance`
- Returns current balance information and status

## Balance Monitoring

The system automatically monitors SMS balance and sends email notifications to configured email addresses when thresholds are reached:

1. **Standard Alert (500-200 credits)**: Sends a standard alert email
2. **Critical Alert (below 200 credits)**: Sends an urgent alert email with stronger wording

Email notifications include:
- Current balance
- Threshold that triggered the alert
- Timestamp of the check

## Implementation Components

- `DigitalSmsConfig`: Configuration properties for the Digital SMS API
- `DigitalSmsService`: Core service for sending SMS and checking balance
- `SmsAdminController`: Admin endpoint for checking SMS balance
- `EmailService`: Updated to support sending balance alert emails
- `NotificationService`: Modified to use Digital SMS instead of Twilio
- `OtpService`: Updated to work with the new SMS delivery system

## Troubleshooting

If SMS sending fails:
1. Check SMS balance using admin endpoint
2. Verify API credentials in configuration
3. Check logs for detailed error information
4. Ensure proper formatting of phone numbers (with country code)
