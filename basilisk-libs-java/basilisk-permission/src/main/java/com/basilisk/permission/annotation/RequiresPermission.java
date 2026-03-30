package com.basilisk.permission.annotation;

import java.lang.annotation.*;

/**
 * Declara que o método exige que o usuário autenticado tenha todas as permissões
 * listadas no recurso especificado.
 *
 * Exemplo:
 *   @RequiresPermission(resource = "USUARIOS", permissions = {"VER", "EDITAR"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    String resource();
    String[] permissions();
}
