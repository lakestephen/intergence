package com.intergence.hgsrest.refinement.collector.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stephen on 23/09/2015.
 */
public class Rules {

    private ArrayList<Rule> rules = new ArrayList<Rule>();

    public void add(Rule rule) {
        rules.add(rule);
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    @Override
    public String toString() {
        return "Rules{" +
                "rules=" + rules +
                '}';
    }
}
