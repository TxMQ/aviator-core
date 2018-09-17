package com.txmq.exo.config.model;

public class HashgraphConfig {
	public String transactionTypesClassName;
	public String[] transactionProcessors;
	public MessagingConfig socketMessaging;
	public MessagingConfig rest;
	public BlockLoggerConfig blockLogger;
}
