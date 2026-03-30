interface FormatCurrencyOptions {
  locale?: string;
  currency?: string;
  minimumFractionDigits?: number;
  maximumFractionDigits?: number;
}

/**
 * Formata um número como moeda brasileira (ou configurável).
 *
 * @example
 * formatCurrency(1234.5)              // "R$ 1.234,50"
 * formatCurrency(1234.5, { currency: "USD" }) // "US$ 1,234.50"
 */
export function formatCurrency(
  value: number,
  options: FormatCurrencyOptions = {}
): string {
  const {
    locale = "pt-BR",
    currency = "BRL",
    minimumFractionDigits = 2,
    maximumFractionDigits = 2,
  } = options;

  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    minimumFractionDigits,
    maximumFractionDigits,
  }).format(value);
}
