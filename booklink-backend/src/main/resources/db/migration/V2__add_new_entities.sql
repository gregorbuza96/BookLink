-- Hotel
CREATE TABLE hotel
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    address     VARCHAR(255)  NOT NULL,
    city        VARCHAR(100)  NOT NULL,
    country     VARCHAR(100)  NOT NULL,
    star_rating INT CHECK (star_rating >= 1 AND star_rating <= 5),
    phone       VARCHAR(50),
    email       VARCHAR(255),
    description TEXT
);

-- Add hotel FK to existing room table
ALTER TABLE room
    ADD COLUMN hotel_id BIGINT REFERENCES hotel (id) ON DELETE SET NULL;

-- Amenity
CREATE TABLE amenity
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Room <-> Amenity join table
CREATE TABLE room_amenity
(
    room_id    BIGINT NOT NULL REFERENCES room (id) ON DELETE CASCADE,
    amenity_id BIGINT NOT NULL REFERENCES amenity (id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, amenity_id)
);

-- Application users
CREATE TABLE app_user
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(10)  NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

-- User profile (1:1 with app_user)
CREATE TABLE user_profile
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES app_user (id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(50),
    address    VARCHAR(255)
);

-- Booking
CREATE TABLE booking
(
    id             BIGSERIAL PRIMARY KEY,
    room_id        BIGINT           NOT NULL REFERENCES room (id),
    user_id        BIGINT           NOT NULL REFERENCES app_user (id),
    check_in_date  DATE             NOT NULL,
    check_out_date DATE             NOT NULL,
    total_price    DOUBLE PRECISION NOT NULL CHECK (total_price >= 0),
    status         VARCHAR(20)      NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date)
);

-- Review
CREATE TABLE review
(
    id         BIGSERIAL PRIMARY KEY,
    room_id    BIGINT    NOT NULL REFERENCES room (id),
    user_id    BIGINT    NOT NULL REFERENCES app_user (id),
    rating     INT       NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
