import { cn } from "@basilisk/utils";

export type StatusColor = "brand" | "success" | "warning" | "danger" | "info" | "neutral";

interface StatusBadgeProps {
  status: string;
  statusMap?: Record<string, { label: string; color: StatusColor }>;
  size?: "sm" | "md";
  pulse?: boolean;
  className?: string;
}

const defaultStatusMap: Record<string, { label: string; color: StatusColor }> = {
  PENDING: { label: "Pendente", color: "warning" },
  IN_PROGRESS: { label: "Em andamento", color: "info" },
  READY: { label: "Pronto", color: "success" },
  DELIVERED: { label: "Entregue", color: "brand" },
  CANCELLED: { label: "Cancelado", color: "danger" },
  ACTIVE: { label: "Ativo", color: "success" },
  INACTIVE: { label: "Inativo", color: "neutral" },
  SCHEDULED: { label: "Agendado", color: "info" },
};

const colorDotVars: Record<StatusColor, string> = {
  brand: "var(--bsk-brand)",
  success: "#16a34a",
  warning: "#f59e0b",
  danger: "var(--bsk-danger)",
  info: "#3b82f6",
  neutral: "var(--bsk-text-secondary)",
};

export function StatusBadge({
  status,
  statusMap,
  size = "md",
  pulse = false,
  className,
}: StatusBadgeProps) {
  const map = { ...defaultStatusMap, ...statusMap };
  const entry = map[status];
  const label = entry?.label ?? status;
  const color: StatusColor = entry?.color ?? "neutral";
  const dotColor = colorDotVars[color];

  const dotSize = size === "sm" ? "0.375rem" : "0.5rem";
  const fontSize = size === "sm" ? "0.6875rem" : "0.75rem";
  const px = size === "sm" ? "0.5rem" : "0.625rem";
  const py = size === "sm" ? "0.125rem" : "0.25rem";

  return (
    <span
      className={cn("inline-flex items-center gap-1.5 rounded-full font-medium", className)}
      style={{
        fontSize,
        paddingLeft: px,
        paddingRight: px,
        paddingTop: py,
        paddingBottom: py,
        backgroundColor: `color-mix(in srgb, ${dotColor} 12%, transparent)`,
        color: dotColor,
      }}
    >
      <span
        className={cn("shrink-0 rounded-full", pulse && "animate-pulse")}
        style={{
          width: dotSize,
          height: dotSize,
          backgroundColor: dotColor,
        }}
        aria-hidden="true"
      />
      {label}
    </span>
  );
}

StatusBadge.displayName = "StatusBadge";
