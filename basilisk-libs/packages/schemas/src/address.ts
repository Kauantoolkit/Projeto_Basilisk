import { z } from "zod";

export const addressSchema = z.object({
  cep: z
    .string()
    .min(8, "CEP inválido")
    .max(9, "CEP inválido")
    .transform((v) => v.replace(/\D/g, "")),
  logradouro: z.string().min(1, "Logradouro obrigatório"),
  numero: z.string().min(1, "Número obrigatório"),
  complemento: z.string().optional(),
  bairro: z.string().min(1, "Bairro obrigatório"),
  cidade: z.string().min(1, "Cidade obrigatória"),
  estado: z
    .string()
    .length(2, "Estado deve ter 2 caracteres")
    .toUpperCase(),
});

export type Address = z.infer<typeof addressSchema>;
