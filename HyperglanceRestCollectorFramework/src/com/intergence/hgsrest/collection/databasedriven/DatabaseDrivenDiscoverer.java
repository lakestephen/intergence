package com.intergence.hgsrest.collection.databasedriven;

import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.collection.SkeletalDiscoveryExecutor;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DatabaseDrivenDiscoverer extends SkeletalDiscoveryExecutor implements DiscoveryExecutor {

    public static final String ENDPOINT_CONSTANT = "_Endpoint";
    private final Logger log = Logger.getLogger(this.getClass());

    public static final String NODE_ID_COLUMN_NAME = "nodeId";
    public static final String ENDPOINT_ID_COLUMN_NAME = "endpointId";
    public static final String TYPE_COLUMN_NAME = "type";
    public static final String LINK_COLUMN_NAME = "linkid";
    public static final String ENDAID_COLUMN_NAME = "endaid";
    public static final String ENDBID_COLUMN_NAME = "endbid";

    private JdbcTemplate jdbcTemplate;

	private List<DatabaseDrivenQueryDefinition> nodeQueries = new ArrayList<DatabaseDrivenQueryDefinition>();
    private List<DatabaseDrivenQueryDefinition> endpointQueries = new ArrayList<DatabaseDrivenQueryDefinition>();
    private List<DatabaseDrivenQueryDefinition> linkQueries = new ArrayList<DatabaseDrivenQueryDefinition>();

	@Override
	public void execute(final ModelUpdate modelUpdate) {
		checkNotNull(jdbcTemplate, "Please supply a sourceSystemDataSource");

        processNodeQueries(modelUpdate);
        processEndpointQueries(modelUpdate);
        processLinkQueries(modelUpdate);

        //NodeCollectorConstants.ATTRIBUTE_NAME;
    }

    private void processNodeQueries(final ModelUpdate modelUpdate) {
        for (DatabaseDrivenQueryDefinition query : nodeQueries) {
            log.info("Processing Node query [" + query + "]");
            String sql = query.getSql();
            jdbcTemplate.query(sql,new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    String nodeId = resultSet.getString(NODE_ID_COLUMN_NAME);
                    String type = resultSet.getString(TYPE_COLUMN_NAME);
                    Map<String, String> attributes = extractAttributesFromResultSet(resultSet, NODE_ID_COLUMN_NAME, TYPE_COLUMN_NAME);
                    modelUpdate.addNode(getCollectorName(), nodeId, type, attributes, DiscoveryType.DISCOVERED);
                }
            });
        }
    }

    private void processEndpointQueries(final ModelUpdate modelUpdate) {
        for (DatabaseDrivenQueryDefinition query : endpointQueries) {
            log.info("Processing Endpoints query [" + query + "]");
            String sql = query.getSql();
            jdbcTemplate.query(sql,new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    String endpointId = resultSet.getString(ENDPOINT_ID_COLUMN_NAME) + ENDPOINT_CONSTANT;
                    String type = resultSet.getString(TYPE_COLUMN_NAME);
                    String nodeId = resultSet.getString(NODE_ID_COLUMN_NAME);
                    Map<String, String> attributes = extractAttributesFromResultSet(resultSet, ENDPOINT_ID_COLUMN_NAME, TYPE_COLUMN_NAME, NODE_ID_COLUMN_NAME);
                    modelUpdate.addEndpoint(getCollectorName(), endpointId, type, nodeId, attributes, DiscoveryType.DISCOVERED);
                }
            });
        }
    }

    private void processLinkQueries(final ModelUpdate modelUpdate) {
        for (DatabaseDrivenQueryDefinition query : linkQueries) {
            log.info("Processing Link query [" + query + "]");
            String sql = query.getSql();
            jdbcTemplate.query(sql,new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    String linkId = resultSet.getString(LINK_COLUMN_NAME);
                    String type = resultSet.getString(TYPE_COLUMN_NAME);
                    String endAId = resultSet.getString(ENDAID_COLUMN_NAME) + ENDPOINT_CONSTANT;
                    String endBId = resultSet.getString(ENDBID_COLUMN_NAME) + ENDPOINT_CONSTANT;
                    Map<String, String> attributes = extractAttributesFromResultSet(resultSet, LINK_COLUMN_NAME, TYPE_COLUMN_NAME, ENDAID_COLUMN_NAME, ENDBID_COLUMN_NAME);
                    modelUpdate.addLink(getCollectorName(), linkId, type, endAId, endBId, attributes, DiscoveryType.DISCOVERED);
                }
            });
        }
    }

    private Map<String, String> extractAttributesFromResultSet(ResultSet resultSet, String... excludedFields) throws SQLException {
        Map<String, String> attributes = new HashMap<String, String>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int columnIndex=1;columnIndex<=metaData.getColumnCount();columnIndex++) {
            String columnName = metaData.getColumnLabel(columnIndex);

            if (!isExclude(columnName, excludedFields)) {
                String value = resultSet.getString(columnIndex);
                attributes.put(columnName, (value==null)?"":value);
            }
        }
        return attributes;
    }

    private boolean isExclude(String columnName, String[] excludedFields) {
        for (String excludedField : excludedFields) {
            if (excludedField.equalsIgnoreCase(columnName)) {
                return true;
            }
        }

        return false;
    }

	public void setSourceSystemDataSource(DataSource sourceSystemDataSource) {
		jdbcTemplate = new JdbcTemplate(sourceSystemDataSource);
	}

    public void setNodeQueries(List<DatabaseDrivenQueryDefinition> nodeQueries) {
        this.nodeQueries = nodeQueries;
    }

    public void setEndpointQueries(List<DatabaseDrivenQueryDefinition> endpointQueries) {
        this.endpointQueries = endpointQueries;
    }

    public void setLinkQueries(List<DatabaseDrivenQueryDefinition> linkQueries) {
        this.linkQueries = linkQueries;
    }

    @Override
	public String toString() {
		return "DatabaseDrivenDiscoverer{" +
				"hgsDatasourceName='" + getCollectorName() + '\'' +
                ", executeOrder='" + getExecuteOrder() + '\'' +
				'}';
	}
}
