# Reel Payment System Guide

## Overview

The Reel Payment System implements a pay-per-reel model where each property owner gets one free reel per property, with additional reels requiring payment before being published.

## Key Features

1. **Free First Reel**: Each property gets one free reel automatically published
2. **Paid Additional Reels**: Any additional reels for the same property cost ₹99 each
3. **Draft Status**: Additional reels are saved in DRAFT status until payment is processed
4. **Razorpay Integration**: Uses Razorpay for secure payment processing

## How It Works

1. When a user uploads a reel, the system checks if this is the first reel for the property:
   - If it's the first reel: It's immediately published (status = PUBLISHED)
   - If it's an additional reel: It's saved as a draft (status = DRAFT) and marked as requiring payment

2. Users can view their draft reels, but these won't appear in feeds or property listings until published

3. To publish a draft reel, the user must make a payment:
   - Initiate payment with the `/reels/{reelId}/payment` endpoint
   - Complete payment through Razorpay
   - Verify payment with the `/reels/{reelId}/verify-payment` endpoint
   - Upon successful verification, the reel is published

## API Endpoints

### Upload Reel
```
POST /api/v1/reels
```
- Response indicates if payment is required (`paymentRequired: true|false`)

### Check Upload Limit
```
GET /api/v1/reels/check-upload-limit?propertyId={propertyId}
```
- Shows if payment is required and pricing details

### Initiate Payment
```
POST /api/v1/reels/{reelId}/payment
```
- Returns payment details for Razorpay integration

### Verify Payment
```
POST /api/v1/reels/{reelId}/verify-payment
```
- Body: `{ "referenceId": "...", "gatewayTransactionId": "...", "gatewayOrderId": "...", "paymentSignature": "..." }`
- Publishes the reel on successful verification

## Configuration

Payment settings are configured in the following properties:
- `reels.price`: Price per reel (default: 99.00 INR)
- `payment.gateway.razorpay.*`: Razorpay integration keys

## Testing

1. Use the `test-reel-payment.sh` script to test the end-to-end flow
2. Ensure Razorpay test credentials are properly configured 