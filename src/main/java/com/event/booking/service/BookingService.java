package com.event.booking.service;

import com.event.booking.dto.BookingRequest;
import com.event.booking.dto.BookingResponse;
import com.event.booking.entity.Booking;
import com.event.booking.entity.BookingStatus;
import com.event.booking.entity.Event;
import com.event.booking.entity.Role;
import com.event.booking.entity.User;
import com.event.booking.exception.BadRequestException;
import com.event.booking.exception.InsufficientTicketsException;
import com.event.booking.exception.ResourceNotFoundException;
import com.event.booking.repository.BookingRepository;
import com.event.booking.repository.EventRepository;
import com.event.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public BookingResponse bookTickets(BookingRequest request) {
        // 1. Validate event exists
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));

        // 2. Validate customer exists and role = CUSTOMER
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new BadRequestException("User with id " + request.getCustomerId() + " is not a customer");
        }

        // 3. Check availableTickets >= numberOfTickets
        if (event.getAvailableTickets() < request.getNumberOfTickets()) {
            throw new InsufficientTicketsException(
                    "Not enough tickets available. Requested: " + request.getNumberOfTickets() +
                            ", Available: " + event.getAvailableTickets());
        }

        // 4. Deduct availableTickets
        event.setAvailableTickets(event.getAvailableTickets() - request.getNumberOfTickets());
        eventRepository.save(event);

        // 5. Save booking with status = CONFIRMED
        Booking booking = Booking.builder()
                .event(event)
                .customer(customer)
                .numberOfTickets(request.getNumberOfTickets())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 6. Trigger async booking confirmation
        notificationService.sendBookingConfirmation(customer.getId(), event.getId());

        return mapToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getName())
                .numberOfTickets(booking.getNumberOfTickets())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .build();
    }
}
