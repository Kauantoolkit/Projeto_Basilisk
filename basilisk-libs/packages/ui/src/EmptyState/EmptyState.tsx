import { cn } from "@basilisk/utils";

export interface EmptyStateProps {
  /** Icone exibido acima do titulo */
  icon?: React.ReactNode;
  /** Titulo */
  title: string;
  /** Descricao */
  description?: string;
  /** Acao (ex: botao) */
  action?: React.ReactNode;
  /** Classe CSS adicional */
  className?: string;
}

export function EmptyState({
  icon,
  title,
  description,
  action,
  className,
}: EmptyStateProps) {
  return (
    <div
      className={cn(
        "flex flex-col items-center justify-center px-4 text-center",
        className
      )}
      style={{ minHeight: "200px", paddingTop: "48px", paddingBottom: "48px" }}
    >
      {icon && (
        <div
          className="mb-4 inline-flex items-center justify-center"
          style={{
            width: "48px",
            height: "48px",
            borderRadius: "12px",
            backgroundColor: "var(--bsk-surface-hover)",
            color: "var(--bsk-text-secondary)",
          }}
        >
          {icon}
        </div>
      )}

      <h3 className="text-base font-semibold text-[var(--bsk-text)]">
        {title}
      </h3>

      {description && (
        <p className="mt-1.5 max-w-sm text-sm text-[var(--bsk-text-secondary)]">
          {description}
        </p>
      )}

      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}
