package com.gotogether.domain.term.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.term.converter.TermConverter;
import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.term.repository.TermRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

	private final TermRepository termRepository;
	private final EventFacade eventFacade;

	@Override
	@Transactional
	public Term createTerm(Long userId, TermRequestDTO request) {
		User user = eventFacade.getUserById(userId);

		if (termRepository.existsTermByUser(user)) {
			throw new GeneralException(ErrorStatus._TERM_ALREADY_EXISTS);
		}

		Term term = TermConverter.of(request, user);
		termRepository.save(term);

		return term;
	}
}