import { TestBed } from '@angular/core/testing';
import { AuthSessionService } from './auth-session.service';

describe('AuthSessionService', () => {
  let service: AuthSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthSessionService]
    });
    service = TestBed.inject(AuthSessionService);
    localStorage.clear();
  });

  it('should store and retrieve session data', () => {
    service.setSession(
      { clave: 'E-001', nombre: 'Ana', email: 'ana@example.local' },
      '2099-01-01T00:00:00.000Z'
    );

    const session = service.getSession();
    expect(session).toBeTruthy();
    expect(session?.user.nombre).toBe('Ana');
    expect(service.getUser()?.clave).toBe('E-001');
  });

  it('should return null when session is missing', () => {
    expect(service.getSession()).toBeNull();
    expect(service.getUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should clear invalid JSON data', () => {
    localStorage.setItem('empleados.auth.session', 'not-json');

    expect(service.getSession()).toBeNull();
    expect(localStorage.getItem('empleados.auth.session')).toBeNull();
  });

  it('should validate expiration date', () => {
    service.setSession(
      { clave: 'E-002', nombre: 'Luis', email: 'luis@example.local' },
      new Date(Date.now() - 1000).toISOString()
    );

    expect(service.isAuthenticated()).toBeFalse();

    service.setSession(
      { clave: 'E-003', nombre: 'Maria', email: 'maria@example.local' },
      new Date(Date.now() + 1000 * 60).toISOString()
    );

    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should clear session data', () => {
    service.setSession(
      { clave: 'E-004', nombre: 'Jorge', email: 'jorge@example.local' },
      '2099-01-01T00:00:00.000Z'
    );

    service.clearSession();
    expect(service.getSession()).toBeNull();
  });
});