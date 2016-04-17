package com.example.zabbix02;

//datu modelis adapterim resursdatoru saraksta skata rindai

public class HostStatus {

	private String hostName;

	private int hostSatus;

	public HostStatus(String name, int status) {

		this.hostName = name;

		this.hostSatus = status;
	}

	public String getName() {
		return hostName;
	}

	public int getStatus() {
		return hostSatus;
	}
}
