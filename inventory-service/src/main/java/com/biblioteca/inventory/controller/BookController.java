package com.biblioteca.inventory.controller;

import com.biblioteca.inventory.dto.BookRequest;
import com.biblioteca.inventory.dto.BookResponse;
import com.biblioteca.inventory.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST reactivo para gestionar libros
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * Obtener todos los libros
     * GET /api/books
     */
    @GetMapping
    public Flux<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Obtener libro por ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookResponse>> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Obtener libro por ISBN
     * GET /api/books/isbn/{isbn}
     */
    @GetMapping("/isbn/{isbn}")
    public Mono<ResponseEntity<BookResponse>> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Buscar libros por autor
     * GET /api/books/search/author?name=autor
     */
    @GetMapping("/search/author")
    public Flux<BookResponse> searchByAuthor(@RequestParam String name) {
        return bookService.searchByAuthor(name);
    }

    /**
     * Buscar libros por título
     * GET /api/books/search/title?name=titulo
     */
    @GetMapping("/search/title")
    public Flux<BookResponse> searchByTitle(@RequestParam String name) {
        return bookService.searchByTitle(name);
    }

    /**
     * Obtener libros por categoría
     * GET /api/books/category/{category}
     */
    @GetMapping("/category/{category}")
    public Flux<BookResponse> getBooksByCategory(@PathVariable String category) {
        return bookService.getBooksByCategory(category);
    }

    /**
     * Obtener libros disponibles
     * GET /api/books/available
     */
    @GetMapping("/available")
    public Flux<BookResponse> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }

    /**
     * Verificar disponibilidad de un libro
     * GET /api/books/{id}/availability
     */
    @GetMapping("/{id}/availability")
    public Mono<ResponseEntity<Boolean>> checkAvailability(@PathVariable Long id) {
        return bookService.checkAvailability(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo libro
     * POST /api/books
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    /**
     * Actualizar un libro
     * PUT /api/books/{id}
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return bookService.updateBook(id, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un libro
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id)
                .map(v -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Decrementar copias disponibles (para préstamo)
     * POST /api/books/{id}/decrement
     */
    @PostMapping("/{id}/decrement")
    public Mono<ResponseEntity<BookResponse>> decrementCopies(@PathVariable Long id) {
        return bookService.decrementAvailableCopies(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Incrementar copias disponibles (para devolución)
     * POST /api/books/{id}/increment
     */
    @PostMapping("/{id}/increment")
    public Mono<ResponseEntity<BookResponse>> incrementCopies(@PathVariable Long id) {
        return bookService.incrementAvailableCopies(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

