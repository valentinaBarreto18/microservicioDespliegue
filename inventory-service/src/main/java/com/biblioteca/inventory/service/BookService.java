package com.biblioteca.inventory.service;

import com.biblioteca.inventory.dto.BookRequest;
import com.biblioteca.inventory.dto.BookResponse;
import com.biblioteca.inventory.model.Book;
import com.biblioteca.inventory.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio para gestionar el inventario de libros
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Obtener todos los libros
     */
    public Flux<BookResponse> getAllBooks() {
        log.info("Obteniendo todos los libros");
        return bookRepository.findAll()
                .map(BookResponse::fromBook)
                .doOnComplete(() -> log.info("Libros obtenidos exitosamente"));
    }

    /**
     * Obtener libro por ID
     */
    public Mono<BookResponse> getBookById(Long id) {
        log.info("Buscando libro con ID: {}", id);
        return bookRepository.findById(id)
                .map(BookResponse::fromBook)
                .doOnSuccess(book -> log.info("Libro encontrado: {}", book))
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ID: " + id)));
    }

    /**
     * Obtener libro por ISBN
     */
    public Mono<BookResponse> getBookByIsbn(String isbn) {
        log.info("Buscando libro con ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .map(BookResponse::fromBook)
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ISBN: " + isbn)));
    }

    /**
     * Buscar libros por autor
     */
    public Flux<BookResponse> searchByAuthor(String author) {
        log.info("Buscando libros del autor: {}", author);
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .map(BookResponse::fromBook);
    }

    /**
     * Buscar libros por título
     */
    public Flux<BookResponse> searchByTitle(String title) {
        log.info("Buscando libros con título: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .map(BookResponse::fromBook);
    }

    /**
     * Buscar libros por categoría
     */
    public Flux<BookResponse> getBooksByCategory(String category) {
        log.info("Buscando libros de categoría: {}", category);
        return bookRepository.findByCategory(category)
                .map(BookResponse::fromBook);
    }

    /**
     * Obtener libros disponibles
     */
    public Flux<BookResponse> getAvailableBooks() {
        log.info("Obteniendo libros disponibles");
        return bookRepository.findAvailableBooks()
                .map(BookResponse::fromBook);
    }

    /**
     * Crear un nuevo libro
     */
    @Transactional
    public Mono<BookResponse> createBook(BookRequest request) {
        log.info("Creando nuevo libro: {}", request.title());
        
        // Verificar si ya existe un libro con ese ISBN
        return bookRepository.findByIsbn(request.isbn())
                .flatMap(existing -> {
                    log.warn("Intento de crear libro con ISBN duplicado: {}", request.isbn());
                    return Mono.<BookResponse>error(
                        new RuntimeException("Ya existe un libro con ISBN: " + request.isbn())
                    );
                })
                .switchIfEmpty(
                    Mono.defer(() -> {
                        try {
                            log.info("Creando libro con datos: ISBN={}, Title={}, Author={}", 
                                request.isbn(), request.title(), request.author());
                            
                            Book newBook = new Book(
                                request.isbn(),
                                request.title(),
                                request.author(),
                                request.publisher(),
                                request.publicationYear(),
                                request.category(),
                                request.totalCopies(),
                                request.availableCopies(),
                                request.description()
                            );
                            
                            log.info("Libro creado en memoria, guardando en base de datos...");
                            return bookRepository.save(newBook)
                                    .map(BookResponse::fromBook)
                                    .doOnSuccess(book -> log.info("Libro creado exitosamente con ID: {}", book.id()))
                                    .doOnError(error -> log.error("Error al guardar libro: {}", error.getMessage(), error));
                        } catch (Exception e) {
                            log.error("Error al crear libro: {}", e.getMessage(), e);
                            return Mono.error(e);
                        }
                    })
                )
                .doOnError(error -> log.error("Error en createBook: {}", error.getMessage(), error));
    }

    /**
     * Actualizar un libro
     */
    @Transactional
    public Mono<BookResponse> updateBook(Long id, BookRequest request) {
        log.info("Actualizando libro con ID: {}", id);
        
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ID: " + id)))
                .flatMap(existingBook -> {
                    Book updatedBook = new Book(
                        id,
                        request.isbn(),
                        request.title(),
                        request.author(),
                        request.publisher(),
                        request.publicationYear(),
                        request.category(),
                        request.totalCopies(),
                        request.availableCopies(),
                        request.description()
                    );
                    return bookRepository.save(updatedBook)
                            .map(BookResponse::fromBook)
                            .doOnSuccess(book -> log.info("Libro actualizado exitosamente: {}", book.id()));
                });
    }

    /**
     * Eliminar un libro
     */
    @Transactional
    public Mono<Void> deleteBook(Long id) {
        log.info("Eliminando libro con ID: {}", id);
        
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ID: " + id)))
                .flatMap(book -> bookRepository.deleteById(id)
                        .doOnSuccess(v -> log.info("Libro eliminado exitosamente: {}", id)));
    }

    /**
     * Verificar disponibilidad de un libro
     */
    public Mono<Boolean> checkAvailability(Long id) {
        log.info("Verificando disponibilidad del libro ID: {}", id);
        return bookRepository.isBookAvailable(id);
    }

    /**
     * Decrementar copias disponibles (cuando se presta un libro)
     */
    @Transactional
    public Mono<BookResponse> decrementAvailableCopies(Long id) {
        log.info("Decrementando copias disponibles del libro ID: {}", id);
        
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ID: " + id)))
                .flatMap(book -> {
                    if (book.availableCopies() <= 0) {
                        return Mono.error(new RuntimeException("No hay copias disponibles del libro ID: " + id));
                    }
                    Book updatedBook = book.withAvailableCopies(book.availableCopies() - 1);
                    return bookRepository.save(updatedBook)
                            .map(BookResponse::fromBook)
                            .doOnSuccess(b -> log.info("Copias disponibles decrementadas: {}", b.availableCopies()));
                });
    }

    /**
     * Incrementar copias disponibles (cuando se devuelve un libro)
     */
    @Transactional
    public Mono<BookResponse> incrementAvailableCopies(Long id) {
        log.info("Incrementando copias disponibles del libro ID: {}", id);
        
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Libro no encontrado con ID: " + id)))
                .flatMap(book -> {
                    if (book.availableCopies() >= book.totalCopies()) {
                        return Mono.error(new RuntimeException(
                            "No se pueden incrementar más copias. Total: " + book.totalCopies()
                        ));
                    }
                    Book updatedBook = book.withAvailableCopies(book.availableCopies() + 1);
                    return bookRepository.save(updatedBook)
                            .map(BookResponse::fromBook)
                            .doOnSuccess(b -> log.info("Copias disponibles incrementadas: {}", b.availableCopies()));
                });
    }
}

