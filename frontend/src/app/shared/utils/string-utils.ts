export function equalsIgnoreCase(
  s1?: string | null,
  s2?: string | null
): boolean {
  return s1?.toLowerCase() === s2?.toLowerCase();
}
