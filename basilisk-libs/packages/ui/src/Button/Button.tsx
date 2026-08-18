import { cva } from "class-variance-authority";
import { forwardRef } from "react";

import { cn } from "@basilisk/utils";

import type { ButtonProps } from "./Button.types";

const buttonVariants = cva(
  [
    "inline-flex items-center justify-center gap-2",
    "font-medium rounded-[8px] transition-all duration-150",
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-[var(--bsk-surface)]",
    "disabled:pointer-events-none disabled:opacity-50",
    "select-none",
  ],
  {
    variants: {
      variant: {
        primary: [
          "bg-[var(--bsk-brand)] text-[var(--bsk-text-on-brand)]",
          "hover:bg-[var(--bsk-brand-hover)] active:bg-[var(--bsk-brand-active)]",
          "focus-visible:ring-[var(--bsk-brand)]",
        ],
        secondary: [
          "bg-[var(--bsk-brand-light)] text-[var(--bsk-text)]",
          "hover:bg-[var(--bsk-brand-light)] active:bg-[var(--bsk-brand-light)]",
          "focus-visible:ring-[var(--bsk-brand)]",
        ],
        outline: [
          "border border-[var(--bsk-brand)] text-[var(--bsk-brand)] bg-transparent",
          "hover:bg-[var(--bsk-brand)] hover:text-[var(--bsk-text-on-brand)]",
          "focus-visible:ring-[var(--bsk-brand)]",
        ],
        ghost: [
          "text-[var(--bsk-brand)] bg-transparent",
          "hover:bg-[var(--bsk-brand-light)]",
          "focus-visible:ring-[var(--bsk-brand)]",
        ],
        danger: [
          "bg-[var(--bsk-danger)] text-[var(--bsk-text-on-brand)]",
          "hover:bg-[var(--bsk-danger-hover)] active:bg-[var(--bsk-danger-active)]",
          "focus-visible:ring-[var(--bsk-danger)]",
        ],
      },
      size: {
        sm: "h-8 px-3 text-sm",
        md: "h-10 px-4 text-sm",
        lg: "h-12 px-6 text-base",
      },
      fullWidth: {
        true: "w-full",
      },
    },
    defaultVariants: {
      variant: "primary",
      size: "md",
    },
  }
);

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant,
      size,
      fullWidth,
      isLoading,
      leftIcon,
      rightIcon,
      children,
      className,
      disabled,
      ...props
    },
    ref
  ) => {
    return (
      <button
        ref={ref}
        className={cn(buttonVariants({ variant, size, fullWidth }), className)}
        disabled={disabled || isLoading}
        aria-busy={isLoading}
        {...props}
      >
        {isLoading ? (
          <svg
            className="h-4 w-4 animate-spin"
            viewBox="0 0 24 24"
            fill="none"
            aria-hidden="true"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
            />
          </svg>
        ) : (
          leftIcon
        )}
        {children}
        {!isLoading && rightIcon}
      </button>
    );
  }
);

Button.displayName = "Button";
