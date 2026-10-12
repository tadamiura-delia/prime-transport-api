CREATE TABLE distance_lieu (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    utilisateur_id VARCHAR(255) NOT NULL,
    lieu_code VARCHAR(255) NOT NULL REFERENCES lieu_travail(code),
    distance_km NUMERIC(5, 2) NOT NULL CHECK (distance_km >= 0),
    UNIQUE(utilisateur_id, lieu_code)
);