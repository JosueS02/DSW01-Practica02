import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthApiService, LoginResponse } from '../../core/services/auth-api.service';
import { AuthSessionService } from '../../core/services/auth-session.service';

describe('Login errors', () => {
  it('should show friendly message and stop loading when api fails', () => {
    const loginSpy = jasmine.createSpy('login');
    const navigateSpy = jasmine.createSpy('navigate').and.returnValue(Promise.resolve(true));
    const setSessionSpy = jasmine.createSpy('setSession');
    const subject = new Subject<LoginResponse>();
    loginSpy.and.returnValue(subject.asObservable());

    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        { provide: AuthApiService, useValue: { login: loginSpy } },
        { provide: AuthSessionService, useValue: { setSession: setSessionSpy } },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });

    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.form.setValue({ email: 'ana@example.local', password: 'WrongPass1' });

    component.submit();
    expect(component.loading).toBeTrue();

    subject.error(new Error('401'));

    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toContain('Credenciales invalidas');
    expect(setSessionSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
