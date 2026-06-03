import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  sessionExpiresAt: string;
  authMode: string;
  empleado: {
    clave: string;
    nombre: string;
    direccion?: string;
    telefono?: string;
    email: string;
    departamento?: {
      id: string;
      nombre: string;
    };
  };
}

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  constructor(private readonly http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/auth/login', payload);
  }

  logout(): Observable<void> {
    return this.http.post<void>('/auth/logout', {});
  }
}
