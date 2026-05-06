# Franchisee Revenue and Withdrawal System Guide

This document outlines the franchisee revenue distribution and emergency withdrawal system implemented in the NearProp application.

## Revenue Distribution System

The revenue distribution system works as follows:

1. **Subscription Revenue Split**:
   - For subscription payments, 50% of the revenue goes to the franchisee of the district where the subscription was purchased
   - 50% goes to the admin/company
   - The system tracks both the number of subscribers and the monetary amount

2. **Other Revenue Types**:
   - For non-subscription revenues (property listings, etc.), the revenue share percentage is configured per franchisee district
   - Typically this is set to 60% for franchisee and 40% for admin

3. **Revenue Recording**:
   - Revenue is recorded through the `DistrictRevenueService.recordRevenue()` method
   - The service automatically calculates the correct split based on revenue type

## Monthly Payment Process

At the end of each month, the admin sends the earned revenue to the franchise's bank account:

1. The admin can view pending payments for each franchisee through the district revenue API
2. After transferring the funds, the admin updates the payment status using `DistrictRevenueService.updatePaymentStatus()`
3. The payment reference and date are recorded for tracking purposes

## Emergency Withdrawal Feature

Franchisees can request emergency withdrawals within a month with the following restrictions:

1. **Withdrawal Limit**:
   - Franchisees can withdraw up to 30% of their total pending earnings from subscriptions
   - The available balance is calculated based on pending commission from subscriptions minus any already withdrawn amounts this month

2. **Request Process**:
   - Franchisee submits a withdrawal request with reason and amount
   - Admin reviews and can approve, reject, or mark as paid
   - Each request tracks its status through the workflow

## API Endpoints

### Franchisee Endpoints
- `POST /api/franchisee/withdrawal/request` - Create a new withdrawal request
- `GET /api/franchisee/withdrawal/{id}` - Get a specific withdrawal request
- `GET /api/franchisee/withdrawal/franchisee` - Get all withdrawal requests for the current franchisee
- `GET /api/franchisee/withdrawal/balance/{franchiseeDistrictId}` - Check available balance for withdrawal

### Admin Endpoints
- `GET /api/franchisee/withdrawal/admin/status/{status}` - Get withdrawal requests by status
- `PUT /api/franchisee/withdrawal/admin/process/{id}` - Process a withdrawal request

## Database Schema

The system uses the following key tables:

1. **district_revenues**:
   - Tracks all revenue transactions for districts
   - Includes fields for franchisee's commission and admin's revenue
   - Records subscription IDs for subscription-based revenue

2. **franchisee_withdrawal_requests**:
   - Records withdrawal requests from franchisees
   - Includes status, requested amount, reason
   - Tracks admin approvals and payment details

## Testing

Use the provided `test_franchisee_withdrawal.sh` script to test the functionality:

1. Ensure there's a `@tokens.json` file with valid admin and franchisee tokens
2. Make sure the server is running
3. Run `./test_franchisee_withdrawal.sh`
4. The script will test the full workflow of creating, approving, and paying a withdrawal request

## Implementation Notes

1. The system calculates correct revenue share percentages automatically based on revenue type
2. For subscription payments, a fixed 50% split is applied regardless of the franchisee's configured share percentage
3. Withdrawal requests track their status through the standard flow: PENDING → APPROVED → PAID (or REJECTED)
4. The system prevents franchisees from requesting more than their available balance
5. Only one pending withdrawal request is allowed at a time per district
6. The database migration file is `V39__add_franchisee_withdrawal_requests.sql` 