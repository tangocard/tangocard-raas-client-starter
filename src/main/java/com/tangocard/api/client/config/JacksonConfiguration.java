package com.tangocard.api.client.config;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

    /**
     * Override the Jackson autoconfigured Bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "objectMapper")
    @Qualifier("raasObjectMapper")
    public ObjectMapper raasObjectMapper() {
        return newConfiguredObjectMapper();
    }

    /**
     * A factory method of obtaining a configured ObjectMapper from
     * non-Spring managed use cases.
     *
     * @return
     */
    public static ObjectMapper newConfiguredObjectMapper() {
        return configureObjectMapper(new Jackson2ObjectMapperBuilder()).build();
    }

    /**
     * Configure a Jackson2ObjectMapperBuilder in a standard, sharable way.
     *
     * With the following features:
     *   - support for Java 8 time constructs (e.g. ZonedDateTime)
     *   - unknown properties are ignored when deserializing
     *   - null and empty fields will not be serialized
     *
     * @param builder
     * @return
     */
    public static Jackson2ObjectMapperBuilder configureObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // disable features
        builder.featuresToDisable(
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        // enable features
        builder.featuresToEnable(
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                SerializationFeature.INDENT_OUTPUT
        );

        // exclude null values
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        // "As of Jackson 2.x, auto-registration will only register older JSR310Module, and not newer JavaTimeModule
        // -- this is due to backwards compatibility. Because of this make sure to either use explicit registration,
        //  or, if you want to use JavaTimeModule but also auto-registration, make sure to register JavaTimeModule
        // BEFORE calling mapper.findAndRegisterModules())." - https://github.com/FasterXML/jackson-modules-java8
        builder.modulesToInstall(new JavaTimeModule());

        // add all the other modules
        builder.findModulesViaServiceLoader(true);

        return builder;
    }
}
