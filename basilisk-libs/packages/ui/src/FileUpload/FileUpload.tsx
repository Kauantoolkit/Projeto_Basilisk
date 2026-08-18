import { useCallback, useRef, useState } from "react";

import { cn } from "@basilisk/utils";

export interface FileUploadProps {
  /** Callback quando arquivos sao selecionados */
  onFilesSelected: (files: File[]) => void;
  /** Filtro de tipo de arquivo (ex: "image/*,.pdf") */
  accept?: string;
  /** Permitir multiplos arquivos */
  multiple?: boolean;
  /** Tamanho maximo em MB */
  maxSizeMB?: number;
  /** Texto do label */
  label?: string;
  /** Texto de ajuda abaixo */
  hint?: string;
  /** Mensagem de erro */
  error?: string;
  /** Desabilitado */
  disabled?: boolean;
  /** Classe CSS adicional */
  className?: string;
  /** Variante visual */
  variant?: "default" | "compact";
  /** Mostrar preview de imagens */
  preview?: boolean;
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function isImageFile(file: File): boolean {
  return file.type.startsWith("image/");
}

export function FileUpload({
  onFilesSelected,
  accept,
  multiple = false,
  maxSizeMB = 10,
  label,
  hint,
  error,
  disabled = false,
  className,
  variant = "default",
  preview,
}: FileUploadProps) {
  const [isDragOver, setIsDragOver] = useState(false);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [sizeError, setSizeError] = useState<string | null>(null);
  const [previews, setPreviews] = useState<Record<string, string>>({});
  const inputRef = useRef<HTMLInputElement>(null);

  const showPreview =
    preview !== undefined ? preview : accept?.includes("image") ?? false;

  const validateAndSet = useCallback(
    (files: File[]) => {
      const maxBytes = maxSizeMB * 1024 * 1024;
      const oversized = files.filter((f) => f.size > maxBytes);

      if (oversized.length > 0) {
        setSizeError(
          `Arquivo(s) excede(m) o limite de ${maxSizeMB}MB: ${oversized.map((f) => f.name).join(", ")}`
        );
        return;
      }

      setSizeError(null);
      setSelectedFiles(files);
      onFilesSelected(files);

      if (showPreview) {
        const newPreviews: Record<string, string> = {};
        files.forEach((file) => {
          if (isImageFile(file)) {
            const url = URL.createObjectURL(file);
            newPreviews[file.name] = url;
          }
        });
        setPreviews((prev) => {
          Object.values(prev).forEach((url) => URL.revokeObjectURL(url));
          return newPreviews;
        });
      }
    },
    [maxSizeMB, onFilesSelected, showPreview]
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setIsDragOver(false);
      if (disabled) return;
      const files = Array.from(e.dataTransfer.files);
      validateAndSet(multiple ? files : files.slice(0, 1));
    },
    [disabled, multiple, validateAndSet]
  );

  const handleDragOver = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      if (!disabled) setIsDragOver(true);
    },
    [disabled]
  );

  const handleDragLeave = useCallback(() => {
    setIsDragOver(false);
  }, []);

  const handleInputChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const files = Array.from(e.target.files || []);
      validateAndSet(files);
      e.target.value = "";
    },
    [validateAndSet]
  );

  const handleClick = useCallback(() => {
    if (!disabled) inputRef.current?.click();
  }, [disabled]);

  const removeFile = useCallback(
    (index: number) => {
      const newFiles = selectedFiles.filter((_, i) => i !== index);
      const removedFile = selectedFiles[index];

      if (previews[removedFile.name]) {
        URL.revokeObjectURL(previews[removedFile.name]);
        setPreviews((prev) => {
          const next = { ...prev };
          delete next[removedFile.name];
          return next;
        });
      }

      setSelectedFiles(newFiles);
      onFilesSelected(newFiles);
    },
    [selectedFiles, onFilesSelected, previews]
  );

  const displayError = error || sizeError;
  const isCompact = variant === "compact";

  return (
    <div className={cn("flex flex-col gap-1.5", className)}>
      {label && (
        <label
          className={cn(
            "text-sm font-medium",
            "text-[var(--bsk-text)]"
          )}
        >
          {label}
        </label>
      )}

      <div
        role="button"
        tabIndex={disabled ? -1 : 0}
        onClick={handleClick}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            handleClick();
          }
        }}
        className={cn(
          "rounded-lg border-2 border-dashed transition-colors duration-150",
          "flex items-center justify-center cursor-pointer",
          "border-[var(--bsk-border)]",
          "bg-[var(--bsk-surface)]",
          isDragOver && "border-[var(--bsk-brand)] bg-[var(--bsk-brand-subtle)]",
          disabled && "opacity-50 cursor-not-allowed",
          displayError && "border-[var(--bsk-danger)]",
          isCompact ? "px-4 py-3 gap-3" : "flex-col gap-2 px-6 py-8",
          "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--bsk-brand)]"
        )}
      >
        {/* Upload icon */}
        <svg
          className={cn(
            "text-[var(--bsk-text-secondary)]",
            isCompact ? "h-5 w-5 shrink-0" : "h-10 w-10"
          )}
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
          <polyline points="17 8 12 3 7 8" />
          <line x1="12" y1="3" x2="12" y2="15" />
        </svg>

        <div className={cn(
          "text-center",
          isCompact && "text-left"
        )}>
          <p className={cn(
            "text-[var(--bsk-text)]",
            isCompact ? "text-sm" : "text-sm font-medium"
          )}>
            Arraste arquivos ou clique para selecionar
          </p>
          {!isCompact && accept && (
            <p className="text-xs text-[var(--bsk-text-secondary)] mt-1">
              {accept}
            </p>
          )}
        </div>
      </div>

      <input
        ref={inputRef}
        type="file"
        accept={accept}
        multiple={multiple}
        onChange={handleInputChange}
        className="hidden"
        disabled={disabled}
        aria-hidden="true"
      />

      {hint && !displayError && (
        <p className="text-xs text-[var(--bsk-text-secondary)]">{hint}</p>
      )}

      {displayError && (
        <p className="text-xs text-[var(--bsk-danger)]">{displayError}</p>
      )}

      {selectedFiles.length > 0 && (
        <ul className="flex flex-col gap-2 mt-1">
          {selectedFiles.map((file, index) => (
            <li
              key={`${file.name}-${index}`}
              className={cn(
                "flex items-center gap-3 rounded-md px-3 py-2",
                "border border-[var(--bsk-border-light)]",
                "bg-[var(--bsk-surface)]"
              )}
            >
              {showPreview && previews[file.name] && (
                <img
                  src={previews[file.name]}
                  alt={file.name}
                  className="h-10 w-10 rounded object-cover shrink-0"
                />
              )}

              <div className="flex-1 min-w-0">
                <p className="text-sm text-[var(--bsk-text)] truncate">
                  {file.name}
                </p>
                <p className="text-xs text-[var(--bsk-text-secondary)]">
                  {formatFileSize(file.size)}
                </p>
              </div>

              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  removeFile(index);
                }}
                className={cn(
                  "shrink-0 rounded p-1 transition-colors",
                  "text-[var(--bsk-text-secondary)]",
                  "hover:text-[var(--bsk-danger)] hover:bg-[var(--bsk-danger)]/10"
                )}
                aria-label={`Remover ${file.name}`}
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
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
