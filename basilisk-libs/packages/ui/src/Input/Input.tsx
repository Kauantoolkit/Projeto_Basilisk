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
            className="text-sm font-medium text-[#0D3B3B]"
          >
            {label}
            {props.required && (
              <span className="ml-1 text-red-500" aria-hidden="true">*</span>
            )}
          </label>
        )}

        <div className="relative flex items-center">
          {leftIcon && (
            <span className="absolute left-3 text-[#8B8B78] pointer-events-none">
              {leftIcon}
            </span>
          )}

          <input
            ref={ref}
            id={id}
            className={cn(
              "h-10 w-full rounded-md border bg-white px-3 py-2 text-sm text-[#0D3B3B]",
              "placeholder:text-[#8B8B78]",
              "transition-colors duration-150",
              "focus:outline-none focus:ring-2 focus:ring-[#3A8585] focus:border-[#3A8585]",
              "disabled:cursor-not-allowed disabled:bg-gray-50 disabled:opacity-60",
              error
                ? "border-red-400 focus:ring-red-400"
                : "border-gray-300 hover:border-[#5BA3A0]",
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
            <span className="absolute right-3 text-[#8B8B78] pointer-events-none">
              {rightIcon}
            </span>
          )}
        </div>

        {error && (
          <p id={`${id}-error`} className="text-xs text-red-500" role="alert">
            {error}
          </p>
        )}

        {!error && helperText && (
          <p id={`${id}-helper`} className="text-xs text-[#8B8B78]">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

Input.displayName = "Input";
