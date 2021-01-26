package com.intergence.hgsrest.directinject.collector.model;


import com.intergence.hgsrest.runner.Endpoint;
import com.intergence.hgsrest.runner.Link;
import com.intergence.hgsrest.runner.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 05/10/2015.
 */
public class Model {

    private List<Node> nodes = new ArrayList<Node>();
    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private List<Link> links = new ArrayList<Link>();

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public List<Link> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return "Model{" +
                "nodes=" + nodes +
                ", endpoints=" + endpoints +
                ", links=" + links +
                '}';
    }
}
