package qa.autotest.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify default URL for page object
 * Used for automatic navigation to the page
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultUrl {
    /**
     * Default URL path for the page (relative or absolute)
     */
    String url() default "";
}
