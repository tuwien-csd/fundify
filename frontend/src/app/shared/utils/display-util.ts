export function wrapLabelRequiredOrOptional(
  label: string,
  required: boolean
): string {
  if (!label) {
    return '';
  }
  return required ? label + ' *' : label + ' (Optional)';
}
