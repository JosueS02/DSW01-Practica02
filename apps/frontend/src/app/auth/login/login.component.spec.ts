import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { of, Subject } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthApiService, LoginResponse } from '../../core/services/auth-api.service';
import { AuthSessionService } from '../../core/services/auth-session.service';

describe('LoginComponent', () => {
  let loginSpy: jasmine.Spy;
  let setSessionSpy: jasmine.Spy;
  let navigateSpy: jasmine.Spy;

  beforeEach(() => {
    loginSpy = jasmine.createSpy('login');
    setSessionSpy = jasmine.createSpy('setSession');
    navigateSpy = jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        { provide: AuthApiService, useValue: { login: loginSpy } },
        { provide: AuthSessionService, useValue: { setSession: setSessionSpy } },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });
  });

  it('should show validation message when form is invalid', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.form.setValue({ email: '', password: '' });

    component.submit();

    expect(loginSpy).not.toHaveBeenCalled();
    expect(component.errorMessage).toBe('Completa los campos requeridos');
  });

  it('should call auth service and navigate on success', () => {
    const response: LoginResponse = {
      sessionExpiresAt: new Date().toISOString(),
      authMode: 'ui-session',
      empleado: { clave: 'E-001', nombre: 'Ana', email: 'ana@example.local' }
    };
    loginSpy.and.returnValue(of(response));

    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.form.setValue({ email: 'ana@example.local', password: 'Passw0rd' });

    component.submit();

    expect(loginSpy).toHaveBeenCalledWith({ email: 'ana@example.local', password: 'Passw0rd' });
    expect(setSessionSpy).toHaveBeenCalledWith(response.empleado, response.sessionExpiresAt);
    expect(navigateSpy).toHaveBeenCalledWith(['/perfil']);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should toggle loading while the request is in flight', () => {
    const subject = new Subject<LoginResponse>();
    loginSpy.and.returnValue(subject.asObservable());

    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.form.setValue({ email: 'ana@example.local', password: 'Passw0rd' });

    component.submit();
    expect(component.loading).toBeTrue();

    subject.next({
      sessionExpiresAt: new Date().toISOString(),
      authMode: 'ui-session',
      empleado: { clave: 'E-002', nombre: 'Luis', email: 'luis@example.local' }
    });
    subject.complete();

    expect(component.loading).toBeFalse();
  });
});
