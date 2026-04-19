package com.event.booking.service;

import com.event.booking.entity.Booking;
import com.event.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final BookingRepository bookingRepository;

    @Async
    public void sendBookingConfirmation(Long customerId, Long eventId) {
        log.info("Booking confirmation email sent to customer {} for event {}", customerId, eventId);
    }

    @Async
    public void notifyCustomersAboutEventUpdate(Long eventId) {
        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        for (Booking booking : bookings) {
            log.info("Notifying customer {} about event update", booking.getCustomer().getName()
            );
        }
    }
}
