package com.example.zabbix02;

//lietotaaja atteiksanaas atbildes struktuura
public class UserLogout {
	
	private String jsonrpc;
	
	private String id;
	
	private String result;
	

	public String getJsonrpc() {
		
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		
		return id;
	}

	public void setId(String id) {
		
		this.id = id;
	}

	public String getResult() {
		
		return result;
	}

	public void setResult(String result) {
		
		this.result = result;
	}

}
