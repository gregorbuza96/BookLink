CREATE TABLE room
(
    id              BIGSERIAL PRIMARY KEY,
    room_number     INT UNIQUE     NOT NULL,
    type            VARCHAR(10)    NOT NULL CHECK (type IN ('SINGLE', 'DOUBLE', 'TRIPLE')),
    comfort         VARCHAR(10)    NOT NULL CHECK (comfort IN ('STANDARD', 'SUPERIOR')),
    price_per_night DOUBLE PRECISION NOT NULL CHECK (price_per_night > 0),
    capacity        INT            NOT NULL CHECK (capacity > 0 AND capacity <= 4),
    status          VARCHAR(10)    NOT NULL CHECK (status IN ('AVAILABLE', 'OCCUPIED')),
    description     TEXT
);
