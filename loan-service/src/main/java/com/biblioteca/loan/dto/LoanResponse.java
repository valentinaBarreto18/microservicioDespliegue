package com.biblioteca.loan.dto;

import com.biblioteca.loan.model.Loan;
import com.biblioteca.loan.model.LoanStatus;
import java.time.LocalDate;

/**
 * DTO Record para respuestas de préstamo
 */
public record LoanResponse(
    Long id,
    Long bookId,
    String userEmail,
    String userName,
    LocalDate loanDate,
    LocalDate dueDate,
    LocalDate returnDate,
    LoanStatus status,
    String notes,
    Boolean isOverdue
) {
    // Constructor desde entidad Loan
    public static LoanResponse fromLoan(Loan loan) {
        return new LoanResponse(
            loan.id(),
            loan.bookId(),
            loan.userEmail(),
            loan.userName(),
            loan.loanDate(),
            loan.dueDate(),
            loan.returnDate(),
            loan.status(),
            loan.notes(),
            loan.isOverdue()
        );
    }
}

