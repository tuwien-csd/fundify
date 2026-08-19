import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNativeDateAdapter } from '@angular/material/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { MatDialog } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { CallsComponent } from './calls.component';
import { CallsStore } from './signal/calls-store';
import { CallWebModel } from './models/call.interface';
import { OAuthServiceMock } from '../testing/mocks/OAuthService.mock';

describe('CallsComponent', () => {
  let fixture: ComponentFixture<CallsComponent>;
  let isLoading: ReturnType<typeof signal<boolean>>;

  const spinner = () =>
    fixture.nativeElement.querySelector('.spinner-container');
  const tableContainer = (): HTMLElement =>
    fixture.nativeElement.querySelector('.table-wrapper');

  beforeEach(async () => {
    isLoading = signal(true);

    await TestBed.configureTestingModule({
      imports: [CallsComponent, RouterTestingModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: OAuthService, useValue: OAuthServiceMock },
        { provide: MatDialog, useValue: { open: jasmine.createSpy('open') } },
        {
          provide: CallsStore,
          useValue: {
            isLoading,
            entities: signal<CallWebModel[]>([]),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CallsComponent);
    fixture.detectChanges();
  });

  it('should show the spinner and hide the table while the calls are loading', () => {
    expect(spinner()).toBeTruthy();
    expect(tableContainer().hidden).toBeTrue();
  });

  it('should hide the spinner and show the table once the calls are loaded', () => {
    isLoading.set(false);
    fixture.detectChanges();

    expect(spinner()).toBeFalsy();
    expect(tableContainer().hidden).toBeFalse();
  });
});
