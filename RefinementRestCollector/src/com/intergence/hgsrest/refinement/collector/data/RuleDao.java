package com.intergence.hgsrest.refinement.collector.data;

import com.intergence.hgsrest.refinement.collector.rule.Rule;

import java.util.List;

/**
 * Created by stephen on 23/09/2015.
 */
public interface RuleDao {

    List<Rule> getRules();
}
