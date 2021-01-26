package com.intergence.hgs.emc.dao;

import com.intergence.hgsrest.emc.data.bean.ControlStationInfo;
import com.intergence.hgsrest.emc.data.bean.DiskInfo;
import com.intergence.hgsrest.emc.data.bean.GeneralConfigInfo;
import com.intergence.hgsrest.emc.data.bean.RaidGroupInfo;
import com.intergence.hgsrest.emc.data.bean.StorageProcessorInfo;
import com.intergence.hgsrest.emc.data.bean.SystemInfo;
import com.intergence.hgsrest.emc.data.dao.CelerraEmcDao;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

/**
 *
 * @author stephen
 */
public class CelerraEmcDaoTest {

	private final Logger log = Logger.getLogger(this.getClass());

	CelerraEmcDao celerraEmcDao = new CelerraEmcDao();
	//TODO need to inject a connection manager

	@Test
	public void getSystemInfoTest() {
		List<SystemInfo> result = celerraEmcDao.getSystemInfo();
		System.out.print(result);
	}

	@Test
	public void getGeneralConfigTest() {
		List<GeneralConfigInfo> result = celerraEmcDao.getGeneralConfig();
		System.out.print(result);
	}

	@Test
	public void getControlStationTest() {
		List<ControlStationInfo> result = celerraEmcDao.getControlStation();
		System.out.print(result);
	}

	@Test
	public void getAllDiskInfoTest() {
		List<DiskInfo> result = celerraEmcDao.getAllDisks();
		System.out.print(result);
	}

	@Test
	public void getAllRaidGroupsInfoTest() {
		List<RaidGroupInfo> result = celerraEmcDao.getAllRaidGroups();
		System.out.print(result);
	}

	@Test
	public void getAllStorageProcessorsTest() {
		List<StorageProcessorInfo> result = celerraEmcDao.getAllStorageProcessors();
		System.out.print(result);
	}

}
