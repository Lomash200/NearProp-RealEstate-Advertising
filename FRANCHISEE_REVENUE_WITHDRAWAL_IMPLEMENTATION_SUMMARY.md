# Franchisee Revenue and Withdrawal System - Implementation Summary

## Overview

This document summarizes the implementation of the franchisee revenue distribution and emergency withdrawal system in the NearProp application. The system allows for:

1. 50% revenue sharing for franchisees from subscriptions in their district
2. Monthly payments to franchisees by admin
3. Emergency withdrawal requests (up to 30% of pending revenue) by franchisees

## Files Created/Modified

### New Entities and DTOs:

1. **FranchiseeWithdrawalRequest.java**
   - Entity for tracking withdrawal requests
   - Contains status tracking, requested amount, reason, and payment details

2. **WithdrawalRequestDto.java**
   - DTO for transferring withdrawal request data
   - Includes franchisee and district information

3. **CreateWithdrawalRequestDto.java**
   - DTO for creating new withdrawal requests
   - Validates input data with annotations

4. **WithdrawalRequestResponseDto.java**
   - DTO for admin responses to withdrawal requests
   - Contains status updates and payment details

5. **FranchiseeDistrictSummaryDto.java**
   - Lightweight DTO for district information in responses

### Services:

1. **WithdrawalRequestService.java**
   - Interface defining withdrawal management operations
   - Methods for creating, retrieving, and processing withdrawal requests

2. **WithdrawalRequestServiceImpl.java**
   - Implementation of withdrawal service
   - Contains business logic for calculating available balance
   - Enforces 30% withdrawal limit
   - Validates request eligibility

### Repository:

1. **WithdrawalRequestRepository.java**
   - JPA repository for withdrawal requests
   - Contains custom queries for calculating balances and finding requests

### Controller:

1. **WithdrawalRequestController.java**
   - REST endpoints for withdrawal management
   - Secured with proper role-based access control
   - Endpoints for both franchisee and admin operations

### Modified Files:

1. **DistrictRevenueServiceImpl.java**
   - Updated to use 50% revenue share for subscription payments
   - Added constant for subscription revenue share percentage

2. **DistrictRevenueRepository.java**
   - Added methods for calculating subscription-specific revenue

### Database Migration:

1. **V39__add_franchisee_withdrawal_requests.sql**
   - Creates the franchisee_withdrawal_requests table
   - Adds necessary indexes for performance

## Testing

A comprehensive test script (`test_franchisee_withdrawal.sh`) was created to test the functionality:

1. Checks available withdrawal balance
2. Creates a withdrawal request
3. Tests admin approval process
4. Tests payment recording
5. Verifies withdrawal history

## Key Business Logic

1. **Revenue Calculation**:
   - For subscription payments: 50% to franchisee, 50% to admin
   - For other revenue types: Uses configured percentage (typically 60/40)

2. **Withdrawal Limits**:
   - Maximum 30% of pending subscription revenue
   - Tracks already withdrawn amounts in current month
   - Prevents multiple pending requests

3. **Request Workflow**:
   - PENDING → APPROVED → PAID (or REJECTED)
   - Admin must provide payment reference when marking as paid

## How to Test

1. Ensure the server is running
2. Configure `@tokens.json` with valid admin and franchisee tokens
3. Run `./test_franchisee_withdrawal.sh`
4. The script will walk through the entire workflow

## Implementation Notes

1. The system calculates correct revenue share percentages automatically based on revenue type
2. For subscription payments, a fixed 50% split is applied regardless of the franchisee's configured share percentage
3. Withdrawal requests track their status through the standard flow: PENDING → APPROVED → PAID (or REJECTED)
4. The system prevents franchisees from requesting more than their available balance
5. Only one pending withdrawal request is allowed at a time per district

## Future Enhancements

1. Add email notifications for withdrawal status changes
2. Implement automatic monthly payments
3. Add reporting features for revenue and withdrawal history 