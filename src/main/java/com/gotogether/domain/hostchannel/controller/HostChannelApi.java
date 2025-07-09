package com.gotogether.domain.hostchannel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.request.InviteMemberRequestDTO;
import com.gotogether.domain.hostchannel.dto.request.OrderStatusRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "HostChannel", description = "호스트 채널 API")
public interface HostChannelApi {

	@Operation(
		summary = "호스트 채널 생성",
		description = "새로운 호스트 채널을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "호스트 채널 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "HOST_CHANNEL4004: 호스트 채널이 이미 존재합니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createEvent(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "호스트 채널 생성 요청 데이터", required = true)
		@RequestBody @Valid HostChannelRequestDTO request
	);

	@Operation(
		summary = "사용자별 호스트 채널 목록 조회",
		description = "현재 로그인한 사용자의 호스트 채널 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = HostChannelListResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@GetMapping
	ApiResponse<List<HostChannelListResponseDTO>> getHostChannelsByUser(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);

	@Operation(
		summary = "호스트 채널 이벤트 목록 조회",
		description = "특정 호스트 채널의 이벤트 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = HostChannelDetailResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@GetMapping("/{hostChannelId}")
	ApiResponse<HostChannelDetailResponseDTO> getDetailHostChannel(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId
	);

	@Operation(
		summary = "호스트 채널 정보 조회",
		description = "호스트 채널의 기본 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = HostChannelInfoResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@GetMapping("/{hostChannelId}/info")
	ApiResponse<HostChannelInfoResponseDTO> getHostChannelInfo(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId
	);

	@Operation(
		summary = "호스트 채널 검색",
		description = "키워드로 호스트 채널을 검색합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 검색 성공",
			content = @Content(schema = @Schema(implementation = HostChannelListResponseDTO.class))
		)
	})
	@GetMapping("/search")
	ApiResponse<List<HostChannelListResponseDTO>> getHostChannelsSearch(
		@Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@Operation(
		summary = "호스트 채널 수정",
		description = "기존 호스트 채널 정보를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@PutMapping("/{hostChannelId}")
	ApiResponse<?> updateHostChannel(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId,
		@Parameter(description = "호스트 채널 수정 요청 데이터", required = true)
		@RequestBody @Valid HostChannelRequestDTO request
	);

	@Operation(
		summary = "호스트 채널 삭제",
		description = "특정 호스트 채널을 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 채널 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "HOST_CHANNEL4002: 멤버가 아직 존재하여, 호스트 채널을 삭제할 수 없습니다. / HOST_CHANNEL4005: 이벤트가 아직 존재하여, 호스트 채널을 삭제할 수 없습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@DeleteMapping("/{hostChannelId}")
	ApiResponse<?> deleteHostChannel(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId
	);

	@Operation(
		summary = "멤버 초대",
		description = "호스트 채널에 새로운 멤버를 초대합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "멤버 초대 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "HOST_CHANNEL4003: 호스트 채널에 이미 존재하는 멤버 입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다. / USER4001: 사용자가 없습니다."
		)
	})
	@PostMapping("/{hostChannelId}/members")
	ApiResponse<?> addMember(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId,
		@Parameter(description = "멤버 초대 요청 데이터", required = true)
		@RequestBody @Valid InviteMemberRequestDTO request
	);

	@Operation(
		summary = "멤버 목록 조회",
		description = "호스트 채널의 멤버 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "멤버 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = HostChannelMemberResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@GetMapping("/{hostChannelId}/members")
	ApiResponse<List<HostChannelMemberResponseDTO>> getMembers(
		@Parameter(description = "호스트 채널 ID", required = true) @PathVariable Long hostChannelId
	);

	@Operation(
		summary = "호스트 대시보드 조회",
		description = "호스트 대시보드 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "호스트 대시보드 조회 성공",
			content = @Content(schema = @Schema(implementation = HostDashboardResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@GetMapping("/dashboard")
	ApiResponse<HostDashboardResponseDTO> getHostDashboard(
		@Parameter(description = "이벤트 ID", required = true) @RequestParam Long eventId
	);

	@Operation(
		summary = "구매/참가자 관리 목록 조회",
		description = "이벤트 참가자 관리 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "참가자 관리 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ParticipantManagementResponseDTO.class))
		)
	})
	@GetMapping("/dashboard/participant-management")
	ApiResponse<List<ParticipantManagementResponseDTO>> getParticipantManagement(
		@Parameter(description = "이벤트 ID", required = true) @RequestParam Long eventId,
		@Parameter(description = "태그 (all/pending/approved)")
		@RequestParam(name = "tag", defaultValue = "all") String tag,
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@Operation(
		summary = "주문 승인",
		description = "참가자의 주문을 승인합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "주문 승인 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "ORDER4003: 이미 취소된 주문입니다. / ORDER4004: 이미 승인된 주문입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "ORDER4001: 주문이 없습니다."
		)
	})
	@PatchMapping("/dashboard/participant-management/approve")
	ApiResponse<?> approveOrderStatus(
		@Parameter(description = "주문 상태 변경 요청 데이터", required = true)
		@RequestBody @Valid OrderStatusRequestDTO request
	);

	@Operation(
		summary = "구매/참가자 관리 목록 엑셀 다운로드",
		description = "구매/참가자 관리 목록을 엑셀 형식으로 다운로드합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "구매/참가자 관리 목록 엑셀 다운로드 성공",
			content = @Content(schema = @Schema(implementation = org.springframework.core.io.Resource.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@GetMapping("/dashboard/participant-management/excel")
	org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> exportParticipantManagementExcel(
		@Parameter(description = "이벤트 ID", required = true) 
		@RequestParam Long eventId
	);
} 