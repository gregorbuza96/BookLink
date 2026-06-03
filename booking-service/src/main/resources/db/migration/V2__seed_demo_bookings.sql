-- Demo bookings for presentations.
-- room_id values match the hotel-service seed order (101->1, 102->2, 103->3,
-- 201->4, 202->5, 301->6, 302->7, 401->8, 402->9).
-- user_id 1 = admin, 2 = user (seeded by user-service).
INSERT INTO booking (room_id, user_id, check_in_date, check_out_date, total_price, status) VALUES
  (1, 2, CURRENT_DATE + 7,  CURRENT_DATE + 10, 660.0,  'CONFIRMED'),
  (3, 2, CURRENT_DATE - 1,  CURRENT_DATE + 2,  900.0,  'CONFIRMED'),
  (6, 1, CURRENT_DATE - 30, CURRENT_DATE - 27, 750.0,  'COMPLETED'),
  (4, 2, CURRENT_DATE + 14, CURRENT_DATE + 16, 320.0,  'PENDING'),
  (7, 2, CURRENT_DATE + 21, CURRENT_DATE + 25, 1320.0, 'CONFIRMED');
