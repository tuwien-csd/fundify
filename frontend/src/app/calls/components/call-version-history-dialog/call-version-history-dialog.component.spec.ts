import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CallVersionHistoryDialogComponent } from './call-version-history-dialog.component';
import { BackendServiceV2 } from '../../../core/services/backend-service-v2.service';
import { NotificationService } from '../../../shared/services/notification-service.service';

describe('CallVersionHistoryDialogComponent', () => {
  let component: CallVersionHistoryDialogComponent;
  let fixture: ComponentFixture<CallVersionHistoryDialogComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- mock object
  let mockBackendService: { client: { GET: jasmine.Spy } };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- mock object
  let mockDialogRef: { close: jasmine.Spy };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- mock object
  let mockNotificationService: { error: jasmine.Spy; success: jasmine.Spy };

  beforeEach(async () => {
    mockBackendService = {
      client: {
        GET: jasmine.createSpy('GET').and.returnValue(Promise.resolve({ data: [] })),
      },
    };
    mockDialogRef = { close: jasmine.createSpy('close') };
    mockNotificationService = {
      error: jasmine.createSpy('error'),
      success: jasmine.createSpy('success'),
    };

    await TestBed.configureTestingModule({
      imports: [CallVersionHistoryDialogComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BackendServiceV2, useValue: mockBackendService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { callId: 'call-1', currentFields: {} } },
        { provide: NotificationService, useValue: mockNotificationService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CallVersionHistoryDialogComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit fetches versions and sets loading to false', async () => {
    const versions = [
      { id: 'v1', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
      { id: 'v2', callId: 'call-1', versionedAt: '2024-02-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.loading).toBeFalse();
    expect(component.versions.length).toBe(2);
  });

  it('ngOnInit sets loadError and notifies when the request returns an error', async () => {
    mockBackendService.client.GET.and.returnValue(
      Promise.resolve({ error: { message: 'boom' } })
    );

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.loading).toBeFalse();
    expect(component.loadError).toBeTrue();
    expect(component.versions.length).toBe(0);
    expect(mockNotificationService.error).toHaveBeenCalled();
  });

  it('ngOnInit sets loadError and notifies when the request rejects', async () => {
    mockBackendService.client.GET.and.returnValue(Promise.reject(new Error('network')));

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.loading).toBeFalse();
    expect(component.loadError).toBeTrue();
    expect(mockNotificationService.error).toHaveBeenCalled();
  });

  it('ngOnInit sorts versions by versionedAt descending', async () => {
    const versions = [
      { id: 'v-old', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
      { id: 'v-new', callId: 'call-1', versionedAt: '2024-06-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.versions[0].id).toBe('v-new');
    expect(component.versions[1].id).toBe('v-old');
  });

  it('goNewer decrements currentIndex when not at latest', async () => {
    const versions = [
      { id: 'v2', callId: 'call-1', versionedAt: '2024-06-01T10:00:00' },
      { id: 'v1', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = 1;
    component.goNewer();

    expect(component.currentIndex).toBe(0);
  });

  it('goNewer does nothing when at latest (index 0)', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = 0;
    component.goNewer();

    expect(component.currentIndex).toBe(0);
  });

  it('goOlder increments currentIndex when not at oldest', async () => {
    const versions = [
      { id: 'v2', callId: 'call-1', versionedAt: '2024-06-01T10:00:00' },
      { id: 'v1', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = 0;
    component.goOlder();

    expect(component.currentIndex).toBe(1);
  });

  it('goOlder does nothing when at oldest', async () => {
    const versions = [
      { id: 'v2', callId: 'call-1', versionedAt: '2024-06-01T10:00:00' },
      { id: 'v1', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = component.versions.length - 1;
    component.goOlder();

    expect(component.currentIndex).toBe(component.versions.length - 1);
  });

  it('isLatest is true when currentIndex is 0', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = 0;

    expect(component.isLatest).toBeTrue();
  });

  it('isOldest is true when currentIndex equals versions.length - 1', async () => {
    const versions = [
      { id: 'v2', callId: 'call-1', versionedAt: '2024-06-01T10:00:00' },
      { id: 'v1', callId: 'call-1', versionedAt: '2024-01-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    component.currentIndex = component.versions.length - 1;

    expect(component.isOldest).toBeTrue();
  });

  it('formatTranslatedText returns em-dash for empty input', () => {
    expect(component.formatTranslatedText(undefined)).toBe('—');
    expect(component.formatTranslatedText([])).toBe('—');
  });

  it('formatTranslatedText formats texts with language', () => {
    const result = component.formatTranslatedText([
      { text: 'Hello', language: 'EN' },
      { text: 'Hallo', language: 'DE' },
    ]);
    expect(result).toBe('Hello (EN), Hallo (DE)');
  });

  it('formatVolume returns em-dash for undefined input', () => {
    expect(component.formatVolume(undefined)).toBe('—');
  });

  it('formatVolume formats amount and currency', () => {
    expect(component.formatVolume({ amount: 1000, currency: 'EUR' })).toBe('1000 EUR');
  });

  it('onClose calls dialogRef.close', () => {
    component.onClose();
    expect(mockDialogRef.close).toHaveBeenCalled();
  });

  it('computeChangedFields detects name change between version and currentFields', async () => {
    const versions = [
      {
        id: 'v1',
        callId: 'call-1',
        versionedAt: '2024-01-01T10:00:00',
        name: [{ text: 'Old Name', language: 'EN' }],
      },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    // Override the injected data to include differing currentFields
    component.data = {
      callId: 'call-1',
      currentFields: { name: [{ text: 'New Name', language: 'EN' }] },
    };

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.changedFields[0].has('name')).toBeTrue();
  });

  it('computeChangedFields returns empty set when nothing changed', async () => {
    const nameValue = [{ text: 'Same Name', language: 'EN' }];
    const versions = [
      {
        id: 'v1',
        callId: 'call-1',
        versionedAt: '2024-01-01T10:00:00',
        name: nameValue,
      },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    // Override data so currentFields match the version exactly
    component.data = {
      callId: 'call-1',
      currentFields: { name: nameValue },
    };

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.changedFields[0].size).toBe(0);
  });
});
