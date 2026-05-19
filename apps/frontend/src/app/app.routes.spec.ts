import { routes } from './app.routes';
import { LoginComponent } from './auth/login/login.component';
import { ProfileComponent } from './auth/profile/profile.component';
import { authSessionGuard } from './core/guards/auth-session.guard';

describe('routes', () => {
  it('should include the login redirect route', () => {
    const redirectRoute = routes.find(route => route.path === '');

    expect(redirectRoute?.redirectTo).toBe('login');
    expect(redirectRoute?.pathMatch).toBe('full');
  });

  it('should include the login route', () => {
    const loginRoute = routes.find(route => route.path === 'login');

    expect(loginRoute?.component).toBe(LoginComponent);
  });

  it('should include the profile route with guard', () => {
    const profileRoute = routes.find(route => route.path === 'perfil');

    expect(profileRoute?.component).toBe(ProfileComponent);
    expect(profileRoute?.canActivate).toEqual([authSessionGuard]);
  });
});