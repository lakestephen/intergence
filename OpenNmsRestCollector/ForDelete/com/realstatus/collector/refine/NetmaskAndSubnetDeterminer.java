package com.realstatus.collector.refine;

import java.util.Map;

import com.realstatus.hgs.collection.AttributeRefinement;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.RefinementExecutor;
import com.realstatus.hgs.model.Endpoint;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.refine.ModelRefinement;
import com.realstatus.hgs.util.IpAddressHelper;

public class NetmaskAndSubnetDeterminer implements RefinementExecutor, AttributeRefinement {
	
	@Override
	public void execute(CollectorPluginDescriptor pluginDescriptor, ModelRefinement refinement, ModelLookup dataModel) {
		
		for (Endpoint endpoint : dataModel.getEndpoints(pluginDescriptor.getDatasourceName())) {

			refineAttributes(endpoint.getAttributes());
			refinement.updateEndpoint(endpoint);
		}
	}

	@Override
	public void refineAttributes(Map<String, String> attributes) {
		if (attributes.get("Netmask") != null) {
			String netmask = attributes.get("Netmask");
			// convert netmask to CIDR /n notation.
			int mask = IpAddressHelper.convertDottedDecimalToCIDRMask(netmask);
			attributes.put("Netmask", String.valueOf(mask));
			
			if (attributes.get("IP Address") != null) {
				// also add subnet attribute
				attributes.put("Subnet", 
						IpAddressHelper.convertAddressToSubnet(attributes.get("IP Address")+'/'+mask));
			}
		}
	}
	
}
