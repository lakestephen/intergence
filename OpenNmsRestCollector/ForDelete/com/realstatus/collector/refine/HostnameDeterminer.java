package com.realstatus.collector.refine;

import java.util.Map;

import com.realstatus.hgs.collection.AttributeRefinement;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.RefinementExecutor;
import com.realstatus.hgs.model.Node;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.refine.ModelRefinement;
import com.realstatus.hgs.util.IpAddressHelper;

public class HostnameDeterminer implements RefinementExecutor, AttributeRefinement {

	public static final String ATTRIBUTE_LABEL = "Label";
	
	@Override
	public void execute(CollectorPluginDescriptor pluginDescriptor, ModelRefinement refinement, ModelLookup lookup) {
		for (Node node : lookup.getNodes(pluginDescriptor.getDatasourceName())) {
			refineAttributes(node.getAttributes());
			
			refinement.updateNode(node);
		}
	}

	@Override
	public void refineAttributes(Map<String, String> attributes) {
		String labelAttribute = attributes.get(ATTRIBUTE_LABEL);
		
		// label is either a hostname (if it can be determined) or an IP Address
		if (labelAttribute != null && !IpAddressHelper.isValidIpAddress(labelAttribute)) {
			attributes.put("hostname", labelAttribute);
		}
	}

}
