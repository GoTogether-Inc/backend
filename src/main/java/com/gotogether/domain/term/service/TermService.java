package com.gotogether.domain.term.service;

import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;

public interface TermService {
	Term createTerm(Long userId, TermRequestDTO request);
}