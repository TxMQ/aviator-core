package com.txmq.aviator.config.model;

import com.txmq.aviator.config.AviatorConfiguration;

@AviatorConfiguration(property="knownDockets")
public class NodeAddress {
	public String hostname;
	public int port;
}
