export function adjustDateForTimezone(date: Date): Date {
  const offsetInMinutes = date.getTimezoneOffset();
  const offsetHours = Math.floor(offsetInMinutes / -60);
  const offsetMinutes = Math.abs(offsetInMinutes % 60);

  date.setHours(offsetHours);
  date.setMinutes(offsetMinutes);

  return date;
}
