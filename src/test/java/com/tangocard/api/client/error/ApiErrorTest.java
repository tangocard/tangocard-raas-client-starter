package com.tangocard.api.client.error;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tangocard.api.client.config.JacksonConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ApiErrorTest {

    // obtain an ObjectMapper configured as Spring would configure it
    private ObjectMapper objectMapper = JacksonConfiguration.newConfiguredObjectMapper();;

    public static final String SERIALIZED_API_ERROR = "{\n" +
            "  \"timestamp\" : \"2020-03-26T19:19:28.564Z\",\n" +
            "  \"requestId\" : \"82b542f0-ed18-4021-8f8f-d176ae0982cd\",\n" +
            "  \"path\" : \"/raas/v2/orders\",\n" +
            "  \"httpCode\" : 400,\n" +
            "  \"httpPhrase\" : \"Bad Request\",\n" +
            "  \"errors\" : [ {\n" +
            "    \"path\" : \"sender.email\",\n" +
            "    \"i18nKey\" : \"422.134\",\n" +
            "    \"message\" : \"not a well-formed email address\",\n" +
            "    \"invalidValue\" : \"INVALID_EMAIL_ADDRESS\",\n" +
            "    \"constraint\" : \"Email\"\n" +
            "  } ]\n" +
            "}";

    @Test
    public void testApiErrorDeserialization() throws IOException {
        ApiError apiError = null;
        try {
            apiError = objectMapper.readValue(SERIALIZED_API_ERROR, ApiError.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(apiError);
    }
}
