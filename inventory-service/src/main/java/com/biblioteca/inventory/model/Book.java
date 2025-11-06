package com.biblioteca.inventory.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Record que representa un libro en el inventario
 */
@Table("books")
public record Book(
    @Id Long id,
    String isbn,
    String title,
    String author,
    String publisher,
    @Column("publication_year") Integer publicationYear,
    String category,
    @Column("total_copies") Integer totalCopies,
    @Column("available_copies") Integer availableCopies,
    String description
) {
    // Constructor para creación de nuevos libros (sin ID)
    public Book(String isbn, String title, String author, String publisher, 
                Integer publicationYear, String category, Integer totalCopies, 
                Integer availableCopies, String description) {
        this(null, isbn, title, author, publisher, publicationYear, category, 
             totalCopies, availableCopies, description);
    }

    // Método para actualizar copias disponibles
    public Book withAvailableCopies(Integer newAvailableCopies) {
        return new Book(id, isbn, title, author, publisher, publicationYear, 
                       category, totalCopies, newAvailableCopies, description);
    }

    // Método para actualizar información completa
    public Book withId(Long newId) {
        return new Book(newId, isbn, title, author, publisher, publicationYear, 
                       category, totalCopies, availableCopies, description);
    }
}

