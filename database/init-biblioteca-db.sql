-- Script de inicialización para la base de datos unificada
-- Este script crea todas las tablas necesarias en una sola base de datos
-- Compatible con el plan gratuito de Render (1 base de datos)

-- ============================================================================
-- TABLA DE LIBROS (INVENTORY SERVICE)
-- ============================================================================

-- Crear tabla de libros
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    publication_year INTEGER,
    category VARCHAR(100),
    total_copies INTEGER NOT NULL DEFAULT 1,
    available_copies INTEGER NOT NULL DEFAULT 1,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

-- Crear índices para books
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_available ON books(available_copies);

-- Insertar datos de ejemplo para books
INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES
('978-0-13-468599-1', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2018, 'Programación', 5, 5, 'Guía completa de mejores prácticas en Java'),
('978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 'Programación', 3, 3, 'Manual de desarrollo ágil de software'),
('978-0-13-235628-2', 'The Clean Coder', 'Robert C. Martin', 'Prentice Hall', 2011, 'Desarrollo Profesional', 2, 2, 'Código de conducta para programadores profesionales'),
('978-0-201-63361-0', 'Design Patterns', 'Gamma, Helm, Johnson, Vlissides', 'Addison-Wesley', 1994, 'Arquitectura', 4, 4, 'Elementos de software orientado a objetos reutilizable'),
('978-0-13-475759-9', 'Refactoring', 'Martin Fowler', 'Addison-Wesley', 2018, 'Programación', 3, 3, 'Mejorando el diseño del código existente'),
('978-0-13-597783-2', 'Spring in Action', 'Craig Walls', 'Manning', 2022, 'Frameworks', 5, 5, 'Guía completa de Spring Framework'),
('978-1-61729-054-7', 'Spring Boot in Action', 'Craig Walls', 'Manning', 2016, 'Frameworks', 4, 4, 'Desarrollo con Spring Boot'),
('978-1-4919-5902-0', 'Building Microservices', 'Sam Newman', 'O Reilly', 2021, 'Arquitectura', 3, 3, 'Diseño de sistemas de grano fino'),
('978-0-13-468599-8', 'Domain-Driven Design', 'Eric Evans', 'Addison-Wesley', 2003, 'Arquitectura', 2, 2, 'Enfrentando la complejidad en el corazón del software'),
('978-1-49197-120-4', 'Reactive Spring', 'Josh Long', 'O Reilly', 2020, 'Programación Reactiva', 4, 4, 'Programación reactiva con Spring')
ON CONFLICT (isbn) DO NOTHING;

-- ============================================================================
-- TABLA DE PRÉSTAMOS (LOAN SERVICE)
-- ============================================================================

-- Crear tipo enum para el estado del préstamo
DO $$ BEGIN
    CREATE TYPE loan_status AS ENUM ('ACTIVE', 'RETURNED', 'OVERDUE', 'CANCELLED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Crear tabla de préstamos
CREATE TABLE IF NOT EXISTS loans (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    loan_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    status loan_status NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_dates CHECK (due_date >= loan_date),
    CONSTRAINT check_return_date CHECK (return_date IS NULL OR return_date >= loan_date)
);

-- Crear índices para loans
CREATE INDEX IF NOT EXISTS idx_loans_book_id ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_user_email ON loans(user_email);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON loans(due_date);
CREATE INDEX IF NOT EXISTS idx_loans_loan_date ON loans(loan_date);
CREATE INDEX IF NOT EXISTS idx_loans_active ON loans(status) WHERE status = 'ACTIVE';

-- Insertar datos de ejemplo para loans
INSERT INTO loans (book_id, user_email, user_name, loan_date, due_date, status, notes) VALUES
(1, 'juan.perez@example.com', 'Juan Pérez', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '9 days', 'ACTIVE', 'Primer préstamo del usuario'),
(2, 'maria.garcia@example.com', 'María García', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '11 days', 'ACTIVE', NULL),
(3, 'carlos.lopez@example.com', 'Carlos López', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '6 days', 'RETURNED', 'Devuelto a tiempo'),
(4, 'ana.martinez@example.com', 'Ana Martínez', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '12 days', 'ACTIVE', NULL),
(5, 'pedro.sanchez@example.com', 'Pedro Sánchez', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '16 days', 'RETURNED', 'Devuelto con retraso'),
(6, 'juan.perez@example.com', 'Juan Pérez', CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '13 days', 'ACTIVE', 'Segundo préstamo del usuario')
ON CONFLICT DO NOTHING;

-- Actualizar return_date para los préstamos devueltos
UPDATE loans SET return_date = CURRENT_DATE - INTERVAL '6 days' WHERE id = 3 AND return_date IS NULL;
UPDATE loans SET return_date = CURRENT_DATE - INTERVAL '10 days' WHERE id = 5 AND return_date IS NULL;

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================

-- Mostrar resumen
SELECT 'Books table' as table_name, COUNT(*) as records FROM books
UNION ALL
SELECT 'Loans table', COUNT(*) FROM loans;

