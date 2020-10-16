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

import com.tangocard.api.client.error.ApiError;
import com.tangocard.api.client.exception.*;
import com.tangocard.api.client.log.ApiClientLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

/**
 * The base class for API Clients, the concrete implementations will be code generated using OpenAPI Generator.
 */
@Slf4j
@Controller
public abstract class ApiClient {

    public static final String ERROR_RESPONSE_LOG_TEMPLATE = "\nERROR RESPONSE:\nRESPONSE HEADERS: {}\nRESPONSE BODY: {}";
    public static final String IMAGE_HEADER_PREFIX = "data:image/png;base64,";

    /**
     * Add handlers for expected HTTP errors
     *
     * @param response
     * @return
     */
    protected WebClient.ResponseSpec handleHttpErrors(final WebClient.ResponseSpec response) {
        response
                // 400 Bad Request
                .onStatus(s -> s.equals(BAD_REQUEST), r -> map(r, BadRequestException::new))
                // 401 Unauthorized
                .onStatus(s -> s.equals(UNAUTHORIZED), r -> map(r, UnauthorizedException::new))
                // 402 Insufficient Funds
                .onStatus(s -> s.equals(PAYMENT_REQUIRED), r -> map(r, InsufficientFundsException::new))
                // 403 Forbidden
                .onStatus(s -> s.equals(FORBIDDEN), r -> map(r, ForbiddenException::new))
                // 404 Not Found
                .onStatus(s -> s.equals(NOT_FOUND), r -> map(r, NotFoundException::new))
                // 409 Conflict
                .onStatus(s -> s.equals(CONFLICT), r -> map(r, ConflictException::new))
                // 429 Too Many Requests
                .onStatus(s -> s.equals(TOO_MANY_REQUESTS), r -> map(r, TooManyRequestsException::new))
                // 422 Unprocessable Entity
                .onStatus(s -> s.equals(UNPROCESSABLE_ENTITY), r -> map(r, UnprocessableEntityException::new))
                // 500 Server Error
                .onStatus(s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR), r -> map(r, ServerErrorException::new))
                // 503 Service Unavailable
                .onStatus(s -> s.equals(HttpStatus.SERVICE_UNAVAILABLE), r -> map(r, ServiceUnavailableException::new))
                // Any other error
                .onStatus(s -> s.isError(), r -> map(r, ApiException::new));
        return response;
    }

    private Mono<ApiException> map(ClientResponse r, Function<ApiError, ApiException> mapper) {
        return r.bodyToMono(ApiError.class)
                .doOnEach(e -> logResponse(r, e))
                .map(mapper);
    }

    private void logResponse(ClientResponse r, Signal<ApiError> e) {
        // called from the `doOnEach` above, `onNext` for each
        // the `onComplete` does not have access to the ApiError
        if (e.isOnNext()) {
            String headers = r.headers().asHttpHeaders().toString();
            // unwrap the ApiError from the Signal using regex
            String error = e.toString()
                    .replaceFirst("^[^(]*\\(", "")
                    .replaceFirst("\\)$", "");
            log.error(ERROR_RESPONSE_LOG_TEMPLATE, headers, ApiClientLogger.redact(error));
        }
    }

    public static String getBasicAuthString(String user, String password) {
        return String.format("Basic %s", Base64.getEncoder()
                .encodeToString(String.format("%s:%s", user, password)
                        .getBytes()));
    }

    public String encodeImageAsBase64EncodedUpload(String resourcePath) throws IOException {
        return IMAGE_HEADER_PREFIX + encodeResourceFileToBase64String(resourcePath);
    }

    public String encodeResourceFileToBase64String(String resourcePath) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return encodeInputStreamToBase64String(classloader.getResourceAsStream(resourcePath));
    }

    public String encodeInputStreamToBase64String(InputStream is) throws IOException {
        byte[] fileContent = IOUtils.toByteArray(is);
        String encoded = Base64.getEncoder().encodeToString(fileContent);
        return encoded;
    }
}
