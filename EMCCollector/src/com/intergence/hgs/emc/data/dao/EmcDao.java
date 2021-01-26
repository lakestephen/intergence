package com.intergence.hgs.emc.data.dao;

import com.intergence.hgs.emc.data.bean.*;

import java.util.List;

/**
 * TODO comments.
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
