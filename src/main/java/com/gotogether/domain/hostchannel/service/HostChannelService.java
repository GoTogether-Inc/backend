package com.gotogether.domain.hostchannel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;

public interface HostChannelService {
	HostChannel createHostChannel(Long userId, HostChannelRequestDTO request);

	List<HostChannelListResponseDTO> getHostChannels(Long userId);

	HostChannelDetailResponseDTO getDetailHostChannel(Long hostChannelId);

	HostChannelInfoResponseDTO getHostChannelInfo(Long hostChannelId);

	Page<HostChannelListResponseDTO> searchHostChannels(String keyword, Pageable pageable);

	void deleteHostChannel(Long hostChannelId);

	HostChannel updateHostChannel(Long hostChannelId, HostChannelRequestDTO request);

	void addMember(Long hostChannelId, String email);

	List<HostChannelMemberResponseDTO> getMembers(Long hostChannelId);

	List<ParticipantManagementResponseDTO> getParticipantManagement(Long eventId, String tags,
		Pageable pageable);

	HostDashboardResponseDTO getHostDashboard(Long eventId);

	void approveOrderStatus(Long orderId);
}
