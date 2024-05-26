TRUNCATE hotels, rooms, reservations RESTART IDENTITY;
INSERT INTO hotels (id) VALUES (default);
INSERT INTO rooms (room_number, room_type, hotel_ref) VALUES (1, 'LUX', 1);