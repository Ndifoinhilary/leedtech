# LeedTech — Student Fee One-Time Payment API

A Spring Boot REST API that processes one-time student fee payments, applies an institutional incentive (matching) credit, and calculates the next payment due date.

---

## Tech Stack

| Layer         | Technology                          |
|---------------|-------------------------------------|
| Language      | Java 21                             |
| Framework     | Spring Boot 4.0.3                   |
| Build Tool    | Maven (wrapper included)            |
| Database      | H2 (in-memory)                      |
| Migrations    | Flyway                              |
| Validation    | Jakarta Bean Validation (Hibernate) |
| ORM           | Spring Data JPA / Hibernate         |
| Testing       | JUnit 5, Mockito, AssertJ           |

---

## Prerequisites

- **Java 21** (or later) installed verify with `java -version`
- No separate database installation required (H2 runs in-memory)
- Maven wrapper is included no Maven installation needed

---

## How to Run the Application

```bash
# Clone the repository
git clone git@github.com:Ndifoinhilary/leedtech.git (using ssh)

git clone https://github.com/Ndifoinhilary/leedtech.git (using https)

cd leedtech

cd backend
# Build and start (Linux / macOS)
./mvnw spring-boot:run

# Build and start (Windows)
mvnw.cmd spring-boot:run

cd frontend
# Build and start (Linux / macOS)
npm install
npm ng s
```

The application starts on **http://localhost:8080**.

### H2 Console

Once running, the in-memory database can be inspected at:

```
http://localhost:8080/h2-console
```

| Setting        | Value                    |
|----------------|--------------------------|
| JDBC URL       | `jdbc:h2:mem:studentdb`  |
| Username       | `sa`                     |
| Password       | *(empty)*                |

---

## How to Run the Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=IncentiveCalculatorTest
./mvnw test -Dtest=PaymentDueDateCalculatorTest
./mvnw test -Dtest=FeePaymentServiceImplTest
```

### Test Coverage Summary

| Test Class                    | What It Covers                                                   |
|-------------------------------|------------------------------------------------------------------|
| `IncentiveCalculatorTest`     | All three incentive tiers, boundary values (99999.99, 100000, 499999.99, 500000), doc examples |
| `PaymentDueDateCalculatorTest`| 90-day calculation, Saturday → Monday adjustment, Sunday → Monday adjustment, brute-force weekday guarantee for 365 days, doc examples |
| `FeePaymentServiceImplTest`   | End-to-end payment processing, balance reduction (payment + incentive), date defaulting, persistence verification, boundary tiers, unknown student handling, payment history |

---

## API Endpoints

### `POST /one-time-fee-payment`

Processes a one-time fee payment for a student.

**Request Body:**

```json
{
  "studentNumber": "STU-001",
  "paymentAmount": 100000,
  "paymentDate": "2026-03-14"
}
```

| Field           | Type       | Required | Notes                                          |
|-----------------|------------|----------|-------------------------------------------------|
| `studentNumber` | `String`   | Yes      | Must not be blank                               |
| `paymentAmount` | `Number`   | Yes      | Must be greater than 0                          |
| `paymentDate`   | `Date`     | No       | ISO format (`yyyy-MM-dd`). Defaults to today if omitted |

**Response (200 OK):**

```json
{
  "studentNumber": "STU-001",
  "previousBalance": 800000,
  "paymentAmount": 100000,
  "incentiveRate": 0.03,
  "incentiveAmount": 3000,
  "newBalance": 697000,
  "nextPaymentDueDate": "2026-06-12"
}
```

**Error Responses:**

| Status | Condition                           |
|--------|-------------------------------------|
| 400    | Missing/blank student number        |
| 400    | Null, zero, or negative payment     |
| 404    | Student number not found            |

---

### `GET /one-time-fee-payment/history?studentNumber=STU-001`

Returns the payment history for a student.

---

## Business Rules

### Fee Incentive (Matching Program)

The institution provides an incentive credit based on the payment amount:

| Payment Amount          | Incentive Rate |
|-------------------------|----------------|
| 0 < x < 100,000        | 1%             |
| 100,000 ≤ x < 500,000  | 3%             |
| x ≥ 500,000             | 5%             |

**Formula:**

```
incentive = paymentAmount × matchRate
newBalance = previousBalance − (paymentAmount + incentive)
```

### Next Payment Due Date

- Calculated as **90 days** from the payment date.
- If the due date falls on **Saturday**, it is moved to the following **Monday** (+2 days).
- If the due date falls on **Sunday**, it is moved to the following **Monday** (+1 day).
- Due dates never fall on a weekend.

### Worked Examples

**Example 1** — Payment of 100K on March 14, 2026:

```
Initial Balance : 800,000
Payment Amount  : 100,000
Match Tier      : 3%
Incentive       : 100,000 × 0.03 = 3,000
New Balance     : 800,000 − (100,000 + 3,000) = 697,000
Due Date        : March 14 + 90 days = June 12, 2026 (Friday — no adjustment)
```

**Example 2** — Payment of 575K on April 5, 2026:

```
Initial Balance : 800,000
Payment Amount  : 575,000
Match Tier      : 5%
Incentive       : 575,000 × 0.05 = 28,750
New Balance     : 800,000 − (575,000 + 28,750) = 196,250
Due Date        : April 5 + 90 days = July 4, 2026 (Saturday → adjusted to Monday July 6, 2026)
```

---

## Project Structure

```
src/main/java/com/hilary/leedtech/
├── LeedTechApplication.java            # Application entry point
├── controller/
│   └── FeePaymentController.java       # REST endpoint (POST + GET)
├── dto/
│   ├── FeePaymentRequest.java          # Inbound request with Bean Validation
│   └── FeePaymentResponse.java         # Outbound response
├── exception/
│   ├── ErrorResponse.java              # Structured error body
│   ├── GlobalExceptionHandle.java      # Centralised @RestControllerAdvice
│   └── ResourceNotFoundException.java  # 404 domain exception
├── model/
│   ├── FeePayment.java                 # Payment entity (JPA)
│   └── StudentAccount.java             # Student account entity (JPA)
├── repository/
│   ├── FeePaymentRepository.java       # Spring Data JPA repository
│   └── StudentAccountRepository.java   # Spring Data JPA repository
├── services/
│   ├── FeePaymentService.java          # Service interface
│   └── impl/
│       └── FeePaymentServiceImpl.java  # Business logic implementation
└── utils/
    ├── IncentiveCalculator.java        # Stateless incentive tier + amount calculation
    └── PaymentDueDateCalculator.java   # Stateless 90-day + weekend adjustment logic

src/main/resources/
├── application.yaml                    # Spring Boot configuration
└── db/migration/
    ├── V1__create_schema.sql           # Flyway: table creation
    └── V2__seed_student_accounts.sql   # Flyway: sample student data

src/test/java/com/hilary/leedtech/
├── LeedTechApplicationTests.java       # Context load smoke test
├── services/impl/
│   └── FeePaymentServiceImplTest.java  # Service layer unit tests
└── utils/
    ├── IncentiveCalculatorTest.java    # Tier boundary + amount tests
    └── PaymentDueDateCalculatorTest.java # Weekend adjustment tests
```

---

## Design Decisions & Trade-offs

### Money Handling — `BigDecimal`

All monetary values use `BigDecimal` to avoid floating-point precision errors. Database columns use `DECIMAL(19, 4)` to match.

### Flyway for Schema Management

Flyway runs versioned migrations (`V1__`, `V2__`) on startup. JPA is set to `ddl-auto: validate` so Hibernate only verifies the schema against the entity model — it never creates or alters tables. This ensures the migration scripts are the single source of truth for the database schema.

### Stateless Utility Classes

`IncentiveCalculator` and `PaymentDueDateCalculator` are pure static utility classes with no Spring dependencies. This makes them trivial to unit-test in isolation without requiring a Spring context.

### Payment Date — Optional Input

The spec says *"Use the current system date as the payment date (or allow it to be passed optionally)"*. The `paymentDate` field is optional in the request. When omitted, it defaults to `LocalDate.now()`.

### H2 In-Memory Database

H2 is used for simplicity and zero-config setup. The database is re-created on every application restart. For production, this would be swapped for PostgreSQL or MySQL via a different Spring profile.

### Balance Can Go Negative

The current implementation does not reject payments that exceed the outstanding balance. This is intentional  overpayment handling was not specified in the requirements and is left as a future enhancement.

### Incentive Tier Boundaries

The tiers use strict fewer-than comparisons matching the spec:
- `0 < x < 100K` → 1% (exclusive upper bound: `x < 100,000`)
- `100K ≤ x < 500K` → 3% (inclusive lower, exclusive upper)
- `x ≥ 500K` → 5% (inclusive lower)

This means a payment of exactly 100,000 gets the 3% rate, and exactly 500,000 gets the 5% rate.

---

## Seeded Test Data

Flyway seeds two student accounts on startup:

| Student Number | Tuition Fee | Initial Balance |
|----------------|-------------|-----------------|
| `STU-001`      | 800,000     | 800,000         |
| `STU-002`      | 1,000,000   | 1,000,000       |

---

## Quick Smoke Test (cURL)

```bash
# Make a payment
curl -X POST http://localhost:8080/one-time-fee-payment \
  -H "Content-Type: application/json" \
  -d '{"studentNumber":"STU-001","paymentAmount":100000,"paymentDate":"2026-03-14"}'

# Check payment history
curl http://localhost:8080/one-time-fee-payment/history?studentNumber=STU-001
```

