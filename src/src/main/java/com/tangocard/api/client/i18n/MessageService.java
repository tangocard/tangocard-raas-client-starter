package com.tangocard.api.client.i18n;

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
import com.tangocard.api.client.error.ApiValidationError;
import com.tangocard.api.client.exception.ApiException;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.Objects;

/**
 * Message service is the internationalization "translator", converting i18n codes
 * into English (because that is the only messages file).
 *
 * To provide your own translations, you can simply substitute the `messages.properties`
 * file for one in another language. To support multiple languages you will need to
 * either expand this class or perform i18n mapping in a different layer such as the
 * front end of a web app.
 *
 * See `src/main/resources/messages.properties`
 */
@NoArgsConstructor
public class MessageService {
    public static final String MESSAGES_BASENAME = "messages";
    private final MessageSource messageSource = getResourceBundleMessageSource();

    private static Locale defaultLocale = new Locale("en", "US");

    private static ResourceBundleMessageSource getResourceBundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MESSAGES_BASENAME);
        messageSource.setDefaultLocale(defaultLocale);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    private static ResourceBundleMessageSource getResourceBundleMessageSource(Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultLocale(locale);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    public String getMessage(String code, Object... args) throws NoSuchMessageException {
        if (code == null) {
            return "";
        }
        if (args == null) {
            args = new String[] {};
        }
        String[] stringArgs = new String[args.length];

        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null && args[i] instanceof DefaultMessageSourceResolvable) {
                stringArgs[i] = ((DefaultMessageSourceResolvable) args[i]).getDefaultMessage();
            } else {
                stringArgs[i] = Objects.toString(args[i], "");
            }
        }

        try {
            return this.messageSource.getMessage(code, stringArgs, Locale.getDefault());
        } catch (Exception var5) {
            throw new NoSuchMessageException(String.format("Error converting '%s' to message", code));
        }
    }

    public String expandApiErrorMessage(ApiError apiError) {
        if (apiError == null) {
            return "";
        }

        StringBuilder expanded = new StringBuilder(apiError.getHttpCode())
                .append(" ").append(apiError.getHttpPhrase()).append(": ");
        boolean first = true;
        if (apiError.getErrors() != null) {
            for (ApiValidationError error : apiError.getErrors()) {
                if (!first) {
                    expanded.append(", ");
                }
                expanded.append("(").append(error.getI18nKey()).append(") ")
                        .append(getMessage(error.getI18nKey(), error.getI18nTokenReplacements()));
                first = false;
            }
        }
        return expanded.toString();
    }
}
