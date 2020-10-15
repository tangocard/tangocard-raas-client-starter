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

import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtilTest {

    public static final String UTC_ZULU_TIME_ZONE_NAME = "Z";
    public static final String LOCAL_TIME_ZONE_NAME = "America/Los_Angeles";
    public static final ZoneId LOCAL_TIME_ZONE = ZoneId.of(LOCAL_TIME_ZONE_NAME);
    public static final boolean IS_DAYLIGHT_SAVINGS_TIME = TimeZone.getTimeZone(LOCAL_TIME_ZONE_NAME).inDaylightTime(new Date());
    public static final int SECONDS_IN_AN_HOUR = 3600;

    @Test
    public void testIsoFormattedUtcZonedDateTime() {
        String formattedDate = DateTimeUtil.isoFormattedUtcZonedDateTime(ZonedDateTime.now());

        Assert.assertNotNull(formattedDate);
        Assert.assertTrue("Does not contain T", formattedDate.contains("T"));
        Assert.assertTrue("Does not end with Z", formattedDate.endsWith(UTC_ZULU_TIME_ZONE_NAME));
        Assert.assertTrue("Bad format", formattedDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z"));
    }

    @Test
    public void testTimeoutInSeconds() {
        Duration duration = DateTimeUtil.timeoutInSeconds(1L);

        Assert.assertEquals(0L, duration.getNano());
        Assert.assertEquals(1L, duration.getSeconds());
        Assert.assertEquals(false, duration.isNegative());
    }

    @Test
    public void testTimeoutInMillis() {
        Duration duration = DateTimeUtil.timeoutInMillis(100L);

        Assert.assertEquals(100000000L, duration.getNano());
        Assert.assertEquals(0L, duration.getSeconds());
        Assert.assertEquals(false, duration.isNegative());
    }

    @Test
    public void testToUtc() {
        ZonedDateTime now = ZonedDateTime.now(LOCAL_TIME_ZONE);

        ZonedDateTime utcDateTime = DateTimeUtil.toUtc(now);

        Assert.assertTrue("Unexpected time zone offset", utcDateTime.toString().endsWith(UTC_ZULU_TIME_ZONE_NAME));
        int actualDifference = utcDateTime.getHour() - now.getHour();
        if (actualDifference < 0) {
            actualDifference += 24; // handle change of date
        }
        int expectedDifference = IS_DAYLIGHT_SAVINGS_TIME ? 7 : 8;
        Assert.assertEquals(expectedDifference, actualDifference);
    }

    @Test
    public void testToZonedDateTime() {
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        int expectedDifference = Math.abs(offset.getTotalSeconds() / SECONDS_IN_AN_HOUR);
        LocalDateTime localDateTime = LocalDateTime.now(LOCAL_TIME_ZONE);

        ZonedDateTime zonedDateTime = DateTimeUtil.toZonedDateTime(localDateTime);

        Assert.assertTrue("Unexpected time zone offset", zonedDateTime.toString().endsWith(UTC_ZULU_TIME_ZONE_NAME));
        int actualDifference = zonedDateTime.getHour() - localDateTime.getHour();
        if (actualDifference < 0) {
            actualDifference += 24; // handle change of date
        }
        Assert.assertEquals(expectedDifference, actualDifference);
    }

    @Test
    public void testToLocalDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        LocalDateTime localDateTime = DateTimeUtil.toLocalDateTime(zonedDateTime);

        Assert.assertTrue("Unexpected LocalDateTime", zonedDateTime.toString().startsWith(localDateTime.toString()));
    }
}
