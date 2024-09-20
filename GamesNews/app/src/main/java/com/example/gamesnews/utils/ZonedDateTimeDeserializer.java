package com.example.gamesnews.utils;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm a, XX 'UTC'");

    @Override
    public ZonedDateTime deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String dateString = json.getAsString();
        // Parse a data recebida no formato UTC
        ZonedDateTime utcDateTime = ZonedDateTime.parse(dateString, formatter);

        // Converte para o fuso horário do sistema do usuário
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault());

        return localDateTime;
    }
}

