import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UrlFieldComponent } from './url-field.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

describe('UrlFieldComponent', () => {
  let component: UrlFieldComponent;
  let fixture: ComponentFixture<UrlFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, UrlFieldComponent],
    }).compileComponents();
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UrlFieldComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UrlFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control value by default', () => {
    expect(component.urlForm.controls['url'].value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.urlForm.controls['url'].valid).toBeTruthy();
  });
});
