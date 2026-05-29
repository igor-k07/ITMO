package com.itmo.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Адаптер для ZonedDateTime для Gson

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    @Override
    public JsonElement serialize(ZonedDateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
    }
}


