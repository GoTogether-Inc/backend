package com.gotogether.domain.reservationemail.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationEmailDetailResponseDTO {
    private Long id;
    private List<String> recipients;
    private String title;
    private String content;
    private String reservationDate;
    private String reservationTime;
}
