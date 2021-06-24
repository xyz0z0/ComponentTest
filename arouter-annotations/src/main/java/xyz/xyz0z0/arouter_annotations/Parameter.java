package xyz.xyz0z0.arouter_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Cheng
 * Date: 2021/6/24 9:48
 * Description: xyz.xyz0z0.arouter_annotations
 * @author Cheng
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {
    String name() default "";
}
