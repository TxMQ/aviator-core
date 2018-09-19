package com.txmq.aviator.config.model;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import com.txmq.aviator.config.AviatorConfiguration;

@AviatorConfiguration(properties= {"blockLoggers", "blockLogger"})
public class BlockLoggerConfig {
	public String loggerClass;
	public DefaultKeyValue<String, String>[] parameters;
}
