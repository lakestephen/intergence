/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware;

import com.intergence.hgsrest.vmware.credentials.Credential;
import com.vmware.vim25.InvalidLocale;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimServiceLocator;
import org.apache.log4j.Logger;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class VSphereSession {
	private static final String PROTOCOL = "https://";
	private static final String SERVICE_SDK_PATH = "/sdk/vimService";
	
	private static final Logger logger = Logger.getLogger(VSphereSession.class);
	
	private VimPortType vSphereApi;
	private ServiceContent serviceContent;
	private Credential sessionCredentials;
	
	public VSphereSession(Credential accountDetails) {
		// Ignore the certificate signing
		System.setProperty("axis.socketSecureFactory", "org.apache.axis.components.net.SunFakeTrustSocketFactory");
		
		// begin a session
		URL url = getSdkAddress( accountDetails.getHostNameOrIp() );
		startSession(url);

		// get the hub object containing all vsphere managers
		retrieveServiceContent();
		
		// validate the existence of the session manager
		VSphereHelper.ensureNotNull(serviceContent.getSessionManager(), "Session Manager");
		
		// authenticate the session
		authenticatedLogin(accountDetails.getUsername(), accountDetails.getPassword());
		
		// cache the credentials
		sessionCredentials = accountDetails;
	}
	
	public VimPortType getApi() {
		return vSphereApi;
	}
	
	public ServiceContent getContent() {
		return serviceContent;
	}
	
	public Credential getSessionCredentials() {
		return sessionCredentials;
	}
	
	public void disconnect() {
		if (vSphereApi == null || serviceContent == null || sessionCredentials == null) {
			return;
		}
		
		try {
			vSphereApi.logout(serviceContent.getSessionManager());
		} catch (RuntimeFault rf) {
			logger.error("RuntimeFault occurred whilst logging out", rf);
			throw new RuntimeException(rf);
		} catch (RemoteException re) {
			logger.error("RemoteException occurred whilst logging out)", re);
			throw new RuntimeException(re);
		} finally {
			vSphereApi = null;
			serviceContent = null;
			sessionCredentials = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			disconnect();
		} finally {
			super.finalize();
		}
	}
	
	private void startSession(URL url) {
		VimServiceLocator locator = new VimServiceLocator();
		locator.setMaintainSession(true);
		
		// get the core vsphere API interface
		try {
			vSphereApi = locator.getVimPort(url);
			((org.apache.axis.client.Stub)vSphereApi).setTimeout(3600000); // 1 hour
		} catch (ServiceException se) {
			logger.error("ServiceException occurred retrieving the vSphere WebServices API object (VimPortType)", se);
			throw new RuntimeException(se);
		}
	}
	
	private void retrieveServiceContent() {
		try {
			serviceContent = vSphereApi.retrieveServiceContent( retrieveInstanceReference() );
		} catch (RuntimeFault rf) {
			logger.error("RuntimeFault exception occurred retrieving the service content", rf);
			throw new RuntimeException(rf);
		} catch (RemoteException re) {
			logger.error("RemoteException occurred retrieving the service content", re);
			throw new RuntimeException(re);
		}
	}
	
	private void authenticatedLogin(String username, String password) {
		try {
			vSphereApi.login(serviceContent.getSessionManager(), username, password, null);
		} catch (InvalidLogin il) {
			logger.error("InvalidLogin exception occurred during authenticated login", il);
			throw new RuntimeException(il);
		} catch (InvalidLocale il) {
			logger.error("InvalidLocale exception occurred during authenticated login", il);
			throw new RuntimeException(il);
		} catch (RuntimeFault rf) {
			logger.error("RuntimeFault exception occurred during authenticated login", rf);
			throw new RuntimeException(rf);
		} catch (RemoteException re) {
			logger.error("RemoteException occurred during authenticated login", re);
			throw new RuntimeException(re);
		}
	}
	
	private static URL getSdkAddress(String hostNameOrIp) {
		try {
			 return new URL(PROTOCOL + hostNameOrIp + SERVICE_SDK_PATH);
		} catch (MalformedURLException mUrle) {
			logger.error("MalformedURLException occurred constructing the vSphere Webservice SDK Url", mUrle);
			throw new IllegalArgumentException(mUrle);
		}
	}
	
	private static ManagedObjectReference retrieveInstanceReference() {
		ManagedObjectReference serviceInstance = new ManagedObjectReference();
		serviceInstance.setType("ServiceInstance");
		serviceInstance.set_value("ServiceInstance");
		return serviceInstance;
	}
}
