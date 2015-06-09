package pl.salonea.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;

@NotNull
@Size(min=6, max=45)
@Pattern.List({
        @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one upper letter."),
        @Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message ="Password must contain one special character."),
        @Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.")
})
@Constraint(validatedBy = {}) // constraints composition
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

        String message() default "Password doesn't match bean validation constraints.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

}
