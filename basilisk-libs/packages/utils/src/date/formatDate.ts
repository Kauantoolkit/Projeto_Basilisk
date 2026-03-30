interface FormatDateOptions {
  locale?: string;
  format?: "short" | "medium" | "long" | "full";
  withTime?: boolean;
}

/**
 * Formata uma data com localização e formato configuráveis.
 *
 * @example
 * formatDate(new Date())                       // "29/03/2026"
 * formatDate(new Date(), { withTime: true })   // "29/03/2026 14:30"
 * formatDate(new Date(), { format: "long" })   // "29 de março de 2026"
 */
export function formatDate(date: Date | string | number, options: FormatDateOptions = {}): string {
  const { locale = "pt-BR", format = "short", withTime = false } = options;
  const d = new Date(date);

  const dateOptions: Intl.DateTimeFormatOptions =
    format === "short"
      ? { day: "2-digit", month: "2-digit", year: "numeric" }
      : format === "medium"
      ? { day: "2-digit", month: "short", year: "numeric" }
      : format === "long"
      ? { day: "2-digit", month: "long", year: "numeric" }
      : { weekday: "long", day: "2-digit", month: "long", year: "numeric" };

  if (withTime) {
    dateOptions.hour = "2-digit";
    dateOptions.minute = "2-digit";
  }

  return new Intl.DateTimeFormat(locale, dateOptions).format(d);
}

/**
 * Retorna a diferença em dias entre duas datas.
 */
export function diffInDays(dateA: Date, dateB: Date = new Date()): number {
  const msPerDay = 1000 * 60 * 60 * 24;
  return Math.round((dateB.getTime() - dateA.getTime()) / msPerDay);
}
