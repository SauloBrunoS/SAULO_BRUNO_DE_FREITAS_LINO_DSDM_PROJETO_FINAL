package com.example.gamesnews.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeDeserializer2 implements JsonDeserializer<ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String dateString = json.getAsString();
        try {
            return ZonedDateTime.parse(dateString, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date: " + dateString, e);
        }
    }
}
