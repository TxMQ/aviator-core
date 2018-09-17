package com.txmq.exo.config.model;

import com.txmq.exo.config.AviatorConfiguration;

@AviatorConfiguration(property="knownDockets")
public class NodeAddress {
	public String hostname;
	public int port;
}
