export interface SignupRequest {
  fullName: string;
  email: string;
  password?: string;
}

export interface SignupResponse {
  customerId: number;
  message: string;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface LoginResponse {
  id: number;
  customerId: number;
  fullName: string;
  email: string;
  token?: string;
  message: string;
}

export interface SessionUser {
  id: number;
  customerId: number;
  fullName: string;
  email: string;
}
