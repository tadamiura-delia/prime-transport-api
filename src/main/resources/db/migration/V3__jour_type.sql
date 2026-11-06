CREATE TABLE jour_type (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    utilisateur_id VARCHAR(255) NOT NULL,
    jour SMALLINT NOT NULL CHECK (jour BETWEEN 0 AND 6),
    lieu_code VARCHAR(50) NOT NULL REFERENCES lieu_travail(code),
    transport_code VARCHAR(50) REFERENCES moyen_transport(code),
    UNIQUE(utilisateur_id, jour)
);
