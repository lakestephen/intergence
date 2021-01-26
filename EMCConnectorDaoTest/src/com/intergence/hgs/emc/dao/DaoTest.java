package com.intergence.hgs.emc.dao;

import com.intergence.hgs.emc.connector.EmcConnectorManagerFactory;
import com.intergence.hgs.emc.data.dao.CelerraEmcDao;
import com.intergence.hgs.emc.data.dao.EmcDao;

/**
 *
 * @author stephen
 */
public class DaoTest {



	public static void main(String[] args) {

		EmcDao emcDao = new CelerraEmcDao(EmcConnectorManagerFactory.getInstance());

		System.out.println(emcDao.getSystemInfo());
		System.out.println("---------------------------");

		System.out.println(emcDao.getGeneralConfig());
		System.out.println("---------------------------");

		System.out.println(emcDao.getControlStation());
		System.out.println("---------------------------");

		System.out.println(emcDao.getAllDisks());
		System.out.println("---------------------------");

		System.out.println(emcDao.getAllRaidGroups());
		System.out.println("---------------------------");

		System.out.println(emcDao.getAllStorageProcessors());
		System.out.println("---------------------------");
	}
}
