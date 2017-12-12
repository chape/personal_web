package core.annotation;

import java.lang.annotation.*;

/**
 * Created by ChaoChao on 08/12/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    Class<? extends Annotation> value();
}
