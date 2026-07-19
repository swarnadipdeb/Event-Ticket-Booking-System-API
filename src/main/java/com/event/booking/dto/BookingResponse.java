package com.event.booking.dto;

import com.event.booking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long customerId;
    private String customerName;
    private int numberOfTickets;
    private LocalDateTime bookingTime;
    private BookingStatus status;
}
