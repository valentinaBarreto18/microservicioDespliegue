package com.biblioteca.inventory.dto;

import com.biblioteca.inventory.model.Book;

/**
 * DTO Record para respuestas de libros
 */
public record BookResponse(
    Long id,
    String isbn,
    String title,
    String author,
    String publisher,
    Integer publicationYear,
    String category,
    Integer totalCopies,
    Integer availableCopies,
    String description,
    Boolean isAvailable
) {
    // Constructor desde entidad Book
    public static BookResponse fromBook(Book book) {
        return new BookResponse(
            book.id(),
            book.isbn(),
            book.title(),
            book.author(),
            book.publisher(),
            book.publicationYear(),
            book.category(),
            book.totalCopies(),
            book.availableCopies(),
            book.description(),
            book.availableCopies() != null && book.availableCopies() > 0
        );
    }
}

