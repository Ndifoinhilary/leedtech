package com.hilary.leedtech.repository;

import com.hilary.leedtech.model.StudentAccount;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {

    Optional<StudentAccount> findByStudentNumber(@NotBlank(message = "Student ID is required") String studentNumber);

}
