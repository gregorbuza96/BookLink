export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface Hotel {
  id?: number;
  name: string;
  address: string;
  city: string;
  country: string;
  starRating?: number;
  phone?: string;
  email?: string;
  description?: string;
}

export interface Amenity {
  id?: number;
  name: string;
  description?: string;
}

export interface Room {
  id?: number;
  roomNumber: number;
  type: 'SINGLE' | 'DOUBLE' | 'TRIPLE';
  comfort: 'STANDARD' | 'SUPERIOR';
  pricePerNight: number;
  capacity: number;
  status: 'AVAILABLE' | 'OCCUPIED';
  description?: string;
  hotelId?: number;
  hotelName?: string;
  amenityIds?: number[];
  amenities?: Amenity[];
}

export interface AppUser {
  id?: number;
  username: string;
  email: string;
  password?: string;
  role: 'USER' | 'ADMIN';
}

export interface UserProfile {
  id?: number;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  userId?: number;
}

export interface Booking {
  id?: number;
  roomId: number;
  roomNumber?: number;
  userId: number;
  username?: string;
  checkInDate: string;
  checkOutDate: string;
  totalPrice?: number;
  status?: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
}

export interface Review {
  id?: number;
  roomId: number;
  roomNumber?: number;
  userId: number;
  username?: string;
  rating: number;
  comment?: string;
  createdAt?: string;
}

export interface ApiError {
  timestamp: string;
  message: string;
  errors?: { [key: string]: string };
}
