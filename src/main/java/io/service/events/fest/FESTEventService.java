package io.service.events.fest;

import io.service.events.model.EventDto;
import io.service.events.service.CalendarService;
import io.service.events.service.EventSearchService;
import io.service.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FESTEventService implements EventService<FESTEventRequest> {
    private final EventSearchService<FESTEventRequest> searchService;
    private final CalendarService calendarService;

    @Override
    public ResponseEntity<?> process(FESTEventRequest request) {
        List<EventDto> events = searchService.findEvents(request);
        calendarService.scheduleMultiple(events);
        return ResponseEntity.ok().body(events);
    }

    @Override
    public void deleteAll(FESTEventRequest request) {
        List<EventDto> events = searchService.findEvents(request);
        calendarService.deleteMultiple(events);
    }
}
