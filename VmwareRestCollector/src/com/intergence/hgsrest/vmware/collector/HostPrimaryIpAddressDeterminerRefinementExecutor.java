package com.intergence.hgsrest.vmware.collector;

import com.intergence.hgsrest.collection.RefinementExecutor;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;
import com.intergence.hgsrest.model.lookup.ModelLookup;
import com.intergence.hgsrest.runner.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HostPrimaryIpAddressDeterminerRefinementExecutor implements RefinementExecutor {


	@Override
	public void execute(ModelLookup dataModel) {

		// Get Nodes that are Hosts
		Collection<Node> hosts = getHosts(dataModel);

		for (Node host : hosts) {
			String ipAddress = null;

//			for (Node merge : modelSubsets.getMergeGroupNodes(host)) {
//				ipAddress = merge.getAttribute(NodeCollectorConstants.ATTRIBUTE_IPADDRESS);
//				if (ipAddress != null && IpAddressHelper.getAddressBits(ipAddress) != 0) {
//					break;
//				}
//			}
//
//			if (ipAddress == null) {
//				Collection<Endpoint> endpoints = queries.getEndPointsOnNode(host);
//				long ipAddressBits = 0;
//				for (Endpoint endpoint : endpoints) {
//					String interfaceIpAddress = endpoint.getAttribute(EndpointCollectorConstants.ATTRIBUTE_IP_ADDRESS);
//					if (interfaceIpAddress != null) {
//						long interfaceIpAddressBits = IpAddressHelper.getAddressBitsUnsigned(interfaceIpAddress);
//						if (ipAddress == null ||
//								((interfaceIpAddressBits < ipAddressBits) && interfaceIpAddressBits != 0)) {
//							ipAddress = interfaceIpAddress;
//							ipAddressBits = interfaceIpAddressBits;
//						}
//					}
//				}
//			}
//
//			if (ipAddress != null) {
//				host.putAttribute(NodeCollectorConstants.ATTRIBUTE_IPADDRESS, ipAddress);
//				refinedHosts.add(host);
//			}

		}
	}

	private Collection<Node> getHosts(ModelLookup dataModel) {
		Collection<Node> allNodesForDatasource = dataModel.getNodes();
		List<Node> filtered = new ArrayList<Node>();
		for (Node node : allNodesForDatasource) {
			if (NodeTypeEnum.SERVER.equals(node.getType())) {
				filtered.add(node);
			}
		}

		return filtered;
	}
	/*
	@Override
	public Collection<Node> execute(ModelLookup dataModel) {
		TopologyWalkingHelper queries = new TopologyWalkingHelper(dataModel);
		MergeGroupHelper modelSubsets = new MergeGroupHelper(dataModel);
		VmWareModelQueries vmwareQueries = new VmWareModelQueries(pluginDescriptor.getDatasourceName(), dataModel);

		Collection<Node> refinedHosts = new ArrayList<Node>();

		Collection<Node> hosts = vmwareQueries.getVmWareNodesOfType(NodeType.HOST);
		for (Node host : hosts) {
			String ipAddress = null;

			for (Node merge : modelSubsets.getMergeGroupNodes(host)) {
				ipAddress = merge.getAttribute(NodeCollectorConstants.ATTRIBUTE_IPADDRESS);
				if (ipAddress != null && IpAddressHelper.getAddressBits(ipAddress) != 0) {
					break;
				}
			}

			if (ipAddress == null) {
				Collection<Endpoint> endpoints = queries.getEndPointsOnNode(host);
				long ipAddressBits = 0;
				for (Endpoint endpoint : endpoints) {
					String interfaceIpAddress = endpoint.getAttribute(EndpointCollectorConstants.ATTRIBUTE_IP_ADDRESS);
					if (interfaceIpAddress != null) {
						long interfaceIpAddressBits = IpAddressHelper.getAddressBitsUnsigned(interfaceIpAddress);
						if (ipAddress == null ||
								((interfaceIpAddressBits < ipAddressBits) && interfaceIpAddressBits != 0)) {
							ipAddress = interfaceIpAddress;
							ipAddressBits = interfaceIpAddressBits;
						}
					}
				}
			}

			if (ipAddress != null) {
				host.putAttribute(NodeCollectorConstants.ATTRIBUTE_IPADDRESS, ipAddress);
				refinedHosts.add(host);
			}
		}

		return refinedHosts;

		hosts = getAllHosts();

		foreach (host) {
			if (!host.hasIP()) {
				endpoints = host.getEndpoints();
				foreach (endpoint)

			}
		}

	}
		return null;
	}*/
}
