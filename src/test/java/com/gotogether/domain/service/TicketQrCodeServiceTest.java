package com.gotogether.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.entity.TicketStatus;
import com.gotogether.domain.ticketqrcode.repository.TicketQrCodeRepository;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class TicketQrCodeServiceTest {

	@InjectMocks
	private TicketQrCodeServiceImpl ticketQrCodeService;

	@Mock
	private TicketQrCodeRepository ticketQrCodeRepository;

	@Mock
	private EventFacade eventFacade;

	private Order order;
	private TicketQrCode ticketQrCode;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(ticketQrCodeService, "qrSecretKey", "test-secret-key");

		order = Order.builder()
			.build();

		ReflectionTestUtils.setField(order, "id", 1L);

		ticketQrCode = TicketQrCode.builder()
			.status(TicketStatus.AVAILABLE)
			.build();

		ReflectionTestUtils.setField(ticketQrCode, "id", 1L);

		order.updateTicketQrCode(ticketQrCode);
	}

	@Test
	@DisplayName("QR 코드 생성")
	void createQrCode_Success() {
		// GIVEN
		when(ticketQrCodeRepository.save(any(TicketQrCode.class))).thenReturn(ticketQrCode);

		// WHEN
		TicketQrCode result = ticketQrCodeService.createQrCode(order);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TicketStatus.AVAILABLE);
		verify(ticketQrCodeRepository).save(any(TicketQrCode.class));
	}

	@Test
	@DisplayName("QR 코드 삭제")
	void deleteQrCode_Success() {
		// GIVEN
		Long orderId = 1L;

		// WHEN
		ticketQrCodeService.deleteQrCode(orderId);

		// THEN
		verify(ticketQrCodeRepository).deleteByOrderId(orderId);
	}

	@Test
	@DisplayName("유효한 서명으로 QR 코드 검증")
	void validateSignedQrCode_Success() {
		// GIVEN
		Long orderId = 1L;
		String orderData = "orderId-" + orderId;
		String signature = ReflectionTestUtils.invokeMethod(ticketQrCodeService, "hmacSHA256", orderData,
			"test-secret-key");

		when(eventFacade.getOrderById(orderId)).thenReturn(order);

		// WHEN & THEN
		assertThatNoException().isThrownBy(() ->
			ticketQrCodeService.validateSignedQrCode(orderId, signature)
		);
	}
} 