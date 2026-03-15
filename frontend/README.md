# Frontend

This Angular app provides the UI for the LeedTech one-time student fee payment exercise.

## Prerequisites

- Node.js and npm
- Backend API running on `http://localhost:8080`

## Run the backend

From the project root:

```bash
cd backend
./mvnw spring-boot:run
```

## Run the frontend

From the project root:

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200/`.

## Implemented one-time payment flow

- Route: `/` and `/one-time-fee-payment`
- Form input:
  - Student number (required)
  - Payment amount (required, greater than 0)
  - Payment date (optional; backend defaults to the current date)
- API call: `POST http://localhost:8080/one-time-fee-payment`
- The result section shows:
  - Student number
  - Previous balance
  - Payment amount
  - Incentive rate
  - Incentive amount
  - New balance
  - Next payment due date

## Validation and error handling

- Prevents submit for empty/invalid fields
- Shows validation messages for required fields and minimum amount
- Displays backend validation/domain errors when returned

## Run tests

```bash
cd frontend
npm test
```

## Notes

- Currency values are displayed in USD format for readability.
- Incentive tier calculation and due-date adjustment logic are implemented in backend and consumed as response values in this UI.
