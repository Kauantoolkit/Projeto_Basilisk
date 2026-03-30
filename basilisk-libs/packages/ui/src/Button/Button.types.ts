import { ButtonHTMLAttributes } from "react";

export type ButtonVariant = "primary" | "secondary" | "outline" | "ghost" | "danger";
export type ButtonSize = "sm" | "md" | "lg";

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /** Variante visual do botão */
  variant?: ButtonVariant;
  /** Tamanho do botão */
  size?: ButtonSize;
  /** Exibe spinner de loading e desabilita o botão */
  isLoading?: boolean;
  /** Ícone exibido antes do conteúdo */
  leftIcon?: React.ReactNode;
  /** Ícone exibido após o conteúdo */
  rightIcon?: React.ReactNode;
  /** Ocupa toda a largura do container */
  fullWidth?: boolean;
}
