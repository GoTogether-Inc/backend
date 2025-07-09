package com.gotogether.global.apipayload.code.status;

import org.springframework.http.HttpStatus;

import com.gotogether.global.apipayload.code.BaseErrorCode;
import com.gotogether.global.apipayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러. 관리자에게 문의하세요."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
	_INVALID_AUTH_USER_ERROR(HttpStatus.BAD_REQUEST, "COMMON4001", "사용자 인증에 실패했습니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4002", "인증 정보가 유효하지 않습니다."),

	_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 없습니다."),
	_USER_PHONE_NUMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "USER4002", "이미 등록된 전화번호입니다."),
	_USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4003", "이미 등록된 이메일입니다."),

	_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT4001", "이벤트가 없습니다."),
	_EVENT_DELETE_FAILED_ORDERS_EXIST(HttpStatus.BAD_REQUEST, "EVENT4002", "구매자가 있는 이벤트는 삭제할 수 없습니다."),

	_HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "HASHTAG4001", "해시태그가 없습니다."),
	_HASHTAG_EXIST(HttpStatus.BAD_REQUEST, "HASHTAG4002", "해시태그가 이미 존재합니다."),

	_HOST_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "HOST_CHANNEL4001", "호스트 채널이 없습니다."),
	_HOST_CHANNEL_DELETE_FAILED_MEMBERS_EXIST(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4002", "멤버가 아직 존재하여, 호스트 채널을 삭제할 수 없습니다."),
	_HOST_CHANNEL_MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4003", "호스트 채널에 이미 존재하는 멤버 입니다."),
	_HOST_CHANNEL_EXISTS(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4004", "호스트 채널이 이미 존재합니다."),
	_HOST_CHANNEL_DELETE_FAILED_EVENTS_EXIST(HttpStatus.BAD_REQUEST, "HOST_CHANNEL4005", "이벤트가 아직 존재하여, 호스트 채널을 삭제할 수 없습니다."),

	_TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET4001", "티켓이 없습니다."),
	_TICKET_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "TICKET4002", "남은 티켓 수량이 부족합니다."),
	_TICKET_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "TICKET4003", "이미 종료된 티켓입니다."),
	_TICKET_SALE_UNAVAILABLE(HttpStatus.BAD_REQUEST, "TICKET4004", "현재 판매 중인 티켓이 아닙니다."),

	_QR_CODE_ALREADY_USED(HttpStatus.BAD_REQUEST, "QR_CODE4001", "이미 사용된 QR 코드입니다."),
	_INVALID_QR_CODE_FORMAT(HttpStatus.BAD_REQUEST, "QR_CODE4002", "잘못된 QR 코드 형식입니다."),
	_QR_CODE_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "QR_CODE4003", "QR 코드 서명이 유효하지 않습니다."),
	_QR_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR_CODE5001", "QR 코드 생성에 실패하였습니다."),

	_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER4001", "주문이 없습니다."),
	_ORDER_NOT_MATCH_USER(HttpStatus.BAD_REQUEST, "ORDER4002", "주문과 사용자 정보가 일치하지 않습니다."),
	_ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "ORDER4003", "이미 취소된 주문입니다."),
	_ORDER_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "ORDER4004", "이미 승인된 주문입니다."),

	_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4001", "토큰이 만료되었습니다."),
	_TOKEN_TYPE_ERROR(HttpStatus.BAD_REQUEST, "TOKEN4002", "토큰 타입이 잘못되었습니다."),
	_TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "TOKEN4003", "로그아웃된 토큰입니다. 다시 로그인해주세요."),
	_TOKEN_NOT_EXISTS(HttpStatus.UNAUTHORIZED, "TOKEN4004", "토큰이 존재하지 않습니다. 로그인이 필요합니다."),

	_BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "BOOKMARK4001", "이미 북마크된 이벤트입니다."),
	_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK4002", "북마크를 찾을 수 없습니다."),

	_RESERVATION_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_EMAIL4001", "예약 메일이 없습니다."),

	_TICKET_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_OPTION4001", "티켓 옵션이 존재하지 않습니다."),
	_TICKET_OPTION_ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_OPTION_ASSIGN4001", "해당 티켓에 부착된 옵션이 없습니다."),
	_TICKET_OPTION_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "TICKET_OPTION_ASSIGN4002", "이미 해당 티켓 옵션이 부착되어 있습니다."),
	_TICKET_OPTION_CHOICE_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_OPTION_CHOICE4001", "선택지를 찾을 수 없습니다."),
	_TICKET_OPTION_ALREADY_ANSWERED(HttpStatus.BAD_REQUEST, "TICKET_OPTION_ANSWER4001", "이미 응답된 티켓 옵션입니다."),
	// 미사용
	_TICKET_OPTION_ANSWER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "TICKET_OPTION_ANSWER4002", "해당 티켓 옵션에 대한 응답이 이미 존재합니다."),

	_TERM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "TERM4001", "이미 약관에 동의한 사용자입니다."),

	_EXCEL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EXCEL5001", "엑셀 파일 생성에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
