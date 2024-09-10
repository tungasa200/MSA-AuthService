package com.yjmedia.yvisbig.bizcom.annotation;


import com.yjmedia.yvisbig.bizcom.enums.AccessScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AcessScope {
    AccessScopeType scope();
}
