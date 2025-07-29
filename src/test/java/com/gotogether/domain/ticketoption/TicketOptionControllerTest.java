package com.gotogether.domain.ticketoption;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionChoiceResponseDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.service.TicketOptionService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class TicketOptionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TicketOptionService ticketOptionService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private TicketOption ticketOption;
	private TicketOptionRequestDTO ticketOptionRequestDTO;
	private TicketOptionDetailResponseDTO ticketOptionDetailResponseDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		ticketOption = TicketOption.builder()
			.eventId(1L)
			.name("참석 세션 선택")
			.description("참석하고 싶은 세션을 선택해주세요 (복수 선택 가능)")
			.type(TicketOptionType.MULTIPLE)
			.isMandatory(true)
			.build();
		ReflectionTestUtils.setField(ticketOption, "id", 1L);

		ticketOptionRequestDTO = TicketOptionRequestDTO.builder()
			.eventId(1L)
			.name("참석 세션 선택")
			.description("참석하고 싶은 세션을 선택해주세요 (복수 선택 가능)")
			.type(TicketOptionType.MULTIPLE)
			.isMandatory(true)
			.choices(Arrays.asList("키노트: AI의 미래", "백엔드 아키텍처 심화", "프론트엔드 최신 트렌드", "DevOps 실무"))
			.build();

		List<TicketOptionChoiceResponseDTO> choices = Arrays.asList(
			TicketOptionChoiceResponseDTO.builder().id(1L).name("키노트: AI의 미래").build(),
			TicketOptionChoiceResponseDTO.builder().id(2L).name("백엔드 아키텍처 심화").build(),
			TicketOptionChoiceResponseDTO.builder().id(3L).name("프론트엔드 최신 트렌드").build(),
			TicketOptionChoiceResponseDTO.builder().id(4L).name("DevOps 실무").build()
		);

		ticketOptionDetailResponseDTO = TicketOptionDetailResponseDTO.builder()
			.id(1L)
			.name("참석 세션 선택")
			.description("참석하고 싶은 세션을 선택해주세요 (복수 선택 가능)")
			.type(TicketOptionType.MULTIPLE)
			.isMandatory(true)
			.choices(choices)
			.build();
	}

	@Test
	@DisplayName("티켓 옵션 생성")
	void createTicketOption() throws Exception {
		// GIVEN
		given(ticketOptionService.createTicketOption(any(TicketOptionRequestDTO.class))).willReturn(ticketOption);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/ticket-options")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticketOptionRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(ticketOptionService).createTicketOption(argThat(req ->
			req.getEventId().equals(ticketOptionRequestDTO.getEventId()) &&
				req.getName().equals(ticketOptionRequestDTO.getName()) &&
				req.getType().equals(ticketOptionRequestDTO.getType()) &&
				req.getIsMandatory().equals(ticketOptionRequestDTO.getIsMandatory())
		));
	}

	@Test
	@DisplayName("이벤트별 티켓 옵션 목록 조회")
	void getTicketOptionsByEventId() throws Exception {
		// GIVEN
		Long eventId = 1L;

		List<TicketOptionDetailResponseDTO> ticketOptions = Arrays.asList(
			TicketOptionDetailResponseDTO.builder()
				.id(1L)
				.name("참석 세션 선택")
				.description("참석하고 싶은 세션을 선택해주세요 (복수 선택 가능)")
				.type(TicketOptionType.MULTIPLE)
				.isMandatory(true)
				.choices(Arrays.asList(
					TicketOptionChoiceResponseDTO.builder().id(1L).name("키노트: AI의 미래").build(),
					TicketOptionChoiceResponseDTO.builder().id(2L).name("백엔드 아키텍처 심화").build()
				))
				.build(),
			TicketOptionDetailResponseDTO.builder()
				.id(2L)
				.name("컨퍼런스 굿즈 사이즈")
				.description("컨퍼런스 티셔츠 사이즈를 선택해주세요")
				.type(TicketOptionType.SINGLE)
				.isMandatory(false)
				.choices(Arrays.asList(
					TicketOptionChoiceResponseDTO.builder().id(3L).name("S").build(),
					TicketOptionChoiceResponseDTO.builder().id(4L).name("M").build(),
					TicketOptionChoiceResponseDTO.builder().id(5L).name("L").build()
				))
				.build()
		);

		given(ticketOptionService.getTicketOptionsByEventId(eventId)).willReturn(ticketOptions);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/ticket-options/events/{eventId}", eventId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].name").value("참석 세션 선택"))
			.andExpect(jsonPath("$.result[0].type").value("MULTIPLE"))
			.andExpect(jsonPath("$.result[0].isMandatory").value(true))
			.andExpect(jsonPath("$.result[1].id").value(2L))
			.andExpect(jsonPath("$.result[1].name").value("컨퍼런스 굿즈 사이즈"))
			.andExpect(jsonPath("$.result[1].type").value("SINGLE"))
			.andExpect(jsonPath("$.result[1].isMandatory").value(false))
			.andDo(print());

		verify(ticketOptionService).getTicketOptionsByEventId(eventId);
	}

	@Test
	@DisplayName("티켓별 옵션 목록 조회")
	void getTicketOptionsByTicketId() throws Exception {
		// GIVEN
		Long ticketId = 1L;

		List<TicketOptionDetailResponseDTO> ticketOptions = Collections.singletonList(
			ticketOptionDetailResponseDTO
		);

		given(ticketOptionService.getTicketOptionsByTicketId(ticketId)).willReturn(ticketOptions);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/ticket-options/tickets/{ticketId}", ticketId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].name").value("참석 세션 선택"))
			.andExpect(jsonPath("$.result[0].choices").isArray())
			.andExpect(jsonPath("$.result[0].choices[0].name").value("키노트: AI의 미래"))
			.andDo(print());

		verify(ticketOptionService).getTicketOptionsByTicketId(ticketId);
	}

	@Test
	@DisplayName("티켓 옵션 상세 조회")
	void getTicketOption() throws Exception {
		// GIVEN
		Long ticketOptionId = 1L;

		given(ticketOptionService.getTicketOption(ticketOptionId)).willReturn(ticketOptionDetailResponseDTO);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/ticket-options/{ticketOptionId}", ticketOptionId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.id").value(1L))
			.andExpect(jsonPath("$.result.name").value("참석 세션 선택"))
			.andExpect(jsonPath("$.result.description").value("참석하고 싶은 세션을 선택해주세요 (복수 선택 가능)"))
			.andExpect(jsonPath("$.result.type").value("MULTIPLE"))
			.andExpect(jsonPath("$.result.isMandatory").value(true))
			.andExpect(jsonPath("$.result.choices").isArray())
			.andExpect(jsonPath("$.result.choices").isNotEmpty())
			.andDo(print());

		verify(ticketOptionService).getTicketOption(ticketOptionId);
	}

	@Test
	@DisplayName("티켓 옵션 수정")
	void updateTicketOption() throws Exception {
		// GIVEN
		Long ticketOptionId = 1L;

		TicketOption updatedTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("네트워킹 점심 참여")
			.description("컨퍼런스 참가자들과의 네트워킹 점심에 참여하시겠습니까?")
			.type(TicketOptionType.SINGLE)
			.isMandatory(false)
			.build();
		ReflectionTestUtils.setField(updatedTicketOption, "id", ticketOptionId);

		given(ticketOptionService.updateTicketOption(any(Long.class), any(TicketOptionRequestDTO.class)))
			.willReturn(updatedTicketOption);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/ticket-options/{ticketOptionId}", ticketOptionId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticketOptionRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value(ticketOptionId))
			.andDo(print());

		verify(ticketOptionService).updateTicketOption(any(Long.class), argThat(req ->
			req.getEventId().equals(ticketOptionRequestDTO.getEventId()) &&
				req.getName().equals(ticketOptionRequestDTO.getName()) &&
				req.getType().equals(ticketOptionRequestDTO.getType()) &&
				req.getIsMandatory().equals(ticketOptionRequestDTO.getIsMandatory())
		));
	}

	@Test
	@DisplayName("티켓 옵션 삭제")
	void deleteTicketOption() throws Exception {
		// GIVEN
		Long ticketOptionId = 1L;

		willDoNothing().given(ticketOptionService).deleteTicketOption(ticketOptionId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/ticket-options/{ticketOptionId}", ticketOptionId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 삭제 성공"))
			.andDo(print());

		verify(ticketOptionService).deleteTicketOption(ticketOptionId);
	}
} 