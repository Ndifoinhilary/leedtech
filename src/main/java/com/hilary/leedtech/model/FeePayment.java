package com.hilary.leedtech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal paymentAmount;

    private BigDecimal incentiveRate;

    private BigDecimal incentiveAmount;

    private LocalDate paymentDate;

    @ManyToOne
    private StudentAccount studentAccount;
}
