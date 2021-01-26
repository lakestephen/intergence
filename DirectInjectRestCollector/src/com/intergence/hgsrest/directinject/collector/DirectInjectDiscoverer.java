package com.intergence.hgsrest.directinject.collector;

import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.collection.SkeletalDiscoveryExecutor;
import com.intergence.hgsrest.directinject.collector.data.DirectInjectDao;
import com.intergence.hgsrest.directinject.collector.model.Model;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import com.intergence.hgsrest.runner.Attribute;
import com.intergence.hgsrest.runner.Endpoint;
import com.intergence.hgsrest.runner.Link;
import com.intergence.hgsrest.runner.Node;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Add in static nodes from a file.
 * Called by the HyperGlance framework
 *
 * @author Stephen Lake
 */
public class DirectInjectDiscoverer extends SkeletalDiscoveryExecutor implements DiscoveryExecutor {

	private final Logger log = Logger.getLogger(this.getClass());

	private DirectInjectDao directInjectDao;

	@Override
	public void execute(ModelUpdate modelUpdate) {
		Model model = directInjectDao.getModel();

		log.info("Processing Direct Inject Model : [" + model + "]");

		processNodes(modelUpdate, model);
		processEndpoints(modelUpdate, model);
		processLinks(modelUpdate, model);

	}


	private void processNodes(ModelUpdate modelUpdate, Model model) {
		for (Node node : model.getNodes()) {
			Map<String, String> attributes = new HashMap<String, String>();
			for (Attribute attribute : node.getAttributes()) {
				attributes.put(attribute.getName(), attribute.getValue());
			}
			modelUpdate.addNode(getCollectorName(), node.getKey(), node.getType(),attributes , DiscoveryType.DISCOVERED);
		}
	}

	private void processEndpoints(ModelUpdate modelUpdate, Model model) {
		for (Endpoint endpoint : model.getEndpoints()) {
			Map<String, String> attributes = new HashMap<String, String>();
			for (Attribute attribute : endpoint.getAttributes()) {
				attributes.put(attribute.getName(), attribute.getValue());
			}
			modelUpdate.addEndpoint(getCollectorName(), endpoint.getKey(), endpoint.getType(), endpoint.getNodeKey(), attributes, DiscoveryType.DISCOVERED);
		}
	}

	private void processLinks(ModelUpdate modelUpdate, Model model) {
		for (Link link : model.getLinks()) {
			Map<String, String> attributes = new HashMap<String, String>();
			for (Attribute attribute : link .getAttributes()) {
				attributes.put(attribute.getName(), attribute.getValue());
			}
			modelUpdate.addLink(getCollectorName(), link.getKey(), link.getType(), link.getEndpointAKey(), link.getEndpointBKey(), attributes, DiscoveryType.DISCOVERED);
		}
	}

	public void setDirectInjectDao(DirectInjectDao directInjectDao) {
		this.directInjectDao = directInjectDao;
	}

	@Override
	public String toString() {
		return "DirectInjectDiscoverer{" +
				"hgsDatasourceName='" + getCollectorName() + '\'' +
				", executeOrder='" + getExecuteOrder() + '\'' +
				'}';
	}

}
