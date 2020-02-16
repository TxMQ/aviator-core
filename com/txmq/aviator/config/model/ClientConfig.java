package com.txmq.aviator.config.model;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
	public List<NodeAddress> knownSockets;
	
	public List<SocketAddress> getKnownSockets() {
		ArrayList<SocketAddress> result = new ArrayList<SocketAddress>();
		for (NodeAddress address : this.knownSockets) {
			result.add(new InetSocketAddress(address.hostname, address.port));
		}
		
		return result;
	}
}
