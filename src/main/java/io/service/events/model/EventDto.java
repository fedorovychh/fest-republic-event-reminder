package io.service.events.model;

import com.google.api.client.util.DateTime;
import lombok.Data;

import java.util.Arrays;

@Data
public class EventDto {
    private String summary;
    private String location;
    private String description;
    private DateTime startDate;
    private DateTime endDate;
    private String[] recurrences;
    private String[] attendees;

    @Override
    public String toString() {
        return "EventDto{" +
                "summary='" + summary + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", recurrence=" + Arrays.toString(recurrences) +
                ", attendee=" + Arrays.toString(attendees) +
                '}';
    }
}
