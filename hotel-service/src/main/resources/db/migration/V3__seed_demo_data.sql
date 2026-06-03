-- ── Demo data for presentations ───────────────────────────
-- Amenities
INSERT INTO amenity (name, description) VALUES
  ('Free WiFi',      'High-speed wireless internet'),
  ('Swimming Pool',  'Outdoor heated pool'),
  ('Free Parking',   'On-site secure parking'),
  ('Breakfast',      'Buffet breakfast included'),
  ('Fitness Center', '24/7 gym access'),
  ('Spa',            'Wellness & spa center'),
  ('Air Conditioning','Climate control in every room'),
  ('Pet Friendly',   'Pets allowed on request');

-- Hotels (images from Unsplash)
INSERT INTO hotel (name, address, city, country, star_rating, phone, email, description, image_url) VALUES
  ('Grand Plaza Hotel', 'Calea Victoriei 12', 'Bucharest', 'Romania', 5, '+40 21 555 0101', 'contact@grandplaza.ro',
   'Five-star luxury in the heart of the capital, steps from the historic centre.',
   'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80'),
  ('Carpathian Retreat', 'Strada Brașovului 8', 'Brașov', 'Romania', 4, '+40 268 555 0202', 'hello@carpathianretreat.ro',
   'A cozy mountain resort with panoramic views of the Carpathians.',
   'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=900&q=80'),
  ('Seaside Resort & Spa', 'Bulevardul Mamaia 200', 'Constanța', 'Romania', 4, '+40 241 555 0303', 'book@seasideresort.ro',
   'Beachfront resort with a full-service spa and direct sea access.',
   'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=900&q=80'),
  ('City Center Inn', 'Piața Unirii 3', 'Cluj-Napoca', 'Romania', 3, '+40 264 555 0404', 'stay@citycenterinn.ro',
   'Comfortable and affordable rooms in the center of Cluj-Napoca.',
   'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=900&q=80');

-- Rooms (room_number is globally unique; ids assigned 1..N in this order)
INSERT INTO room (room_number, type, comfort, price_per_night, capacity, status, description, hotel_id, image_url) VALUES
  (101, 'DOUBLE', 'SUPERIOR', 220.0, 2, 'AVAILABLE', 'Deluxe double with city view',
     (SELECT id FROM hotel WHERE name='Grand Plaza Hotel'),
     'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=900&q=80'),
  (102, 'SINGLE', 'STANDARD', 140.0, 1, 'AVAILABLE', 'Cozy single room',
     (SELECT id FROM hotel WHERE name='Grand Plaza Hotel'),
     'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=900&q=80'),
  (103, 'TRIPLE', 'SUPERIOR', 300.0, 3, 'OCCUPIED', 'Spacious triple suite',
     (SELECT id FROM hotel WHERE name='Grand Plaza Hotel'),
     'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=900&q=80'),
  (201, 'DOUBLE', 'STANDARD', 160.0, 2, 'AVAILABLE', 'Mountain-view double',
     (SELECT id FROM hotel WHERE name='Carpathian Retreat'),
     'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=900&q=80'),
  (202, 'DOUBLE', 'SUPERIOR', 210.0, 2, 'AVAILABLE', 'Premium chalet room',
     (SELECT id FROM hotel WHERE name='Carpathian Retreat'),
     'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=80'),
  (301, 'DOUBLE', 'SUPERIOR', 250.0, 2, 'AVAILABLE', 'Sea-view room with balcony',
     (SELECT id FROM hotel WHERE name='Seaside Resort & Spa'),
     'https://images.unsplash.com/photo-1631049421450-348ccd7f8949?auto=format&fit=crop&w=900&q=80'),
  (302, 'TRIPLE', 'SUPERIOR', 330.0, 3, 'AVAILABLE', 'Family suite near the beach',
     (SELECT id FROM hotel WHERE name='Seaside Resort & Spa'),
     'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=900&q=80'),
  (401, 'SINGLE', 'STANDARD', 90.0, 1, 'AVAILABLE', 'Budget single in the center',
     (SELECT id FROM hotel WHERE name='City Center Inn'),
     'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80'),
  (402, 'DOUBLE', 'STANDARD', 120.0, 2, 'AVAILABLE', 'Standard double room',
     (SELECT id FROM hotel WHERE name='City Center Inn'),
     'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=900&q=80');

-- Room amenities (a sample per room)
INSERT INTO room_amenity (room_id, amenity_id)
SELECT r.id, a.id FROM room r, amenity a
WHERE r.room_number IN (101, 103, 301, 302)
  AND a.name IN ('Free WiFi', 'Air Conditioning', 'Breakfast', 'Spa');

INSERT INTO room_amenity (room_id, amenity_id)
SELECT r.id, a.id FROM room r, amenity a
WHERE r.room_number IN (102, 201, 202, 401, 402)
  AND a.name IN ('Free WiFi', 'Free Parking');

-- Reviews (user_id 1 = admin, 2 = user, seeded by user-service)
INSERT INTO review (room_id, user_id, username, rating, comment) VALUES
  ((SELECT id FROM room WHERE room_number=101), 2, 'user',  5, 'Amazing room, great view and very clean!'),
  ((SELECT id FROM room WHERE room_number=101), 1, 'admin', 4, 'Comfortable stay, would book again.'),
  ((SELECT id FROM room WHERE room_number=301), 2, 'user',  5, 'Loved waking up to the sea view.'),
  ((SELECT id FROM room WHERE room_number=201), 2, 'user',  4, 'Cozy and quiet, perfect for the mountains.');
