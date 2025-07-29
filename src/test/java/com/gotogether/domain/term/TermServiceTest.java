package com.gotogether.domain.term;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.term.repository.TermRepository;
import com.gotogether.domain.term.service.TermServiceImpl;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class TermServiceTest {

	@InjectMocks
	private TermServiceImpl termService;

	@Mock
	private TermRepository termRepository;

	@Mock
	private EventFacade eventFacade;

	private User user;
	private TermRequestDTO termRequestDTO;
	private Term term;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		termRequestDTO = TermRequestDTO.builder()
			.serviceAgreed(true)
			.privacyPolicyAgree(true)
			.personalInfoUsageAgreed(true)
			.marketingAgreed(false)
			.build();

		term = Term.builder()
			.isServiceAgreed(termRequestDTO.isServiceAgreed())
			.isPrivacyPolicyAgree(termRequestDTO.isPrivacyPolicyAgree())
			.isPersonalInfoUsageAgreed(termRequestDTO.isPersonalInfoUsageAgreed())
			.isMarketingAgreed(termRequestDTO.isMarketingAgreed())
			.agreedAt(LocalDateTime.now())
			.user(user)
			.build();
		ReflectionTestUtils.setField(term, "id", 1L);
	}

	@Test
	@DisplayName("약관 동의 생성")
	void createTerm() {
		// GIVEN
		Long userId = 1L;

		given(eventFacade.getUserById(userId)).willReturn(user);
		given(termRepository.existsTermByUser(user)).willReturn(false);
		given(termRepository.save(any(Term.class))).willReturn(term);

		// WHEN
		Term result = termService.createTerm(userId, termRequestDTO);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.isServiceAgreed()).isTrue();
		assertThat(result.isPrivacyPolicyAgree()).isTrue();
		assertThat(result.isPersonalInfoUsageAgreed()).isTrue();
		assertThat(result.isMarketingAgreed()).isFalse();
		assertThat(result.getUser()).isEqualTo(user);

		verify(eventFacade).getUserById(userId);
		verify(termRepository).existsTermByUser(user);
		verify(termRepository).save(any(Term.class));
	}

	@Test
	@DisplayName("약관 동의 생성 실패")
	void createTerm_AlreadyExists() {
		// GIVEN
		Long userId = 1L;

		given(eventFacade.getUserById(userId)).willReturn(user);
		given(termRepository.existsTermByUser(user)).willReturn(true);

		// WHEN & THEN
		assertThatThrownBy(() -> termService.createTerm(userId, termRequestDTO))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(ErrorStatus._TERM_ALREADY_EXISTS);

		verify(eventFacade).getUserById(userId);
		verify(termRepository).existsTermByUser(user);
		verify(termRepository, never()).save(any(Term.class));
	}
} 