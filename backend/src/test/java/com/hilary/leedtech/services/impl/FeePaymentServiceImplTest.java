package com.hilary.leedtech.services.impl;

import com.hilary.leedtech.dto.FeePaymentRequest;
import com.hilary.leedtech.dto.FeePaymentResponse;
import com.hilary.leedtech.exception.ResourceNotFoundException;
import com.hilary.leedtech.model.FeePayment;
import com.hilary.leedtech.model.StudentAccount;
import com.hilary.leedtech.repository.FeePaymentRepository;
import com.hilary.leedtech.repository.StudentAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeePaymentServiceImplTest {

    @Mock
    private FeePaymentRepository feePaymentRepository;

    @Mock
    private StudentAccountRepository studentAccountRepository;

    @InjectMocks
    private FeePaymentServiceImpl feePaymentService;

    private StudentAccount studentAccount;

    @BeforeEach
    void setUp() {
        studentAccount = new StudentAccount();
        studentAccount.setId(1L);
        studentAccount.setStudentNumber("STU-001");
        studentAccount.setTuitionFee(new BigDecimal("800000"));
        studentAccount.setBalance(new BigDecimal("800000"));
        studentAccount.setNextPaymentDueDate(null);
    }



    @Nested
    @DisplayName("processPayment")
    class ProcessPaymentTests {

        @Test
        @DisplayName("Doc Example 1: 100K payment on March 14 2026, balance 800K → new balance 697K, due June 12")
        void docExample1() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("100000"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getStudentNumber()).isEqualTo("STU-001");
            assertThat(response.getPreviousBalance()).isEqualByComparingTo("800000");
            assertThat(response.getPaymentAmount()).isEqualByComparingTo("100000");
            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.03");
            assertThat(response.getIncentiveAmount()).isEqualByComparingTo("3000");
            assertThat(response.getNewBalance()).isEqualByComparingTo("697000");
            assertThat(response.getNextPaymentDueDate()).isEqualTo(LocalDate.of(2026, 6, 12));
        }

        @Test
        @DisplayName("Doc Example 2: 575K payment on April 5 2026, balance 800K → new balance 196,250, due July 6 (Saturday adj.)")
        void docExample2() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 4, 5), new BigDecimal("575000"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getPreviousBalance()).isEqualByComparingTo("800000");
            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.05");
            assertThat(response.getIncentiveAmount()).isEqualByComparingTo("28750");
            assertThat(response.getNewBalance()).isEqualByComparingTo("196250");
            assertThat(response.getNextPaymentDueDate()).isEqualTo(LocalDate.of(2026, 7, 6));
        }

        @Test
        @DisplayName("Tier 1 payment: 50K → 1% incentive, balance reduced by 50,500")
        void tierOnePayment() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("50000"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.01");
            assertThat(response.getIncentiveAmount()).isEqualByComparingTo("500");
            assertThat(response.getNewBalance()).isEqualByComparingTo("749500");
        }

        @Test
        @DisplayName("Payment date defaults to current date when not provided")
        void defaultsToCurrentDate() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", null, new BigDecimal("100000"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            ArgumentCaptor<FeePayment> captor = ArgumentCaptor.forClass(FeePayment.class);
            verify(feePaymentRepository).save(captor.capture());
            assertThat(captor.getValue().getPaymentDate()).isEqualTo(LocalDate.now());

            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("Student account balance is persisted correctly")
        void persistsUpdatedBalance() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("100000"));

            feePaymentService.processPayment(request);

            ArgumentCaptor<StudentAccount> captor = ArgumentCaptor.forClass(StudentAccount.class);
            verify(studentAccountRepository).save(captor.capture());

            assertThat(captor.getValue().getBalance()).isEqualByComparingTo("697000");
            assertThat(captor.getValue().getNextPaymentDueDate()).isEqualTo(LocalDate.of(2026, 6, 12));
        }

        @Test
        @DisplayName("Throws ResourceNotFoundException for unknown student")
        void unknownStudent_throwsException() {
            when(studentAccountRepository.findByStudentNumber("UNKNOWN")).thenReturn(Optional.empty());

            FeePaymentRequest request = new FeePaymentRequest("UNKNOWN", null, new BigDecimal("100000"));

            assertThatThrownBy(() -> feePaymentService.processPayment(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student account not found");
        }

        @Test
        @DisplayName("Boundary: payment of exactly 99,999.99 → tier 1 (1%)")
        void boundary_justBelowTierTwo() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("99999.99"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.01");
        }

        @Test
        @DisplayName("Boundary: payment of exactly 100,000.00 → tier 2 (3%)")
        void boundary_exactlyAtTierTwo() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("100000.00"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.03");
        }

        @Test
        @DisplayName("Boundary: payment of exactly 499,999.99 → tier 2 (3%)")
        void boundary_justBelowTierThree() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("499999.99"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.03");
        }

        @Test
        @DisplayName("Boundary: payment of exactly 500,000.00 → tier 3 (5%)")
        void boundary_exactlyAtTierThree() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(studentAccountRepository.save(any())).thenReturn(studentAccount);
            when(feePaymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FeePaymentRequest request = new FeePaymentRequest("STU-001", LocalDate.of(2026, 3, 14), new BigDecimal("500000.00"));

            FeePaymentResponse response = feePaymentService.processPayment(request);

            assertThat(response.getIncentiveRate()).isEqualByComparingTo("0.05");
        }
    }



    @Nested
    @DisplayName("getPaymentHistory")
    class GetPaymentHistoryTests {

        @Test
        @DisplayName("Returns empty list when student has no payments")
        void noPayments_returnsEmptyList() {
            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(feePaymentRepository.findByStudentAccountStudentNumber("STU-001")).thenReturn(List.of());

            List<FeePaymentResponse> history = feePaymentService.getPaymentHistory("STU-001");

            assertThat(history).isEmpty();
        }

        @Test
        @DisplayName("Returns payment history for valid student")
        void validStudent_returnsHistory() {
            FeePayment payment = FeePayment.builder()
                    .id(1L)
                    .paymentAmount(new BigDecimal("100000"))
                    .incentiveRate(new BigDecimal("0.03"))
                    .incentiveAmount(new BigDecimal("3000"))
                    .paymentDate(LocalDate.of(2026, 3, 14))
                    .studentAccount(studentAccount)
                    .build();

            when(studentAccountRepository.findByStudentNumber("STU-001")).thenReturn(Optional.of(studentAccount));
            when(feePaymentRepository.findByStudentAccountStudentNumber("STU-001")).thenReturn(List.of(payment));

            List<FeePaymentResponse> history = feePaymentService.getPaymentHistory("STU-001");

            assertThat(history).hasSize(1);
            assertThat(history.getFirst().getPaymentAmount()).isEqualByComparingTo("100000");
            assertThat(history.getFirst().getIncentiveRate()).isEqualByComparingTo("0.03");
        }

        @Test
        @DisplayName("Throws ResourceNotFoundException for unknown student")
        void unknownStudent_throwsException() {
            when(studentAccountRepository.findByStudentNumber("UNKNOWN")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> feePaymentService.getPaymentHistory("UNKNOWN"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

