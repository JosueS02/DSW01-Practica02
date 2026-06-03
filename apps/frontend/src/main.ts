import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

export function bootstrapApp() {
  return bootstrapApplication(AppComponent, {
    providers: [provideRouter(routes), provideHttpClient()]
  });
}

bootstrapApp().catch((err) => console.error(err));
