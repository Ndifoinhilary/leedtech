CREATE TABLE student_account
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_number        VARCHAR(50)    NOT NULL UNIQUE,
    tuition_fee           DECIMAL(19, 4) NOT NULL,
    balance               DECIMAL(19, 4) NOT NULL,
    next_payment_due_date DATE
);

CREATE TABLE fee_payment
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_amount     DECIMAL(19, 4) NOT NULL,
    incentive_rate     DECIMAL(5, 4)  NOT NULL,
    incentive_amount   DECIMAL(19, 4) NOT NULL,
    payment_date       DATE           NOT NULL,
    student_account_id BIGINT         NOT NULL,
    CONSTRAINT fk_fee_payment_student FOREIGN KEY (student_account_id) REFERENCES student_account (id)
);

