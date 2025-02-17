package com.gotogether.domain.ticketqrcode.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketType;
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

	@Override
	@Transactional
	public TicketQrCode createQrCode(Event event, Ticket ticket, TicketType ticketType) {

		TicketQrCode ticketQrCode = null;;

		if (ticket.getType() == TicketType.FIRST_COME) {

			String qrCodeImageUrl = generateQrCodeImageUrl(event, ticket);

			ticketQrCode = TicketQrCode.builder()
				.qrCodeImageUrl(qrCodeImageUrl)
				.status(TicketStatus.AVAILABLE)
				.build();
		}else{

			ticketQrCode = TicketQrCode.builder()
				.qrCodeImageUrl(null)
				.status(TicketStatus.PENDING)
				.build();
		}


		ticketQrCodeRepository.save(ticketQrCode);

		return ticketQrCode;
	}

	@Override
	@Transactional
	public void deleteQrCode(Long orderId) {
		ticketQrCodeRepository.deleteByOrderId(orderId);
	}

	private String generateQrCodeImageUrl(Event Event, Ticket ticket) {

		try {

			int width = 200;
			int height = 200;

			Long eventId = Event.getId();
			Long ticketId = ticket.getId();
			String qrCodeId = "ticketId:" + ticketId + "-" + "eventId: " + eventId;

			BitMatrix encode = new MultiFormatWriter()
				.encode(qrCodeId, BarcodeFormat.QR_CODE, width, height);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			MatrixToImageWriter.writeToStream(encode, "PNG", out);

			return Base64.getEncoder().encodeToString(out.toByteArray());

		} catch (Exception e) {
			throw new GeneralException(ErrorStatus._QR_CODE_GENERATION_FAILED);
		}
	}
}