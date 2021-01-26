package com.intergence.hgsrest.runner;

import com.google.common.base.Strings;
import com.intergence.hgsrest.model.enumeration.AlarmSeverityEnum;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by stephen on 10/02/2015.
 */
public class Topology implements ModelUpdate {

    public static final String COLLECTOR_NAME_KEY = "origin";
    public static final String DISCOVERY_TYPE_KEY = "discovery";

    public static final Set<String> reservedAttributeNames = new HashSet<String>();

    private static Logger log = Logger.getLogger(Topology.class);

    private final String name; //NOTE: Needed for reflection introspection by the gson library
    private final List<Node> nodes = new ArrayList<Node>();
    private final List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private final List<Link> links = new ArrayList<Link>();

    public Topology(String name, Set<String> reservedAttributeNames) {
        this.name = checkNotNull(name);
        this.reservedAttributeNames.add(COLLECTOR_NAME_KEY);
        this.reservedAttributeNames.addAll(reservedAttributeNames);

        log.info("Using reserved attributes [" + reservedAttributeNames + "]");
    }

    @Override
    public Node addNode(String collectorName, String key, String type,  Map<String, String> attributes, DiscoveryType discoveryType) {
        checkNotNull(key);
        checkNotNull(type);
        checkNotNull(attributes);

        Map<String, String> processedAttributes = processAttributes(collectorName, key, type, attributes, discoveryType);

        Node newNode = new Node(key, type, processedAttributes);
        addOrMerge(key, newNode, nodes);

        return newNode;
    }

    @Override
    public Endpoint addEndpoint(String collectorName, String key, String type, String nodeKey, Map<String, String> attributes, DiscoveryType discoveryType) {
        checkNotNull(key);
        checkNotNull(type);
        checkNotNull(attributes);
        checkState(!key.equals(nodeKey));

        Map<String, String> processedAttributes = processAttributes(collectorName, key, type, attributes, discoveryType);

        Endpoint newEndpoint = new Endpoint(key, type, nodeKey, processedAttributes);
        addOrMerge(key, newEndpoint, endpoints);

        return newEndpoint;
    }

    @Override
    public Link addLink(String collectorName, String key, String type, String endAId, String endBId, Map<String, String> attributes, DiscoveryType discoveryType) {
        checkNotNull(key);
        checkNotNull(type);
        checkNotNull(attributes);

        Map<String, String> processedAttributes = processAttributes(collectorName, key, type, attributes, discoveryType);

        Link newLink = new Link(key, type, endAId, endBId, processedAttributes);
        addOrMerge(key, newLink, links);

        return newLink;
    }

    @Override
    public void addAlarm(String collectorName, String vSphereId, String nodeVSphereId, Object o, AlarmSeverityEnum severity, Map<String, String> attributes, DiscoveryType discoveryType) {
        log.warn("The Rest API does not support alarms");
    }


    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public List<Endpoint> getEndpoints() {
        return Collections.unmodifiableList(endpoints);
    }

    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }



    private Map<String, String> processAttributes(String collectorName, String key, String type, Map<String, String> attributes, DiscoveryType discoveryType) {
        // defensive copy
        Map<String, String> processedAttributes = new HashMap<String, String>(attributes);

        removeReservedAttributeNames(key, type, collectorName, processedAttributes);
        addCannedAttribute(key, type, COLLECTOR_NAME_KEY, collectorName, processedAttributes);
        addCannedAttribute(key, type, DISCOVERY_TYPE_KEY, discoveryType.toString(), processedAttributes);
        removeEmptyOrNullAttributes(key, type, collectorName, processedAttributes);

        return processedAttributes;
    }

    private void removeReservedAttributeNames(String key, String type, String collectorName, Map<String, String> attributes) {
        for (String reservedAttributeName : reservedAttributeNames) {
            for ( Iterator<String> iterator = attributes.keySet().iterator(); iterator.hasNext(); ) {
                String attribute = iterator.next();
                if (reservedAttributeName.equalsIgnoreCase(attribute)) {
                    log.warn("Removing attribute that is a reserved name [" + reservedAttributeName+ "] for [" + collectorName + ", " + type + ", " + key + "]");
                    iterator.remove();
                }
            }
        }
    }

    private void addCannedAttribute(String key, String type, String attributeKey, String attributeValue, Map<String, String> attributes) {
        if (attributes.equals(attributeKey)) {
            log.warn("Overriding attribute [" + attributeKey + "] for [" + attributeValue + ", " + type + ", " + key + "] to add canned attribute [" + attributeValue + "]");
        }

        attributes.put(attributeKey, attributeValue);
    }

    private void removeEmptyOrNullAttributes(String key, String type, String collectorName, Map<String, String> attributes) {
        for ( Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> attribute = iterator.next();
            if (Strings.isNullOrEmpty(attribute.getValue())) {
                log.warn("Removing empty attribute [" + attribute.getKey() + "] for [" + collectorName + ", " + type + ", " + key + "]");
                iterator.remove();
            }
        }
    }

    private <T extends SkeletalTopology> void addOrMerge(String key, T newSkeletalTopology, List<T> topologyList) {
        for (int i=0;i<topologyList.size();i++) {
            SkeletalTopology existingSkeletalTopology = topologyList.get(i);
            if (key.equals(existingSkeletalTopology.getKey())) {
                mergeTopology(newSkeletalTopology, existingSkeletalTopology);
                return;
            }
        }
        topologyList.add(newSkeletalTopology);
    }

    private <T extends SkeletalTopology> void mergeTopology(T newSkeletalTopology, SkeletalTopology existingSkeletalTopology) {
        // We have an existing topology that has a matched key
        if (existingSkeletalTopology.equals(newSkeletalTopology)) {
            log.debug("Ignoring exact duplicate [" + newSkeletalTopology + "]");
        }
        else {
            // First check to see if common attributes have the same value
            boolean okToMerge = true;
            List<Attribute> existingAttributes = existingSkeletalTopology.getAttributes();
            List<Attribute> newAttributes = newSkeletalTopology.getAttributes();

            for (Attribute existingAttribute : existingAttributes) {
                for (Attribute newAttribute : newAttributes) {
                    if (existingAttribute.getName().equals( newAttribute.getName())) {
                        if (!existingAttribute.equals(newAttribute)) {
                            okToMerge = false;
                            break;
                        }
                    }
                }
            }

            if (okToMerge) {
                log.info("Merging [" + newSkeletalTopology + "] into [" + existingSkeletalTopology + "]");
                for (Attribute newAttribute : newAttributes) {
                    // Check if it exists in the existing attribute list.
                    boolean needsAdding = true;
                    for (Attribute existingAttribute : existingAttributes) {
                        if (existingAttribute.getName().equals( newAttribute.getName())) {
                            needsAdding = false;
                            break;
                        }
                    }
                    if (needsAdding) {
                        existingAttributes.add(newAttribute);
                    }
                }
            }
            else {
                log.warn("Cant merge [" + newSkeletalTopology + "] into [" + existingSkeletalTopology + "] as common attributes differ");
            }
        }
    }

    @Override
    public String toString() {
        return "Topology{" +
                "name='" + name + '\'' +
                ", nodeCount=" + nodes.size() +
                ", endpointCount=" + endpoints.size() +
                ", linkCount=" + links.size() +
                '}';
    }
}
