import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { authSessionGuard } from './auth-session.guard';
import { AuthSessionService } from '../services/auth-session.service';

describe('authSessionGuard', () => {
  it('should allow navigation when authenticated', () => {
    const isAuthenticatedSpy = jasmine.createSpy('isAuthenticated').and.returnValue(true);
    const parseUrlSpy = jasmine.createSpy('parseUrl');

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthSessionService, useValue: { isAuthenticated: isAuthenticatedSpy } },
        { provide: Router, useValue: { parseUrl: parseUrlSpy } }
      ]
    });

    const result = TestBed.runInInjectionContext(() => authSessionGuard({} as any, {} as any));

    expect(result).toBeTrue();
    expect(parseUrlSpy).not.toHaveBeenCalled();
  });

  it('should redirect to /login when unauthenticated', () => {
    const isAuthenticatedSpy = jasmine.createSpy('isAuthenticated').and.returnValue(false);
    const urlTree = {} as UrlTree;
    const parseUrlSpy = jasmine.createSpy('parseUrl').and.returnValue(urlTree);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthSessionService, useValue: { isAuthenticated: isAuthenticatedSpy } },
        { provide: Router, useValue: { parseUrl: parseUrlSpy } }
      ]
    });

    const result = TestBed.runInInjectionContext(() => authSessionGuard({} as any, {} as any));

    expect(parseUrlSpy).toHaveBeenCalledWith('/login');
    expect(result).toBe(urlTree);
  });
});