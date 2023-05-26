package com.protocol.annotation

/**
 * Created by ljq on 2019/5/8
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Protocol(val value: String)