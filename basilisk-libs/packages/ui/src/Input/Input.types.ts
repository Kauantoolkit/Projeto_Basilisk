import { InputHTMLAttributes } from "react";

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  /** Rótulo do campo */
  label?: string;
  /** Texto de erro (exibe o campo em estado de erro) */
  error?: string;
  /** Texto auxiliar abaixo do campo */
  helperText?: string;
  /** Ícone no lado esquerdo */
  leftIcon?: React.ReactNode;
  /** Ícone no lado direito */
  rightIcon?: React.ReactNode;
  /** Ocupa toda a largura */
  fullWidth?: boolean;
}
