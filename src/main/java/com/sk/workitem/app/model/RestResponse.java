package com.sk.workitem.app.model;

import java.util.Map;

import lombok.Data;

@Data
public class RestResponse {
	private String responseMsg;
	private String responseCode;
	private Map<String, Object> objData;
}
