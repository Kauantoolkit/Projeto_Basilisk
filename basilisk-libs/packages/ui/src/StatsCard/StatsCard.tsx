import { cn } from "@basilisk/utils";

interface StatsDelta {
  value: number;
  label?: string;
}

interface StatsCardProps {
  label: string;
  value: string | number;
  icon?: React.ReactNode;
  delta?: StatsDelta;
  className?: string;
}

export function StatsCard({
  label,
  value,
  icon,
  delta,
  className,
}: StatsCardProps) {
  const deltaPositive = delta && delta.value > 0;
  const deltaNeutral = delta && delta.value === 0;
  const deltaColor = deltaNeutral
    ? "var(--bsk-text-secondary)"
    : deltaPositive
    ? "#16a34a"
    : "var(--bsk-danger)";
  const deltaArrow = deltaNeutral ? "→" : deltaPositive ? "↑" : "↓";

  return (
    <div
      className={cn("overflow-hidden", className)}
      style={{
        backgroundColor: "var(--bsk-surface)",
        border: "1px solid var(--bsk-border-light)",
        borderRadius: "12px",
      }}
    >
      <div style={{ padding: "20px" }}>
        {/* Top row: icon + label */}
        <div className="flex items-center" style={{ gap: "12px", marginBottom: "12px" }}>
          {icon && (
            <span
              className="inline-flex items-center justify-center shrink-0"
              style={{
                width: "2.25rem",
                height: "2.25rem",
                borderRadius: "8px",
                backgroundColor: "var(--bsk-surface-hover)",
                color: "var(--bsk-text-secondary)",
              }}
              aria-hidden="true"
            >
              {icon}
            </span>
          )}
          <span
            className="text-xs font-medium uppercase tracking-wide"
            style={{ color: "var(--bsk-text-secondary)" }}
          >
            {label}
          </span>
        </div>

        {/* Value */}
        <p
          className="text-3xl font-bold leading-none"
          style={{
            fontFamily: "var(--bsk-font-body)",
            fontVariantNumeric: "tabular-nums",
            color: "var(--bsk-text)",
            marginBottom: "12px",
          }}
        >
          {value}
        </p>

        {/* Delta */}
        {delta !== undefined && (
          <p
            className="text-xs font-medium inline-flex items-center"
            style={{ color: deltaColor, gap: "4px", fontVariantNumeric: "tabular-nums" }}
          >
            <span aria-hidden="true">{deltaArrow}</span>
            <span>{Math.abs(delta.value)}%</span>
            {delta.label && (
              <span style={{ color: "var(--bsk-text-secondary)" }}>{delta.label}</span>
            )}
          </p>
        )}
      </div>
    </div>
  );
}

StatsCard.displayName = "StatsCard";
