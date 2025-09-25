import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericDetailsContainerComponent } from './generic-details-container.component';

describe('GenericDetailsContainerComponent', () => {
  let component: GenericDetailsContainerComponent;
  let fixture: ComponentFixture<GenericDetailsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenericDetailsContainerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GenericDetailsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
