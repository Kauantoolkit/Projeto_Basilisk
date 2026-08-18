import { HTMLAttributes, forwardRef } from "react";

import { cn } from "@basilisk/utils";

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  /** Remove o padding padrão */
  noPadding?: boolean;
}

interface CardSubComponents {
  Header: typeof CardHeader;
  Body: typeof CardBody;
  Footer: typeof CardFooter;
}

export const Card = forwardRef<HTMLDivElement, CardProps>(
  ({ className, noPadding, children, ...props }, ref) => (
    <div
      ref={ref}
      className={cn(
        "rounded-[12px] border border-[var(--bsk-border-light)] bg-[var(--bsk-surface-raised)] shadow-[var(--bsk-shadow-card)]",
        !noPadding && "p-5",
        className
      )}
      {...props}
    >
      {children}
    </div>
  )
) as React.ForwardRefExoticComponent<CardProps & React.RefAttributes<HTMLDivElement>> &
  CardSubComponents;

Card.displayName = "Card";

const CardHeader = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn("mb-4 border-b border-[var(--bsk-border-light)] pb-4", className)}
      {...props}
    />
  )
);
CardHeader.displayName = "Card.Header";

const CardBody = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn("", className)} {...props} />
  )
);
CardBody.displayName = "Card.Body";

const CardFooter = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn("mt-4 border-t border-[var(--bsk-border-light)] pt-4", className)}
      {...props}
    />
  )
);
CardFooter.displayName = "Card.Footer";

Card.Header = CardHeader;
Card.Body = CardBody;
Card.Footer = CardFooter;
