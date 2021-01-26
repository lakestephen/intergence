package com.intergence.hgsrest.refinement.collector.rule;

/**
 * Created by stephen on 23/09/2015.
 */
public class Rule {

    private String name;

    private TopologyType sourceTopologyType;
    private String sourceAttributeNameRegex;
    private String sourceAttributeValueRegex;

    private TopologyType destinationTopologyType;
    private String destinationAttributeNameRegex;
    private String destinationAttributeValueRegex;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TopologyType getSourceTopologyType() {
        return sourceTopologyType;
    }

    public void setSourceTopologyType(TopologyType sourceTopologyType) {
        this.sourceTopologyType = sourceTopologyType;
    }

    public String getSourceAttributeNameRegex() {
        return sourceAttributeNameRegex;
    }

    public void setSourceAttributeNameRegex(String sourceAttributeNameRegex) {
        this.sourceAttributeNameRegex = sourceAttributeNameRegex;
    }

    public String getSourceAttributeValueRegex() {
        return sourceAttributeValueRegex;
    }

    public void setSourceAttributeValueRegex(String sourceAttributeValueRegex) {
        this.sourceAttributeValueRegex = sourceAttributeValueRegex;
    }


    public TopologyType getDestinationTopologyType() {
        return destinationTopologyType;
    }

    public void setDestinationTopologyType(TopologyType destinationTopologyType) {
        this.destinationTopologyType = destinationTopologyType;
    }

    public String getDestinationAttributeNameRegex() {
        return destinationAttributeNameRegex;
    }

    public void setDestinationAttributeNameRegex(String destinationAttributeNameRegex) {
        this.destinationAttributeNameRegex = destinationAttributeNameRegex;
    }

    public String getDestinationAttributeValueRegex() {
        return destinationAttributeValueRegex;
    }

    public void setDestinationAttributeValueRegex(String destinationAttributeValueRegex) {
        this.destinationAttributeValueRegex = destinationAttributeValueRegex;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", sourceTopologyType=" + sourceTopologyType +
                ", sourceAttributeNameRegex='" + sourceAttributeNameRegex + '\'' +
                ", sourceAttributeValueRegex='" + sourceAttributeValueRegex + '\'' +
                ", destinationTopologyType=" + destinationTopologyType +
                ", destinationAttributeNameRegex='" + destinationAttributeNameRegex + '\'' +
                ", destinationAttributeValueRegex='" + destinationAttributeValueRegex + '\'' +
                '}';
    }
}
