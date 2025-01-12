package io.service.events.common;

import com.google.api.client.util.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {
    public static DateTime toDateTime(String dateString, String timeString) {
        int currentYear = LocalDate.now().getYear();
        String fullDateString = dateString + "." + currentYear;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(fullDateString, dateFormatter);
        LocalTime time = LocalTime.parse(timeString);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        Date utilDate = Date.from(localDateTime
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant());
        return new DateTime(utilDate);
    }
}
