import { cn } from "@basilisk/utils";

export interface SidebarItem {
  /** Icone do item */
  icon?: React.ReactNode;
  /** Texto do item */
  label: string;
  /** Valor identificador */
  value: string;
  /** Contador opcional */
  count?: number;
}

export interface SidebarProps {
  /** Lista de itens de navegacao */
  items: SidebarItem[];
  /** Item ativo */
  activeItem?: string;
  /** Callback ao clicar em um item */
  onItemClick: (value: string) => void;
  /** Conteudo do cabecalho */
  header?: React.ReactNode;
  /** Conteudo do rodape */
  footer?: React.ReactNode;
  /** Classe CSS adicional */
  className?: string;
}

export function Sidebar({
  items,
  activeItem,
  onItemClick,
  header,
  footer,
  className,
}: SidebarProps) {
  return (
    <aside
      className={cn(
        "flex flex-col min-h-screen h-screen sticky top-0 w-60",
        "bg-[var(--bsk-surface-raised)] border-r border-[var(--bsk-border-light)]",
        className
      )}
    >
      {header && (
        <div className="px-4 py-3 border-b border-[var(--bsk-border-light)]">
          {header}
        </div>
      )}

      <nav className="flex-1 overflow-y-auto py-2">
        <ul className="flex flex-col gap-0.5 px-2">
          {items.map((item) => {
            const isActive = item.value === activeItem;

            return (
              <li key={item.value}>
                <button
                  type="button"
                  onClick={() => onItemClick(item.value)}
                  className={cn(
                    "flex items-center gap-3 w-full rounded-[7px] px-3 py-2 text-sm font-medium",
                    "transition-colors duration-150",
                    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-[var(--bsk-brand)]",
                    isActive
                      ? "bg-[var(--bsk-brand-subtle)] text-[var(--bsk-brand)]"
                      : "text-[var(--bsk-text-secondary)] hover:bg-[var(--bsk-surface)] hover:text-[var(--bsk-text)]"
                  )}
                >
                  {item.icon && (
                    <span className="shrink-0" aria-hidden="true">
                      {item.icon}
                    </span>
                  )}

                  <span className="flex-1 text-left truncate">
                    {item.label}
                  </span>

                  {item.count !== undefined && (
                    <span
                      className={cn(
                        "shrink-0 inline-flex items-center justify-center rounded-full px-1.5 py-0.5 text-xs",
                        isActive
                          ? "bg-[var(--bsk-brand-light)] text-[var(--bsk-brand)]"
                          : "bg-[var(--bsk-surface)] text-[var(--bsk-text-secondary)]"
                      )}
                    >
                      {item.count}
                    </span>
                  )}
                </button>
              </li>
            );
          })}
        </ul>
      </nav>

      {footer && (
        <div className="px-4 py-3 border-t border-[var(--bsk-border-light)]">
          {footer}
        </div>
      )}
    </aside>
  );
}
