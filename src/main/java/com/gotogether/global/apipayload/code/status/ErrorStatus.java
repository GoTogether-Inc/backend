package com.gotogether.global.apipayload.code.status;

import org.springframework.http.HttpStatus;

import com.gotogether.global.apipayload.code.BaseErrorCode;
import com.gotogether.global.apipayload.code.ErrorReasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러. 관리자에게 문의하세요."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청"),

	_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 없습니다."),

	_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT4001", "이벤트가 없습니다."),

	_HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "HASHTAG4001", "해시태그가 없습니다."),
	_HASHTAG_EXIST(HttpStatus.BAD_REQUEST, "HASHTAG4002", "해시태그가 이미 존재합니다."),

	_HOST_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "HOST_CHANNEL4001", "호스트 채널이 없습니다."),
	_HOST_CHANNEL_DELETE_FAILED_MEMBERS_EXIST(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4002", "멤버가 아직 존재하여, 호스트 채널을 삭제할 수 없습니다."),
	_HOST_CHANNEL_MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4003", "호스트 채널에 이미 존재하는 멤버 입니다."),
	_HOST_CHANNEL_EXISTS(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4004", "호스트 채널이 이미 존재합니다.");


	private HttpStatus httpStatus;
	private String code;
	private String message;

	@Override
	public ErrorReasonDto getReason() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReasonDto getReasonHttpStatus() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
