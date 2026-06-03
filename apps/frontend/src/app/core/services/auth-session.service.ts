import { Injectable } from '@angular/core';

export interface AuthenticatedUser {
  clave: string;
  nombre: string;
  email: string;
  direccion?: string;
  telefono?: string;
  departamento?: {
    id: string;
    nombre: string;
  };
}

interface StoredSession {
  user: AuthenticatedUser;
  sessionExpiresAt: string;
}

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly storageKey = 'empleados.auth.session';

  setSession(user: AuthenticatedUser, sessionExpiresAt: string): void {
    const payload: StoredSession = { user, sessionExpiresAt };
    localStorage.setItem(this.storageKey, JSON.stringify(payload));
  }

  getSession(): StoredSession | null {
    const raw = localStorage.getItem(this.storageKey);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as StoredSession;
    } catch {
      this.clearSession();
      return null;
    }
  }

  getUser(): AuthenticatedUser | null {
    return this.getSession()?.user ?? null;
  }

  isAuthenticated(): boolean {
    const session = this.getSession();
    if (!session) {
      return false;
    }

    return new Date(session.sessionExpiresAt).getTime() > Date.now();
  }

  clearSession(): void {
    localStorage.removeItem(this.storageKey);
  }
}
