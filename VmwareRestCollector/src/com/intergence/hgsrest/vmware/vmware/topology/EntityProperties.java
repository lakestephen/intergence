/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class EntityProperties {	
	private static final AttributeMap hostNodeProperties = HostNodeCollector.getProperties();
	private static final AttributeMap vmNodeProperties = VirtualMachineNodeCollector.getProperties();
	private static final AttributeMap datastoreNodeProperties = DatastoreNodeCollector.getProperties();
	private static final AttributeMap dvsNodeProperties = DVSwitchNodeCollector.getProperties();
	private static final AttributeMap netNodeProperties = NetworkNodeCollector.getProperties();
	
	private static final AttributeMap vSwitchPropertiesOnHost = VSwitchNestedNodeCollector.getPropertiesOnHost();
	
	private static final AttributeMap hostLinkProperties = HostLinkCollector.getProperties();
	private static final AttributeMap vmLinkProperties   = VirtualMachineLinkCollector.getProperties();
	private static final AttributeMap dvsLinkProperties = DVSwitchLinkCollector.getProperties();
	private static final AttributeMap netLinkProperties = NetworkLinkCollector.getProperties();
	
	private static final AttributeMap hostEndPointProperties = HostEndPointCollector.getProperties();
	private static final AttributeMap vmEndPointProperties = VirtualMachineEndPointCollector.getProperties();
	private static final AttributeMap datastoreEndPointProperties = DatastoreEndPointCollector.getProperties();
	
	private static final AttributeMap resourcePoolProperties = ResourcePoolGroupCollector.getProperties();
	private static final AttributeMap computeResourceProperties = ComputeResourceGroupCollector.getProperties();
	
	private double version;
	
	public EntityProperties(double version) {
		this.version = version;
	}

	public String[] getHostProperties() {
		return composePropertiesArray(hostNodeProperties, hostEndPointProperties, hostLinkProperties, vSwitchPropertiesOnHost);
	}
	
	public String[] getVmProperties() {
		return composePropertiesArray(vmNodeProperties, vmEndPointProperties, vmLinkProperties, null);
	}
	
	public String[] getDatastoreProperties() {
		return composePropertiesArray(datastoreNodeProperties, datastoreEndPointProperties, null, null);
	}
	
	public String[] getDvsProperties() {
		return composePropertiesArray(dvsNodeProperties, null, dvsLinkProperties, null);
	}
	
	public String[] getNetProperties() {
		return composePropertiesArray(netNodeProperties, null, netLinkProperties, null);
	}
	
	public String[] getResourcePoolProperties() {
		return resourcePoolProperties.getAttributePaths(version).toArray(new String[0]);
	}
	
	public String[] getComputeResourceProperties() {
		return computeResourceProperties.getAttributePaths(version).toArray(new String[0]);
	}
	
	private String[] composePropertiesArray(AttributeMap nodeAttributes, AttributeMap endpointAttributes, AttributeMap linkAttributes, AttributeMap nestedEntityAttributes) {
		Collection<String> nodeProperties = propertiesOf(nodeAttributes);
		Collection<String> endpointProperties = propertiesOf(endpointAttributes);
		Collection<String> linkProperties = propertiesOf(linkAttributes);
		Collection<String> nestedEntityProperties = propertiesOf(nestedEntityAttributes);
		
		List<String> properties = new ArrayList<String>();
		properties.addAll(nodeProperties);
		properties.addAll(endpointProperties);
		properties.addAll(linkProperties);
		properties.addAll(nestedEntityProperties);
		
		return properties.toArray(new String[0]);
	}
	
	private Collection<String> propertiesOf(AttributeMap attributes) {
		if (attributes == null) { return new ArrayList<String>(); }
		return attributes.getAttributePaths(version);
	}
}
