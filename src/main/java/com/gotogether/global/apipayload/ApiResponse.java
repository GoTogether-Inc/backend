package com.gotogether.global.apipayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gotogether.global.apipayload.code.status.SuccessStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

	@JsonProperty("isSuccess")
	private final Boolean isSuccess;
	private final String code;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	public static <T> ApiResponse<T> onSuccess(T result) {
		return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
	}

	public static <T> ApiResponse<T> onSuccessCreated(T result) {
		return new ApiResponse<>(true, SuccessStatus._CREATED.getCode(), SuccessStatus._CREATED.getMessage(), result);
	}

	public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
		return new ApiResponse<>(false, code, message, data);
	}
}
