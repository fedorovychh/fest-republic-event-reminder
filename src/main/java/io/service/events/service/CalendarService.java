package io.service.events.service;

import io.service.events.model.EventDto;

import java.util.List;

public interface CalendarService {
    void schedule(EventDto eventDto);

    void scheduleMultiple(List<EventDto> eventDto);

    void deleteMultiple(List<EventDto> eventDto);

    void list();
}
