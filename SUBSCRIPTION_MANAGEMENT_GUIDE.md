# Subscription Management Guide

This guide explains the subscription system in NearProp and recent changes to how subscriptions work.

## Overview

The NearProp subscription system allows users to purchase subscription plans that provide various features and capabilities. This guide explains how subscriptions work in the system, including the automatic property activation/deactivation feature.

## Key Changes

1. **User-Based Subscriptions**: Subscriptions are now associated directly with users rather than properties. This means:
   - A subscription belongs to a user, not a property
   - User capabilities are determined by their subscription plan
   - Multiple properties can be managed under a single user subscription

2. **Removed Property-Specific Subscriptions**: The `propertyId` field has been removed from the subscription model, as subscriptions are now solely user-based.

## Subscription Plans

Subscription plans are defined by the following attributes:

- **Name**: The name of the plan
- **Description**: A description of what the plan offers
- **Type**: The type of plan (SELLER, ADVISOR, DEVELOPER, FRANCHISEE, PROPERTY)
- **Price**: The cost of the subscription
- **Duration**: How long the subscription lasts (in days)
- **Max Properties**: Maximum number of properties allowed under this subscription
- **Max Reels**: Maximum number of reels allowed per property
- **Content Hide/Delete After**: When content becomes hidden/deleted after subscription expires

## Subscription Lifecycle

1. **Creation**: User purchases a subscription
2. **Active**: Subscription is active and user can use all features
3. **Expiration**: Subscription expires after the duration period
4. **Grace Period**: Short period after expiration where content is still visible
5. **Content Hidden**: Content is hidden from public view but still accessible to the owner
6. **Content Deleted**: Content is permanently deleted

## Property Activation Management

The system now automatically manages property activation status based on subscription status:

### When a Subscription is Created

1. The system checks for inactive properties owned by the user
2. If the subscription plan has property limits, it activates properties up to that limit
3. Properties are activated in the order they were created
4. An email notification is sent to the user with details of activated properties

### When a Subscription Expires

1. All properties associated with the expired subscription are automatically deactivated
2. An email notification is sent to the user with details of deactivated properties
3. Properties remain deactivated until the user renews their subscription or purchases a new one

### When a Subscription is Renewed

1. The system reactivates previously deactivated properties up to the subscription limit
2. An email notification is sent to the user with details of reactivated properties

### Subscription Expiration Warnings

1. The system sends warning emails before a subscription expires
2. Users with auto-renew enabled are informed that their subscription will be automatically renewed
3. Users without auto-renew are prompted to renew their subscription to avoid property deactivation

## Subscription Data Structure

Subscriptions are stored in the database with the following key fields:

- **User**: The user who owns the subscription
- **Plan**: The subscription plan
- **Start Date**: When the subscription starts
- **End Date**: When the subscription ends
- **Status**: Current status of the subscription (ACTIVE, EXPIRED, CANCELLED, etc.)
- **Auto Renew**: Whether the subscription should automatically renew

## Subscription Endpoints

### Get Available Plans

```
GET /api/subscriptions/plans
```

Returns all available subscription plans.

### Get Plans by Type

```
GET /api/subscriptions/plans/{type}
```

Returns subscription plans of a specific type (SELLER, ADVISOR, DEVELOPER, FRANCHISEE).

### Create Subscription

```
POST /api/subscriptions
```

Request body:
```json
{
    "planId": 10,
    "autoRenew": false,
    "isRenewal": false,
    "paymentMethod": "ONLINE",
    "paymentReferenceId": "pay_123456789"
}
```

Creates a new subscription for the current user.

### Get Subscription Details

```
GET /api/subscriptions/{id}
```

Returns details of a specific subscription.

### Get User's Subscriptions

```
GET /api/subscriptions/my-subscriptions
```

Returns all subscriptions for the current user.

### Cancel Subscription

```
POST /api/subscriptions/{id}/cancel
```

Cancels a subscription.

### Renew Subscription

```
POST /api/subscriptions/{id}/renew
```

Renews an existing subscription.

## Subscription Properties

- **Auto-Renewal**: Subscriptions can be configured to automatically renew when they expire.
- **Renewal History**: The system tracks renewal history through the `previousSubscriptionId` field.
- **Subscription Status**: Subscriptions can have various statuses (ACTIVE, EXPIRED, CANCELLED, etc.).

## Property Management

Even though subscriptions are no longer directly tied to properties, the system still tracks which properties are associated with which subscriptions through the Property entity:

- Properties have a `subscriptionId` field that references the subscription that activates them
- Properties have a `subscriptionExpiry` field that indicates when their activation expires
- Properties are automatically activated/deactivated based on subscription status

## Testing

A Postman collection is available at `Postman collection/User/NearProp_User_Subscription.postman_collection.json` for testing the subscription API.
