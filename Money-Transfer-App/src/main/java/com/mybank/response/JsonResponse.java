package com.mybank.response;

public class JsonResponse {
	public static final String STATUS_SUCCESS = "Success";
	public static final String STATUS_FAILURE = "Failure";
	private String status = STATUS_SUCCESS;
	private int responseCode = 200;
	private String message = "";
	private Object data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static String getSuccessStatus() {
		return STATUS_SUCCESS;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public static String getStatusFailure() {
		return STATUS_FAILURE;
	}
	

}
