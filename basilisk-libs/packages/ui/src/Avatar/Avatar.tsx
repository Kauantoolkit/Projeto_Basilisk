import { cn } from "@basilisk/utils";

export interface AvatarProps {
  /** URL da imagem */
  src?: string;
  /** Nome para gerar iniciais */
  name?: string;
  /** Tamanho */
  size?: "sm" | "md" | "lg" | "xl";
  /** Classe CSS adicional */
  className?: string;
}

const sizeMap = {
  sm: { dimension: 32, text: "text-xs" },
  md: { dimension: 40, text: "text-sm" },
  lg: { dimension: 48, text: "text-base" },
  xl: { dimension: 64, text: "text-lg" },
} as const;

function getInitials(name: string): string {
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

export function Avatar({ src, name, size = "md", className }: AvatarProps) {
  const { dimension, text } = sizeMap[size];

  if (src) {
    return (
      <img
        src={src}
        alt={name || "Avatar"}
        className={cn("rounded-full object-cover shrink-0", className)}
        style={{ width: dimension, height: dimension }}
      />
    );
  }

  return (
    <div
      className={cn(
        "rounded-full shrink-0 inline-flex items-center justify-center font-medium text-white",
        "bg-[var(--bsk-brand)]",
        text,
        className
      )}
      style={{ width: dimension, height: dimension }}
      role="img"
      aria-label={name || "Avatar"}
    >
      {name ? getInitials(name) : "?"}
    </div>
  );
}
