package com.biblioteca.loan.service;

import com.biblioteca.loan.dto.LoanRequest;
import com.biblioteca.loan.dto.LoanResponse;
import com.biblioteca.loan.model.Loan;
import com.biblioteca.loan.model.LoanStatus;
import com.biblioteca.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Servicio para gestionar préstamos de libros
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${inventory.service.url:http://inventory-service:8081}")
    private String inventoryServiceUrl;

    // Límite de libros que un usuario puede tener prestados simultáneamente
    private static final Long MAX_ACTIVE_LOANS_PER_USER = 5L;

    /**
     * Obtener todos los préstamos
     */
    public Flux<LoanResponse> getAllLoans() {
        log.info("Obteniendo todos los préstamos");
        return loanRepository.findAll()
                .map(LoanResponse::fromLoan)
                .doOnComplete(() -> log.info("Préstamos obtenidos exitosamente"));
    }

    /**
     * Obtener préstamo por ID
     */
    public Mono<LoanResponse> getLoanById(Long id) {
        log.info("Buscando préstamo con ID: {}", id);
        return loanRepository.findById(id)
                .map(LoanResponse::fromLoan)
                .doOnSuccess(loan -> log.info("Préstamo encontrado: {}", loan))
                .switchIfEmpty(Mono.error(new RuntimeException("Préstamo no encontrado con ID: " + id)));
    }

    /**
     * Obtener préstamos por email de usuario
     */
    public Flux<LoanResponse> getLoansByUser(String userEmail) {
        log.info("Buscando préstamos del usuario: {}", userEmail);
        return loanRepository.findByUserEmail(userEmail)
                .map(LoanResponse::fromLoan);
    }

    /**
     * Obtener préstamos activos de un usuario
     */
    public Flux<LoanResponse> getActiveLoanssByUser(String userEmail) {
        log.info("Buscando préstamos activos del usuario: {}", userEmail);
        return loanRepository.findActiveLoanssByUser(userEmail)
                .map(LoanResponse::fromLoan);
    }

    /**
     * Obtener préstamos por libro
     */
    public Flux<LoanResponse> getLoansByBook(Long bookId) {
        log.info("Buscando préstamos del libro ID: {}", bookId);
        return loanRepository.findByBookId(bookId)
                .map(LoanResponse::fromLoan);
    }

    /**
     * Obtener préstamos por estado
     */
    public Flux<LoanResponse> getLoansByStatus(LoanStatus status) {
        log.info("Buscando préstamos con estado: {}", status);
        return loanRepository.findByStatus(status)
                .map(LoanResponse::fromLoan);
    }

    /**
     * Obtener préstamos vencidos
     */
    public Flux<LoanResponse> getOverdueLoans() {
        log.info("Buscando préstamos vencidos");
        return loanRepository.findOverdueLoans(LocalDate.now())
                .map(LoanResponse::fromLoan);
    }

    /**
     * Crear un nuevo préstamo
     */
    @Transactional
    public Mono<LoanResponse> createLoan(LoanRequest request) {
        log.info("Creando nuevo préstamo para el libro ID: {} - Usuario: {}", 
                 request.bookId(), request.userEmail());

        // Verificar límite de préstamos activos del usuario
        return loanRepository.countActiveLoanssByUser(request.userEmail())
                .flatMap(count -> {
                    if (count >= MAX_ACTIVE_LOANS_PER_USER) {
                        return Mono.error(new RuntimeException(
                            "El usuario ha alcanzado el límite máximo de préstamos activos: " + MAX_ACTIVE_LOANS_PER_USER
                        ));
                    }

                    // Verificar disponibilidad del libro en el servicio de inventario
                    return checkBookAvailability(request.bookId())
                            .flatMap(isAvailable -> {
                                if (!isAvailable) {
                                    return Mono.error(new RuntimeException(
                                        "El libro ID " + request.bookId() + " no está disponible"
                                    ));
                                }

                                // Crear el préstamo
                                Loan newLoan = new Loan(
                                    request.bookId(),
                                    request.userEmail(),
                                    request.userName(),
                                    LocalDate.now(),
                                    request.dueDate(),
                                    LoanStatus.ACTIVE,
                                    request.notes()
                                );

                                // Guardar préstamo y decrementar copias disponibles
                                return loanRepository.save(newLoan)
                                        .flatMap(savedLoan -> 
                                            decrementBookCopies(savedLoan.bookId())
                                                .thenReturn(savedLoan)
                                        )
                                        .map(LoanResponse::fromLoan)
                                        .doOnSuccess(loan -> log.info("Préstamo creado exitosamente: {}", loan.id()));
                            });
                });
    }

    /**
     * Devolver un libro (marcar préstamo como devuelto)
     */
    @Transactional
    public Mono<LoanResponse> returnLoan(Long loanId) {
        log.info("Procesando devolución del préstamo ID: {}", loanId);

        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new RuntimeException("Préstamo no encontrado con ID: " + loanId)))
                .flatMap(loan -> {
                    if (loan.status() != LoanStatus.ACTIVE && loan.status() != LoanStatus.OVERDUE) {
                        return Mono.error(new RuntimeException(
                            "El préstamo ya ha sido devuelto o está cancelado"
                        ));
                    }

                    Loan returnedLoan = loan.withReturn(LocalDate.now());
                    
                    return loanRepository.save(returnedLoan)
                            .flatMap(savedLoan -> 
                                incrementBookCopies(savedLoan.bookId())
                                    .thenReturn(savedLoan)
                            )
                            .map(LoanResponse::fromLoan)
                            .doOnSuccess(l -> log.info("Préstamo devuelto exitosamente: {}", l.id()));
                });
    }

    /**
     * Renovar un préstamo (extender fecha de vencimiento)
     */
    @Transactional
    public Mono<LoanResponse> renewLoan(Long loanId, LocalDate newDueDate) {
        log.info("Renovando préstamo ID: {} con nueva fecha: {}", loanId, newDueDate);

        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new RuntimeException("Préstamo no encontrado con ID: " + loanId)))
                .flatMap(loan -> {
                    if (loan.status() != LoanStatus.ACTIVE) {
                        return Mono.error(new RuntimeException(
                            "Solo se pueden renovar préstamos activos"
                        ));
                    }

                    if (newDueDate.isBefore(LocalDate.now())) {
                        return Mono.error(new RuntimeException(
                            "La nueva fecha de vencimiento debe ser futura"
                        ));
                    }

                    Loan renewedLoan = new Loan(
                        loan.id(),
                        loan.bookId(),
                        loan.userEmail(),
                        loan.userName(),
                        loan.loanDate(),
                        newDueDate,
                        loan.returnDate(),
                        loan.status(),
                        loan.notes()
                    );

                    return loanRepository.save(renewedLoan)
                            .map(LoanResponse::fromLoan)
                            .doOnSuccess(l -> log.info("Préstamo renovado exitosamente: {}", l.id()));
                });
    }

    /**
     * Cancelar un préstamo
     */
    @Transactional
    public Mono<LoanResponse> cancelLoan(Long loanId) {
        log.info("Cancelando préstamo ID: {}", loanId);

        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new RuntimeException("Préstamo no encontrado con ID: " + loanId)))
                .flatMap(loan -> {
                    if (loan.status() != LoanStatus.ACTIVE) {
                        return Mono.error(new RuntimeException(
                            "Solo se pueden cancelar préstamos activos"
                        ));
                    }

                    Loan cancelledLoan = loan.withStatus(LoanStatus.CANCELLED);
                    
                    return loanRepository.save(cancelledLoan)
                            .flatMap(savedLoan -> 
                                incrementBookCopies(savedLoan.bookId())
                                    .thenReturn(savedLoan)
                            )
                            .map(LoanResponse::fromLoan)
                            .doOnSuccess(l -> log.info("Préstamo cancelado exitosamente: {}", l.id()));
                });
    }

    /**
     * Actualizar préstamos vencidos
     */
    @Transactional
    public Flux<LoanResponse> updateOverdueLoans() {
        log.info("Actualizando préstamos vencidos");
        
        return loanRepository.findOverdueLoans(LocalDate.now())
                .flatMap(loan -> {
                    Loan overdueloan = loan.withStatus(LoanStatus.OVERDUE);
                    return loanRepository.save(overdueloan);
                })
                .map(LoanResponse::fromLoan)
                .doOnComplete(() -> log.info("Préstamos vencidos actualizados"));
    }

    /**
     * Verificar disponibilidad del libro en el servicio de inventario
     */
    private Mono<Boolean> checkBookAvailability(Long bookId) {
        log.debug("Verificando disponibilidad del libro ID: {}", bookId);
        
        return webClientBuilder.build()
                .get()
                .uri(inventoryServiceUrl + "/api/books/{id}/availability", bookId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(available -> log.debug("Disponibilidad del libro {}: {}", bookId, available))
                .onErrorResume(error -> {
                    log.error("Error al verificar disponibilidad del libro {}: {}", bookId, error.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Decrementar copias disponibles en el servicio de inventario
     */
    private Mono<Void> decrementBookCopies(Long bookId) {
        log.debug("Decrementando copias del libro ID: {}", bookId);
        
        return webClientBuilder.build()
                .post()
                .uri(inventoryServiceUrl + "/api/books/{id}/decrement", bookId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Copias decrementadas para el libro {}", bookId))
                .onErrorResume(error -> {
                    log.error("Error al decrementar copias del libro {}: {}", bookId, error.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Incrementar copias disponibles en el servicio de inventario
     */
    private Mono<Void> incrementBookCopies(Long bookId) {
        log.debug("Incrementando copias del libro ID: {}", bookId);
        
        return webClientBuilder.build()
                .post()
                .uri(inventoryServiceUrl + "/api/books/{id}/increment", bookId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Copias incrementadas para el libro {}", bookId))
                .onErrorResume(error -> {
                    log.error("Error al incrementar copias del libro {}: {}", bookId, error.getMessage());
                    return Mono.empty();
                });
    }
}

