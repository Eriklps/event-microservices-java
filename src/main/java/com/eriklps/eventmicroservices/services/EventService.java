package com.eriklps.eventmicroservices.services;

import com.eriklps.eventmicroservices.domain.Event;
import com.eriklps.eventmicroservices.domain.Subscription;

import com.eriklps.eventmicroservices.dtos.EmailRequestDTO;
import com.eriklps.eventmicroservices.dtos.EventRequestDTO;
import com.eriklps.eventmicroservices.exceptions.EventFullException;
import com.eriklps.eventmicroservices.exceptions.EventNotFoundException;
import com.eriklps.eventmicroservices.repositories.EventRepository;
import com.eriklps.eventmicroservices.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailServiceClient emailServiceClient;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfterOrderByDate(LocalDateTime.now());
    }

    public Event createEvent(EventRequestDTO eventRequest) {
        Event newEvent = new Event(eventRequest);
        return eventRepository.save(newEvent);
    }

    private Boolean isEventFull(Event event){
        return event.getRegisteredParticipants() >= event.getMaxParticipants();
    }

    public void registerParticipant(String eventId, String participantEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        if(isEventFull(event)) {
            throw new EventFullException();
        }

        Subscription subscription = new Subscription(event, participantEmail);
        subscriptionRepository.save(subscription);

        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);

        EmailRequestDTO emailRequest = new EmailRequestDTO(participantEmail, "Sign up confirmation", "You have been successfully registered for the event!");

        emailServiceClient.sendEmail(emailRequest);
    }
}