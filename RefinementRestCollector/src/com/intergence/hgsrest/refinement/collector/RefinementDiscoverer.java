package com.intergence.hgsrest.refinement.collector;

import com.google.common.base.Strings;
import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.collection.SkeletalDiscoveryExecutor;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import com.intergence.hgsrest.refinement.collector.data.RuleDao;
import com.intergence.hgsrest.refinement.collector.rule.Rule;
import com.intergence.hgsrest.refinement.collector.rule.TopologyType;
import com.intergence.hgsrest.runner.Attribute;
import com.intergence.hgsrest.runner.Endpoint;
import com.intergence.hgsrest.runner.Node;
import com.intergence.hgsrest.runner.SkeletalTopology;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Connect and merge models
 * Called by the HyperGlance framework
 *
 * @author Stephen Lake
 */
public class RefinementDiscoverer extends SkeletalDiscoveryExecutor implements DiscoveryExecutor {

	private final Logger log = Logger.getLogger(this.getClass());

	private RuleDao ruleDao;

	@Override
	public void execute(ModelUpdate modelUpdate) {
		List<Rule> rules = ruleDao.getRules();

		log.info("Processing Refinement Rules in order: [" + rules + "]");

		for (Rule rule : rules) {
			try {
				checkRule(rule);
				processRule(rule, modelUpdate);
			}
			catch (Exception e) {
				log.error("Refinement Rule [" + rule + "] error", e);
			}
		}
	}

	private void checkRule(Rule rule) {
		checkState(!Strings.isNullOrEmpty(rule.getName()), "Rule is missing a name [%s]", rule);
		checkNotNull(rule.getSourceTopologyType(), "Rule is missing a sourceTopologyType [%s]", rule);
		checkState(!Strings.isNullOrEmpty(rule.getSourceAttributeNameRegex()), "Rule is missing a sourceAttributeNameRegex [%s]", rule);

		checkState(!Strings.isNullOrEmpty(rule.getName()), "Rule is missing a name [%s]", rule);
		checkNotNull(rule.getDestinationTopologyType(), "Rule is missing a destinationTopologyType [%s]", rule);
		checkState(!Strings.isNullOrEmpty(rule.getDestinationAttributeNameRegex()), "Rule is missing a destinationAttributeNameRegex [%s]", rule);

		checkState(Strings.isNullOrEmpty(rule.getSourceAttributeValueRegex()) == Strings.isNullOrEmpty(rule.getDestinationAttributeValueRegex()), "When supplying value Regex, please supply source and destination [%s]", rule);
	}

	private void processRule(Rule rule, ModelUpdate modelUpdate) {
		log.info("Processing Refinement Rule: [" + rule.getName() + "]");
		log.debug("Rule Details: [" + rule + "]");

		// Find Source candidates
		TopologyType sourceTopologyType = rule.getSourceTopologyType();
		String sourceAttributeNameRegex = rule.getSourceAttributeNameRegex();
		Map<SkeletalTopology, String> sourceCandidates = getTopologyAndValueWithAttributesThatMatch(sourceTopologyType, sourceAttributeNameRegex, modelUpdate);
		if (sourceCandidates.size() == 0) {
			return;
		}

		// Find Destination candidates
		String destinationAttributeNameRegex = rule.getDestinationAttributeNameRegex();
		TopologyType destinationTopologyType = rule.getDestinationTopologyType();
		Map<SkeletalTopology, String> destinationCandidates = getTopologyAndValueWithAttributesThatMatch(destinationTopologyType, destinationAttributeNameRegex, modelUpdate);

		boolean exactMatch = Strings.isNullOrEmpty(rule.getSourceAttributeValueRegex()) && Strings.isNullOrEmpty(rule.getDestinationAttributeValueRegex());


		Set<Match> matches;
		if (exactMatch) {
			matches = getExactMatches(sourceCandidates, destinationCandidates);
		}
		else {
			matches = getRegexMatches(sourceCandidates, destinationCandidates, rule);
		}

		log.info("Adding [" + matches.size() + "] matches to the model [" + matches + "]");

		for (Match match : matches) {

			Endpoint endpoint1 = getEndpointForTopology(match.topology1, modelUpdate);
			Endpoint endpoint2 = getEndpointForTopology(match.topology2, modelUpdate);


			String key = endpoint1.getKey() + "-" + endpoint2.getKey()  + "-Inferred";;
			log.info("Creating inferred link [" + key + "]");
			modelUpdate.addLink(getCollectorName(),
					key,
					"inferred",
					endpoint1.getKey(),
					endpoint2.getKey(),
					new HashMap<String, String>(),
					DiscoveryType.INFERRED);
		}
	}

	private Map<SkeletalTopology, String> getTopologyAndValueWithAttributesThatMatch(String attributeNameRegex, List<? extends SkeletalTopology> topologies) {
		Map<SkeletalTopology, String> result = new HashMap<SkeletalTopology, String>();
		Pattern p = Pattern.compile(attributeNameRegex);

		for (SkeletalTopology topology : topologies) {
			// look for attributes where where source attribute name matches
			List<Attribute> attributes = topology.getAttributes();

			for (Attribute attribute : attributes) {
				Matcher m = p.matcher(attribute.getName());

				if (m.matches()) {
					result.put(topology, attribute.getValue());
				}
			}
		}

		return result;
	}

	/**
	 * Search each combination of source and destination for an exact value match
	 * @param sourceCandidates
	 * @param destinationCandidates
	 * @return
	 */
	private Set<Match> getExactMatches(Map<SkeletalTopology, String> sourceCandidates, Map<SkeletalTopology, String> destinationCandidates) {
		// Match source and destination
		Set<Match> matches = new HashSet<Match>();
		for (Map.Entry<SkeletalTopology, String> source : sourceCandidates.entrySet()) {
			for (Map.Entry<SkeletalTopology, String> destination : destinationCandidates.entrySet()) {

				// Do not match with our self
				if (source.getKey() == destination.getKey()) {
					log.debug("Not evaluating self match [" + source + "] with [" + destination + "]");
				}
				else if (isPairingNodeWithItsEndpoint(source.getKey(), destination.getKey())){
					log.debug("Not evaluating node match to its own endpoint [" + source + "] with [" + destination + "]");
				}
				else {
					if (source.getValue().equals(destination.getValue())) {
						boolean added = matches.add(new Match(source.getKey(), destination.getKey()));
						if (added) {
							log.debug("Adding match [" + source + "] with [" + destination + "]");
						}
						else {
							log.debug("Not adding duplicate match [" + source + "] with [" + destination + "]");
						}
					}
				}
			}
		}
		return matches;
	}

	private Set<Match> getRegexMatches(Map<SkeletalTopology, String> sourceCandidates, Map<SkeletalTopology, String> destinationCandidates, Rule rule) {
		Set<SkeletalTopology> sourceMatches = findRegexMatches(sourceCandidates, rule.getSourceAttributeValueRegex());
		log.debug("Found [" + sourceMatches.size() + "] source regex matches for [" + rule.getSourceAttributeValueRegex() + "] in [" + sourceCandidates + "]");
		if (sourceMatches.size() == 0) {
			return Collections.emptySet();
		}

		Set<SkeletalTopology> destinationMatches = findRegexMatches(destinationCandidates, rule.getDestinationAttributeValueRegex());
		log.debug("Found [" + destinationMatches.size() + "] destination regex matches for [" + rule.getDestinationAttributeValueRegex() + "] in [" + destinationCandidates + "]");
		if (destinationMatches.size() ==0) {
			return Collections.emptySet();
		}

		Set<Match> matches = new HashSet<Match>();

		for (SkeletalTopology source : sourceMatches) {
			for (SkeletalTopology destination : destinationMatches) {
				// Do not match with our self
				if (source == destination) {
					log.debug("Not evaluating self match [" + source + "] with [" + destination + "]");
				}
				else if (isPairingNodeWithItsEndpoint(source, destination)){
					log.debug("Not evaluating node match to its own endpoint [" + source + "] with [" + destination + "]");
				}
				else {
					boolean added = matches.add(new Match(source, destination));
					if (added) {
						log.debug("Adding match [" + source + "] with [" + destination + "]");
					}
					else {
						log.debug("Not adding duplicate match [" + source + "] with [" + destination + "]");
					}
				}
			}
		}

		return matches;
	}

	private boolean isPairingNodeWithItsEndpoint(SkeletalTopology source, SkeletalTopology destination) {
		if (source instanceof Node && destination instanceof Endpoint) {
			return source.getKey().equals(((Endpoint) destination).getNodeKey());
		}
		if (destination instanceof Node && source instanceof Endpoint) {
			return destination.getKey().equals(((Endpoint) source).getNodeKey());
		}
		return false;
	}

	private Set<SkeletalTopology> findRegexMatches(Map<SkeletalTopology, String> sourceCandidates, String attributeValueRegex) {
		Pattern sourceAttributeValueRegexPattern = Pattern.compile(attributeValueRegex);
		Set<SkeletalTopology> matches = new HashSet<SkeletalTopology>();
		for (Map.Entry<SkeletalTopology, String> source : sourceCandidates.entrySet()) {
			Matcher matcher = sourceAttributeValueRegexPattern.matcher(source.getValue());
			if (matcher.matches()) {
				matches.add(source.getKey());
			}
		}
		return matches;
	}

	private Endpoint getEndpointForTopology(SkeletalTopology topology, ModelUpdate modelUpdate) {
		if (topology instanceof Endpoint) {
			return (Endpoint)topology;
		}
		else if (topology instanceof Node) {
			Node node = (Node)topology;
			String endpointKey = node.getKey() + "_Endpoint-Inferred";
			log.info("Creating inferred endpoint [" + endpointKey + "] for Node [" + node.getKey() + "]");
			return modelUpdate.addEndpoint(getCollectorName(),
					endpointKey,
					"inferred",
					node.getKey(),
					new HashMap<String, String>(),
					DiscoveryType.INFERRED);

		}
		else {
			throw new RuntimeException();
		}
	}

	private Map<SkeletalTopology, String> getTopologyAndValueWithAttributesThatMatch(TopologyType topologyType, String attributeNameRegex, ModelUpdate modelUpdate) {
		Map<SkeletalTopology, String> result;

		switch (topologyType) {
			case NODE:
				result = getTopologyAndValueWithAttributesThatMatch(attributeNameRegex, modelUpdate.getNodes());
				break;
			case ENDPOINT:
				result = getTopologyAndValueWithAttributesThatMatch(attributeNameRegex, modelUpdate.getEndpoints());
				break;
			default:
				throw new RuntimeException("No handler for [" + topologyType + "]");
		}

		log.debug("Found [" + result.size() + "] candidate [" + topologyType + "] with attribute name matching regex [" + attributeNameRegex + "]");
		return result;
	}

	public void setRuleDao(RuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

	@Override
    public String toString() {
        return "RefinementDiscoverer{" +
                "hgsDatasourceName='" + getCollectorName() + '\'' +
				", executeOrder='" + getExecuteOrder() + '\'' +
				'}';
    }

	class Match {

		private final SkeletalTopology topology1;
		private final SkeletalTopology topology2;


		Match(SkeletalTopology topology1, SkeletalTopology topology2) {

			List<SkeletalTopology> endpoints = new ArrayList<SkeletalTopology>();
			endpoints.add(topology1);
			endpoints.add(topology2);
			Collections.sort(endpoints, new Comparator<SkeletalTopology>() {
				@Override
				public int compare(SkeletalTopology o1, SkeletalTopology o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			this.topology1 = endpoints.get(0);
			this.topology2 = endpoints.get(1);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Match match = (Match) o;

			if (topology1 != null ? !topology1.equals(match.topology1) : match.topology1 != null) return false;
			return !(topology2 != null ? !topology2.equals(match.topology2) : match.topology2 != null);

		}

		@Override
		public int hashCode() {
			int result = topology1 != null ? topology1.hashCode() : 0;
			result = 31 * result + (topology2 != null ? topology2.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Match{" +
					"topology1=" + topology1 +
					", topology2=" + topology2 +
					'}';
		}
	}
}
