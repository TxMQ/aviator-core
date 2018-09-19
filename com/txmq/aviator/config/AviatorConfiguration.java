package com.txmq.aviator.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies a class as a "destination" type when parsing aviator-config.json.  For example,
 * MessagingConfig is the type used to deserialize the "rest" property of the config file.  Annotating
 * MessagingConfig with @ConfigPropertyParser(property="com.txmq.exo.config.MessagingConfig") tells
 * the deserializer to deserialize that section of the config file to MessagingConfig. 
 * 
 * @author craigdrabik
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AviatorConfiguration {
	public String[] properties() default {};
	public String property() default "";
}
