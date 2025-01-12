package io.service.events.fest;

import lombok.Data;

@Data
public class FESTEventRequest {
    private String url;
    private String location = "!FEST";
    private String[] attendees;
}
