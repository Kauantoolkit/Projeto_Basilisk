package com.basilisk.status.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configura a máquina de estados de uma entidade JPA.
 *
 * Uso:
 *   @StatusConfig(
 *       states = {"PENDING", "IN_PROGRESS", "READY", "DELIVERED", "CANCELLED"},
 *       initialState = "PENDING",
 *       transitions = {"PENDING->IN_PROGRESS", "IN_PROGRESS->READY", "READY->DELIVERED", "*->CANCELLED"}
 *   )
 *   public class Order { ... }
 *
 * Nota: "*->X" significa que qualquer estado pode transicionar para X.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StatusConfig {

    /** Estados válidos da entidade. */
    String[] states();

    /** Estado inicial atribuído na criação. */
    String initialState();

    /**
     * Transições permitidas no formato "FROM->TO".
     * Use "*->TO" para permitir transição de qualquer estado.
     */
    String[] transitions();
}
