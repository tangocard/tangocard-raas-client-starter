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

import lombok.Builder;
import lombok.Value;

/**
 * This is the field validation error response object from RaaS.
 * It is a child object of `ApiError` as a list item of the `errors` field.
 */
@Value
@Builder
public class ApiValidationError {
    String path;
    String i18nKey;
    Object[] i18nTokenReplacements;
    String message;
    String invalidValue;
    String constraint;
}
