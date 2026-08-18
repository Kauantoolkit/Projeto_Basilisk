import { cva } from "class-variance-authority";

import { cn } from "@basilisk/utils";

const badgeVariants = cva(
  "inline-flex items-center rounded-[7px] px-2.5 py-0.5 text-xs font-medium",
  {
    variants: {
      variant: {
        default: "bg-[var(--bsk-brand-light)] text-[var(--bsk-brand-hover)]",
        primary: "bg-[var(--bsk-brand)] text-[var(--bsk-text-on-brand)]",
        success: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",
        warning: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400",
        error: "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400",
        info: "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400",
        outline: "border border-[var(--bsk-brand)] text-[var(--bsk-brand)]",
      },
    },
    defaultVariants: { variant: "default" },
  }
);

interface BadgeProps {
  variant?: "default" | "primary" | "success" | "warning" | "error" | "info" | "outline";
  children: React.ReactNode;
  className?: string;
}

export function Badge({ variant, children, className }: BadgeProps) {
  return (
    <span className={cn(badgeVariants({ variant }), className)}>
      {children}
    </span>
  );
}
