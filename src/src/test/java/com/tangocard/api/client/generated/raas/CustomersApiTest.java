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
import com.tangocard.api.client.generated.raas.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.tangocard.api.client.util.DateTimeUtil.timeoutInSeconds;
import static org.junit.Assert.fail;

/**
 * This class is NOT code generated, it tests generated code.
 * That is why it's package name includes `generated`.
 *
 * NOTE: "Customer" is an old term for what is now called "Group" in the
 *       documentation. They are synonyms.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/application.properties")
@ContextConfiguration(classes = {CustomersApi.class, WebClientConfig.class})
public class CustomersApiTest {

    public static String ACCOUNT_IDENTIFIER = "raas-client";
    public static String CUSTOMER_IDENTIFIER = "raas-client";

    @Autowired
    private CustomersApi customersApi;

    // GET /customers
    @Test
    public void testGetCustomers() {
        CustomerViewSummary customerViewSummary = null;
        try {
            Flux<CustomerViewSummary> customerViewSummaryFlux = customersApi.listCustomers();
            customerViewSummary = customerViewSummaryFlux
                    .blockLast(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(customerViewSummary);
    }

    // GET /customers/{customerIdentifier}
    @Test
    public void testGetCustomer() {
        CustomerViewSummary customerView = null;
        try {
            Mono<CustomerViewSummary> customerViewMono = customersApi.getCustomer(CUSTOMER_IDENTIFIER);
            customerView = customerViewMono.block(timeoutInSeconds(60L));
            customerView.getCustomerIdentifier();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }

        Assert.assertNotNull(customerView);
        Assert.assertEquals("Unexpected mis-match of customer id",
                CUSTOMER_IDENTIFIER, customerView.getCustomerIdentifier());
    }

    // GET /customers/{customerIdentifier}/accounts
    @Test
    public void testGetCustomerAccounts() {
        AccountViewSummary accountView = null;
        try {
            Flux<AccountViewSummary> accountViewFlux = customersApi.listCustomerAccounts1(CUSTOMER_IDENTIFIER);
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


    // POST /customers
    //      {
    //        "customerIdentifier": "string",
    //        "displayName": "string"
    //      }
    @Test
    public void testPostCustomers() {
        String customerIdentifier = UUID.randomUUID().toString();
        CreateCustomerCriteria customerCriteria = CreateCustomerCriteria.builder()
                .customerIdentifier(customerIdentifier)
                .displayName("Test Customer " + customerIdentifier)
                .build();
        CustomerViewNew newCustomer = null;
        try {
            Mono<CustomerViewNew> newCustomerMono = customersApi.createCustomer(customerCriteria);
            newCustomer = newCustomerMono.block(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("Unexpected mis-match of account id", customerIdentifier, newCustomer.getCustomerIdentifier());
    }

    // POST /customers/{customerIdentifier}/accounts
    //      {
    //        "accountIdentifier": "string",
    //        "contactEmail": "string",
    //        "displayName": "string"
    //      }
    @Test
    public void testPostCustomerAccounts() {
        // setup - create a new customer, in order to create a new account under it
        String customerIdentifier = UUID.randomUUID().toString();
        CreateCustomerCriteria customerCriteria = CreateCustomerCriteria.builder()
                .customerIdentifier(customerIdentifier)
                .displayName("Test Customer " + customerIdentifier)
                .build();
        CustomerViewNew newCustomer = null;
        try {
            Mono<CustomerViewNew> newCustomerMono = customersApi.createCustomer(customerCriteria);
            newCustomer = newCustomerMono.block(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(newCustomer);

        String accountIdentifier = UUID.randomUUID().toString();
        CreateAccountCriteria accountCriteria = CreateAccountCriteria.builder()
                .accountIdentifier(accountIdentifier)
                .displayName("Test Account " + accountIdentifier)
                .contactEmail("noreply@tangocard.com")
                .build();

        // validate
        AccountView newAccount = null;
        try {
            Mono<AccountView> newAccountMono = customersApi.createCustomerAccount(customerIdentifier, accountCriteria);
            newAccount = newAccountMono.block(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
        Assert.assertNotNull(newAccount);
    }

}
