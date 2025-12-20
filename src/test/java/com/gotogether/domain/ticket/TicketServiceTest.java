package com.gotogether.domain.ticket;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticket.service.TicketServiceImpl;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.scheduler.EventScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventFacade eventFacade;

    @Mock
    private EventScheduler eventScheduler;

    private Event event;
    private TicketRequestDTO request;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .title("Test Ticket")
                .description("This is a test event.")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .bannerImageUrl("https://example.com/banner.jpg")
                .address("Test Location")
                .locationLat(100.0)
                .locationLng(200.0)
                .onlineType(OnlineType.ONLINE)
                .category(Category.DEVELOPMENT_STUDY)
                .organizerEmail("test@example.com")
                .organizerPhoneNumber("010-1234-5678")
                .hostChannel(null)
                .build();

        request = TicketRequestDTO.builder()
                .eventId(1L)
                .ticketType(TicketType.FIRST_COME)
                .ticketName("Test Ticket")
                .ticketDescription("This is a test ticket.")
                .ticketPrice(10000)
                .availableQuantity(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        ticket = Ticket.builder()
                .event(event)
                .name(request.getTicketName())
                .description(request.getTicketDescription())
                .price(request.getTicketPrice())
                .availableQuantity(request.getAvailableQuantity())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .type(request.getTicketType())
                .status(TicketStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("티켓 생성")
    void createTicket() {
        // GIVEN
        when(eventFacade.getEventById(any())).thenReturn(event);
        when(ticketRepository.save(any())).thenReturn(ticket);

        // WHEN
        Ticket result = ticketService.createTicket(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getTicketName());
        verify(eventScheduler).scheduleUpdateTicketStatus(any(), any());
    }

    @Test
    @DisplayName("이벤트별 티켓 목록 조회")
    void getTickets() {
        // GIVEN
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findByEventId(any())).thenReturn(tickets);

        // WHEN
        List<TicketListResponseDTO> result = ticketService.getTickets(1L);

        // THEN
        assertThat(result.get(0).getTicketName()).isEqualTo(ticket.getName());
    }

    @Test
    @DisplayName("티켓 삭제")
    void deleteTicket() {
        // GIVEN
        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        // WHEN
        ticketService.deleteTicket(1L);

        // THEN
        verify(ticketRepository).delete(ticket);
        verify(eventScheduler).deleteScheduledTicketJob(1L);
    }

    @Test
    @DisplayName("티켓 상태 완료로 업데이트")
    void updateTicketStatusToCompleted() {
        // GIVEN
        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        // WHEN
        ticketService.updateTicketStatusToCompleted(1L);

        // THEN
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSE);
    }

    @Test
    @DisplayName("[예외] 존재하지 않는 이벤트에 티켓 생성 시 실패")
    void createTicket_Fail_EventNotFound() {
        // GIVEN
        when(eventFacade.getEventById(any())).thenThrow(new GeneralException(ErrorStatus._EVENT_NOT_FOUND));

        // WHEN & THEN
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus._EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("[예외] 존재하지 않는 티켓 삭제 시 실패")
    void deleteTicket_Fail_TicketNotFound() {
        // GIVEN
        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> ticketService.deleteTicket(1L))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus._TICKET_NOT_FOUND);
    }

    @Test
    @DisplayName("[예외] 존재하지 않는 티켓 상태 업데이트 시 실패")
    void updateTicketStatus_Fail_TicketNotFound() {
        // GIVEN
        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> ticketService.updateTicketStatusToCompleted(1L))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus._TICKET_NOT_FOUND);
    }
}