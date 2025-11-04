package com.biblioteca.loan.model;

/**
 * Estados posibles de un préstamo
 */
public enum LoanStatus {
    ACTIVE,      // Préstamo activo
    RETURNED,    // Libro devuelto
    OVERDUE,     // Préstamo vencido
    CANCELLED    // Préstamo cancelado
}

