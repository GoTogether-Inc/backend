package com.gotogether.domain.event.facade;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFacade {

	private final EventRepository eventRepository;
	private final HostChannelRepository hostChannelRepository;

	public Event getEventById(Long eventId) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));
	}

	public HostChannel getHostChannelById(Long hostChannelId) {
		return hostChannelRepository.findById(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));
	}
}