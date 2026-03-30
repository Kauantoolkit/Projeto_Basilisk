import { cva } from "class-variance-authority";

import { cn } from "@basilisk/utils";

const badgeVariants = cva(
  "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
  {
    variants: {
      variant: {
        default: "bg-[#EAF2F2] text-[#1E5C5C]",
        primary: "bg-[#3A8585] text-white",
        success: "bg-green-100 text-green-800",
        warning: "bg-yellow-100 text-yellow-800",
        error: "bg-red-100 text-red-800",
        info: "bg-blue-100 text-blue-800",
        outline: "border border-[#3A8585] text-[#3A8585]",
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
