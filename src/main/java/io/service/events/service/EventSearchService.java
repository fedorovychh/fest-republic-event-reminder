package io.service.events.service;

import io.service.events.model.EventDto;

import java.util.List;

public interface EventSearchService<T> {
    List<EventDto> findEvents(T object);
}
