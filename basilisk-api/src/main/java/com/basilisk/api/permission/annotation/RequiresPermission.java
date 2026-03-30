package com.basilisk.api.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Protege um endpoint exigindo que o usuário autenticado tenha
 * TODAS as permissões informadas no recurso informado.
 *
 * Exemplos:
 *   @RequiresPermission(resource = "clientes", permissions = "VER")
 *   @RequiresPermission(resource = "clientes", permissions = {"VER", "EDITAR"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String resource();
    String[] permissions();
}
