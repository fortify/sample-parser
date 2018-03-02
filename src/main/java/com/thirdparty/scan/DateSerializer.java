package com.thirdparty.scan;

/**
 * (c) Copyright [2017] Micro Focus or one of its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateSerializer extends StdConverter<Date, String> {
    @Override
    public String convert(final Date value) {
        return DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC).format(value.toInstant());
    }
}
