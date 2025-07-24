package com.gotogether.domain.reservationemail.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.reservationemail.converter.ReservationEmailConverter;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.entity.ReservationEmailStatus;
import com.gotogether.domain.reservationemail.entity.ReservationEmailTargetType;
import com.gotogether.domain.reservationemail.facade.ReservationEmailFacade;
import com.gotogether.domain.reservationemail.repository.ReservationEmailRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.scheduler.EventScheduler;
import com.gotogether.global.service.MetricService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationEmailServiceImpl implements ReservationEmailService {

	private final ReservationEmailRepository reservationEmailRepository;

	private final EmailService mailService;
	private final MetricService metricService;

	private final EventFacade eventFacade;
	private final ReservationEmailFacade reservationEmailFacade;

	private final EventScheduler eventScheduler;

	@Override
	@Transactional
	public ReservationEmail createReservationEmail(ReservationEmailRequestDTO request) {
		Event event = eventFacade.getEventById(request.getEventId());

		Ticket ticket = null;
		if (request.getTargetType() == ReservationEmailTargetType.TICKET) {
			ticket = findTicketFromEvent(event, request.getTicketId());
		}

		ReservationEmail reservationEmail = ReservationEmailConverter.of(request, event, ticket);
		reservationEmailRepository.save(reservationEmail);

		eventScheduler.scheduleEmail(reservationEmail.getId(), reservationEmail.getReservationDate());

		metricService.recordReservationEmailCreation(reservationEmail.getId());

		return reservationEmail;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReservationEmailDetailResponseDTO> getReservationEmails(Long eventId, String status) {
		List<ReservationEmail> reservationEmails;

		if (status != null) {
			ReservationEmailStatus reservationEmailStatus = ReservationEmailStatus.valueOf(status.toUpperCase());
			reservationEmails = reservationEmailRepository.findByEventIdAndStatus(eventId, reservationEmailStatus);
		} else {
			reservationEmails = reservationEmailRepository.findByEventId(eventId);
		}

		return reservationEmails.stream()
			.map(ReservationEmailConverter::toReservationEmailDetailResponseDTO)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ReservationEmail updateReservationEmail(Long reservationEmailId, ReservationEmailRequestDTO request) {
		ReservationEmail reservationEmail = reservationEmailFacade.getReservationEmailById(reservationEmailId);
		Event event = eventFacade.getEventById(request.getEventId());

		reservationEmail.update(event, request);
		reservationEmailRepository.save(reservationEmail);

		eventScheduler.deleteScheduledEmailJob(reservationEmail.getId());
		eventScheduler.scheduleEmail(reservationEmail.getId(), reservationEmail.getReservationDate());

		return reservationEmail;
	}

	@Override
	@Transactional
	public void deleteReservationEmail(Long reservationEmailId) {
		ReservationEmail reservationEmail = reservationEmailFacade.getReservationEmailById(reservationEmailId);
		eventScheduler.deleteScheduledEmailJob(reservationEmailId);
		reservationEmailRepository.delete(reservationEmail);
	}

	@Override
	@Transactional
	public void sendReservationEmail(Long reservationEmailId) {
		ReservationEmail reservationEmail = reservationEmailRepository.findById(reservationEmailId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._RESERVATION_EMAIL_NOT_FOUND));

		mailService.sendEmail(
			reservationEmail.getRecipients().toArray(new String[0]),
			reservationEmail.getTitle(),
			reservationEmail.getContent()
		);

		reservationEmail.markAsSent();

		metricService.recordReservationEmailDispatch(reservationEmail.getId());

		reservationEmailRepository.save(reservationEmail);
	}

	private Ticket findTicketFromEvent(Event event, Long ticketId) {
		return event.getTickets().stream()
			.filter(ticket -> ticket.getId().equals(ticketId))
			.findFirst()
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));
	}
}