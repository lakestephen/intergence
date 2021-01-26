package com.intergence.hgsrest.runner;

import com.google.common.collect.Sets;
import com.intergence.hgsrest.model.update.DiscoveryType;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TopologyTest {

	
	@Test
	public void removingEmptyAttributesCleansMap() {

		Topology name = new Topology("NAME", Collections.<String>emptySet());

		Map<String, String> attributes = new HashMap<String, String>();

		attributes.put("VALID", "Valid");
		attributes.put("EMPTY", "");
		attributes.put("NULL", null);

		assertEquals(3, attributes.size());

		name.addEndpoint("CollectorName", "Key", "Type", "NodeKey", attributes, DiscoveryType.DISCOVERED);

		Endpoint endpoint = name.getEndpoints().get(0);

		assertEquals(3, endpoint.getAttributes().size());
		assertEquals(Topology.COLLECTOR_NAME_KEY, endpoint.getAttributes().get(0).getName());
		assertEquals(Topology.DISCOVERY_TYPE_KEY, endpoint.getAttributes().get(1).getName());
		assertEquals("VALID", endpoint.getAttributes().get(2).getName());
	}

	@Test
	public void removingReservedAttributeCleansMap() {

		String reserved_keyword1 = "RESERVED_KEYWORD1";
		String reserved_keyword2 = "RESERVED_KEYWORD2";

		Topology name = new Topology("NAME", Sets.newHashSet(reserved_keyword1, reserved_keyword2));

		Map<String, String> attributes = new HashMap<String, String>();

		attributes.put("VALID", "Valid");
		attributes.put(reserved_keyword1, "TO_BE_REMOVED1");
		attributes.put(reserved_keyword2, "TO_BE_REMOVED2");

		assertEquals(3, attributes.size());

		name.addEndpoint("CollectorName", "Key", "Type", "NodeKey", attributes, DiscoveryType.DISCOVERED);

		Endpoint endpoint = name.getEndpoints().get(0);

		assertEquals(3, endpoint.getAttributes().size());
		assertEquals(Topology.COLLECTOR_NAME_KEY, endpoint.getAttributes().get(0).getName());
		assertEquals(Topology.DISCOVERY_TYPE_KEY, endpoint.getAttributes().get(1).getName());
		assertEquals("VALID", endpoint.getAttributes().get(2).getName());
	}


	@Test
	public void addingDuplicateKeyMergesDistinctAttributesWhereCommonAttributesAreIdentical() {
		String collectorName = "CollectorName";
		String key = "Key";
		String type = "Type";
		String nodeKey = "NodeKey";

		Topology name = new Topology("NAME", Collections.<String>emptySet());

		Map<String, String> attributes1 = new HashMap<String, String>();
		attributes1.put("VALID", "Valid");
		name.addEndpoint(collectorName, key, type, nodeKey, attributes1, DiscoveryType.DISCOVERED);

		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put("VALID2", "Valid2");
		name.addEndpoint(collectorName, key, type, nodeKey, attributes2, DiscoveryType.DISCOVERED);

		Endpoint endpoint = name.getEndpoints().get(0);

		assertEquals(4, endpoint.getAttributes().size());
		assertEquals(Topology.COLLECTOR_NAME_KEY, endpoint.getAttributes().get(0).getName());
		assertEquals(Topology.DISCOVERY_TYPE_KEY, endpoint.getAttributes().get(1).getName());
		assertEquals("VALID", endpoint.getAttributes().get(2).getName());
		assertEquals("VALID2", endpoint.getAttributes().get(3).getName());
	}

	@Test
	public void addingDuplicateKeyDoesNothingWhenCommonAttributesDifferInValues() {
		String collectorName = "CollectorName";
		String key = "Key";
		String type = "Type";
		String nodeKey = "NodeKey";

		Topology name = new Topology("NAME", Collections.<String>emptySet());

		Map<String, String> attributes1 = new HashMap<String, String>();
		attributes1.put("VALID", "Valid");
		name.addEndpoint(collectorName, key, type, nodeKey, attributes1, DiscoveryType.DISCOVERED);

		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put("VALID", "Valid-different");
		attributes2.put("VALID2", "Valid2");
		name.addEndpoint(collectorName, key, type, nodeKey, attributes2, DiscoveryType.DISCOVERED);

		Endpoint endpoint = name.getEndpoints().get(0);

		assertEquals(3, endpoint.getAttributes().size());
		assertEquals(Topology.COLLECTOR_NAME_KEY, endpoint.getAttributes().get(0).getName());
		assertEquals(Topology.DISCOVERY_TYPE_KEY, endpoint.getAttributes().get(1).getName());
		assertEquals("VALID", endpoint.getAttributes().get(2).getName());
	}

}