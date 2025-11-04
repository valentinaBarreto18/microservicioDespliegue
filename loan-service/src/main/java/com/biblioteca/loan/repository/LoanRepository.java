package com.biblioteca.loan.repository;

import com.biblioteca.loan.model.Loan;
import com.biblioteca.loan.model.LoanStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Repository reactivo para la entidad Loan
 */
@Repository
public interface LoanRepository extends R2dbcRepository<Loan, Long> {
    
    /**
     * Buscar préstamos por email del usuario
     */
    Flux<Loan> findByUserEmail(String userEmail);
    
    /**
     * Buscar préstamos por ID del libro
     */
    Flux<Loan> findByBookId(Long bookId);
    
    /**
     * Buscar préstamos por estado
     */
    Flux<Loan> findByStatus(LoanStatus status);
    
    /**
     * Buscar préstamos activos de un usuario
     */
    @Query("SELECT * FROM loans WHERE user_email = :userEmail AND status = 'ACTIVE'")
    Flux<Loan> findActiveLoanssByUser(String userEmail);
    
    /**
     * Buscar préstamos activos de un libro
     */
    @Query("SELECT * FROM loans WHERE book_id = :bookId AND status = 'ACTIVE'")
    Flux<Loan> findActiveLoansByBook(Long bookId);
    
    /**
     * Buscar préstamos vencidos
     */
    @Query("SELECT * FROM loans WHERE status = 'ACTIVE' AND due_date < :currentDate AND return_date IS NULL")
    Flux<Loan> findOverdueLoans(LocalDate currentDate);
    
    /**
     * Contar préstamos activos de un usuario
     */
    @Query("SELECT COUNT(*) FROM loans WHERE user_email = :userEmail AND status = 'ACTIVE'")
    Mono<Long> countActiveLoanssByUser(String userEmail);
    
    /**
     * Verificar si un libro tiene préstamos activos
     */
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM loans WHERE book_id = :bookId AND status = 'ACTIVE'")
    Mono<Boolean> hasActiveLoans(Long bookId);
}

