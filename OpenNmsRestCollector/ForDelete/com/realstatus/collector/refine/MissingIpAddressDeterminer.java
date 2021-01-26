package com.realstatus.collector.refine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.collect.Collections2;
import com.realstatus.hgs.collection.AttributeRefinement;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.JdbcDatasource;
import com.realstatus.hgs.collection.RefinementExecutor;
import com.realstatus.hgs.model.Node;
import com.realstatus.hgs.model.lookup.AttributeValuePredicate;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.refine.ModelRefinement;
import com.realstatus.hgs.util.JndiDataSourceHelper;

public class MissingIpAddressDeterminer implements RefinementExecutor {

	private static final Logger logger = Logger.getLogger(MissingIpAddressDeterminer.class);
	
	private JdbcTemplate jdbcTemplate;
	
	private static final String GET_IP_ADDRESS_SQL = 
		"SELECT nodeId, MAX(ipaddr) as ipAddress FROM ipinterface GROUP BY nodeid HAVING COUNT(ipaddr) = 1";
	
	public static final String ATTRIBUTE_IPADDRESS = "IP Address";
	
	@Override
	public void execute(CollectorPluginDescriptor pluginDescriptor, ModelRefinement refinement, ModelLookup dataModel) {		
		initialiseJdbcTemplate(pluginDescriptor);
		
		Collection<Node> withNullIpAddresses = Collections2.filter(
				dataModel.getNodes(pluginDescriptor.getDatasourceName()), 
				new AttributeValuePredicate<Node>(ATTRIBUTE_IPADDRESS, null));
				
		if (withNullIpAddresses.size() == 0) {
			return;
		}
		
		addIpAddressToNodes(refinement, dataModel.getNodes(), withNullIpAddresses, getIpAddressesByNodeId());
	}
	
	private void initialiseJdbcTemplate(CollectorPluginDescriptor pluginDescriptor) {
		String jndiName = pluginDescriptor.getJdbcDatasource().getJndiName();
		DataSource dataSource = new JndiDataSourceHelper().lookup(jndiName);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private void addIpAddressToNodes(ModelRefinement refinement, Collection<Node> nodes, Collection<Node> withNullIpAddresses,
			final Map<String, String> ipAddressesByOnmsNodeId) {
				
		for (Node withoutIpAddress : withNullIpAddresses) {
			String ipAddr = ipAddressesByOnmsNodeId.get(withoutIpAddress.getForeignSourceId());
			if (ipAddr != null) {
				withoutIpAddress.putAttribute(ATTRIBUTE_IPADDRESS, ipAddr);
			} else {
				logger.warn("Unable to find IP Address for Node with ONMS.id : " + withoutIpAddress.getForeignSourceId());
			}
			
			refinement.updateNode(withoutIpAddress);
		}
	}
	
	private Map<String, String> getIpAddressesByNodeId() {
		final Map<String, String> ipAddressesByOnmsNodeId = new HashMap<String, String>();
		
		jdbcTemplate.query(GET_IP_ADDRESS_SQL, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				ipAddressesByOnmsNodeId.put(
						resultSet.getString("nodeId"), 
						resultSet.getString("ipAddress"));
			}
			
		});
		return ipAddressesByOnmsNodeId;
	}
}
