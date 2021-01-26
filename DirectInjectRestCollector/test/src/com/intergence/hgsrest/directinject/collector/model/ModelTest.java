package com.intergence.hgsrest.directinject.collector.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intergence.hgsrest.runner.Endpoint;
import com.intergence.hgsrest.runner.Link;
import com.intergence.hgsrest.runner.Node;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephen on 05/10/2015.
 */
public class ModelTest {

    private Logger log = Logger.getLogger(this.getClass());

    @Test
    public void shortcutToGeneratingRules() {

        Model model = new Model();

        addNodes(model);
        addEndpoints(model);
        addLinks(model);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(model);

        log.info(json);

        Model rulesUnpacked = gson.fromJson(json, Model.class);

    }

    private void addNodes(Model model) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("ip", "1.2.3.4");
        attributes.put("location", "london");
        Node node = new Node("Server12", "server",attributes);


        Map<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "5.6.7.8");
        attributes2.put("type", "HP");
        Node node2 = new Node("router888", "router",attributes2);

        model.addNode(node);
        model.addNode(node2);
    }

    private void addLinks(Model model) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("length", "20Meters");
        Link link = new Link("serverRouterLink", "wire","router888_Endpoint", "Server12_Endpoint" ,attributes);

        model.addLink(link);
    }

    private void addEndpoints(Model model) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("ip", "5.6.7.8");
        Endpoint endpoint1 = new Endpoint("router888_Endpoint", "socket", "Server12", attributes);

        Map<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("ip", "5.6.7.8");
        Endpoint endpoint2 = new Endpoint("Server12_Endpoint", "NIC", "router888", attributes2);

        model.addEndpoint(endpoint1);
        model.addEndpoint(endpoint2);

    }
}