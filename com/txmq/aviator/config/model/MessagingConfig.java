package com.txmq.aviator.config.model;

import com.swirlds.platform.Platform;
import com.txmq.aviator.config.AviatorConfiguration;

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
	
	public MessagingConfig getConfigForGrizzly(Platform platform, boolean testMode) {
		MessagingConfig result = new MessagingConfig();
		
		//If a port has been defined in the config, use it over the derived port.
		if (this.port > 0) {
			result.port = this.port;				
		} else {
			//Test if there's a derived port value.  If not, we have an invalid messaging config
			if (this.derivedPort != null) {
				//Calculate the port for socket connections based on the hashgraph's port
				//If we're in test mode, mock this up to be a typical value, e.g. 5220X
				if (!testMode) {
					result.port = platform.getAddress().getPortExternalIpv4() + this.derivedPort;
				} else {
					result.port = 50204 + this.derivedPort;
				}
			} else {
				throw new IllegalArgumentException(
					"One of \"port\" or \"derivedPort\" must be defined."
				);
			}
		}
		
		if (this.handlers != null && this.handlers.length > 0) {
			result.handlers = this.handlers;
		} else {
			throw new IllegalArgumentException(
				"No handlers were defined in configuration"
			);
		}
		
		result.secured = this.secured;
		result.clientKeystore = this.clientKeystore;
		result.clientTruststore = this.clientTruststore;
		result.serverKeystore = this.serverKeystore;
		result.serverTruststore = this.serverTruststore;
		return result;
	}
}
