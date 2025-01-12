package io.service.events.service.impl;

import io.service.events.model.EventDto;
import io.service.events.config.GoogleAuth;
import io.service.events.exception.GoogleCalendarException;
import io.service.events.exception.NoAttendeeException;
import io.service.events.common.StringUtils;
import io.service.events.service.CalendarService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {
    private static final String CALENDAR_ID = "primary";
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleAuth googleAuth;
    private final Calendar calendar;

    @Autowired
    public CalendarServiceImpl(GoogleAuth googleAuth) {
        this.googleAuth = googleAuth;
        calendar = getCalendar();
    }

    @Override
    public void schedule(EventDto eventDto) {
        Event event = formEventObject(eventDto);
        Events listedEvents = getListed();
        List<Event> items = listedEvents.getItems();
        if (!containsEvent(items, event)) {
            Event inserted = insertAndExecute(event);
            logEvent(inserted, "Created new event");
        }
    }

    @Override
    public void scheduleMultiple(List<EventDto> eventDto) {
        List<Event> events = eventDto.stream()
                .map(this::formEventObject)
                .toList();
        Events listedEvents = getListed();
        List<Event> items = listedEvents.getItems();
        List<Event> executed = events.stream()
                .filter(event -> !containsEvent(items, event))
                .map(this::insertAndExecute)
                .toList();
        executed.forEach(event -> logEvent(event, "Created new event"));
    }

    @Override
    public void deleteMultiple(List<EventDto> eventDto) {
        List<Event> events = eventDto.stream()
                .map(this::formEventObject)
                .toList();
        Events listed = getListed();
        List<Event> items = listed.getItems();
        for (Event event : events) {
            for (Event item : items) {
                if (item.getSummary().equals(event.getSummary())) {
                    try {
                        calendar.events()
                                .delete(CALENDAR_ID, item.getId())
                                .execute();
                        logEvent(event, "Successfully deleted");
                    } catch (IOException e) {
                        throw new GoogleCalendarException("Can't delete Google Calendar Event!", e);
                    }
                }
            }
        }
    }

    // List the next 10 events from the primary calendar.
    @Override
    public void list() {
        Events events = getListed();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            log.info("No upcoming events found.");
        } else {
            log.info("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                log.info(String.format("%s (%s)", event.getSummary(), start));
            }
        }
    }

    private boolean containsEvent(List<Event> items, Event event) {
        for (Event item : items) {
            String summary = item.getSummary();
            EventDateTime start = item.getStart();
            if (event.getSummary().equals(summary)
                    && event.getStart().equals(start)
            ) {
                logEvent(event, "Event exists");
                return true;
            }
        }
        return false;
    }

    private Events getListed() {
        try {
            DateTime now = new DateTime(System.currentTimeMillis());
            return calendar.events().list("primary")
                    .setMaxResults(120)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            throw new GoogleCalendarException("Can't list events from Google Calendar!", e);
        }
    }

    private void logEvent(Event event, String message) {
        log.info(message + " " + event);
    }

    private Event insertAndExecute(Event event) {
        try {
            Calendar.Events calendarEvents = calendar.events();
            Calendar.Events.Insert insert = calendarEvents.insert(CALENDAR_ID, event);
            return insert.execute();
        } catch (IOException e) {
            throw new GoogleCalendarException("Can't insert event: " + event, e);
        }
    }

    private Event formEventObject(EventDto eventDto) {
        Event event = new Event()
                .setSummary(eventDto.getSummary())
                .setLocation(eventDto.getLocation())
                .setDescription(eventDto.getDescription());

        DateTime startDate = eventDto.getStartDate();
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDate = eventDto.getEndDate();
        if (endDate == null) {
            EventDateTime end = new EventDateTime()
                    .setDateTime(startDate)
                    .setTimeZone("America/Los_Angeles");
            event.setEnd(end);
        }

        String[] eventRequestRecurrence = eventDto.getRecurrences();
        if (!StringUtils.isArrayBlank(eventRequestRecurrence)) {
            event.setRecurrence(Arrays.asList(eventRequestRecurrence));
        }

        String[] attendees = eventDto.getAttendees();
        if (!StringUtils.isArrayBlank(attendees)) {
            List<EventAttendee> eventAttendees = new ArrayList<>();
            for (String attendee : attendees) {
                EventAttendee eventAttendee = new EventAttendee().setEmail(attendee);
                eventAttendees.add(eventAttendee);
            }
            event.setAttendees(eventAttendees);
        } else {
            throw new NoAttendeeException("Attendee can't be empty!!!");
        }

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        return event;
    }

    private Calendar getCalendar() {
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleCalendarException("Can't get Google Calendar", e);
        }
        HttpCredentialsAdapter httpCredentialsAdapter = googleAuth.getHttpCredentialsAdapter();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, httpCredentialsAdapter)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
