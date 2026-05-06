# Franchisee Profile and Bank Management Testing Guide

This guide will help you test the new franchisee profile management and bank details features.

## Prerequisites

1. The application is running on http://localhost:8080
2. You have valid JWT tokens for:
   - A franchisee user
   - An admin user

## Testing Endpoints

### 1. Add Bank Account

**Endpoint:** `POST /api/franchisee/bank-details`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>' \
--data '{
    "accountName": "Rohit Franchise",
    "accountNumber": "1234567890",
    "ifscCode": "SBIN0001234",
    "bankName": "State Bank of India",
    "branchName": "Indore Main Branch",
    "accountType": "Current",
    "upiId": "rohit@sbi",
    "isPrimary": true
}'
```

### 2. Get All Bank Accounts

**Endpoint:** `GET /api/franchisee/bank-details`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>'
```

### 3. Get Primary Bank Account

**Endpoint:** `GET /api/franchisee/bank-details/primary`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details/primary' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>'
```

### 4. Update Bank Account

**Endpoint:** `PUT /api/franchisee/bank-details/{id}`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>' \
--data '{
    "accountName": "Updated Rohit Franchise",
    "accountNumber": "1234567890",
    "ifscCode": "SBIN0001234",
    "bankName": "State Bank of India",
    "branchName": "Indore Main Branch",
    "accountType": "Current",
    "upiId": "rohit.new@sbi",
    "isPrimary": true
}'
```

### 5. Delete Bank Account

**Endpoint:** `DELETE /api/franchisee/bank-details/{id}`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/franchisee/bank-details/2' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>'
```

### 6. Set Bank Account as Primary

**Endpoint:** `PUT /api/franchisee/bank-details/{id}/set-primary`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1/set-primary' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>'
```

### 7. Verify Bank Account (Admin only)

**Endpoint:** `PUT /api/franchisee/bank-details/{id}/verify`  
**Auth Required:** Yes (ADMIN role)

**Request:**
```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1/verify' \
--header 'Authorization: Bearer <ADMIN_JWT_TOKEN>'
```

### 8. Get Complete Franchisee Profile

**Endpoint:** `GET /api/franchisee/profile`  
**Auth Required:** Yes (FRANCHISEE role)

**Request:**
```bash
curl --location 'http://localhost:8080/api/franchisee/profile' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>'
```

### 9. Admin View Franchisee Profile

**Endpoint:** `GET /api/franchisee/profile/{userId}`  
**Auth Required:** Yes (ADMIN role)

**Request:**
```bash
curl --location 'http://localhost:8080/api/franchisee/profile/79' \
--header 'Authorization: Bearer <ADMIN_JWT_TOKEN>'
```

### 10. Create Withdrawal Request with Bank Details

**Endpoint:** `POST /api/franchisee/withdrawal/request`  
**Auth Required:** Yes (FRANCHISEE role)

**Request with Specific Bank Account:**
```bash
curl --location 'http://localhost:8080/api/franchisee/withdrawal/request' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>' \
--data '{
    "franchiseeDistrictId": 6,
    "requestedAmount": 5000,
    "reason": "Monthly withdrawal for business expenses",
    "bankDetailId": 1
}'
```

**Request Using Primary Bank Account:**
```bash
curl --location 'http://localhost:8080/api/franchisee/withdrawal/request' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <FRANCHISEE_JWT_TOKEN>' \
--data '{
    "franchiseeDistrictId": 6,
    "requestedAmount": 5000,
    "reason": "Monthly withdrawal for business expenses"
}'
```

## Testing Flow

1. **Initial Setup:**
   - Login as a franchisee user and add a bank account
   - Verify the bank account is added successfully
   - Set it as primary if needed

2. **Profile Management:**
   - View your franchisee profile
   - Verify bank details are included in the profile

3. **Withdrawal Process:**
   - Create a withdrawal request
   - Verify the request includes the bank details
   - Login as admin to approve the request

## Expected Results

- You should be able to add, update, and manage multiple bank accounts
- Bank details should appear in your franchisee profile
- Bank details should be pre-filled when making withdrawal requests
- Admin should see bank details when approving withdrawal requests
