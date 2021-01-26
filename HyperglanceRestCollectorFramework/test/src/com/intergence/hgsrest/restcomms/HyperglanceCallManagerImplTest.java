package com.intergence.hgsrest.restcomms;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.runner.Topology;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by stephen on 12/04/2015.
 */
public class HyperglanceCallManagerImplTest {


    public static final String DATASOURCE_NAME = "Intergence";
    public static final String COLLECTOR_NAME = "EMC";

    @Test
    public void canMakeComplexCall() throws IOException {
        HyperglanceCallManagerImpl hyperglanceCallManager = getHyperglanceCallManager();
        String complexJson = Resources.toString(Resources.getResource("complex.json"), Charsets.UTF_8);
        hyperglanceCallManager.put(HyperglanceCallManagerImpl.TOPOLOGY_REST_ADDRESS, complexJson);
    }

    @Test
    public void canManageDuplicateNodes() throws IOException {
        HyperglanceCallManagerImpl hyperglanceCallManager = getHyperglanceCallManager();

        Topology topology = new Topology(DATASOURCE_NAME, Collections.<String>emptySet());

        LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
        map.put("Name", "TEST");

        topology.addNode(COLLECTOR_NAME, "STEVE1", "server", map, DiscoveryType.DISCOVERED);
        topology.addNode(COLLECTOR_NAME, "STEVE2", "server", map, DiscoveryType.DISCOVERED);
        topology.addNode(COLLECTOR_NAME, "STEVE3", "cloud", map, DiscoveryType.DISCOVERED);
        topology.addNode(COLLECTOR_NAME, "STEVE3", "cloud", map, DiscoveryType.DISCOVERED);
        topology.addNode(COLLECTOR_NAME, "STEVE3", "ANY", map, DiscoveryType.DISCOVERED);

        topology.addEndpoint(COLLECTOR_NAME, "end1", "nic", "STEVE1", map, DiscoveryType.DISCOVERED);
        topology.addEndpoint(COLLECTOR_NAME, "end2", "nic", "STEVE2", map, DiscoveryType.DISCOVERED);
        topology.addEndpoint(COLLECTOR_NAME, "end3", "nic", "STEVE3", map, DiscoveryType.DISCOVERED);
        topology.addEndpoint(COLLECTOR_NAME, "end3", "nic", "STEVE3", map, DiscoveryType.DISCOVERED);
        topology.addEndpoint(COLLECTOR_NAME, "end3", "ANY", "STEVE3", map, DiscoveryType.DISCOVERED);

        topology.addLink(COLLECTOR_NAME, "link1", "optical", "end1", "end2", map, DiscoveryType.DISCOVERED);
        topology.addLink(COLLECTOR_NAME, "link2", "optical", "end1", "end3", map, DiscoveryType.DISCOVERED);
        topology.addLink(COLLECTOR_NAME, "link3", "optical", "end2", "end3", map, DiscoveryType.DISCOVERED);
        topology.addLink(COLLECTOR_NAME, "link3", "optical", "end2", "end3", map, DiscoveryType.DISCOVERED);
        topology.addLink(COLLECTOR_NAME, "link3", "ANY", "end2", "end3", map, DiscoveryType.DISCOVERED);

        hyperglanceCallManager.replaceTopology(topology);
    }

    private HyperglanceCallManagerImpl getHyperglanceCallManager() throws IOException {
        HyperglanceCallManagerImpl hyperglanceCallManager = new HyperglanceCallManagerImpl();

        hyperglanceCallManager.setRootHost("https://192.168.0.24");
        hyperglanceCallManager.setRootEndpoint("/hgapi");
        hyperglanceCallManager.setHyperglanceApiKey("cc3752ba-3b97-4f13-adeb-e048758b5855");
        hyperglanceCallManager.setHyperglanceDatasourceName(DATASOURCE_NAME);
        hyperglanceCallManager.setBypassSslSecurity(true);

        DefaultJsonComms jsonComms = new DefaultJsonComms();
        VerisonPathGetter verisonPathGetter = new VerisonPathGetter();
        verisonPathGetter.setJsonComms(jsonComms);
        hyperglanceCallManager.setJsonComms(jsonComms);
        hyperglanceCallManager.setVerisonPathGetter(verisonPathGetter);
        hyperglanceCallManager.setAuthorisationKeyEncoder(new AuthorisationKeyEncoder());

        hyperglanceCallManager.init();
        return hyperglanceCallManager;
    }
}