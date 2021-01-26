package com.intergence.hgsrest.restcomms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by stephen on 19/02/2015.
 */
public class VerisonPathGetter {

    private Logger log = Logger.getLogger(this.getClass());

    private JsonComms jsonComms;

    String getAbsolutePathToRequiredVersion(String rootHost, String rootEndpoint, String calculatedAuthorisationKey, String requiredApiVersion) throws IOException {
        checkNotNull(rootHost);
        checkNotNull(rootEndpoint);
        checkNotNull(calculatedAuthorisationKey);
        checkNotNull(requiredApiVersion);

        String versionResponse = jsonComms.get(rootHost + rootEndpoint, calculatedAuthorisationKey);

        RootResponse rootResponse = buildRootResponse(versionResponse);
        
        Version selectedVersion = extractVersion(requiredApiVersion, rootResponse);
        checkState(selectedVersion != null, "Unable to find exactly one version in [" + rootResponse + "]") ;
        log.info("Found HGAPI version [" + requiredApiVersion + "] - [" + selectedVersion + "]");

        if (!"current".equals(selectedVersion.getStatus())) {
            log.warn("The version of HGAPI you are using is [" + selectedVersion.getStatus() + "]");
        }

        String path = buildPath(selectedVersion, rootHost);
        log.info("Root path set to [" + path + "]");

        return path;
    }

    private RootResponse buildRootResponse(String versionResponse) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(versionResponse, RootResponse.class);
    }

    private Version extractVersion(String requiredApiVersion, RootResponse rootResponse) {
        Version selectedVersion = null;
        for (Version version : rootResponse.versions) {
            if (requiredApiVersion.equals(version.getId())){
                if (selectedVersion != null) {
                    throw new RuntimeException("More than one version [" + requiredApiVersion + "] found");
                }
                selectedVersion = version;
            }
        }
        return selectedVersion;
    }

    private String buildPath(Version selectedVersion, String rootEndpoint) {
        String versionPath = selectedVersion.getPath();
        checkNotNull(versionPath);


        if (versionPath.startsWith("http")) {
            return versionPath;
        } else {
            if (versionPath.endsWith("/")) {
                versionPath = versionPath.substring(0, versionPath.length() - 1);
            }
            return rootEndpoint + versionPath;
        }

    }

    public void setJsonComms(JsonComms jsonComms) {
        this.jsonComms = jsonComms;
    }

}
