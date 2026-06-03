import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthApiService, LoginResponse } from '../../core/services/auth-api.service';
import { AuthSessionService } from '../../core/services/auth-session.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="auth-shell">
      <article class="auth-card">
        <header class="auth-head">
          <p class="auth-kicker">Portal de Empleados</p>
          <h1>Iniciar sesion</h1>
          <p class="auth-copy">Accede con tu correo corporativo para ver tu informacion de perfil.</p>
        </header>

        <form class="auth-form" [formGroup]="form" (ngSubmit)="submit()">
          <label class="field">
            <span>Correo</span>
            <input
              type="email"
              formControlName="email"
              placeholder="usuario@empresa.com"
              [class.invalid]="form.controls.email.invalid && form.controls.email.touched"
            />
          </label>

          <label class="field">
            <span>Contrasena</span>
            <input
              type="password"
              formControlName="password"
              placeholder="Tu contrasena"
              [class.invalid]="form.controls.password.invalid && form.controls.password.touched"
            />
          </label>

          <button class="primary-btn" type="submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Validando...' : 'Entrar' }}
          </button>
        </form>

        <p class="error-banner" *ngIf="errorMessage">{{ errorMessage }}</p>
      </article>
    </section>
  `,
  styles: [
    `
      .auth-shell {
        min-height: 100vh;
        display: grid;
        place-items: center;
        padding: 1.5rem;
      }

      .auth-card {
        width: min(460px, 100%);
        padding: 2rem;
        border-radius: 22px;
        border: 1px solid #d5dceb;
        background: #ffffff;
        box-shadow: 0 18px 46px rgba(14, 31, 64, 0.14);
      }

      .auth-head h1 {
        margin: 0;
        font-size: clamp(1.7rem, 3vw, 2.2rem);
        line-height: 1.15;
      }

      .auth-kicker {
        margin: 0 0 0.35rem;
        color: #0f766e;
        font-size: 0.82rem;
        letter-spacing: 0.08em;
        font-weight: 700;
        text-transform: uppercase;
      }

      .auth-copy {
        margin: 0.75rem 0 0;
        color: #4f5f7d;
        line-height: 1.45;
      }

      .auth-form {
        margin-top: 1.4rem;
        display: grid;
        gap: 0.95rem;
      }

      .field {
        display: grid;
        gap: 0.45rem;
        font-weight: 600;
        color: #25324a;
      }

      .field input {
        width: 100%;
        padding: 0.78rem 0.82rem;
        border: 1px solid #c9d2e4;
        border-radius: 12px;
        font: inherit;
        color: #13223d;
        background: #f9fbff;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;
      }

      .field input:focus {
        outline: none;
        border-color: #0d9488;
        box-shadow: 0 0 0 4px rgba(13, 148, 136, 0.14);
      }

      .field input.invalid {
        border-color: #dc2626;
      }

      .primary-btn {
        margin-top: 0.4rem;
        border: 0;
        border-radius: 12px;
        background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
        color: #ffffff;
        font: inherit;
        font-weight: 700;
        padding: 0.8rem 1rem;
        cursor: pointer;
        transition: transform 0.15s ease, filter 0.2s ease;
      }

      .primary-btn:hover:not(:disabled) {
        transform: translateY(-1px);
        filter: brightness(1.03);
      }

      .primary-btn:disabled {
        cursor: not-allowed;
        opacity: 0.65;
      }

      .error-banner {
        margin: 1rem 0 0;
        padding: 0.72rem 0.86rem;
        border-radius: 10px;
        background: #fee2e2;
        color: #991b1b;
        font-weight: 600;
      }
    `
  ]
})
export class LoginComponent {
  loading = false;
  errorMessage = '';
  readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authApiService: AuthApiService,
    private readonly authSessionService: AuthSessionService,
    private readonly router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Completa los campos requeridos';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    const value = this.form.getRawValue();
    this.authApiService.login({
      email: value.email ?? '',
      password: value.password ?? ''
    }).subscribe({
      next: (response: LoginResponse) => {
        this.authSessionService.setSession(response.empleado, response.sessionExpiresAt);
        this.loading = false;
        void this.router.navigate(['/perfil']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Credenciales invalidas o cuenta bloqueada';
      }
    });
  }
}
