# Razorpay Integration Guide

This guide provides instructions for integrating Razorpay payment gateway in frontend applications for NearProp.

## Credentials

The following test credentials are configured in the backend:

- Key ID: `rzp_test_LoJiA2mTb0THiq`
- Key Secret: `NlSHoXdS7mqWTIUHXfFR2LaI`

**Note:** These are test credentials and should only be used in development/testing environments.

## Backend Integration

The backend is already configured with these credentials. The payment flow is as follows:

1. Frontend calls the `/payments/initiate` endpoint to create a payment order
2. Backend creates an order in Razorpay and returns the order details
3. Frontend displays the Razorpay checkout form
4. After payment, frontend calls the `/payments/verify` endpoint to verify the payment

## Frontend Integration

### Web Integration

1. Add Razorpay checkout script to your HTML:

```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

2. Implement the payment flow:

```javascript
// Step 1: Initiate payment from your server
async function initiatePayment(amount, currency, paymentType) {
  const response = await fetch('/api/payments/initiate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${userToken}`
    },
    body: JSON.stringify({
      amount,
      currency,
      paymentType,
      // Add other required fields based on payment type
    })
  });
  
  const result = await response.json();
  return result.data; // Contains referenceId, gatewayOrderId, etc.
}

// Step 2: Open Razorpay checkout
function openRazorpayCheckout(paymentData) {
  const options = {
    key: 'rzp_test_LoJiA2mTb0THiq',
    amount: paymentData.amount * 100, // Amount in smallest currency unit (paise)
    currency: paymentData.currency,
    name: 'NearProp',
    description: 'Payment for services',
    order_id: paymentData.paymentToken,
    handler: function(response) {
      // Step 3: Verify payment
      verifyPayment(
        paymentData.referenceId,
        response.razorpay_payment_id,
        response.razorpay_order_id,
        response.razorpay_signature
      );
    },
    prefill: {
      name: userDetails.name,
      email: userDetails.email,
      contact: userDetails.mobileNumber
    },
    theme: {
      color: '#3399cc'
    }
  };
  
  const rzp = new Razorpay(options);
  rzp.open();
}

// Step 3: Verify payment with your server
async function verifyPayment(referenceId, gatewayTransactionId, gatewayOrderId, paymentSignature) {
  const response = await fetch('/api/payments/verify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${userToken}`
    },
    body: JSON.stringify({
      referenceId,
      gatewayTransactionId,
      gatewayOrderId,
      paymentSignature
    })
  });
  
  const result = await response.json();
  if (result.success) {
    // Payment successful, show success message or redirect
    showSuccessMessage(result.data);
  } else {
    // Payment failed, show error message
    showErrorMessage(result.message);
  }
}

// Example usage
async function makePayment() {
  try {
    const paymentData = await initiatePayment(999.00, 'INR', 'SUBSCRIPTION');
    openRazorpayCheckout(paymentData);
  } catch (error) {
    console.error('Payment initiation failed:', error);
    showErrorMessage('Failed to initiate payment. Please try again.');
  }
}
```

### React Native Integration

For mobile apps, use the [react-native-razorpay](https://www.npmjs.com/package/react-native-razorpay) package:

1. Install the package:

```bash
npm install react-native-razorpay
```

2. Implement the payment flow:

```javascript
import RazorpayCheckout from 'react-native-razorpay';

// Step 1: Initiate payment from your server (same as web)
async function initiatePayment(amount, currency, paymentType) {
  // Same as web implementation
}

// Step 2: Open Razorpay checkout
function openRazorpayCheckout(paymentData) {
  const options = {
    description: 'Payment for services',
    image: 'https://your-app-logo.png',
    currency: paymentData.currency,
    key: 'rzp_test_LoJiA2mTb0THiq',
    amount: paymentData.amount * 100,
    name: 'NearProp',
    order_id: paymentData.paymentToken,
    prefill: {
      email: userDetails.email,
      contact: userDetails.mobileNumber,
      name: userDetails.name
    },
    theme: { color: '#3399cc' }
  };
  
  RazorpayCheckout.open(options)
    .then((data) => {
      // Step 3: Verify payment
      verifyPayment(
        paymentData.referenceId,
        data.razorpay_payment_id,
        data.razorpay_order_id,
        data.razorpay_signature
      );
    })
    .catch((error) => {
      console.log(`Payment error: ${error.code} | ${error.description}`);
      showErrorMessage('Payment failed. Please try again.');
    });
}

// Step 3: Verify payment with your server (same as web)
async function verifyPayment(referenceId, gatewayTransactionId, gatewayOrderId, paymentSignature) {
  // Same as web implementation
}
```

## Testing

1. Use the test credentials provided above
2. For test payments, use the following cards:
   - Card Number: 4111 1111 1111 1111
   - Expiry: Any future date
   - CVV: Any 3 digits
   - Name: Any name
3. For UPI, use `success@razorpay` as the UPI ID

## Troubleshooting

If payments are not showing up in the Razorpay dashboard:

1. Ensure the correct credentials are being used
2. Check that the payment flow is complete (initiate → checkout → verify)
3. Verify that the backend is correctly communicating with Razorpay API
4. Check the application logs for any errors

## Going Live

When ready to go live:

1. Replace test credentials with production credentials
2. Update the configuration in `application.properties`
3. Update the key in the frontend code
4. Test the entire flow in the production environment with a small amount
