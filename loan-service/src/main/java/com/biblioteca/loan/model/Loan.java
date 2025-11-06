package com.biblioteca.loan.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

/**
 * Record que representa un préstamo de libro
 */
@Table("loans")
public record Loan(
    @Id Long id,
    @Column("book_id") Long bookId,
    @Column("user_email") String userEmail,
    @Column("user_name") String userName,
    @Column("loan_date") LocalDate loanDate,
    @Column("due_date") LocalDate dueDate,
    @Column("return_date") LocalDate returnDate,
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

