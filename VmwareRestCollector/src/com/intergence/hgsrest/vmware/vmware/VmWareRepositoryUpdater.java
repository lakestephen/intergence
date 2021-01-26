/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware;

import com.intergence.hgsrest.model.enumeration.AlarmSeverityEnum;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class VmWareRepositoryUpdater {
	private static final String LINK_ATTRIBUTE_LOGICAL = "logical";
	
	private final ModelUpdate topology;
	private final String collectorName;
	
	private static Logger logger = Logger.getLogger(VmWareRepositoryUpdater.class);
	
	public VmWareRepositoryUpdater(ModelUpdate topology, String collectorName) {
		this.topology = topology;
		this.collectorName = collectorName;
	}
	
	public void addAlarm(String vSphereId, String nodeVSphereId, AlarmSeverityEnum severity, Map<String, String> attributes) {
		topology.addAlarm(collectorName, vSphereId, nodeVSphereId, null, severity, attributes, DiscoveryType.DISCOVERED);
	}
	
	public void addNode(String vSphereId, NodeTypeEnum type, Map<String, String> attributes) {
		attributes.put(NodeCollectorConstants.ATTRIBUTE_TYPE, type.getDisplayableName()); // TODO same pattern as in SimpleTopologyHelper
		topology.addNode(collectorName, vSphereId, type.getDisplayableName(), attributes, DiscoveryType.DISCOVERED);
	}
	
	private void tryAddEndpoint(String nodeForeignSourceId, String foreignSourceId, Map<String, String> attributes, boolean updateAttributes) {
		try {
			topology.addEndpoint(collectorName, foreignSourceId, "endpoint", nodeForeignSourceId, attributes, DiscoveryType.DISCOVERED);
		} catch (NoSuchElementException nsee) {
			logger.warn("Tried to attach an endpoint to a node with id " + nodeForeignSourceId + " but it does not exist. Skipping this addition and continuing.");
		}
	}
	
	public void addEndPoint(String vSphereAttachmentNodeId, String vSphereEndPointId, Map<String, String> attributes) {
		String foreignSourceId = vSphereAttachmentNodeId + "/" + vSphereEndPointId;
		tryAddEndpoint(vSphereAttachmentNodeId, foreignSourceId, attributes, true);
	}

	public void addLink(String vSphereNodeAId, String vSphereNodeAEndPointId, String vSphereNodeBId, String vSphereNodeBEndPointId, boolean isLogical, Map<String, String> sharedEndPointAttributes) {
		String endAId = vSphereNodeAId + "/" + vSphereNodeAEndPointId;
		String endBId = vSphereNodeBId + "/" + vSphereNodeBEndPointId;
		
		String linkId = vSphereNodeAId + "/" + vSphereNodeAEndPointId + "/" + vSphereNodeBId + "/" + vSphereNodeBEndPointId; // note: this is a directed key rather than an undirected key
		
		if (vSphereNodeAEndPointId == null) {
			endAId = vSphereNodeAId + "/" + vSphereNodeBId + "/" + vSphereNodeBEndPointId;
		}
		
		if (vSphereNodeBEndPointId == null) {
			endBId = vSphereNodeBId + "/" + vSphereNodeAId + "/" + vSphereNodeAEndPointId;
		}
		
		
		tryAddEndpoint(vSphereNodeAId, endAId, sharedEndPointAttributes, false);
		tryAddEndpoint(vSphereNodeBId, endBId, sharedEndPointAttributes, false);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(LINK_ATTRIBUTE_LOGICAL, String.valueOf(isLogical));
		
		topology.addLink(collectorName, linkId, "link", endAId, endBId, attributes, DiscoveryType.DISCOVERED);
	}
}
