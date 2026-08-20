package com.basilisk.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida CPF brasileiro: 11 dígitos, dígitos verificadores corretos
 * e sequências repetidas (111.111.111-11) rejeitadas.
 * Aceita com ou sem máscara. Valor nulo ou vazio é considerado válido.
 */
@Documented
@Constraint(validatedBy = ValidCpfValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCpf {

    String message() default "CPF inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}