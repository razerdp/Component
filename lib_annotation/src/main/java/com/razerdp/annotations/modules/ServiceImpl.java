package com.razerdp.annotations.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by razerdp on 2019/7/18
 * <p>
 * Description annotation for module service
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ServiceImpl {

    //service tag
    int value() default 0;
}