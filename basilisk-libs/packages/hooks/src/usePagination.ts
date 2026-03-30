import { useMemo, useState } from "react";

interface UsePaginationOptions {
  total: number;
  initialPage?: number;
  initialLimit?: number;
}

interface UsePaginationReturn {
  page: number;
  limit: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPrevPage: boolean;
  setPage: (page: number) => void;
  setLimit: (limit: number) => void;
  nextPage: () => void;
  prevPage: () => void;
  goToFirstPage: () => void;
  goToLastPage: () => void;
}

/**
 * Gerencia estado de paginação com helpers de navegação.
 *
 * @example
 * const { page, limit, totalPages, nextPage } = usePagination({ total: 100 });
 */
export function usePagination({
  total,
  initialPage = 1,
  initialLimit = 20,
}: UsePaginationOptions): UsePaginationReturn {
  const [page, setPageState] = useState(initialPage);
  const [limit, setLimitState] = useState(initialLimit);

  const totalPages = useMemo(() => Math.max(1, Math.ceil(total / limit)), [total, limit]);

  const setPage = (newPage: number) => {
    setPageState(Math.min(Math.max(1, newPage), totalPages));
  };

  const setLimit = (newLimit: number) => {
    setLimitState(newLimit);
    setPageState(1);
  };

  return {
    page,
    limit,
    totalPages,
    hasNextPage: page < totalPages,
    hasPrevPage: page > 1,
    setPage,
    setLimit,
    nextPage: () => setPage(page + 1),
    prevPage: () => setPage(page - 1),
    goToFirstPage: () => setPage(1),
    goToLastPage: () => setPage(totalPages),
  };
}
