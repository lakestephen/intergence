package com.intergence.hgsrest.refinement.collector.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by stephen on 23/09/2015.
 */
public class RuleTest {

    private Logger log = Logger.getLogger(this.getClass());

    @Test
    public void shortcutToGeneratingRules() {

        Rule rule = new Rule();

        rule.setSourceTopologyType(TopologyType.ENDPOINT);
        rule.setSourceAttributeNameRegex("ipaddress");

        rule.setDestinationTopologyType(TopologyType.ENDPOINT);
        rule.setDestinationAttributeNameRegex("ip");

        Rules rules = new Rules();
        rules.add(rule);
        rules.add(rule);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(rules);

        Rules rulesUnpacked = gson.fromJson(json, Rules.class);

        log.info(json);
    }

}
