import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { AuthApiService } from './auth-api.service';

describe('AuthApiService', () => {
  it('should call login endpoint with payload', () => {
    const postSpy = jasmine.createSpy('post').and.returnValue(of({}));

    TestBed.configureTestingModule({
      providers: [
        AuthApiService,
        { provide: HttpClient, useValue: { post: postSpy } }
      ]
    });

    const service = TestBed.inject(AuthApiService);
    service.login({ email: 'ana@example.local', password: 'Passw0rd' }).subscribe();

    expect(postSpy).toHaveBeenCalledWith('/auth/login', {
      email: 'ana@example.local',
      password: 'Passw0rd'
    });
  });

  it('should call logout endpoint', () => {
    const postSpy = jasmine.createSpy('post').and.returnValue(of(void 0));

    TestBed.configureTestingModule({
      providers: [
        AuthApiService,
        { provide: HttpClient, useValue: { post: postSpy } }
      ]
    });

    const service = TestBed.inject(AuthApiService);
    service.logout().subscribe();

    expect(postSpy).toHaveBeenCalledWith('/auth/logout', {});
  });
});