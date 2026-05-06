# Property Subscription Activation Fix

## Issues Identified

1. **Property Not Automatically Activating with Subscription**
   - When a subscription is created, existing properties are not being automatically activated
   - The `activateUserProperties` method in `SubscriptionServiceImpl` was not correctly handling all subscription types

2. **Email Notifications Not Being Sent**
   - Email notifications for subscription creation and property activation were not being sent
   - Missing method in `SubscriptionEmailService` interface and implementation

## Fixes Implemented

### 1. Fixed Property Activation Logic

Updated `SubscriptionServiceImpl.java` to:
- Include all relevant subscription types (SELLER, DEVELOPER, FRANCHISEE) in the property activation logic
- Added proper logging for property activation
- Added error handling for email notifications
- Added a call to send subscription creation notification email

```java
// Activate user's inactive properties if this is a property or advisor subscription
if (plan.getType() == PlanType.PROPERTY || plan.getType() == PlanType.ADVISOR || 
    plan.getType() == PlanType.SELLER || plan.getType() == PlanType.DEVELOPER || 
    plan.getType() == PlanType.FRANCHISEE) {
    
    List<Property> activatedProperties = activateUserProperties(currentUser.getId(), savedSubscription);
    log.info("Activated {} properties for user {} with subscription {}", 
        activatedProperties.size(), currentUser.getId(), savedSubscription.getId());
    
    // Send email notification for activated properties
    if (!activatedProperties.isEmpty()) {
        try {
            subscriptionEmailService.sendSubscriptionCreatedNotification(savedSubscription, activatedProperties);
            log.info("Property activation notification email sent for {} properties", activatedProperties.size());
        } catch (Exception e) {
            log.error("Failed to send property activation notification email: {}", e.getMessage(), e);
        }
    }
}
```

### 2. Fixed Email Notification System

1. Added missing method to `SubscriptionEmailService` interface:
```java
/**
 * Send notification when a subscription is created (without activated properties)
 * 
 * @param subscription The newly created subscription
 */
void sendSubscriptionCreatedNotification(Subscription subscription);
```

2. Implemented the method in `SubscriptionEmailServiceImpl`:
```java
@Override
@Async
public void sendSubscriptionCreatedNotification(Subscription subscription) {
    if (subscription == null) {
        log.error("Cannot send subscription created notification: subscription is null");
        return;
    }
    
    if (subscription.getUser() == null) {
        log.error("Cannot send subscription created notification: user is null for subscription ID: {}", subscription.getId());
        return;
    }
    
    log.info("Sending subscription created notification for subscription ID: {}, user: {}", 
            subscription.getId(), subscription.getUser().getEmail());
    
    User user = subscription.getUser();
    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("user", user);
    templateVariables.put("subscription", subscription);
    templateVariables.put("propertyCount", 0);
    templateVariables.put("appUrl", appUrl);
    templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
    templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
    templateVariables.put("planName", subscription.getPlan().getName());
    templateVariables.put("planPrice", subscription.getPrice());
    
    String subject = "Your NearProp Subscription Has Been Created";
    String template = "subscription-created";
    
    sendEmail(user.getEmail(), subject, template, templateVariables);
}
```

3. Added a call to this method in `SubscriptionServiceImpl.createSubscription()`:
```java
// Send subscription creation email notification
try {
    subscriptionEmailService.sendSubscriptionCreatedNotification(savedSubscription);
    log.info("Subscription creation notification email sent for subscription ID: {}", savedSubscription.getId());
} catch (Exception e) {
    log.error("Failed to send subscription creation notification email: {}", e.getMessage(), e);
}
```

## Manual Fix for Existing Properties

For existing properties that need to be activated with an existing subscription, use the provided script:

```bash
./test-fix-property-activation.sh
```

This script will:
1. Prompt for a property ID
2. Prompt for a subscription ID
3. Call the API to activate the property with the subscription

## Testing

To test the automatic property activation when creating a new subscription:

```bash
./test-property-subscription-activation.sh
```

This script will:
1. Create a test property
2. Check if it's inactive
3. Create a subscription
4. Check if the property is automatically activated
5. Check logs for email notifications

## Email Templates

All necessary email templates are available in `src/main/resources/templates/email/`:
- subscription-created.html
- subscription-renewed.html
- subscription-expired.html
- subscription-cancelled.html
- subscription-expiry-warning.html
- property-created.html
- property-activated.html
- property-deactivated.html
- property-updated.html
- property-approved.html
- property-rejected.html
- property-approval-request.html 