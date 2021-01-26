package com.intergence.hgsrest.emc.data.dao;

import com.intergence.hgsrest.emc.data.bean.ControlStationInfo;
import com.intergence.hgsrest.emc.data.bean.DiskInfo;
import com.intergence.hgsrest.emc.data.bean.GeneralConfigInfo;
import com.intergence.hgsrest.emc.data.bean.RaidGroupInfo;
import com.intergence.hgsrest.emc.data.bean.StorageProcessorInfo;
import com.intergence.hgsrest.emc.data.bean.SystemInfo;

import java.util.List;

/**
 *  EMC Data Access Object. i.e. get the details of the EMC system from the EMC system.
 *
 * @author stephen
 */
public interface EmcDao {

	List<SystemInfo> getSystemInfo();
	List<GeneralConfigInfo> getGeneralConfig();
	List<ControlStationInfo> getControlStation();
	List<DiskInfo> getAllDisks();
	List<RaidGroupInfo> getAllRaidGroups();
	List<StorageProcessorInfo> getAllStorageProcessors();

}
