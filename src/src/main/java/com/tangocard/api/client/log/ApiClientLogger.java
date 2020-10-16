package com.tangocard.api.client.log;

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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

import static java.nio.charset.Charset.defaultCharset;

/**
 * A cross cutting concern for logging HTTP calls at the `DEBUG` level.
 */
public class ApiClientLogger extends LoggingHandler {
    public ApiClientLogger(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String event, Object arg) {
        if (arg instanceof ByteBuf) {
            ByteBuf msg = (ByteBuf) arg;
            return redact(decode(msg, msg.readerIndex(), msg.readableBytes(), defaultCharset()));
        }
        return super.format(ctx, event, arg);
    }

    private String decode(ByteBuf buffer, int readerIndex, int readableBytes, Charset charset) {
        return buffer.toString(readerIndex, readableBytes, charset).trim();
    }

    /**
     * Perform log redaction.
     *
     * WARNING: This is a stub. It is not sufficient for production level logging.
     * Please protect your secrets.
     *
     * @param raw
     * @return
     */
    public static String redact(String raw) {
        return raw.replaceFirst("Authorization: Basic [a-zA-Z0-9=]*", "Authorization: Basic XXXXXXXXXXXXXXXXXXXX");
    }
}
