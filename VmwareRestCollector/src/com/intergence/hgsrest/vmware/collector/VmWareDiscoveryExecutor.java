package com.intergence.hgsrest.vmware.collector;

import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.collection.SkeletalDiscoveryExecutor;
import com.intergence.hgsrest.model.update.ModelUpdate;
import com.intergence.hgsrest.vmware.credentials.Credential;
import com.intergence.hgsrest.vmware.credentials.VmWareCredentialRepository;
import com.intergence.hgsrest.vmware.vmware.AlarmCollector;
import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.intergence.hgsrest.vmware.vmware.VSphereWrapper;
import com.intergence.hgsrest.vmware.vmware.VmWareRepositoryUpdater;
import com.intergence.hgsrest.vmware.vmware.topology.ComputeResourceGroupCollector;
import com.intergence.hgsrest.vmware.vmware.topology.DVSwitchLinkCollector;
import com.intergence.hgsrest.vmware.vmware.topology.DVSwitchNodeCollector;
import com.intergence.hgsrest.vmware.vmware.topology.DatastoreEndPointCollector;
import com.intergence.hgsrest.vmware.vmware.topology.DatastoreNodeCollector;
import com.intergence.hgsrest.vmware.vmware.topology.EntityProperties;
import com.intergence.hgsrest.vmware.vmware.topology.HostEndPointCollector;
import com.intergence.hgsrest.vmware.vmware.topology.HostLinkCollector;
import com.intergence.hgsrest.vmware.vmware.topology.HostNodeCollector;
import com.intergence.hgsrest.vmware.vmware.topology.Inventory;
import com.intergence.hgsrest.vmware.vmware.topology.ModelMapper;
import com.intergence.hgsrest.vmware.vmware.topology.NetworkLinkCollector;
import com.intergence.hgsrest.vmware.vmware.topology.NetworkNodeCollector;
import com.intergence.hgsrest.vmware.vmware.topology.ResourcePoolGroupCollector;
import com.intergence.hgsrest.vmware.vmware.topology.VSwitchMappings;
import com.intergence.hgsrest.vmware.vmware.topology.VSwitchNestedNodeCollector;
import com.intergence.hgsrest.vmware.vmware.topology.VirtualMachineEndPointCollector;
import com.intergence.hgsrest.vmware.vmware.topology.VirtualMachineLinkCollector;
import com.intergence.hgsrest.vmware.vmware.topology.VirtualMachineNodeCollector;
import org.apache.log4j.Logger;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class VmWareDiscoveryExecutor extends SkeletalDiscoveryExecutor implements DiscoveryExecutor {

	private static final Logger logger = Logger.getLogger(VmWareDiscoveryExecutor.class);

	private static final String NETWORKS_TO_NICS_BINDING = "networksToNics";
	
	private VmWareCredentialRepository vmWareCredentialRepository;

    @Override
	public void execute(ModelUpdate update) {
	    checkNotNull(vmWareCredentialRepository, "Please supply a vmWareCredentialRepository");

		Collection<Credential> serverAccounts = vmWareCredentialRepository.findAll();
		VmWareRepositoryUpdater updater = new VmWareRepositoryUpdater(update, getCollectorName());
		processServerAccounts(serverAccounts, updater);
	}

	private void processServerAccounts(Collection<Credential> serverAccounts, VmWareRepositoryUpdater updater) {
		for (Credential loginDetails : serverAccounts) {
            logger.info("Trying Vmware account [" + loginDetails + "]");
			try {
				VSphereWrapper service = VSphereWrapper.tryStartSession(loginDetails);
				if (service != null) {

					try {
						onExecute(service, updater);
					}
					catch (Throwable t) {
						logger.error("Processing over a VSphere instance failed: (" + loginDetails.getHostNameOrIp() + ")", t);
					}
					finally {
						service.getSessionWrapper().disconnect();
					}
				}
			}
			catch (Throwable t) {
				logger.error("Connecting or disconnecting from a VSphere instance failed: (" + loginDetails.getHostNameOrIp() + ")", t);
			}
		}
	}

	public void onExecute(VSphereWrapper service, VmWareRepositoryUpdater updater) {
		
		logger.debug("Detected VmWare version: " + service.getVersion());
		
		EntityProperties properties = new EntityProperties(service.getVersion());
		final String[] HOST_PROPERTIES        = properties.getHostProperties();
		final String[] VM_PROPERTIES          = properties.getVmProperties();
		final String[] DATASTORE_PROPERTIES   = properties.getDatastoreProperties();
		final String[] DVS_PROPERTIES         = properties.getDvsProperties();
		final String[] NET_PROPERTIES         = properties.getNetProperties();
		final String[] RES_POOL_PROPERTIES    = properties.getResourcePoolProperties();
		final String[] COMPUTE_RES_PROPERTIES = properties.getComputeResourceProperties();
		
		
		Inventory hosts = VSphereHelper.getInventoryView(service, "HostSystem", HOST_PROPERTIES);
		Inventory vms = VSphereHelper.getInventoryView(service, "VirtualMachine", VM_PROPERTIES);
		
		Inventory datastores;
		Inventory dvs;
		Inventory networks;
		if (service.getVersion() < 4.0) {
			datastores = new Inventory();
			dvs = new Inventory();
			networks = new Inventory();
		}
		else {
			datastores = VSphereHelper.getInventoryView(service, "Datastore", DATASTORE_PROPERTIES);
			dvs = VSphereHelper.getInventoryView(service, "DistributedVirtualSwitch", DVS_PROPERTIES);
			networks = VSphereHelper.getInventoryView(service, "Network", NET_PROPERTIES);
		}
		
		Inventory resourcePools = VSphereHelper.getInventoryView(service, "ResourcePool", RES_POOL_PROPERTIES);
		Inventory clusters = VSphereHelper.getInventoryView(service, "ComputeResource", COMPUTE_RES_PROPERTIES);
		
		// create a model mapper that handles the translation of vsphere-domain entities into normalised-entities that are persisted to the database
		ModelMapper modelMapper = new ModelMapper(service, updater);
		
		// groups (pools and clusters)
		ResourcePoolGroupCollector.collect(modelMapper, vms, resourcePools, clusters);
		ComputeResourceGroupCollector.collect(modelMapper, hosts, datastores, networks, clusters);
		
		// nodes
		HostNodeCollector.persist(modelMapper, hosts);
		VirtualMachineNodeCollector.persist(modelMapper, vms, hosts);
		DatastoreNodeCollector.persist(modelMapper, datastores);
		DVSwitchNodeCollector.persist(modelMapper, dvs);
		NetworkNodeCollector.persist(modelMapper, networks);
		
		VSwitchMappings vSwitchMaps = new VSwitchMappings(modelMapper, hosts, vms, networks);
		VSwitchNestedNodeCollector.persist(modelMapper, vSwitchMaps, hosts);
		
		// endpoints
		HostEndPointCollector.persist(modelMapper, hosts);
		VirtualMachineEndPointCollector.persist(modelMapper, vms, NETWORKS_TO_NICS_BINDING);
		DatastoreEndPointCollector.persist(modelMapper, datastores);
		
		// links
		HostLinkCollector.persist(modelMapper, hosts, datastores, vms, vSwitchMaps);
		VirtualMachineLinkCollector.persist(modelMapper, vms, networks, vSwitchMaps, NETWORKS_TO_NICS_BINDING);
		DVSwitchLinkCollector.persist(modelMapper, dvs, hosts, networks, vms);
		NetworkLinkCollector.persist(modelMapper, networks, vSwitchMaps);


		AlarmCollector.collect(service, updater);
	}

	public void setVmWareCredentialRepository(VmWareCredentialRepository vmWareCredentialRepository) {
		this.vmWareCredentialRepository = vmWareCredentialRepository;
	}

    @Override
    public String toString() {
        return "VmWareDiscoveryExecutor{" +
                ", datasourceName='" + getCollectorName() + '\'' +
				", executeOrder='" + getExecuteOrder() + '\'' +
                '}';
    }
}
