package com.intergence.hgsrest.refinement.collector;

import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.refinement.collector.data.JsonRulesDao;
import com.intergence.hgsrest.runner.Link;
import com.intergence.hgsrest.runner.Topology;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by stephen on 23/09/2015.
 */
public class RefinementDiscovererTest {

    public static final String MATCH_IP_ENDPOINT = "com\\intergence\\hgsrest\\refinement\\collector\\matchIpEndpoints.json";
    public static final String MATCH_IP_IPADDRESS_ENDPOINT = "com\\intergence\\hgsrest\\refinement\\collector\\matchIpToIpAddressEndpoints.json";
    public static final String MATCH_IP_ENDPOINT_TO_NODE = "com\\intergence\\hgsrest\\refinement\\collector\\matchIpEndpointsToNode.json";
    public static final String MATCH_SPECIFIC_ENDPOINTS = "com\\intergence\\hgsrest\\refinement\\collector\\specificEndpoints.json";

    @Test
    public void createsLinkWhenMatchesIdenticalEndpointAttribute() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_ENDPOINT);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());

        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);

        assertEquals(2, topology.getEndpoints().size());

        buildRefinementCollector(ruleDao).execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(1, links.size());
        Link link = links.get(0);
        assertEquals("EndpointServer1", link.getEndpointAKey());
        assertEquals("EndpointServer2", link.getEndpointBKey());
    }

    @Test
    public void createsLinkWhenMatchesIdenticalEndpointToNodeAttribute() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_ENDPOINT_TO_NODE);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());

        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addNode("CollectorName", "Server1", "server", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);

        assertEquals(1, topology.getEndpoints().size());

        buildRefinementCollector(ruleDao).execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(1, links.size());
        Link link = links.get(0);
        assertEquals("EndpointServer2", link.getEndpointAKey());
        assertEquals("Server1_Endpoint-Inferred", link.getEndpointBKey());
    }

    @Test
    public void doesNotCreateLinkWhenAttributesNotInRegexButValuesMatch() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_ENDPOINT);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ipaddress", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);
        assertEquals(2, topology.getEndpoints().size());

        buildRefinementCollector(ruleDao).execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(0, links.size());
    }

    @Test
    public void createsLinkWhenMatchesNonIdenticalEndpointAttribute() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_IPADDRESS_ENDPOINT);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ipaddress", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);
        assertEquals(2, topology.getEndpoints().size());

        buildRefinementCollector(ruleDao).execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(1, links.size());
        Link link = links.get(0);
        assertEquals("EndpointServer1", link.getEndpointAKey());
        assertEquals("EndpointServer2", link.getEndpointBKey());
    }

    @Test
    public void doesNotCreateLinkWithSelf() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_IPADDRESS_ENDPOINT);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        attributes1.put("ipaddress", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);
        assertEquals(1, topology.getEndpoints().size());

        buildRefinementCollector(ruleDao).execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(1, topology.getEndpoints().size());
        assertEquals(0, links.size());
    }

    @Test
    public void doesNotCreateMultipleLinkWhenMultipleAttributesMatch() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_ENDPOINT);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        attributes1.put("ipaddress", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "1.2.3.4");
        attributes2.put("ipaddress", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);
        assertEquals(2, topology.getEndpoints().size());

        RefinementDiscoverer refinementDiscoverer = buildRefinementCollector(ruleDao);
        refinementDiscoverer.execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(1, links.size());
    }


    @Test
    public void createsSpecificNodeConnection() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_SPECIFIC_ENDPOINTS);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes3 = new HashMap<String, String>();
        attributes3.put("ip", "5.5.5.5");
        topology.addEndpoint("CollectorName", "EndpointServer3", "server", "Server3", attributes3, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes4 = new HashMap<String, String>();
        attributes4.put("something", "Steve");
        topology.addEndpoint("CollectorName", "EndpointServer4", "server", "Server4", attributes4, DiscoveryType.DISCOVERED);

        assertEquals(4, topology.getEndpoints().size());

        RefinementDiscoverer refinementDiscoverer = buildRefinementCollector(ruleDao);
        refinementDiscoverer.execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(4, topology.getEndpoints().size());
        assertEquals(1, links.size());
    }

    @Test
    public void doesNotCreateSpecificNodeConnectionWhenNoMatch() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_SPECIFIC_ENDPOINTS);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "SHOULD_NOT_MATCH");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "SHOULD_NOT_MATCH");
        topology.addEndpoint("CollectorName", "EndpointServer2", "server", "Server2", attributes2, DiscoveryType.DISCOVERED);
        assertEquals(2, topology.getEndpoints().size());

        RefinementDiscoverer refinementDiscoverer = buildRefinementCollector(ruleDao);
        refinementDiscoverer.execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(2, topology.getEndpoints().size());
        assertEquals(0, links.size());
    }

    @Test
    public void doesNotMatchBetweenANodeAndItsOwnEndpoint() {

        JsonRulesDao ruleDao = new JsonRulesDao();
        ruleDao.setRulesClasspathFilename(MATCH_IP_ENDPOINT_TO_NODE);

        Topology topology = new Topology("TEST", Collections.<String>emptySet());


        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("ip", "1.2.3.4");
        topology.addNode("CollectorName", "Server1", "server", attributes1, DiscoveryType.DISCOVERED);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "1.2.3.4");
        topology.addEndpoint("CollectorName", "EndpointServer1", "server", "Server1", attributes2, DiscoveryType.DISCOVERED);
        assertEquals(1, topology.getNodes().size());
        assertEquals(1, topology.getEndpoints().size());

        RefinementDiscoverer refinementDiscoverer = buildRefinementCollector(ruleDao);
        refinementDiscoverer.execute(topology);

        List<Link> links = topology.getLinks();

        assertEquals(1, topology.getNodes().size());
        assertEquals(1, topology.getEndpoints().size());
        assertEquals(0, links.size());
    }


    private RefinementDiscoverer buildRefinementCollector(JsonRulesDao ruleDao) {
        RefinementDiscoverer refinementDiscoverer = new RefinementDiscoverer();
        refinementDiscoverer.setRuleDao(ruleDao);
        refinementDiscoverer.setCollectorName("Refinement");
        return refinementDiscoverer;
    }

}