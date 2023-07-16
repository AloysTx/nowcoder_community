package com.aloys.nowcoder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 作用范围 方法
@Target(ElementType.METHOD)
// 有效期 运行时
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
