package com.tangocard.api.client;

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

import lombok.Getter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Encapsulated `UriComponentsBuilder` to make the use of query params more fluent in the generated code.
 * This class only wraps the functionality that is called upon in the generated code.
 */
public class QueryParamsUriComponentsBuilder {

    @Getter
    private final UriComponentsBuilder uriComponentsBuilder;

    /**
     * Get a new, empty instance of a OptionalQueryParamsUriComponentsBuilder
     */
    public QueryParamsUriComponentsBuilder() {
        this.uriComponentsBuilder = UriComponentsBuilder.newInstance();
    }

    /**
     * This private constructor is to support the static `fromUriString()` method
     * @param path
     */
    private QueryParamsUriComponentsBuilder(String path) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromUriString(path);
    }

    /**
     * Create a builder that is initialized with the given URI string.
     * <p><strong>Note:</strong> The presence of reserved characters can prevent
     * correct parsing of the URI string. For example if a query parameter
     * contains {@code '='} or {@code '&'} characters, the query string cannot
     * be parsed unambiguously. Such values should be substituted for URI
     * variables to enable correct parsing:
     * <pre class="code">
     * String uriString = &quot;/hotels/42?filter={value}&quot;;
     * UriComponentsBuilder.fromUriString(uriString).buildAndExpand(&quot;hot&amp;cold&quot;);
     * </pre>
     * @param uri the URI string to initialize with
     * @return the new {@code UriComponentsBuilder}
     */
    public static QueryParamsUriComponentsBuilder fromUriString(String uri) {
        return new QueryParamsUriComponentsBuilder(uri);
    }

    /**
     * Build a {@code UriComponents} instance from the various components contained in this builder.
     * @return the URI components
     */
    public UriComponents build() {
        return uriComponentsBuilder.build();
    }

    /**
     * Add an optional query param with any number of values
     * @param name
     * @param values
     */
    public <T> QueryParamsUriComponentsBuilder queryParam(String name, T... values) {
        if (!ObjectUtils.isEmpty(values)) {
            for (Object value : values) {
                if (value instanceof Optional) {
                    value = ((Optional)value).orElse(null);
                }
                String valueAsString = (value != null ? value.toString() : null);
                uriComponentsBuilder.queryParam(name, valueAsString);
            }
        } else {
            uriComponentsBuilder.queryParam(name, new String[0]); // empty array
        }

        return this;
    }
}
