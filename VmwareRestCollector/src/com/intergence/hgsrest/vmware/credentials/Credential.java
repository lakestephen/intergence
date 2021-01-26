/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.credentials;

public class Credential {

	private int key;
	private String username;
	private String password;
	private String hostNameOrIp;
	
	public Credential(int key, String username, String password, String hostNameOrIp) {
		this.key = key;
		this.username = username;
		this.password = password;
		this.hostNameOrIp = hostNameOrIp;
	}

	public int getKey() {
		return key;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHostNameOrIp() {
		return hostNameOrIp;
	}

    @Override
    public String toString() {
        return "Credential{" +
                "key=" + key +
                ", username='" + username + '\'' +
                ", password='*******'" +
                ", hostNameOrIp='" + hostNameOrIp + '\'' +
                '}';
    }
}
