TRUNCATE hotels, rooms, reservations RESTART IDENTITY;
INSERT INTO hotels (id, created_at, version)
VALUES (default, now(), 1);
INSERT INTO rooms (room_number, room_type, hotel_ref, created_at, version)
VALUES (1, 'LUX', 1, now(), 1);