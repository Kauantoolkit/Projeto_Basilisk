import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * Combina classes CSS com suporte a Tailwind Merge.
 * Usa clsx para lógica condicional e twMerge para resolver conflitos do Tailwind.
 *
 * @example
 * cn("px-4 py-2", isActive && "bg-basilisk-500", className)
 */
export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
