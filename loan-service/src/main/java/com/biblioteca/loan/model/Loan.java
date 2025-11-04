package com.biblioteca.loan.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

/**
 * Record que representa un préstamo de libro
 */
@Table("loans")
public record Loan(
    @Id Long id,
    Long bookId,
    String userEmail,
    String userName,
    LocalDate loanDate,
    LocalDate dueDate,
    LocalDate returnDate,
    LoanStatus status,
    String notes
) {
    // Constructor para crear nuevos préstamos (sin ID)
    public Loan(Long bookId, String userEmail, String userName, 
                LocalDate loanDate, LocalDate dueDate, LoanStatus status, String notes) {
        this(null, bookId, userEmail, userName, loanDate, dueDate, null, status, notes);
    }

    // Método para actualizar el estado
    public Loan withStatus(LoanStatus newStatus) {
        return new Loan(id, bookId, userEmail, userName, loanDate, dueDate, 
                       returnDate, newStatus, notes);
    }

    // Método para marcar como devuelto
    public Loan withReturn(LocalDate returnDate) {
        return new Loan(id, bookId, userEmail, userName, loanDate, dueDate, 
                       returnDate, LoanStatus.RETURNED, notes);
    }

    // Verificar si el préstamo está vencido
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               LocalDate.now().isAfter(dueDate) && 
               returnDate == null;
    }
}

