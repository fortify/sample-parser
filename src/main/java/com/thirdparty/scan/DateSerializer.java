package com.thirdparty.scan;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by svobodpe on 2017-01-25.
 */
public class DateSerializer extends StdConverter<Date, String> {
    @Override
    public String convert(final Date value) {
        return DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC).format(value.toInstant());
    }
}
