package com.intergence.hgs.emc.dao;

import com.intergence.hgs.emc.data.bean.*;
import com.intergence.hgs.emc.data.dao.CelerraEmcDao;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

/**
 *
 * @author stephen
 */
public class CelerraEmcDaoTest {

	private final Logger log = Logger.getLogger(this.getClass());

	CelerraEmcDao celerraEmcDao = new CelerraEmcDao(null);

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
