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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * This is the generic error response object from RaaS
 */
@Value
@Builder
public class ApiError {
    ZonedDateTime timestamp;
    String requestId;
    Integer httpCode;
    String httpPhrase;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String path;
    String message;
    String i18nKey;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Object[] i18nTokenReplacements;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ApiValidationError> errors;
    /**
     * @deprecated `code` is a deprecated error categorization field (it is not unique)
     * Instead use `i18nKey` for unique error codes.
     * @return
     */
    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer code;
}
