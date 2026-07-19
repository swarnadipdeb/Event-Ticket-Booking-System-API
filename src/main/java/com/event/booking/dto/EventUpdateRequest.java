package com.event.booking.dto;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateRequest {

    private String title;

    private String description;

    private String location;

    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;
}
