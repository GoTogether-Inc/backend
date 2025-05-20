package com.gotogether.domain.term.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermRequestDTO {

	@AssertTrue(message = "서비스 이용 약관에 동의해야 합니다.")
	private boolean serviceAgreed;

	@AssertTrue(message = "개인정보처리 방침에 동의해야 합니다.")
	private boolean privacyPolicyAgree;

	@AssertTrue(message = "개인정보 수집·이용에 동의해야 합니다.")
	private boolean personalInfoUsageAgreed;

	private boolean marketingAgreed;
}