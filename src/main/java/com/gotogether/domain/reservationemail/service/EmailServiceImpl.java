package com.gotogether.domain.reservationemail.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;

	@Override
	public void sendEmail(String[] recipients, String subject, String content) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(recipients);
			message.setSubject(subject);
			message.setText(content);
			mailSender.send(message);

			log.info("[EMAIL] 예약메일 발송 성공: 수신자={}", String.join(", ", recipients));
		} catch (Exception e) {
			log.error("[EMAIL] 예약메일 발송 실패: 수신자={}, 에러={}", String.join(", ", recipients), e.getMessage(), e);
			throw new GeneralException(ErrorStatus._RESERVATION_SEND_EMAIL_FAILED);
		}
	}
}