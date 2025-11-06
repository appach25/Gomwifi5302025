CREATE TABLE activation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_de_jour INTEGER NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    appareil_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (appareil_id) REFERENCES appareil(id)
);
