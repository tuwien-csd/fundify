import {
  Component,
  forwardRef,
  Injectable,
  Input,
  input,
  OnInit,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  NG_VALUE_ACCESSOR,
  Validators,
} from '@angular/forms';
import { POST_ADDRESS_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { TextFieldComponent } from '../text-field/text-field.component';
import { PostAddress } from '../../models/interfaces/post-address.interface';
import { SharedModule } from '../../shared.module';

export type AddressFrom = {
  streetLine: FormControl<string>;
  city: FormControl<string>;
  postalCode: FormControl<string>;
  countryCode: FormControl<string>;
};

@Component({
  selector: 'app-address-form',
  templateUrl: './address-form.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AddressFormComponent),
      multi: true,
    },
  ],
  imports: [TextFieldComponent, FormsModule, SharedModule],
})
export class AddressFormComponent implements OnInit {
  @Input() required: boolean = false;
  private fb = new FormBuilder();

  inputFormGroup = input.required<FormGroup<AddressFrom>>();

  constants = POST_ADDRESS_LABELS;
  validators = VALIDATORS;
  label: string = this.constants.ADDRESS_LABEL;

  ngOnInit(): void {
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }
}

@Injectable({ providedIn: 'root' })
export class AddressFormFactory {
  private fb = new FormBuilder();

  public buildFormGroup(postAddress?: PostAddress): FormGroup<AddressFrom> {
    return this.fb.group<AddressFrom>({
      streetLine: this.fb.control(postAddress?.streetLine ?? '', {
        nonNullable: true,
        validators: [Validators.pattern(/\S/)],
      }),
      city: this.fb.control(postAddress?.city ?? '', { nonNullable: true }),
      postalCode: this.fb.control(postAddress?.postalCode ?? '', {
        nonNullable: true,
        validators: [Validators.pattern(/\S/)],
      }),
      countryCode: this.fb.control(postAddress?.countryCode ?? '', {
        nonNullable: true,
        validators: [Validators.pattern(/\S/)],
      }),
    });
  }
}
