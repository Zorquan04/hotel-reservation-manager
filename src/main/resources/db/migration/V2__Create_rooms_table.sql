DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'rooms') THEN
        CREATE TABLE rooms (
            id SERIAL PRIMARY KEY,
            number VARCHAR(50) UNIQUE NOT NULL,
            price DOUBLE PRECISION NOT NULL,
            standard VARCHAR(255) NOT NULL
        );
    END IF;
END $$;