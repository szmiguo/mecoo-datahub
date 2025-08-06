package com.mecoo.common.annotation;

/**
 * Hutool默认的转换器在对枚举类型转整形的时候，会默认按照元素下标来转
 * 比如一个 性别枚举类有两个枚举值，分别是 M(1),F(2),如果使用 Hutool 将枚举转成整数会转成 0，,1
 * 因此用这个注解来标记那些在业务上枚举值和整形值有对应关系的枚举类别
 * 这些类别需要自定义转换器，通常的做法是取默认的 val 字段用来表示其值。
 *
 * @author: lin
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = TYPE)
public @interface IntPrimeValue {

    /**
     * 转换依赖的取值字段，默认定义 val 字段的值表示枚举转成整形的值
     */
    String valueField() default "val";
}
