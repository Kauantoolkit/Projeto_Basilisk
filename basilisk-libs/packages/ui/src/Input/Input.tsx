import { forwardRef, useId } from "react";

import { cn } from "@basilisk/utils";

import type { InputProps } from "./Input.types";

export const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      error,
      helperText,
      leftIcon,
      rightIcon,
      fullWidth,
      className,
      id: idProp,
      ...props
    },
    ref
  ) => {
    const generatedId = useId();
    const id = idProp ?? generatedId;

    return (
      <div className={cn("flex flex-col gap-1", fullWidth && "w-full")}>
        {label && (
          <label
            htmlFor={id}
            className="text-sm font-medium text-[var(--bsk-text)]"
          >
            {label}
            {props.required && (
              <span className="ml-1 text-[var(--bsk-danger)]" aria-hidden="true">*</span>
            )}
          </label>
        )}

        <div className="relative flex items-center">
          {leftIcon && (
            <span className="absolute left-3 text-[var(--bsk-text-secondary)] pointer-events-none">
              {leftIcon}
            </span>
          )}

          <input
            ref={ref}
            id={id}
            className={cn(
              "h-10 w-full rounded-md border bg-[var(--bsk-surface)] px-3 py-2 text-sm text-[var(--bsk-text)]",
              "placeholder:text-[var(--bsk-text-secondary)]",
              "transition-colors duration-150",
              "focus:outline-none focus:ring-2 focus:ring-[var(--bsk-brand)] focus:border-[var(--bsk-brand)]",
              "disabled:cursor-not-allowed disabled:bg-[var(--bsk-surface-raised)] disabled:opacity-60",
              error
                ? "border-red-400 focus:ring-red-400"
                : "border-[var(--bsk-border)] hover:border-[var(--bsk-brand-subtle)]",
              leftIcon && "pl-10",
              rightIcon && "pr-10",
              fullWidth && "w-full",
              className
            )}
            aria-invalid={!!error}
            aria-describedby={
              error ? `${id}-error` : helperText ? `${id}-helper` : undefined
            }
            {...props}
          />

          {rightIcon && (
            <span className="absolute right-3 text-[var(--bsk-text-secondary)] pointer-events-none">
              {rightIcon}
            </span>
          )}
        </div>

        {error && (
          <p id={`${id}-error`} className="text-xs text-[var(--bsk-danger)]" role="alert">
            {error}
          </p>
        )}

        {!error && helperText && (
          <p id={`${id}-helper`} className="text-xs text-[var(--bsk-text-secondary)]">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

Input.displayName = "Input";
