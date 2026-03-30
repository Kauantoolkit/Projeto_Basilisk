/**
 * Converte uma string em slug amigável para URL.
 * Remove acentos, caracteres especiais e substitui espaços por hífens.
 *
 * @example
 * slugify("Olá Mundo!")  // "ola-mundo"
 * slugify("São Paulo")   // "sao-paulo"
 */
export function slugify(str: string): string {
  return str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9\s-]/g, "")
    .replace(/\s+/g, "-")
    .replace(/-+/g, "-");
}
