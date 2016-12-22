package com.thirdparty.scan;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
@JsonSerialize
public class Scan {
    
    private List<Finding> findings = new LinkedList<>();

    private Date scanDate;

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(List<Finding> findings) {
        this.findings = findings;
    }

    public Date getScanDate() {
        return scanDate;
    }

    @JsonDeserialize(converter = DateConverter.class)
    public void setScanDate(final Date scanDate) {
        this.scanDate = scanDate;
    }

    private static class DateConverter implements Converter<String, Date> {
        private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {DateTimeFormatter.ISO_DATE_TIME};

        @Override
        public Date convert(final String dateStr) {
            for (final DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
                final TemporalAccessor temporalAccessor;
                try {
                    temporalAccessor = formatter.parse(dateStr);
                } catch (final DateTimeException e) {
                    // try next parser
                    continue;
                }
                final Instant instant;
                if (temporalAccessor.query(TemporalQueries.offset()) != null) {
                    instant = OffsetDateTime.from(temporalAccessor).toInstant();
                } else {
                    instant = LocalDateTime.from(temporalAccessor).toInstant(ZoneOffset.UTC);
                }
                return Date.from(instant);
            }
            // no parser worked
            throw new IllegalArgumentException("Unsupported date format: " + dateStr);
        }

        @Override
        public JavaType getInputType(final TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        @Override
        public JavaType getOutputType(final TypeFactory typeFactory) {
            return typeFactory.constructType(Date.class);
        }
    }
}
