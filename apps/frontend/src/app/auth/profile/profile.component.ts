import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthApiService } from '../../core/services/auth-api.service';
import { AuthSessionService } from '../../core/services/auth-session.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="profile-shell">
      <article class="profile-card">
        <header class="profile-head">
          <p class="profile-kicker">Sesion activa</p>
          <h1>Bienvenido</h1>
          <p class="profile-copy">Estos son los datos del usuario que inicio sesion.</p>
        </header>

        <div class="profile-grid" *ngIf="user as currentUser; else noSession">
          <div class="row">
            <span class="label">Clave</span>
            <strong>{{ currentUser.clave }}</strong>
          </div>
          <div class="row">
            <span class="label">Nombre</span>
            <strong>{{ currentUser.nombre }}</strong>
          </div>
          <div class="row">
            <span class="label">Email</span>
            <strong>{{ currentUser.email }}</strong>
          </div>
          <div class="row" *ngIf="currentUser.direccion">
            <span class="label">Direccion</span>
            <strong>{{ currentUser.direccion }}</strong>
          </div>
          <div class="row" *ngIf="currentUser.telefono">
            <span class="label">Telefono</span>
            <strong>{{ currentUser.telefono }}</strong>
          </div>
          <div class="row" *ngIf="currentUser.departamento">
            <span class="label">Departamento</span>
            <strong>{{ currentUser.departamento.nombre }} ({{ currentUser.departamento.id }})</strong>
          </div>

          <div class="actions">
            <button class="ghost-btn" type="button" (click)="logout()" [disabled]="loading">
              {{ loading ? 'Cerrando...' : 'Cerrar sesion' }}
            </button>
          </div>
        </div>

        <p class="error-banner" *ngIf="errorMessage">{{ errorMessage }}</p>

        <ng-template #noSession>
          <p class="empty-copy">No hay una sesion activa.</p>
          <button class="primary-btn" type="button" (click)="goToLogin()">Ir a login</button>
        </ng-template>
      </article>
    </section>
  `,
  styles: [
    `
      .profile-shell {
        min-height: 100vh;
        display: grid;
        place-items: center;
        padding: 1.5rem;
      }

      .profile-card {
        width: min(760px, 100%);
        border-radius: 24px;
        border: 1px solid #d5dceb;
        background: #ffffff;
        box-shadow: 0 18px 46px rgba(14, 31, 64, 0.14);
        padding: 1.9rem;
      }

      .profile-kicker {
        margin: 0;
        color: #0f766e;
        font-size: 0.82rem;
        letter-spacing: 0.08em;
        font-weight: 700;
        text-transform: uppercase;
      }

      .profile-head h1 {
        margin: 0.25rem 0 0;
        font-size: clamp(1.7rem, 3vw, 2.3rem);
      }

      .profile-copy {
        margin: 0.65rem 0 0;
        color: #4f5f7d;
      }

      .profile-grid {
        margin-top: 1.25rem;
        display: grid;
        gap: 0.72rem;
      }

      .row {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        gap: 0.8rem;
        padding: 0.74rem 0.86rem;
        border: 1px solid #dbe2f0;
        border-radius: 12px;
        background: #f9fbff;
      }

      .label {
        color: #4f5f7d;
        font-weight: 600;
      }

      .actions {
        margin-top: 0.5rem;
      }

      .primary-btn,
      .ghost-btn {
        border-radius: 12px;
        font: inherit;
        font-weight: 700;
        padding: 0.76rem 1.05rem;
        cursor: pointer;
      }

      .primary-btn {
        border: 0;
        background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
        color: #ffffff;
      }

      .ghost-btn {
        border: 1px solid #c9d2e4;
        background: #ffffff;
        color: #22314d;
      }

      .ghost-btn:hover:not(:disabled),
      .primary-btn:hover:not(:disabled) {
        filter: brightness(1.03);
      }

      .ghost-btn:disabled,
      .primary-btn:disabled {
        cursor: not-allowed;
        opacity: 0.65;
      }

      .empty-copy {
        color: #4f5f7d;
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
export class ProfileComponent {
  loading = false;
  errorMessage = '';

  constructor(
    private readonly authApiService: AuthApiService,
    private readonly authSessionService: AuthSessionService,
    private readonly router: Router
  ) {}

  get user() {
    return this.authSessionService.getUser();
  }

  logout(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authApiService.logout().subscribe({
      next: () => {
        this.authSessionService.clearSession();
        void this.router.navigate(['/login']);
      },
      error: () => {
        this.authSessionService.clearSession();
        this.errorMessage = 'La sesion se cerro localmente';
        void this.router.navigate(['/login']);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  goToLogin(): void {
    void this.router.navigate(['/login']);
  }
}
