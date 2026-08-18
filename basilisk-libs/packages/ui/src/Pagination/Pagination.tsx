import { cn } from "@basilisk/utils";

export interface PaginationProps {
  /** Pagina atual (1-based) */
  currentPage: number;
  /** Total de paginas */
  totalPages: number;
  /** Callback ao trocar de pagina */
  onPageChange: (page: number) => void;
  /** Classe CSS adicional */
  className?: string;
}

function getPageNumbers(current: number, total: number): (number | "ellipsis")[] {
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  const pages: (number | "ellipsis")[] = [1];

  if (current > 3) {
    pages.push("ellipsis");
  }

  const start = Math.max(2, current - 1);
  const end = Math.min(total - 1, current + 1);

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  if (current < total - 2) {
    pages.push("ellipsis");
  }

  pages.push(total);

  return pages;
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  className,
}: PaginationProps) {
  if (totalPages <= 1) return null;

  const pages = getPageNumbers(currentPage, totalPages);

  const baseBtn = cn(
    "inline-flex items-center justify-center rounded-md text-sm font-medium h-9 min-w-[36px] px-2",
    "transition-colors duration-150",
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--bsk-brand)]"
  );

  const ghostBtn = cn(
    baseBtn,
    "text-[var(--bsk-text)] bg-transparent",
    "hover:bg-[var(--bsk-brand-light)]"
  );

  const outlineBtn = cn(
    baseBtn,
    "border border-[var(--bsk-brand)] text-[var(--bsk-brand)] bg-transparent"
  );

  const disabledBtn = cn(baseBtn, "opacity-50 cursor-not-allowed text-[var(--bsk-text-secondary)]");

  return (
    <nav
      className={cn("flex items-center gap-1", className)}
      aria-label="Paginacao"
    >
      <button
        type="button"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage <= 1}
        className={currentPage <= 1 ? disabledBtn : ghostBtn}
        aria-label="Pagina anterior"
      >
        <svg
          className="h-4 w-4"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <polyline points="15 18 9 12 15 6" />
        </svg>
      </button>

      {pages.map((page, index) =>
        page === "ellipsis" ? (
          <span
            key={`ellipsis-${index}`}
            className="inline-flex items-center justify-center h-9 min-w-[36px] text-sm text-[var(--bsk-text-secondary)]"
          >
            ...
          </span>
        ) : (
          <button
            key={page}
            type="button"
            onClick={() => onPageChange(page)}
            className={page === currentPage ? outlineBtn : ghostBtn}
            aria-current={page === currentPage ? "page" : undefined}
            aria-label={`Pagina ${page}`}
          >
            {page}
          </button>
        )
      )}

      <button
        type="button"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage >= totalPages}
        className={currentPage >= totalPages ? disabledBtn : ghostBtn}
        aria-label="Proxima pagina"
      >
        <svg
          className="h-4 w-4"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <polyline points="9 18 15 12 9 6" />
        </svg>
      </button>
    </nav>
  );
}
