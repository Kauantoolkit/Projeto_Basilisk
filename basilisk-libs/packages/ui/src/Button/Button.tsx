import { cva, type VariantProps } from "class-variance-authority";
import { forwardRef } from "react";

import { cn } from "@basilisk/utils";

import type { ButtonProps } from "./Button.types";

const buttonVariants = cva(
  [
    "inline-flex items-center justify-center gap-2",
    "font-medium rounded-md transition-all duration-150",
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2",
    "disabled:pointer-events-none disabled:opacity-50",
    "select-none",
  ],
  {
    variants: {
      variant: {
        primary: [
          "bg-[#3A8585] text-white",
          "hover:bg-[#1E5C5C] active:bg-[#0D3B3B]",
          "focus-visible:ring-[#3A8585]",
        ],
        secondary: [
          "bg-[#EAF2F2] text-[#0D3B3B]",
          "hover:bg-[#5BA3A0]/20 active:bg-[#5BA3A0]/30",
          "focus-visible:ring-[#3A8585]",
        ],
        outline: [
          "border border-[#3A8585] text-[#3A8585] bg-transparent",
          "hover:bg-[#3A8585] hover:text-white",
          "focus-visible:ring-[#3A8585]",
        ],
        ghost: [
          "text-[#3A8585] bg-transparent",
          "hover:bg-[#EAF2F2]",
          "focus-visible:ring-[#3A8585]",
        ],
        danger: [
          "bg-red-600 text-white",
          "hover:bg-red-700 active:bg-red-800",
          "focus-visible:ring-red-500",
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
