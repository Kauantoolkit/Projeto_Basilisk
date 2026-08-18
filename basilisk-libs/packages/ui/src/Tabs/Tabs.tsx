import { cn } from "@basilisk/utils";

export interface TabItem {
  /** Valor identificador da tab */
  value: string;
  /** Texto exibido */
  label: string;
  /** Contador opcional */
  count?: number;
}

export interface TabsProps {
  /** Lista de tabs */
  tabs: TabItem[];
  /** Tab ativa */
  activeTab: string;
  /** Callback ao trocar de tab */
  onTabChange: (value: string) => void;
  /** Classe CSS adicional */
  className?: string;
}

export function Tabs({ tabs, activeTab, onTabChange, className }: TabsProps) {
  return (
    <div
      className={cn(
        "flex border-b border-[var(--bsk-border-light)]",
        className
      )}
      role="tablist"
    >
      {tabs.map((tab) => {
        const isActive = tab.value === activeTab;

        return (
          <button
            key={tab.value}
            type="button"
            role="tab"
            aria-selected={isActive}
            onClick={() => onTabChange(tab.value)}
            className={cn(
              "relative px-4 py-2.5 text-sm font-medium transition-colors duration-150",
              "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-[var(--bsk-brand)]",
              isActive
                ? "text-[var(--bsk-brand)]"
                : "text-[var(--bsk-text-secondary)] hover:text-[var(--bsk-text)]"
            )}
          >
            <span className="flex items-center gap-1.5">
              {tab.label}
              {tab.count !== undefined && (
                <span
                  className={cn(
                    "inline-flex items-center justify-center rounded-full px-1.5 py-0.5 text-xs",
                    isActive
                      ? "bg-[var(--bsk-brand-light)] text-[var(--bsk-brand)]"
                      : "bg-[var(--bsk-surface-raised)] text-[var(--bsk-text-secondary)]"
                  )}
                >
                  {tab.count}
                </span>
              )}
            </span>

            {isActive && (
              <span
                className="absolute bottom-0 left-0 right-0 h-0.5 bg-[var(--bsk-brand)]"
                aria-hidden="true"
              />
            )}
          </button>
        );
      })}
    </div>
  );
}
