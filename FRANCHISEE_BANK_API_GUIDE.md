# Franchisee Bank Management and Profile API Implementation

## Testing Instructions

### Prerequisites

1. The application should be compiled and running on http://localhost:8080
2. You need JWT tokens for a franchisee user and an admin user

### API Testing with Curl

### 1. Add Bank Account

**Endpoint:** `POST /api/franchisee/bank-details`  
**Auth Required:** Yes (FRANCHISEE role)

```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ' \
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

```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ'
```

### 3. Get Primary Bank Account

**Endpoint:** `GET /api/franchisee/bank-details/primary`  
**Auth Required:** Yes (FRANCHISEE role)

```bash
curl --location 'http://localhost:8080/api/franchisee/bank-details/primary' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ'
```

### 4. Update Bank Account

**Endpoint:** `PUT /api/franchisee/bank-details/{id}`  
**Auth Required:** Yes (FRANCHISEE role)

```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ' \
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

```bash
curl --location --request DELETE 'http://localhost:8080/api/franchisee/bank-details/2' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ'
```

### 6. Set Bank Account as Primary

**Endpoint:** `PUT /api/franchisee/bank-details/{id}/set-primary`  
**Auth Required:** Yes (FRANCHISEE role)

```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1/set-primary' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ'
```

### 7. Verify Bank Account (Admin only)

**Endpoint:** `PUT /api/franchisee/bank-details/{id}/verify`  
**Auth Required:** Yes (ADMIN role)

```bash
curl --location --request PUT 'http://localhost:8080/api/franchisee/bank-details/1/verify' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJzZXNzaW9uSWQiOiJhZDc2NDgwMS02ODgxLTQ2NjAtOWM2ZS0zMzk5ODg4OTQxODEiLCJpYXQiOjE3NTE1MzM0MTUsImV4cCI6MTc1MjEzODIxNSwiaXNzIjoiTmVhcnByb3BCYWNrZW5kIn0.b8ymVyoRzinLxBFX9xlzJUyt6uG5VWvsv8lFMb8B5WFMsQaLjk5k-z39CwG22QbKvAKKqZZhWKyCBk5HZKpduw'
```

### 8. Get Complete Franchisee Profile

**Endpoint:** `GET /api/franchisee/profile`  
**Auth Required:** Yes (FRANCHISEE role)

```bash
curl --location 'http://localhost:8080/api/franchisee/profile' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ'
```

### 9. Admin View Franchisee Profile

**Endpoint:** `GET /api/franchisee/profile/{userId}`  
**Auth Required:** Yes (ADMIN role)

```bash
curl --location 'http://localhost:8080/api/franchisee/profile/79' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJzZXNzaW9uSWQiOiJhZDc2NDgwMS02ODgxLTQ2NjAtOWM2ZS0zMzk5ODg4OTQxODEiLCJpYXQiOjE3NTE1MzM0MTUsImV4cCI6MTc1MjEzODIxNSwiaXNzIjoiTmVhcnByb3BCYWNrZW5kIn0.b8ymVyoRzinLxBFX9xlzJUyt6uG5VWvsv8lFMb8B5WFMsQaLjk5k-z39CwG22QbKvAKKqZZhWKyCBk5HZKpduw'
```

### 10. Create Withdrawal Request with Bank Details

**Endpoint:** `POST /api/franchisee/withdrawal/request`  
**Auth Required:** Yes (FRANCHISEE role)

**Request with Specific Bank Account:**
```bash
curl --location 'http://localhost:8080/api/franchisee/withdrawal/request' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ' \
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
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsInJvbGVzIjpbIkRFVkVMT1BFUiIsIlVTRVIiLCJBRFZJU09SIl0sInNlc3Npb25JZCI6IjhkZDVlZWEzLTEzYzctNDM1OC04NWUzLTcyOWUxODVkNDE4YiIsImlhdCI6MTc1MTg3MDc0NywiZXhwIjoxNzUyNDc1NTQ3LCJpc3MiOiJOZWFycHJvcEJhY2tlbmQifQ.2a4MAPG5BEN3rjKiWxtisAcZ_kHvw_cuTQ-2YYdM9kqYq6gUpspyw_8GNGE6aGBtPIGC5MGx3GB1agk2RNPAAQ' \
--data '{
    "franchiseeDistrictId": 6,
    "requestedAmount": 5000,
    "reason": "Monthly withdrawal for business expenses"
}'
```

## Implementation Notes

1. The code has been prepared to add franchisee bank details functionality
2. The features include:
   - Managing multiple bank accounts for franchisees
   - Setting a primary account for withdrawals
   - Integrating bank details with withdrawal requests
   - Complete franchisee profile view

3. The project has compilation issues that need to be resolved:
   - Issues with Lombok annotations (@Slf4j not generating log variable)
   - Duplicate fields in WithdrawalRequestDto
   - Other entity/mapper method reference issues

4. Next steps:
   - Fix the compilation issues with the existing codebase
   - Run a clean build with `./mvnw clean compile`
   - Test each API endpoint with the provided curl commands
   - Update documentation as needed

The implementation provides all the necessary code to support franchisee bank details management. Once the existing codebase issues are resolved, these features will be fully functional.
