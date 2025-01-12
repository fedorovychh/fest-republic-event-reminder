package io.service.events.fest;

import io.service.events.common.DateTimeUtils;
import io.service.events.common.WebClient;
import io.service.events.exception.NoContentException;
import io.service.events.model.EventDto;
import io.service.events.service.EventSearchService;
import com.google.api.client.util.DateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FESTEventSearch implements EventSearchService<FESTEventRequest> {

    static String FEST_EVENT_PATTERN = "(\\d{2}\\.\\d{2}), (\\d{2}:\\d{2}) – (.+)";

    private final WebClient webClient;

    @Override
    public List<EventDto> findEvents(FESTEventRequest request) {
        String url = request.getUrl();
        String websiteContent = webClient.parseResource(url)
                .orElseThrow(() -> new NoContentException("Can't get content from: " + url));

        List<EventDto> events = new ArrayList<>();

        Pattern pattern = Pattern.compile(FEST_EVENT_PATTERN);
        Document document = Jsoup.parse(websiteContent);
        Elements divs = document.select("div");
        for (Element div : divs) {
            String event = div.ownText();
            Matcher matcher = pattern.matcher(event);
            if (matcher.matches()) {
                log.info("Found event: " + event);

                String date = matcher.group(1);  // dd.MM
                String time = matcher.group(2);  // HH:mm
                String eventName = matcher.group(3);  // event Name

                DateTime dateTime = DateTimeUtils.toDateTime(date, time);

                EventDto eventDto = new EventDto();
                eventDto.setSummary(eventName);
                eventDto.setDescription(eventName + " at " + date + " " + time);
                eventDto.setStartDate(dateTime);
                eventDto.setLocation(request.getLocation());
                eventDto.setAttendees(request.getAttendees());

                events.add(eventDto);
            }
        }

        return events;
    }
}
