package com.tangocard.api.client.util;

/*-
 * #%L
 * Tango Card RaaS API Client Starter
 * %%
 * Copyright (C) 2009 - 2020 Tango Card
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.*;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.systemDefault;

public class DateTimeUtil {

    /**
     * A date format String that does most of the formatting work for RFC3339.  Note that since
     * Java's SimpleDateFormat does not provide all the facilities needed for RFC3339 there is still
     * some custom code to finish the job.
     */
    public static final String RFC_3339_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * Get a Duration in seconds
     *
     * @param secondsToWait
     * @return
     */
    public static Duration timeoutInSeconds(Long secondsToWait) {
        final Instant now = Instant.now();
        return Duration.between(now, now.plusSeconds(secondsToWait));
    }

    /**
     * Get a Duration in milliseconds
     *
     * @param millisToWait
     * @return
     */
    public static Duration timeoutInMillis(Long millisToWait) {
        final Instant now = Instant.now();
        return Duration.between(now, now.plusMillis(millisToWait));
    }

    /**
     * Convert a ZonedDateTime to the UTC time zone
     *
     * @param zonedDateTime
     * @return
     */
    public static ZonedDateTime toUtc(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
    }

    /**
     * Get an ISO 8601 formatted date time String from a ZonedDateTime
     *
     * @param zonedDateTime
     * @return
     */
    public static String isoFormattedUtcZonedDateTime(ZonedDateTime zonedDateTime) {
        return toUtc(zonedDateTime)
                .withNano(0)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * Converts a system default time zone LocalDateTime to a UTC ZonedDateTime
     * @param localDateTime
     * @return
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return (localDateTime == null) ? null : toUtc(localDateTime.atZone(systemDefault()));
    }

    /**
     * Converts a ZonedDateTime in any time zone to a LocalDateTime in the system default time zone
     *
     * @param zonedDateTime
     * @return
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return (zonedDateTime == null) ? null : zonedDateTime.withZoneSameInstant(systemDefault()).toLocalDateTime();
    }
}
