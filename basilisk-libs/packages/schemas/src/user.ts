import { z } from "zod";

import { addressSchema } from "./address";

export const userSchema = z.object({
  id: z.string().uuid().optional(),
  name: z.string().min(2, "Nome deve ter ao menos 2 caracteres").max(100),
  email: z.string().email("E-mail inválido"),
  phone: z
    .string()
    .regex(/^\d{10,11}$/, "Telefone inválido")
    .optional(),
  cpf: z
    .string()
    .regex(/^\d{11}$/, "CPF inválido")
    .optional(),
  role: z.enum(["admin", "user", "viewer"]).default("user"),
  address: addressSchema.optional(),
  createdAt: z.date().optional(),
  updatedAt: z.date().optional(),
});

export const createUserSchema = userSchema.omit({ id: true, createdAt: true, updatedAt: true });
export const updateUserSchema = userSchema.partial().omit({ id: true });

export type User = z.infer<typeof userSchema>;
export type CreateUser = z.infer<typeof createUserSchema>;
export type UpdateUser = z.infer<typeof updateUserSchema>;
