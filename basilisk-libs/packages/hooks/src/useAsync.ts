import { useCallback, useEffect, useRef, useState } from "react";

type AsyncStatus = "idle" | "loading" | "success" | "error";

interface UseAsyncState<T> {
  status: AsyncStatus;
  data: T | null;
  error: Error | null;
  isLoading: boolean;
  isSuccess: boolean;
  isError: boolean;
}

interface UseAsyncReturn<T> extends UseAsyncState<T> {
  execute: (...args: unknown[]) => Promise<T | null>;
  reset: () => void;
}

/**
 * Gerencia estado de operações assíncronas (loading, data, error).
 *
 * @example
 * const { execute, isLoading, data, error } = useAsync(fetchUsers);
 * useEffect(() => { execute() }, []);
 */
export function useAsync<T>(
  asyncFunction: (...args: unknown[]) => Promise<T>,
  immediate = false
): UseAsyncReturn<T> {
  const [state, setState] = useState<UseAsyncState<T>>({
    status: "idle",
    data: null,
    error: null,
    isLoading: false,
    isSuccess: false,
    isError: false,
  });

  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => { mountedRef.current = false; };
  }, []);

  const execute = useCallback(
    async (...args: unknown[]) => {
      setState({ status: "loading", data: null, error: null, isLoading: true, isSuccess: false, isError: false });
      try {
        const result = await asyncFunction(...args);
        if (mountedRef.current) {
          setState({ status: "success", data: result, error: null, isLoading: false, isSuccess: true, isError: false });
        }
        return result;
      } catch (err) {
        const error = err instanceof Error ? err : new Error(String(err));
        if (mountedRef.current) {
          setState({ status: "error", data: null, error, isLoading: false, isSuccess: false, isError: true });
        }
        return null;
      }
    },
    [asyncFunction]
  );

  useEffect(() => {
    if (immediate) execute();
  }, [immediate]); // eslint-disable-line react-hooks/exhaustive-deps

  const reset = useCallback(() => {
    setState({ status: "idle", data: null, error: null, isLoading: false, isSuccess: false, isError: false });
  }, []);

  return { ...state, execute, reset };
}
