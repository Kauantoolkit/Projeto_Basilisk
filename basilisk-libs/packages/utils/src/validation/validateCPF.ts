/**
 * Valida um CPF brasileiro (com ou sem máscara).
 *
 * @example
 * validateCPF("123.456.789-09") // false
 * validateCPF("529.982.247-25") // true
 */
export function validateCPF(cpf: string): boolean {
  const cleaned = cpf.replace(/\D/g, "");

  if (cleaned.length !== 11) return false;
  if (/^(\d)\1+$/.test(cleaned)) return false;

  const calcDigit = (digits: string, factor: number): number => {
    const sum = digits
      .split("")
      .reduce((acc, d, i) => acc + parseInt(d) * (factor - i), 0);
    const remainder = (sum * 10) % 11;
    return remainder >= 10 ? 0 : remainder;
  };

  const d1 = calcDigit(cleaned.slice(0, 9), 10);
  const d2 = calcDigit(cleaned.slice(0, 10), 11);

  return parseInt(cleaned[9]) === d1 && parseInt(cleaned[10]) === d2;
}
