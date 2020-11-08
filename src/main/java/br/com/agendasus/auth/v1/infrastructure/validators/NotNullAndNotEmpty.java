package br.com.agendasus.auth.v1.infrastructure.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NotNullAndNotEmptyValidator.class)
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullAndNotEmpty {

    String message() default "O campo está vazio";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}
