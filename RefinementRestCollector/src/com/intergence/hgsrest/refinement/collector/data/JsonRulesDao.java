package com.intergence.hgsrest.refinement.collector.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.intergence.hgsrest.refinement.collector.rule.Rule;
import com.intergence.hgsrest.refinement.collector.rule.Rules;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 23/09/2015.
 */
public class JsonRulesDao implements RuleDao {

    private final Logger log = Logger.getLogger(this.getClass());

    private List<Rule> rules;

    @Override
    public List<Rule> getRules() {
        checkNotNull(rules);
        return rules;
    }

    public void setRulesClasspathFilename(String rulesFilename) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(rulesFilename);
        log.info("Loading Refinement rules from [" + rulesFilename + "]");

        try {
            InputStreamReader reader = new InputStreamReader(resourceAsStream);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Rules rulesUnpacked = gson.fromJson(reader, Rules.class);
            rules = new ArrayList<Rule>(rulesUnpacked.getRules());
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Error reading json rules file [" + rulesFilename + "]", e);
        }
    }
}