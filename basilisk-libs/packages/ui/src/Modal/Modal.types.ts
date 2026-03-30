export interface ModalProps {
  /** Controla visibilidade do modal */
  isOpen: boolean;
  /** Callback para fechar o modal */
  onClose: () => void;
  /** Título exibido no header */
  title?: string;
  /** Conteúdo do modal */
  children: React.ReactNode;
  /** Conteúdo dos botões de ação no footer */
  footer?: React.ReactNode;
  /** Tamanho do modal */
  size?: "sm" | "md" | "lg" | "xl" | "full";
  /** Fecha ao clicar no overlay */
  closeOnOverlayClick?: boolean;
  /** Fecha ao pressionar Escape */
  closeOnEsc?: boolean;
}
