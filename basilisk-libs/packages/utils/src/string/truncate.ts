/**
 * Trunca uma string no limite de caracteres indicado, adicionando sufixo.
 *
 * @example
 * truncate("Hello World", 5)       // "Hello..."
 * truncate("Hello World", 5, " →") // "Hello →"
 */
export function truncate(str: string, maxLength: number, suffix = "..."): string {
  if (str.length <= maxLength) return str;
  return str.slice(0, maxLength).trimEnd() + suffix;
}
