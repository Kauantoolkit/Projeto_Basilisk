import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";

import { tokenManager } from "./tokenManager";

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (accessToken: string, refreshToken: string, user: AuthUser) => void;
  logout: () => void;
  updateUser: (user: Partial<AuthUser>) => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
  children: React.ReactNode;
  /** Chamado no logout para redirecionar ou limpar estado da app */
  onLogout?: () => void;
  /** Carrega o usuário a partir do token existente */
  loadUser?: (token: string) => Promise<AuthUser | null>;
}

export function AuthProvider({ children, onLogout, loadUser }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const init = async () => {
      const token = tokenManager.getAccessToken();
      if (!token || !loadUser) {
        setIsLoading(false);
        return;
      }
      try {
        const userData = await loadUser(token);
        setUser(userData);
      } catch {
        tokenManager.clearTokens();
      } finally {
        setIsLoading(false);
      }
    };
    init();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const login = useCallback(
    (accessToken: string, refreshToken: string, userData: AuthUser) => {
      tokenManager.setTokens(accessToken, refreshToken);
      setUser(userData);
    },
    []
  );

  const logout = useCallback(() => {
    tokenManager.clearTokens();
    setUser(null);
    onLogout?.();
  }, [onLogout]);

  const updateUser = useCallback((partial: Partial<AuthUser>) => {
    setUser((prev) => (prev ? { ...prev, ...partial } : null));
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de <AuthProvider>");
  return ctx;
}
