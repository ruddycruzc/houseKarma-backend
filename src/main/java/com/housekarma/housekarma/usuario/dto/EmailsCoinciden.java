
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailsCoincidenValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailsCoinciden {

    String message() default "Los emails no coinciden";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
