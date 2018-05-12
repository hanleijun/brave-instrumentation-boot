package cn.focus.eco.house.zipkin.brave.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @Author: leijunhan (leijunhan@sohu-inc.com)
 * @Date: 2017/12/9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan(basePackages = "cn.focus.eco.house.zipkin.brave")
public @interface FocusChain {
        /**
         * Exclude specific auto-configuration classes such that they will never be applied.
         * @return the classes to exclude
         */
        Class<?>[] exclude() default {};

        /**
         * Exclude specific auto-configuration class names such that they will never be
         * applied.
         * @return the class names to exclude
         * @since 1.3.0
         */
        String[] excludeName() default {};

        /**
         * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
         * for a type-safe alternative to String-based package names.
         * @return base packages to scan
         * @since 1.3.0
         */
        @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
        String[] scanBasePackages() default {};

        /**
         * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
         * scan for annotated components. The package of each class specified will be scanned.
         * <p>
         * Consider creating a special no-op marker class or interface in each package that
         * serves no purpose other than being referenced by this attribute.
         * @return base packages to scan
         * @since 1.3.0
         */
        @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
        Class<?>[] scanBasePackageClasses() default {};
}
