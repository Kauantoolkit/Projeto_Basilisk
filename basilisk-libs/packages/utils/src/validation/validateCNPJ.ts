/**
 * Valida um CNPJ brasileiro (com ou sem máscara).
 *
 * @example
 * validateCNPJ("11.222.333/0001-81") // true
 * validateCNPJ("00.000.000/0000-00") // false
 */
export function validateCNPJ(cnpj: string): boolean {
  const cleaned = cnpj.replace(/\D/g, "");

  if (cleaned.length !== 14) return false;
  if (/^(\d)\1+$/.test(cleaned)) return false;

  const calcDigit = (digits: string, weights: number[]): number => {
    const sum = digits
      .split("")
      .reduce((acc, d, i) => acc + parseInt(d) * weights[i], 0);
    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  };

  const w1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const w2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

  const d1 = calcDigit(cleaned.slice(0, 12), w1);
  const d2 = calcDigit(cleaned.slice(0, 13), w2);

  return parseInt(cleaned[12]) === d1 && parseInt(cleaned[13]) === d2;
}
