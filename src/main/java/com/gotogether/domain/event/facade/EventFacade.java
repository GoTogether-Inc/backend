package com.gotogether.domain.event.facade;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFacade {

	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final HostChannelRepository hostChannelRepository;
	private final UserRepository userRepository;
	private final TicketRepository ticketRepository;

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	public Event getEventById(Long eventId) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));
	}

	public HostChannel getHostChannelById(Long hostChannelId) {
		return hostChannelRepository.findById(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
    
	public Ticket getTicketById(Long ticketId) {
		return ticketRepository.findById(ticketId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));
	}
}