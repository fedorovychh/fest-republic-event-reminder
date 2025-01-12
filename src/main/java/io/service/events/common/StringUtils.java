package io.service.events.common;

public class StringUtils {
    public static boolean isArrayBlank(String[] strings) {
        return strings == null || strings.length == 0;
    }
}
