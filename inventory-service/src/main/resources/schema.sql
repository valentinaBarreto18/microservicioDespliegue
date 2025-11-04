-- Schema para Inventory Service
-- Base de datos: inventory_db

-- Eliminar tabla si existe
DROP TABLE IF EXISTS books CASCADE;

-- Crear tabla de libros
CREATE TABLE books (
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

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_available ON books(available_copies);

-- Insertar datos de ejemplo
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
('978-1-49197-120-4', 'Reactive Spring', 'Josh Long', 'O Reilly', 2020, 'Programación Reactiva', 4, 4, 'Programación reactiva con Spring');

-- Comentarios en las tablas
COMMENT ON TABLE books IS 'Tabla principal de inventario de libros';
COMMENT ON COLUMN books.isbn IS 'Número ISBN único del libro';
COMMENT ON COLUMN books.available_copies IS 'Número de copias disponibles para préstamo';
COMMENT ON COLUMN books.total_copies IS 'Número total de copias en el inventario';

