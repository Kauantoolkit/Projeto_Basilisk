import { SelectHTMLAttributes, forwardRef, useId } from "react";

import { cn } from "@basilisk/utils";

interface SelectOption {
  value: string | number;
  label: string;
  disabled?: boolean;
}

interface SelectProps extends Omit<SelectHTMLAttributes<HTMLSelectElement>, "size"> {
  label?: string;
  options: SelectOption[];
  error?: string;
  helperText?: string;
  placeholder?: string;
  fullWidth?: boolean;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, options, error, helperText, placeholder, fullWidth, className, id: idProp, ...props }, ref) => {
    const generatedId = useId();
    const id = idProp ?? generatedId;

    return (
      <div className={cn("flex flex-col gap-1", fullWidth && "w-full")}>
        {label && (
          <label htmlFor={id} className="text-sm font-medium text-[var(--bsk-text)]">
            {label}
            {props.required && <span className="ml-1 text-[var(--bsk-danger)]" aria-hidden="true">*</span>}
          </label>
        )}

        <select
          ref={ref}
          id={id}
          className={cn(
            "h-10 w-full rounded-md border bg-[var(--bsk-surface)] px-3 py-2 text-sm text-[var(--bsk-text)]",
            "transition-colors duration-150 appearance-none",
            "focus:outline-none focus:ring-2 focus:ring-[var(--bsk-brand)] focus:border-[var(--bsk-brand)]",
            "disabled:cursor-not-allowed disabled:bg-[var(--bsk-surface-raised)] disabled:opacity-60",
            error
              ? "border-red-400 focus:ring-red-400"
              : "border-[var(--bsk-border)] hover:border-[var(--bsk-brand-subtle)]",
            fullWidth && "w-full",
            className
          )}
          aria-invalid={!!error}
          {...props}
        >
          {placeholder && (
            <option value="" disabled>
              {placeholder}
            </option>
          )}
          {options.map((opt) => (
            <option key={opt.value} value={opt.value} disabled={opt.disabled}>
              {opt.label}
            </option>
          ))}
        </select>

        {error && <p className="text-xs text-[var(--bsk-danger)]" role="alert">{error}</p>}
        {!error && helperText && <p className="text-xs text-[var(--bsk-text-secondary)]">{helperText}</p>}
      </div>
    );
  }
);

Select.displayName = "Select";
