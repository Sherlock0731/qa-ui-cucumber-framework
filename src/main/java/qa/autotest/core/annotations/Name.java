package qa.autotest.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for naming page elements and page objects
 * Used for better readability in logs and reports
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Name {
    /**
     * Human-readable name for the element or page
     */
    String value();
}
