package com.biblioteca.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO Record para recibir solicitudes de creación/actualización de libros
 */
public record BookRequest(
    @NotBlank(message = "El ISBN es obligatorio")
    String isbn,
    
    @NotBlank(message = "El título es obligatorio")
    String title,
    
    @NotBlank(message = "El autor es obligatorio")
    String author,
    
    String publisher,
    
    @Min(value = 1000, message = "El año de publicación debe ser válido")
    Integer publicationYear,
    
    String category,
    
    @NotNull(message = "El total de copias es obligatorio")
    @Min(value = 1, message = "Debe haber al menos una copia")
    Integer totalCopies,
    
    @NotNull(message = "Las copias disponibles son obligatorias")
    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    Integer availableCopies,
    
    String description
) {}

