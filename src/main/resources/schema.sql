DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS hotels;

CREATE TABLE hotels
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    created_at  timestamptz not null,
    modified_at timestamptz,
    version     INT         not null
);

CREATE TABLE rooms
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hotel_ref   INT REFERENCES hotels (id) NOT NULL,
    room_type   VARCHAR                    NOT NULL,
    room_number INT         NOT NULL,

    created_at  timestamptz not null,
    modified_at timestamptz,
    version     INT         not null
);

CREATE TABLE reservations
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hotel_ref   INT REFERENCES hotels (id) NOT NULL,
    room_type   VARCHAR                    NOT NULL,
    email       VARCHAR                    NOT NULL,
    "from"      DATE                       NOT NULL,
    period      INTERVAL                   NOT NULL,

    created_at  timestamptz                not null,
    modified_at timestamptz,
    version     INT                        not null
)