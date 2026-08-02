import { Role } from './enums';

export interface User {
  id: number;
  fullName: string;
  email: string;
  phoneNumber?: string;
  designation?: string;
  department?: string;
  role: Role;
  enabled: boolean;
  profileImageUrl?: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phoneNumber?: string;
  designation?: string;
  department?: string;
  role: Role;
}
