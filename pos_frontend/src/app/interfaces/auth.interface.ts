export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  email: string;
  role: 'SUPERVISOR' | 'OPERATOR';
  message?: string;
}

export interface AuthState {
  isAuthenticated: boolean;
  userEmail?: string;
  role?: string;
  id?: number;
  isLoading: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface RegisterResponse {
  success: boolean;
  message?: string;
} 