TRUNCATE hotels, rooms, reservations;
INSERT INTO hotels (id, created_at, version)
VALUES ('0196f8b0-2b3c-7f12-8a45-1c2d3e4f5a6b', now(), 1);
INSERT INTO rooms (id, room_number, room_type, hotel_ref, created_at, version)
VALUES ('0196f8b0-2b3c-7f12-8a46-1c2d3e4f5a6b', 1, 'LUX', '0196f8b0-2b3c-7f12-8a45-1c2d3e4f5a6b', now(), 1);
