package com.biblioteca.inventory.repository;

import com.biblioteca.inventory.model.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository reactivo para la entidad Book
 */
@Repository
public interface BookRepository extends R2dbcRepository<Book, Long> {
    
    /**
     * Buscar libro por ISBN
     */
    Mono<Book> findByIsbn(String isbn);
    
    /**
     * Buscar libros por autor
     */
    Flux<Book> findByAuthorContainingIgnoreCase(String author);
    
    /**
     * Buscar libros por título
     */
    Flux<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Buscar libros por categoría
     */
    Flux<Book> findByCategory(String category);
    
    /**
     * Buscar libros disponibles
     */
    @Query("SELECT * FROM books WHERE available_copies > 0")
    Flux<Book> findAvailableBooks();
    
    /**
     * Verificar si un libro está disponible
     */
    @Query("SELECT CASE WHEN available_copies > 0 THEN true ELSE false END FROM books WHERE id = :id")
    Mono<Boolean> isBookAvailable(Long id);
    
    /**
     * Actualizar copias disponibles
     */
    @Query("UPDATE books SET available_copies = :availableCopies WHERE id = :id")
    Mono<Integer> updateAvailableCopies(Long id, Integer availableCopies);
}

