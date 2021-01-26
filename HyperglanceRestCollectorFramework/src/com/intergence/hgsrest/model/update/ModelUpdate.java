package com.intergence.hgsrest.model.update;

import com.intergence.hgsrest.model.enumeration.AlarmSeverityEnum;
import com.intergence.hgsrest.runner.Endpoint;
import com.intergence.hgsrest.runner.Link;
import com.intergence.hgsrest.runner.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by stephen on 27/01/2015.
 */
public interface ModelUpdate {

    Node addNode(String collectorName, String key, String type, Map<String, String> attributes, DiscoveryType discoveryType);
    Endpoint addEndpoint(String collectorName, String key, String type, String nodeKey, Map<String, String> attributes, DiscoveryType discoveryType);
    Link addLink(String collectorName, String key,String type, String endAId, String endBId, Map<String, String> attributes, DiscoveryType discoveryType);
    void addAlarm(String collectorName, String vSphereId, String nodeVSphereId, Object o, AlarmSeverityEnum severity, Map<String, String> attributes, DiscoveryType discoveryType);

    List<Node> getNodes();
    List<Endpoint> getEndpoints();

}
