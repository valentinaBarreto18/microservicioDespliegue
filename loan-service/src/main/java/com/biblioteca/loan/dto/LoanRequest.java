package com.biblioteca.loan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO Record para solicitudes de préstamo
 */
public record LoanRequest(
    @NotNull(message = "El ID del libro es obligatorio")
    Long bookId,
    
    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email debe ser válido")
    String userEmail,
    
    @NotBlank(message = "El nombre del usuario es obligatorio")
    String userName,
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    LocalDate dueDate,
    
    String notes
) {}

