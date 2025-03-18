package com.gotogether.domain.reservationemail.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEmailRequestDTO {
    @JsonProperty("eventId")
    private Long eventId;

    @JsonProperty("recipients")
    private List<String> recipients;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("reservationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate reservationDate;

    @JsonProperty("reservationTime")
    private String reservationTime;
}