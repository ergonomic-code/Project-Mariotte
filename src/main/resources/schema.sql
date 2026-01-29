DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS hotels CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;

CREATE TABLE hotels
(
    id uuid PRIMARY KEY,

    created_at  timestamptz not null,
    modified_at timestamptz,
    version     INT         not null
);

CREATE TABLE rooms
(
    id        uuid PRIMARY KEY,
    hotel_ref uuid REFERENCES hotels (id) NOT NULL,
    room_type   VARCHAR                    NOT NULL,
    room_number INT         NOT NULL,

    created_at  timestamptz not null,
    modified_at timestamptz,
    version     INT         not null
);

CREATE TABLE reservations
(
    id        uuid PRIMARY KEY,
    hotel_ref uuid REFERENCES hotels (id) NOT NULL,
    room_type   VARCHAR                    NOT NULL,
    email       VARCHAR                    NOT NULL,
    "from"      DATE                       NOT NULL,
    period      INTERVAL                   NOT NULL,

    created_at  timestamptz                not null,
    modified_at timestamptz,
    version     INT                        not null
)
