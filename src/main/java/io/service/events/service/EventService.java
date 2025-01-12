package io.service.events.service;

import org.springframework.http.ResponseEntity;

public interface EventService<T> {
    ResponseEntity<?> process(T request);

    void deleteAll(T request);
}
