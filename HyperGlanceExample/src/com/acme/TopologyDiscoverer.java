package com.acme;

import java.util.HashMap;
import java.util.Map;

import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.DiscoveryExecutor;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.update.ModelUpdate;

public class TopologyDiscoverer implements DiscoveryExecutor {

	@Override
	public void execute(CollectorPluginDescriptor descriptor, ModelUpdate update, ModelLookup lookup) {
		// Attributes for topology elements are supplied in the form of simple <K,V> pair Maps
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("Legs", "4");
		attributes.put("Eyes", "2");
		attributes.put("Conservation Status", "Least Concern");
		
		// Supply topology elements using the ModelUpdate object
		update.addNode("Coyote", attributes);
	}

}
