import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ProfileComponent } from './profile.component';
import { AuthApiService } from '../../core/services/auth-api.service';
import { AuthSessionService } from '../../core/services/auth-session.service';

describe('ProfileComponent', () => {
  it('should expose the current user from the session', () => {
    const getUserSpy = jasmine.createSpy('getUser').and.returnValue({
      clave: 'E-010',
      nombre: 'Carla',
      email: 'carla@example.local'
    });

    TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthApiService, useValue: { logout: jasmine.createSpy('logout') } },
        { provide: AuthSessionService, useValue: { getUser: getUserSpy } },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true)) } }
      ]
    });

    const fixture = TestBed.createComponent(ProfileComponent);
    const component = fixture.componentInstance;

    expect(component.user?.nombre).toBe('Carla');
    expect(getUserSpy).toHaveBeenCalled();
  });

  it('should logout and navigate to login on success', () => {
    const logoutSubject = new Subject<void>();
    const logoutSpy = jasmine.createSpy('logout').and.returnValue(logoutSubject.asObservable());
    const clearSessionSpy = jasmine.createSpy('clearSession');
    const navigateSpy = jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthApiService, useValue: { logout: logoutSpy } },
        { provide: AuthSessionService, useValue: { clearSession: clearSessionSpy, getUser: jasmine.createSpy('getUser') } },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });

    const fixture = TestBed.createComponent(ProfileComponent);
    const component = fixture.componentInstance;

    component.logout();
    expect(component.loading).toBeTrue();

    logoutSubject.next();
    logoutSubject.complete();

    expect(clearSessionSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.errorMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('should handle logout error by clearing session and navigating', () => {
    const logoutSubject = new Subject<void>();
    const logoutSpy = jasmine.createSpy('logout').and.returnValue(logoutSubject.asObservable());
    const clearSessionSpy = jasmine.createSpy('clearSession');
    const navigateSpy = jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthApiService, useValue: { logout: logoutSpy } },
        { provide: AuthSessionService, useValue: { clearSession: clearSessionSpy, getUser: jasmine.createSpy('getUser') } },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });

    const fixture = TestBed.createComponent(ProfileComponent);
    const component = fixture.componentInstance;

    component.logout();
    logoutSubject.error(new Error('network'));

    expect(clearSessionSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.errorMessage).toBe('La sesion se cerro localmente');
  });

  it('should navigate to login when goToLogin is called', () => {
    const navigateSpy = jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthApiService, useValue: { logout: jasmine.createSpy('logout') } },
        { provide: AuthSessionService, useValue: { getUser: jasmine.createSpy('getUser') } },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });

    const fixture = TestBed.createComponent(ProfileComponent);
    const component = fixture.componentInstance;

    component.goToLogin();

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});