# TODO - Fix Transfer dropdown + Admin customer accounts visibility

## Step 1: Transfer page dropdown fix
- [x] Add debugging logs for `customerId` and API response in `frontend/src/app/pages/transfer/transfer.ts`.
- [x] Improve UI messaging when accounts list is empty.


## Step 2: Admin page (customers list + click to show accounts)
- [x] Update `frontend/src/app/pages/admin/admin.ts` to store `selectedCustomer` and derive/filter accounts for that customer.
- [x] Update `frontend/src/app/pages/admin/admin.html` to render customers and show selected customer accounts.



## Step 3: DB verification instructions
- [x] Provide SQL queries for MySQL `banking_db` to confirm data stored (customers/accounts/transactions).


## Step 4: Testing
- [x] Run backend + frontend.
- [x] Verify Transfer page shows accounts in dropdown.
- [x] Verify Admin shows all customers and clicking a customer shows their accounts.

