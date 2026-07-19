package com.event.booking.service;

import com.event.booking.dto.EventCreateRequest;
import com.event.booking.dto.EventResponse;
import com.event.booking.dto.EventUpdateRequest;
import com.event.booking.entity.Event;
import com.event.booking.entity.Role;
import com.event.booking.entity.User;
import com.event.booking.exception.BadRequestException;
import com.event.booking.exception.ResourceNotFoundException;
import com.event.booking.repository.EventRepository;
import com.event.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + request.getOrganizerId()));

        if (organizer.getRole() != Role.ORGANIZER) {
            throw new BadRequestException("User with id " + request.getOrganizerId() + " is not an organizer");
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .totalTickets(request.getTotalTickets())
                .availableTickets(request.getTotalTickets())
                .organizer(organizer)
                .build();

        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        Event updatedEvent = eventRepository.save(event);

        // Trigger async notification to all customers who booked this event
        notificationService.notifyCustomersAboutEventUpdate(eventId);

        return mapToResponse(updatedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return mapToResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByOrganizer(Long organizerId) {
        if (!userRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("Organizer not found with id: " + organizerId);
        }
        return eventRepository.findByOrganizerId(organizerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .totalTickets(event.getTotalTickets())
                .availableTickets(event.getAvailableTickets())
                .organizerId(event.getOrganizer().getId())
                .organizerName(event.getOrganizer().getName())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
