-- Script de inicialización para la base de datos de Loan Service
-- Este script se ejecuta automáticamente al iniciar el contenedor de PostgreSQL
-- La base de datos loan_db ya está creada por POSTGRES_DB en docker-compose

-- Crear tipo enum para el estado del préstamo
CREATE TYPE loan_status AS ENUM ('ACTIVE', 'RETURNED', 'OVERDUE', 'CANCELLED');

-- Crear tabla de préstamos
CREATE TABLE loans (
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

-- Crear índices
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_loans_user_email ON loans(user_email);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_loans_loan_date ON loans(loan_date);
CREATE INDEX idx_loans_active ON loans(status) WHERE status = 'ACTIVE';

-- Insertar datos de ejemplo
INSERT INTO loans (book_id, user_email, user_name, loan_date, due_date, status, notes) VALUES
(1, 'juan.perez@example.com', 'Juan Pérez', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '9 days', 'ACTIVE', 'Primer préstamo del usuario'),
(2, 'maria.garcia@example.com', 'María García', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '11 days', 'ACTIVE', NULL),
(3, 'carlos.lopez@example.com', 'Carlos López', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '6 days', 'RETURNED', 'Devuelto a tiempo'),
(4, 'ana.martinez@example.com', 'Ana Martínez', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '12 days', 'ACTIVE', NULL),
(5, 'pedro.sanchez@example.com', 'Pedro Sánchez', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '16 days', 'RETURNED', 'Devuelto con retraso'),
(6, 'juan.perez@example.com', 'Juan Pérez', CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '13 days', 'ACTIVE', 'Segundo préstamo del usuario');

-- Actualizar return_date para los préstamos devueltos
UPDATE loans SET return_date = CURRENT_DATE - INTERVAL '6 days' WHERE id = 3;
UPDATE loans SET return_date = CURRENT_DATE - INTERVAL '10 days' WHERE id = 5;

