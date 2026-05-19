import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  it('should create the app root component', () => {
    TestBed.configureTestingModule({
      imports: [AppComponent]
    });

    const fixture = TestBed.createComponent(AppComponent);
    const component = fixture.componentInstance;

    expect(component).toBeTruthy();
  });
});