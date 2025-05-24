DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'reservations') THEN
        CREATE TABLE reservations (
            id SERIAL PRIMARY KEY,
            date DATE NOT NULL,
            room_id BIGINT NOT NULL,
            user_id BIGINT NOT NULL,
            CONSTRAINT fk_room FOREIGN KEY (room_id) REFERENCES rooms(id),
            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
        );
    END IF;
END $$;