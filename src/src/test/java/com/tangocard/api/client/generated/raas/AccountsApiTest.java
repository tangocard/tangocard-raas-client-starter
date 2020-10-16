package com.tangocard.api.client.generated.raas;

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

import com.tangocard.api.client.config.WebClientConfig;
import com.tangocard.api.client.generated.raas.model.AccountView;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.tangocard.api.client.util.DateTimeUtil.timeoutInSeconds;
import static org.junit.Assert.fail;

/**
 * This class is NOT code generated, it tests generated code.
 * That is why it's package name includes `generated`.
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/application.properties")
@ContextConfiguration(classes = {AccountsApi.class, WebClientConfig.class})
public class AccountsApiTest {
    public static String ACCOUNT_IDENTIFIER = "raas-client";

    @Value("${tangocard.raas.accountId}")
    private String accountId;

    @Autowired
    private AccountsApi accountsApi;

    // GET /accounts
    @Test
    public void listCustomerAccounts() {
        AccountView accountView = null;
        try {
            Flux<AccountView> accountViewFlux = accountsApi.listCustomerAccounts();
            accountView = accountViewFlux
                    .filter(a -> a.getAccountIdentifier().equals(ACCOUNT_IDENTIFIER))
                    .blockLast(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(accountView);
        Assert.assertEquals(ACCOUNT_IDENTIFIER, accountView.getAccountIdentifier());
    }

    // GET /accounts/{identifier}
    @Test
    public void getAccount() {
        AccountView accountView = null;
        try {
            Mono<AccountView> accountViewMono = accountsApi.getAccount(accountId);
            accountView = accountViewMono.block(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(accountView);
        Assert.assertEquals("Unexpected mis-match of account id", accountId, accountView.getAccountIdentifier());
    }
}
