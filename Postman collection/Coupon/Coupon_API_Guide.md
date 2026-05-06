# NearProp Coupon API Guide

This guide explains how to use the coupon system in the NearProp platform.

## Overview

The coupon system allows administrators to create discount coupons that users can apply to their subscription purchases. Coupons can offer either percentage-based or fixed amount discounts.

## Coupon Types

1. **Percentage Discount**: Applies a percentage discount to the subscription price
   - Example: 25% off a ₹2000 subscription = ₹500 discount
   - Optional max discount cap: Limit the maximum discount amount

2. **Fixed Amount Discount**: Applies a fixed amount discount to the subscription price
   - Example: ₹500 off any subscription

## Admin APIs

### Creating Coupons

Administrators can create coupons with the following properties:

- `code`: Unique coupon code (e.g., "WELCOME25")
- `description`: Description of the coupon
- `discountType`: Either "PERCENTAGE" or "FIXED_AMOUNT"
- `discountPercentage`: Percentage discount (if type is PERCENTAGE)
- `discountAmount`: Fixed amount discount (if type is FIXED_AMOUNT)
- `maxDiscount`: Maximum discount amount (for percentage discounts)
- `validFrom`: Start date of coupon validity
- `validUntil`: End date of coupon validity
- `maxUses`: Maximum number of times the coupon can be used
- `active`: Whether the coupon is active
- `subscriptionType`: Optional restriction to specific subscription types (PROPERTY, SELLER, ADVISOR, DEVELOPER, FRANCHISEE)

### Managing Coupons

Administrators can:
- List all coupons
- View active coupons
- Get coupon details
- Update coupon properties
- Activate/deactivate coupons
- Delete coupons
- Filter coupons by subscription plan type

## User APIs

### Validating Coupons

Users can validate a coupon before applying it:

```
POST /api/coupons/validate
{
    "code": "WELCOME25",
    "orderAmount": 2000,
    "planType": "PROPERTY"
}
```

The response includes:
- Whether the coupon is valid
- Original price
- Discount amount
- Final price after discount

### Applying Coupons to Subscriptions

To apply a coupon when creating a subscription:

```
POST /api/subscriptions
{
    "planId": 1,
    "autoRenew": false,
    "paymentMethod": "CREDIT_CARD",
    "paymentReferenceId": "pay_123456789",
    "couponCode": "WELCOME25"
}
```

## Testing Coupons

1. Create a coupon as an admin
2. Validate the coupon using the validation API
3. Create a subscription with the coupon code
4. Verify that the discount is applied correctly

## Implementation Notes

- Coupons are validated at the time of use
- Invalid coupons are rejected with appropriate error messages
- Coupon usage is tracked and limited according to the `maxUses` setting
- Expired or inactive coupons cannot be used 