package com.basilisk.permission.annotation;

import java.lang.annotation.*;

/**
 * Declara que o método exige que o usuário autenticado tenha a permissão especificada.
 *
 * Formato: resource:action
 *
 * Exemplos:
 *   @RequiresPermission("clients:read")
 *   @RequiresPermission("payments:write")
 *   @RequiresPermission("reports:export")
 *
 * Para múltiplas permissões (todas obrigatórias):
 *   @RequiresPermission(value = {"clients:read", "clients:write"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    String[] value();
}
