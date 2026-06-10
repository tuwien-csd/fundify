import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProgramVersionHistoryDialogComponent } from './program-version-history-dialog.component';
import { BackendServiceV2 } from '../../../core/services/backend-service-v2.service';

describe('ProgramVersionHistoryDialogComponent', () => {
  let component: ProgramVersionHistoryDialogComponent;
  let fixture: ComponentFixture<ProgramVersionHistoryDialogComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- mock object
  let mockBackendService: { client: { GET: jasmine.Spy } };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- mock object
  let mockDialogRef: { close: jasmine.Spy };

  beforeEach(async () => {
    mockBackendService = {
      client: {
        GET: jasmine.createSpy('GET').and.returnValue(Promise.resolve({ data: [] })),
      },
    };
    mockDialogRef = { close: jasmine.createSpy('close') };

    await TestBed.configureTestingModule({
      imports: [ProgramVersionHistoryDialogComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BackendServiceV2, useValue: mockBackendService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { programId: 'program-1', currentFields: {} } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramVersionHistoryDialogComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit fetches versions and sets loading to false', async () => {
    const versions = [
      { id: 'v1', programId: 'program-1', versionedAt: '2024-01-01T10:00:00' },
      { id: 'v2', programId: 'program-1', versionedAt: '2024-02-01T10:00:00' },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.loading).toBeFalse();
    expect(component.versions.length).toBe(2);
  });

  describe('goNewer and goOlder navigation', () => {
    beforeEach(async () => {
      const versions = [
        { id: 'v-new', programId: 'program-1', versionedAt: '2024-06-01T10:00:00' },
        { id: 'v-old', programId: 'program-1', versionedAt: '2024-01-01T10:00:00' },
      ];
      mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

      fixture.detectChanges();
      await fixture.whenStable();
    });

    it('goOlder increments currentIndex when not at oldest', () => {
      component.currentIndex = 0;
      component.goOlder();
      expect(component.currentIndex).toBe(1);
    });

    it('goOlder does nothing when at oldest', () => {
      component.currentIndex = component.versions.length - 1;
      component.goOlder();
      expect(component.currentIndex).toBe(component.versions.length - 1);
    });

    it('goNewer decrements currentIndex when not at latest', () => {
      component.currentIndex = 1;
      component.goNewer();
      expect(component.currentIndex).toBe(0);
    });

    it('goNewer does nothing when at latest (index 0)', () => {
      component.currentIndex = 0;
      component.goNewer();
      expect(component.currentIndex).toBe(0);
    });
  });

  it('formatTranslatedText returns em-dash for empty', () => {
    expect(component.formatTranslatedText(undefined)).toBe('—');
    expect(component.formatTranslatedText([])).toBe('—');
  });

  it('formatDateRange returns em-dash for undefined', () => {
    expect(component.formatDateRange(undefined)).toBe('—');
  });

  it('formatDateRange formats start and end date', () => {
    const result = component.formatDateRange({
      start: '2024-03-05T00:00:00',
      end: '2025-11-20T00:00:00',
    });
    expect(result).toBe('05.03.24 – 20.11.25');
  });

  it('onClose calls dialogRef.close', () => {
    component.onClose();
    expect(mockDialogRef.close).toHaveBeenCalled();
  });

  it('computeChangedFields detects description change', async () => {
    const versions = [
      {
        id: 'v1',
        programId: 'program-1',
        versionedAt: '2024-01-01T10:00:00',
        description: [{ text: 'Old Desc', language: 'EN' }],
      },
    ];
    mockBackendService.client.GET.and.returnValue(Promise.resolve({ data: versions }));

    // Override data so currentFields differ from the version
    component.data = {
      programId: 'program-1',
      currentFields: { description: [{ text: 'New Desc', language: 'EN' }] },
    };

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.changedFields[0].has('description')).toBeTrue();
  });
});
