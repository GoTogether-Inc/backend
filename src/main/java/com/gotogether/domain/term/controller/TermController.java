package com.gotogether.domain.term.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.term.service.TermService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermController {

	private final TermService termService;

	@PostMapping
	public ApiResponse<?> createTerm(
		@AuthUser Long userId,
		@RequestBody @Valid TermRequestDTO request) {
		Term term = termService.createTerm(userId, request);
		return ApiResponse.onSuccessCreated(term.getId());
	}
}