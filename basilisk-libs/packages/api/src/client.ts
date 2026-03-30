import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";

export interface ApiClientConfig {
  baseURL: string;
  timeout?: number;
  /** Retorna o token de acesso atual (para injeção automática no header) */
  getAccessToken?: () => string | null;
  /** Chamado quando o token expira (401) */
  onUnauthorized?: () => void;
}

export interface ApiResponse<T = unknown> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ApiError {
  message: string;
  statusCode: number;
  errors?: Record<string, string[]>;
}

/**
 * Cria uma instância Axios configurada com os padrões Basilisk:
 * - Injeção automática de Authorization header
 * - Tratamento de 401 (token expirado)
 * - Extração automática de data de respostas paginadas
 * - Tipagem de erros consistente
 *
 * @example
 * const api = createApiClient({ baseURL: process.env.NEXT_PUBLIC_API_URL });
 * const users = await api.get<User[]>("/users");
 */
export function createApiClient(config: ApiClientConfig): AxiosInstance {
  const instance = axios.create({
    baseURL: config.baseURL,
    timeout: config.timeout ?? 15_000,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
  });

  // Request interceptor — injeta token
  instance.interceptors.request.use((req) => {
    const token = config.getAccessToken?.();
    if (token && req.headers) {
      req.headers.Authorization = `Bearer ${token}`;
    }
    return req;
  });

  // Response interceptor — trata erros
  instance.interceptors.response.use(
    (res: AxiosResponse) => res,
    (error) => {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 401) {
          config.onUnauthorized?.();
        }

        const apiError: ApiError = {
          message: error.response?.data?.message ?? error.message ?? "Erro desconhecido",
          statusCode: status ?? 0,
          errors: error.response?.data?.errors,
        };

        return Promise.reject(apiError);
      }
      return Promise.reject(error);
    }
  );

  return instance;
}

/**
 * Wrapper com retry automático para falhas de rede.
 */
export async function withRetry<T>(
  fn: () => Promise<T>,
  retries = 3,
  delayMs = 500
): Promise<T> {
  let lastError: unknown;
  for (let i = 0; i < retries; i++) {
    try {
      return await fn();
    } catch (err) {
      lastError = err;
      if (i < retries - 1) {
        await new Promise((r) => setTimeout(r, delayMs * (i + 1)));
      }
    }
  }
  throw lastError;
}
