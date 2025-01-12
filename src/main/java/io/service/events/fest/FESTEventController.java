package io.service.events.fest;

import io.service.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fest")
@RequiredArgsConstructor
public class FESTEventController {
    private final EventService<FESTEventRequest> eventService;

    @GetMapping("/events")
    public ResponseEntity<?> process(@RequestBody FESTEventRequest request) {
        return eventService.process(request);
    }

    @DeleteMapping("/events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(@RequestBody FESTEventRequest request) {
        eventService.deleteAll(request);
    }
}
