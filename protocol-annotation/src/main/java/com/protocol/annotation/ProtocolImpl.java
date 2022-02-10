package com.protocol.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ljq on 2019/5/8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ProtocolImpl {

    String value();
}
