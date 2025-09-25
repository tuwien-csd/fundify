import { UntypedFormControl } from '@angular/forms';

export class CheckboxItemControlWrapper<T> {
  label: string; // value to be shown in the UI
  value: T; // value to be saved in backend

  control: UntypedFormControl;

  constructor({
    label,
    value,
    defaultValue = false,
  }: {
    label: string;
    value: T;
    defaultValue?: boolean;
  }) {
    this.label = label;
    this.value = value;
    this.control = new UntypedFormControl(defaultValue || false);
  }

  get selected(): boolean {
    return Boolean(this.control.value);
  }
}
