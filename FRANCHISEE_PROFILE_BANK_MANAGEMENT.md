# Franchisee Profile and Bank Management

This document outlines the implementation of franchisee profile and bank details management in the NearProp application.

## Overview

The following features have been implemented:

1. Complete franchisee profile API that shows all details including:
   - Business information
   - Contact information
   - Bank accounts
   - Assigned districts
   - Revenue statistics

2. Bank details management for franchisees:
   - Add multiple bank accounts
   - Update bank details
   - Delete bank accounts
   - Set a primary bank account
   - Verify bank accounts (admin only)

## API Endpoints

### Franchisee Profile

#### Get Complete Profile (Franchisee)
```
GET /api/franchisee/profile
```
Returns the complete profile of the authenticated franchisee, including all bank details, district assignments, and revenue information.

#### Get Franchisee Profile (Admin)
```
GET /api/franchisee/profile/{userId}
```
Allows administrators to view the complete profile of any franchisee.

### Bank Details Management

#### Add Bank Account
```
POST /api/franchisee/bank-details
```
Add a new bank account with the following details:
- Account name
- Account number
- IFSC code
- Bank name
- Branch name (optional)
- Account type (optional)
- UPI ID (optional)
- Is primary (optional)

#### Get All Bank Accounts
```
GET /api/franchisee/bank-details
```
Retrieve all bank accounts associated with the franchisee.

#### Get Specific Bank Account
```
GET /api/franchisee/bank-details/{id}
```
Retrieve details of a specific bank account.

#### Get Primary Bank Account
```
GET /api/franchisee/bank-details/primary
```
Retrieve the primary bank account of the franchisee.

#### Update Bank Account
```
PUT /api/franchisee/bank-details/{id}
```
Update an existing bank account's details.

#### Delete Bank Account
```
DELETE /api/franchisee/bank-details/{id}
```
Delete a bank account. Note that the primary account cannot be deleted if there are other accounts.

#### Set Bank Account as Primary
```
PUT /api/franchisee/bank-details/{id}/set-primary
```
Set a specific bank account as the primary account.

#### Verify Bank Account (Admin)
```
PUT /api/franchisee/bank-details/{id}/verify
```
Allow administrators to verify a bank account after validation.

## Database Schema

### franchisee_bank_details
- id (PK)
- user_id (FK)
- account_name
- account_number
- ifsc_code
- bank_name
- branch_name
- account_type
- upi_id
- is_primary
- is_verified
- verified_at
- verified_by
- created_at
- updated_at

## Withdrawal Process

When a franchisee requests a withdrawal:

1. Franchisee submits a withdrawal request with amount and reason
2. The request is associated with the franchisee's primary bank account
3. Admin reviews and approves/rejects the request
4. If approved, payment is processed to the primary bank account
5. Admin updates the status to PAID once the transaction is complete
6. The amount is deducted from the franchisee's available balance

## Security

- All endpoints are secured with role-based access control
- Franchisees can only access their own bank details and profile
- Only administrators can verify bank accounts and view all franchisee profiles
