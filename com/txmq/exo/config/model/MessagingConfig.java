package com.txmq.exo.config.model;

import com.txmq.exo.config.AviatorConfiguration;

@AviatorConfiguration(properties= {"rest", "socketMessaging"})
public class MessagingConfig {
	public Integer port;
	public Integer derivedPort;
	public boolean secured;
	public String[] handlers;
	public KeystoreConfig clientKeystore;
	public KeystoreConfig clientTruststore;
	public KeystoreConfig serverKeystore;
	public KeystoreConfig serverTruststore;
}
