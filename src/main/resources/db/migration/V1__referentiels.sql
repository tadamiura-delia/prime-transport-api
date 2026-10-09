CREATE TABLE lieu_travail (
    code    VARCHAR(50)  PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    ordre   INTEGER      NOT NULL,
    -- Lieu dont le front dépend (samedi et dimanche) : non supprimable.
    systeme BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE moyen_transport (
    code    VARCHAR(50)  PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    ordre   INTEGER      NOT NULL
);

INSERT INTO lieu_travail (code, libelle, ordre, systeme) VALUES
    ('teletravail',   'Télétravail',                          1, FALSE),
    ('conges-ferie',  'Congés / Férié',                       2, TRUE),
    ('agence-rennes', 'Delia Rennes (61, Rue Jean Guéhenno)', 3, FALSE),
    ('agence-nantes', 'Delia Nantes (11 Imp Juton)',          4, FALSE),
    ('mission-1',     'Mission 1',                            5, FALSE),
    ('mission-2',     'Mission 2',                            6, FALSE);

-- « Aucun transport » n'est pas une ligne : c'est l'absence de valeur.
INSERT INTO moyen_transport (code, libelle, ordre) VALUES
    ('velo',        'Vélo',        1),
    ('covoiturage', 'Covoiturage', 2);
