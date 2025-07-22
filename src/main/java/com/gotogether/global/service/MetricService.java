package com.gotogether.global.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricService {

    private final MeterRegistry meterRegistry;

    /**
     * 티켓 구매 완료 메트릭
     */
    public void recordTicketPurchase(Long eventId, Long ticketId, int quantity, double totalAmount) {
        try {
            Counter.builder("business.ticket.purchase")
                    .tag("event_id", String.valueOf(eventId))
                    .tag("ticket_id", String.valueOf(ticketId))
                    .register(meterRegistry)
                    .increment(quantity);

            Counter.builder("business.revenue.total")
                    .tag("event_id", String.valueOf(eventId))
                    .register(meterRegistry)
                    .increment((long) totalAmount);

            log.info("티켓 구매 메트릭 기록: eventId={}, ticketId={}, quantity={}, amount={}", 
                    eventId, ticketId, quantity, totalAmount);
        } catch (Exception e) {
            log.warn("티켓 구매 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 티켓 사용 (QR 코드 검증) 메트릭
     */
    public void recordTicketUsage(Long orderId, Long eventId) {
        try {
            Counter.builder("business.ticket.usage")
                    .tag("event_id", String.valueOf(eventId))
                    .register(meterRegistry)
                    .increment();

            log.info("티켓 사용 메트릭 기록: orderId={}, eventId={}", orderId, eventId);
        } catch (Exception e) {
            log.warn("티켓 사용 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 이벤트 생성 메트릭
     */
    public void recordEventCreation(Long eventId, String category) {
        try {
            Counter.builder("business.event.created")
                    .tag("category", category)
                    .register(meterRegistry)
                    .increment();

            log.info("이벤트 생성 메트릭 기록: eventId={}, category={}", eventId, category);
        } catch (Exception e) {
            log.warn("이벤트 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자 등록 메트릭
     */
    public void recordUserRegistration(String provider) {
        try {
            Counter.builder("business.user.registration")
                    .tag("provider", provider)
                    .register(meterRegistry)
                    .increment();

            log.info("사용자 등록 메트릭 기록: provider={}", provider);
        } catch (Exception e) {
            log.warn("사용자 등록 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 주문 취소 메트릭
     */
    public void recordOrderCancellation(Long eventId, double refundAmount) {
        try {
            Counter.builder("business.order.cancelled")
                    .tag("event_id", String.valueOf(eventId))
                    .register(meterRegistry)
                    .increment();

            Counter.builder("business.revenue.refunded")
                    .tag("event_id", String.valueOf(eventId))
                    .register(meterRegistry)
                    .increment((long) refundAmount);

            log.info("주문 취소 메트릭 기록: eventId={}, refundAmount={}", eventId, refundAmount);
        } catch (Exception e) {
            log.warn("주문 취소 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 북마크 생성 메트릭
     */
    public void recordBookmarkCreation(Long eventId) {
        try {
            Counter.builder("business.bookmark.created")
                    .tag("event_id", String.valueOf(eventId))
                    .register(meterRegistry)
                    .increment();

            log.info("북마크 생성 메트릭 기록: eventId={}", eventId);
        } catch (Exception e) {
            log.warn("북마크 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 호스트 채널 생성 메트릭
     */
    public void recordHostChannelCreation(Long hostChannelId) {
        try {
            Counter.builder("business.host_channel.created")
                    .tag("host_channel_id", String.valueOf(hostChannelId))
                    .register(meterRegistry)
                    .increment();

            log.info("호스트 채널 생성 메트릭 기록: hostChannelId={}", hostChannelId);
        } catch (Exception e) {
            log.warn("호스트 채널 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 예약 메일 생성 메트릭
     */
    public void recordReservationEmailCreation(Long reservationEmailId) {
        try {
            Counter.builder("business.reservation_email.created")
                    .tag("reservation_email_id", String.valueOf(reservationEmailId))
                    .register(meterRegistry)
                    .increment();

            log.info("예약 메일 생성 메트릭 기록: reservationEmailId={}", reservationEmailId);
        } catch (Exception e) {
            log.warn("예약 메일 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 예약 메일 발송 메트릭
     */
    public void recordReservationEmailDispatch(Long reservationEmailId) {
        try {
            Counter.builder("business.reservation_email.sent")
                    .tag("reservation_email_id", String.valueOf(reservationEmailId))
                    .register(meterRegistry)
                    .increment();

            log.info("예약 메일 발송 메트릭 기록: reservationEmailId={}", reservationEmailId);
        } catch (Exception e) {
            log.warn("예약 메일 발송 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 티켓 옵션 생성 메트릭
     */
    public void recordTicketOptionCreation(Long ticketOptionId) {
        try {
            Counter.builder("business.ticket_option.created")
                    .tag("ticket_option_id", String.valueOf(ticketOptionId))
                    .register(meterRegistry)
                    .increment();

            log.info("티켓 옵션 생성 메트릭 기록: ticketOptionId={}", ticketOptionId);
        } catch (Exception e) {
            log.warn("티켓 옵션 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 티켓 옵션 응답 생성 메트릭
     */
    public void recordTicketOptionAnswerCreation(Long ticketOptionAnswerId) {
        try {
            Counter.builder("business.ticket_option_answer.created")
                    .tag("ticket_option_answer_id", String.valueOf(ticketOptionAnswerId))
                    .register(meterRegistry)
                    .increment();

            log.info("티켓 옵션 응답 생성 메트릭 기록: ticketOptionAnswerId={}", ticketOptionAnswerId);
        } catch (Exception e) {
            log.warn("티켓 옵션 응답 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * 티켓 옵션 할당 메트릭
     */
    public void recordTicketOptionAssignment(Long ticketOptionId, Long ticketId) {
        try {
            Counter.builder("business.ticket_option.assigned")
                    .tag("ticket_option_id", String.valueOf(ticketOptionId))
                    .tag("ticket_id", String.valueOf(ticketId))
                    .register(meterRegistry)
                    .increment();

            log.info("티켓 옵션 할당 메트릭 기록: ticketOptionId={}, ticketId={}", ticketOptionId, ticketId);
        } catch (Exception e) {
            log.warn("티켓 옵션 할당 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * SMS 발송 결과 메트릭
     */
    public void recordSmsDispatch(boolean isSuccess) {
        try {
            Counter.builder("business.sms.sent")
                .tag("result", String.valueOf(isSuccess))
                .register(meterRegistry)
                .increment();

            log.info("SMS 발송 메트릭 기록: result={}", isSuccess);
        } catch (Exception e) {
            log.warn("SMS 발송 메트릭 기록 실패: {}", e.getMessage());
        }
    }

    /**
     * S3 Pre-signed URL 생성 메트릭
     */
    public void recordPresignedUrlGeneration(boolean isSuccess) {
        try {
            Counter.builder("business.pre_signed_url.generated")
                    .tag("result", String.valueOf(isSuccess))
                    .register(meterRegistry)
                    .increment();

            log.info("S3 Pre-signed URL 생성 메트릭 기록: result={}", isSuccess);
        } catch (Exception e) {
            log.warn("S3 Pre-signed URL 생성 메트릭 기록 실패: {}", e.getMessage());
        }
    }
}