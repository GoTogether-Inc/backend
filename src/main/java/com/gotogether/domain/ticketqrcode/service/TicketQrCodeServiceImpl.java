package com.gotogether.domain.ticketqrcode.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.entity.TicketStatus;
import com.gotogether.domain.ticketqrcode.repository.TicketQrCodeRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketQrCodeServiceImpl implements TicketQrCodeService {

	private final TicketQrCodeRepository ticketQrCodeRepository;
	private final EventFacade eventFacade;

	@Value("${qr.secret-key}")
	private String qrSecretKey;

	@Override
	@Transactional
	public TicketQrCode createQrCode(Order order) {
		String qrCodeImageUrl = generateSignedQrCodeImage(order);

		TicketQrCode ticketQrCode = TicketQrCode.builder()
			.qrCodeImageUrl(qrCodeImageUrl)
			.status(TicketStatus.AVAILABLE)
			.build();

		ticketQrCodeRepository.save(ticketQrCode);

		return ticketQrCode;
	}

	@Override
	@Transactional
	public void deleteQrCode(Long orderId) {
		ticketQrCodeRepository.deleteByOrderId(orderId);
	}

	@Override
	@Transactional
	public void validateSignedQrCode(Long orderId, String sig) {
		validateSignature(orderId, sig);

		Order order = eventFacade.getOrderById(orderId);

		TicketQrCode qrCode = getAvailableQrCode(order);
		qrCode.updateStatus(TicketStatus.USED);
	}

	private String generateSignedQrCodeImage(Order order) {
		try {
			int width = 200;
			int height = 200;

			String orderData = "orderId-" + order.getId();
			String signature = hmacSHA256(orderData, qrSecretKey);
			String qrCodePayload = orderData + "&sig=" + signature;

			BitMatrix encode = new MultiFormatWriter()
				.encode(qrCodePayload, BarcodeFormat.QR_CODE, width, height);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			MatrixToImageWriter.writeToStream(encode, "PNG", out);

			return Base64.getEncoder().encodeToString(out.toByteArray());

		} catch (Exception e) {
			throw new GeneralException(ErrorStatus._QR_CODE_GENERATION_FAILED);
		}
	}

	private String hmacSHA256(String data, String secret) {
		try {
			Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			hmacSHA256.init(secretKey);

			byte[] hash = hmacSHA256.doFinal(data.getBytes());

			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("HMAC SHA256 signing failed", e);
		}
	}

	private void validateSignature(Long orderId, String sig) {
		String orderData = "orderId-" + orderId;
		String newSignature = hmacSHA256(orderData, qrSecretKey);

		if (!newSignature.equals(sig)) {
			throw new GeneralException(ErrorStatus._QR_CODE_INVALID_SIGNATURE);
		}
	}

	private TicketQrCode getAvailableQrCode(Order order) {
		TicketQrCode qrCode = order.getTicketQrCode();

		if (qrCode.getStatus() != TicketStatus.AVAILABLE) {
			throw new GeneralException(ErrorStatus._QR_CODE_ALREADY_USED);
		}

		return qrCode;
	}
}