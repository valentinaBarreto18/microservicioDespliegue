package com.biblioteca.loan.controller;

import com.biblioteca.loan.dto.LoanRequest;
import com.biblioteca.loan.dto.LoanResponse;
import com.biblioteca.loan.model.LoanStatus;
import com.biblioteca.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Controlador REST reactivo para gestionar préstamos
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    /**
     * Obtener todos los préstamos
     * GET /api/loans
     */
    @GetMapping
    public Flux<LoanResponse> getAllLoans() {
        return loanService.getAllLoans();
    }

    /**
     * Obtener préstamo por ID
     * GET /api/loans/{id}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LoanResponse>> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Obtener préstamos por usuario
     * GET /api/loans/user/{email}
     */
    @GetMapping("/user/{email}")
    public Flux<LoanResponse> getLoansByUser(@PathVariable String email) {
        return loanService.getLoansByUser(email);
    }

    /**
     * Obtener préstamos activos de un usuario
     * GET /api/loans/user/{email}/active
     */
    @GetMapping("/user/{email}/active")
    public Flux<LoanResponse> getActiveLoansByUser(@PathVariable String email) {
        return loanService.getActiveLoanssByUser(email);
    }

    /**
     * Obtener préstamos por libro
     * GET /api/loans/book/{bookId}
     */
    @GetMapping("/book/{bookId}")
    public Flux<LoanResponse> getLoansByBook(@PathVariable Long bookId) {
        return loanService.getLoansByBook(bookId);
    }

    /**
     * Obtener préstamos por estado
     * GET /api/loans/status/{status}
     */
    @GetMapping("/status/{status}")
    public Flux<LoanResponse> getLoansByStatus(@PathVariable LoanStatus status) {
        return loanService.getLoansByStatus(status);
    }

    /**
     * Obtener préstamos vencidos
     * GET /api/loans/overdue
     */
    @GetMapping("/overdue")
    public Flux<LoanResponse> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }

    /**
     * Crear un nuevo préstamo
     * POST /api/loans
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        return loanService.createLoan(request);
    }

    /**
     * Devolver un libro
     * POST /api/loans/{id}/return
     */
    @PostMapping("/{id}/return")
    public Mono<ResponseEntity<LoanResponse>> returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Renovar un préstamo
     * POST /api/loans/{id}/renew
     */
    @PostMapping("/{id}/renew")
    public Mono<ResponseEntity<LoanResponse>> renewLoan(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDueDate) {
        return loanService.renewLoan(id, newDueDate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Cancelar un préstamo
     * POST /api/loans/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public Mono<ResponseEntity<LoanResponse>> cancelLoan(@PathVariable Long id) {
        return loanService.cancelLoan(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar préstamos vencidos
     * POST /api/loans/update-overdue
     */
    @PostMapping("/update-overdue")
    public Flux<LoanResponse> updateOverdueLoans() {
        return loanService.updateOverdueLoans();
    }
}

