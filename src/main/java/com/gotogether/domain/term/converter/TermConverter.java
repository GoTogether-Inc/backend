package com.gotogether.domain.term.converter;

import java.time.LocalDateTime;

import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.user.entity.User;

public class TermConverter {

	public static Term of(TermRequestDTO request, User user) {
		return Term.builder()
			.isPersonalInfoUsageAgreed(request.isMarketingAgreed())
			.isPrivacyPolicyAgree(request.isPrivacyPolicyAgree())
			.isServiceAgreed(request.isServiceAgreed())
			.isMarketingAgreed(request.isMarketingAgreed())
			.agreedAt(LocalDateTime.now())
			.user(user)
			.build();
	}
}