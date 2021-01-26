package com.intergence.hgsrest.restcomms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intergence.hgsrest.runner.Topology;
import com.intergence.hgsrest.security.SecurityBypass;
import org.apache.log4j.Logger;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class HyperglanceCallManagerImpl implements HyperglanceCallManager {

	private Logger log = Logger.getLogger(this.getClass());

	private JsonComms jsonComms;
    private VerisonPathGetter verisonPathGetter;
    private AuthorisationKeyEncoder authorisationKeyEncoder;

	private String rootHost;
    private String rootEndpoint;
	private String hyperglanceDatasourceName;
	private String hyperglanceApiKey;
    private boolean bypassSslSecurity = false;

	private String calculatedAuthorisationKey;
    private String fullPath;
    private static final String COMPATIBLE_API_VERIOSN = "1.0";

	public static final String TOPOLOGY_REST_ADDRESS = "/topology";
    public static final String NETWORK_REST_ADDRESS = "/network";

    public void init() throws IOException {
        try {
            if (bypassSslSecurity) {
                new SecurityBypass().bypassSecurity();
            }
            calculatedAuthorisationKey = authorisationKeyEncoder.calculateAuthorisationKey(hyperglanceDatasourceName, hyperglanceApiKey);
            fullPath = verisonPathGetter.getAbsolutePathToRequiredVersion(rootHost, rootEndpoint, calculatedAuthorisationKey, COMPATIBLE_API_VERIOSN);
        } catch (IOException e) {
            log.warn("Error initialising system", e);
            throw new RuntimeException("Error initialising HGAPI system", e);
        }
    }

    @Override
    public void getTopology() throws IOException {
        get(TOPOLOGY_REST_ADDRESS);
    }

    @Override
	public void replaceTopology(Topology topology) throws IOException {
		checkNotNull(topology);

        log.info("Replacing Topology with [" + topology + "]");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(topology);

		put(TOPOLOGY_REST_ADDRESS, json);
	}

    private void get(String restServiceAddress) throws IOException {
        checkNotNull(calculatedAuthorisationKey, "Please initialise correctly.");
        checkNotNull(fullPath, "Please initialise correctly.");
        checkState(restServiceAddress.startsWith("/"));

        String response = jsonComms.get(fullPath + restServiceAddress, calculatedAuthorisationKey);

        log.info("Response: " + response);
    }

    /**
     * Only protected rather than private for testing.
     */
	protected void put(String restServiceAddress, String json) throws IOException {
		checkNotNull(calculatedAuthorisationKey, "Please initialise correctly.");
        checkNotNull(fullPath, "Please initialise correctly.");
        checkState(restServiceAddress.startsWith("/"));

        jsonComms.put(fullPath + restServiceAddress, calculatedAuthorisationKey, json);
    }

    public void setRootHost(String rootHost) {
        this.rootHost = rootHost;
    }

    public void setRootEndpoint(String rootEndpoint) {
		this.rootEndpoint = rootEndpoint;
	}

	public void setHyperglanceApiKey(String hyperglanceApiKey) {
		this.hyperglanceApiKey = hyperglanceApiKey;
	}

	public void setHyperglanceDatasourceName(String hyperglanceDatasourceName) {
		this.hyperglanceDatasourceName = hyperglanceDatasourceName;
	}

    public void setJsonComms(DefaultJsonComms jsonComms) {
        this.jsonComms = jsonComms;
    }

    public void setVerisonPathGetter(VerisonPathGetter verisonPathGetter) {
        this.verisonPathGetter = verisonPathGetter;
    }

    public void setAuthorisationKeyEncoder(AuthorisationKeyEncoder authorisationKeyEncoder) {
        this.authorisationKeyEncoder = authorisationKeyEncoder;
    }

    public void setBypassSslSecurity(boolean bypassSslSecurity) {
        this.bypassSslSecurity = bypassSslSecurity;
    }
}
