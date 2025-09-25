import { CheckboxItemControlWrapper } from './checkbox-item-control-wrapper';
import { UntypedFormArray, UntypedFormControl } from '@angular/forms';
import { createCheckboxGroupValidator } from '../validators/checkbox-group.validator';

export class CheckboxGroupControlWrapper<T> {
  name?: string;
  required?: boolean;

  items: CheckboxItemControlWrapper<T>[];
  control: UntypedFormArray;

  constructor(
    items: CheckboxItemControlWrapper<T>[] = [],
    name: string = '',
    required: boolean = false
  ) {
    this.name = name;
    this.items = items;
    this.required = required;

    this.control = new UntypedFormArray(
      this.getAllItemControls(),
      createCheckboxGroupValidator(this.required)
    );
  }

  get value(): T[] {
    return this.selectedItems.map((item) => item.value);
  }

  private get selectedItems(): CheckboxItemControlWrapper<T>[] {
    return this.items.filter((item) => item.selected);
  }

  private getAllItemControls(): UntypedFormControl[] {
    return this.items.map((item) => item.control);
  }
}
