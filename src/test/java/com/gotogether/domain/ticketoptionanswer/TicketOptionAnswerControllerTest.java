package com.gotogether.domain.ticketoptionanswer;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerListResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserOrderAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.TicketOptionAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.TicketOptionAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.service.TicketOptionAnswerService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class TicketOptionAnswerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TicketOptionAnswerService ticketOptionAnswerService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private TicketOptionAnswerRequestDTO ticketOptionAnswerRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		ticketOptionAnswerRequestDTO = TicketOptionAnswerRequestDTO.builder()
			.ticketOptionId(1L)
			.ticketOptionChoiceId(2L)
			.answerText(null)
			.ticketOptionChoiceIds(null)
			.build();
	}

	@Test
	@DisplayName("티켓 옵션 응답 등록 - 단일 선택")
	void createTicketOptionAnswer() throws Exception {
		// GIVEN
		willDoNothing().given(ticketOptionAnswerService)
			.createTicketOptionAnswer(any(Long.class), any(TicketOptionAnswerRequestDTO.class));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/ticket-option-answers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticketOptionAnswerRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 응답 등록 완료"))
			.andDo(print());

		verify(ticketOptionAnswerService).createTicketOptionAnswer(any(Long.class), argThat(req ->
			req.getTicketOptionId().equals(ticketOptionAnswerRequestDTO.getTicketOptionId()) &&
				req.getTicketOptionChoiceId().equals(ticketOptionAnswerRequestDTO.getTicketOptionChoiceId())
		));
	}

	@Test
	@DisplayName("티켓 옵션 응답 등록 - 다중 선택")
	void createTicketOptionAnswerMultiple() throws Exception {
		// GIVEN
		TicketOptionAnswerRequestDTO multipleChoiceRequest = TicketOptionAnswerRequestDTO.builder()
			.ticketOptionId(1L)
			.ticketOptionChoiceId(null)
			.answerText(null)
			.ticketOptionChoiceIds(Arrays.asList(1L, 2L, 3L))
			.build();

		willDoNothing().given(ticketOptionAnswerService)
			.createTicketOptionAnswer(any(Long.class), any(TicketOptionAnswerRequestDTO.class));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/ticket-option-answers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(multipleChoiceRequest))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 응답 등록 완료"))
			.andDo(print());

		verify(ticketOptionAnswerService).createTicketOptionAnswer(any(Long.class), argThat(req ->
			req.getTicketOptionId().equals(1L) &&
				req.getTicketOptionChoiceIds().size() == 3 &&
				req.getTicketOptionChoiceIds().contains(1L) &&
				req.getTicketOptionChoiceIds().contains(2L) &&
				req.getTicketOptionChoiceIds().contains(3L)
		));
	}

	@Test
	@DisplayName("티켓 옵션 응답 등록 - 텍스트 입력")
	void createTicketOptionAnswerText() throws Exception {
		// GIVEN
		TicketOptionAnswerRequestDTO textRequest = TicketOptionAnswerRequestDTO.builder()
			.ticketOptionId(3L)
			.ticketOptionChoiceId(null)
			.answerText("견과류 알레르기가 있어서 점심 메뉴 선택 시 고려 부탁드립니다.")
			.ticketOptionChoiceIds(null)
			.build();

		willDoNothing().given(ticketOptionAnswerService)
			.createTicketOptionAnswer(any(Long.class), any(TicketOptionAnswerRequestDTO.class));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/ticket-option-answers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(textRequest))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 응답 등록 완료"))
			.andDo(print());

		verify(ticketOptionAnswerService).createTicketOptionAnswer(any(Long.class), argThat(req ->
			req.getTicketOptionId().equals(3L) &&
				req.getAnswerText().equals("견과류 알레르기가 있어서 점심 메뉴 선택 시 고려 부탁드립니다.")
		));
	}

	@Test
	@DisplayName("티켓별 응답 목록 조회")
	void getAnswersByTicket() throws Exception {
		// GIVEN
		Long ticketId = 1L;

		List<PurchaserAnswerDetailResponseDTO> answerDetails = Arrays.asList(
			PurchaserAnswerDetailResponseDTO.builder()
				.userId(1L)
				.orders(Collections.singletonList(
					PurchaserOrderAnswerResponseDTO.builder()
						.orderId(1L)
						.optionAnswers(Arrays.asList(
							TicketOptionAnswerDetailResponseDTO.builder()
								.optionName("관심 기술 스택")
								.optionType("MULTIPLE")
								.answer("Spring Boot, React")
								.build(),
							TicketOptionAnswerDetailResponseDTO.builder()
								.optionName("컨퍼런스 굿즈 사이즈")
								.optionType("SINGLE")
								.answer("L")
								.build()
						))
						.build()
				))
				.build(),
			PurchaserAnswerDetailResponseDTO.builder()
				.userId(2L)
				.orders(Collections.singletonList(
					PurchaserOrderAnswerResponseDTO.builder()
						.orderId(2L)
						.optionAnswers(Arrays.asList(
							TicketOptionAnswerDetailResponseDTO.builder()
								.optionName("관심 기술 스택")
								.optionType("MULTIPLE")
								.answer("Vue.js")
								.build(),
							TicketOptionAnswerDetailResponseDTO.builder()
								.optionName("특별 요청사항")
								.optionType("TEXT")
								.answer("첫 참석인데 정말 기대됩니다!")
								.build()
						))
						.build()
				))
				.build()
		);

		given(ticketOptionAnswerService.getAnswersByTicket(ticketId)).willReturn(answerDetails);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/ticket-option-answers")
				.param("ticketId", String.valueOf(ticketId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].userId").value(1L))
			.andExpect(jsonPath("$.result[0].orders[0].orderId").value(1L))
			.andExpect(jsonPath("$.result[0].orders[0].optionAnswers[0].optionName").value("관심 기술 스택"))
			.andExpect(jsonPath("$.result[0].orders[0].optionAnswers[0].answer").value("Spring Boot, React"))
			.andExpect(jsonPath("$.result[1].userId").value(2L))
			.andExpect(jsonPath("$.result[1].orders[0].optionAnswers[1].optionType").value("TEXT"))
			.andDo(print());

		verify(ticketOptionAnswerService).getAnswersByTicket(ticketId);
	}

	@Test
	@DisplayName("구매자 응답 목록 조회")
	void getPurchaserAnswers() throws Exception {
		// GIVEN
		Long ticketId = 1L;

		PurchaserAnswerListResponseDTO purchaserAnswers = PurchaserAnswerListResponseDTO.builder()
			.orderCount(3)
			.ticketOptions(Arrays.asList(
				PurchaserAnswerResponseDTO.builder()
					.optionId(1L)
					.optionName("관심 기술 스택")
					.optionType("MULTIPLE")
					.ticketOptionAnswers(Arrays.asList(
						TicketOptionAnswerResponseDTO.builder()
							.id(1L)
							.answer("Spring Boot")
							.build(),
						TicketOptionAnswerResponseDTO.builder()
							.id(2L)
							.answer("React")
							.build(),
						TicketOptionAnswerResponseDTO.builder()
							.id(3L)
							.answer("Vue.js")
							.build()
					))
					.build(),
				PurchaserAnswerResponseDTO.builder()
					.optionId(2L)
					.optionName("점심 메뉴 선택")
					.optionType("SINGLE")
					.ticketOptionAnswers(Arrays.asList(
						TicketOptionAnswerResponseDTO.builder()
							.id(4L)
							.answer("한식")
							.build(),
						TicketOptionAnswerResponseDTO.builder()
							.id(5L)
							.answer("중식")
							.build()
					))
					.build(),
				PurchaserAnswerResponseDTO.builder()
					.optionId(3L)
					.optionName("특별 요청사항")
					.optionType("TEXT")
					.ticketOptionAnswers(Collections.singletonList(
						TicketOptionAnswerResponseDTO.builder()
							.id(6L)
							.answer("채식주의자라서 식단 조절 부탁드립니다.")
							.build()
					))
					.build()
			))
			.build();

		given(ticketOptionAnswerService.getPurchaserAnswers(ticketId)).willReturn(purchaserAnswers);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/ticket-option-answers/purchaser-answer")
				.param("ticketId", String.valueOf(ticketId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.orderCount").value(3))
			.andExpect(jsonPath("$.result.ticketOptions").isArray())
			.andExpect(jsonPath("$.result.ticketOptions[0].optionId").value(1L))
			.andExpect(jsonPath("$.result.ticketOptions[0].optionName").value("관심 기술 스택"))
			.andExpect(jsonPath("$.result.ticketOptions[0].optionType").value("MULTIPLE"))
			.andExpect(jsonPath("$.result.ticketOptions[0].ticketOptionAnswers").isArray())
			.andExpect(jsonPath("$.result.ticketOptions[0].ticketOptionAnswers[0].answer").value("Spring Boot"))
			.andExpect(jsonPath("$.result.ticketOptions[1].optionName").value("점심 메뉴 선택"))
			.andExpect(jsonPath("$.result.ticketOptions[2].optionType").value("TEXT"))
			.andDo(print());

		verify(ticketOptionAnswerService).getPurchaserAnswers(ticketId);
	}
} 