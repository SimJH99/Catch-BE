package com.encore.event.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Service 대신
@Target({ElementType.TYPE}) // 어노테이션 적용대상 FIELD, TYPE(class, interface)
@Retention(RetentionPolicy.RUNTIME) //클래스 파일까지 존재하며, 실행 시 사용한다. 즉, 지속 시간이 가장 길다.
@Component
public @interface UseCase {
    @AliasFor(annotation = Component.class)
    String value() default "";

}

