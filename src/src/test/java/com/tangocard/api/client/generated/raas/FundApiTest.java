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
import com.tangocard.api.client.exception.BadRequestException;
import com.tangocard.api.client.exception.TooManyRequestsException;
import com.tangocard.api.client.generated.raas.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tangocard.api.client.util.DateTimeUtil.timeoutInSeconds;
import static org.junit.Assert.fail;

/**
 * This class is NOT code generated, it tests generated code.
 * That is why it's package name includes `generated`.
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/application.properties")
@ContextConfiguration(classes = {FundApi.class, WebClientConfig.class})
public class FundApiTest {

    public static final String EXISTING_CREDIT_CARD_TOKEN = "1aeff8d6-4062-4931-a552-88ad2bc00b53";

    @Value("${tangocard.raas.accountId}")
    private String accountId;
    @Value("${tangocard.raas.customerId}")
    private String customerId;

    @Autowired
    private FundApi fundApi;

    // POST /creditCardDeposits
    // GET  /creditCardDeposits/{depositId}
    /**
     * Testing both create and read together
     */
    @Test
    public void testMakeDeposit_TooManyRequestsIsOK() {
        try {
            CreateCreditCardDepositCriteria criteria = CreateCreditCardDepositCriteria.builder()
                    .creditCardToken(EXISTING_CREDIT_CARD_TOKEN)
                    .customerIdentifier(accountId)
                    .accountIdentifier(customerId)
                    .amount(BigDecimal.valueOf(1.00))
                    .build();
            Mono<CreditCardDepositView> depositMono = fundApi.createCreditCardDeposit(criteria);
            CreditCardDepositView creditCardDepositView = depositMono.block(timeoutInSeconds(60L));
            Assert.assertNotNull("creditCardDepositView is null", creditCardDepositView);
        } catch (TooManyRequestsException tmre) {
            // `429 Too Many Requests` is an acceptable response
            //  This happens because of funding rate limits that are
            //  outside the client's control.
        }
    }

    // POST /creditCards
    // POST /creditCardDeposits
    // GET  /creditCardDeposits/{depositId}
    // POST /creditCardUnregisters
    /**
     * Testing both create and unregister together so that the test clean up after itself
     */
    @Test
    public void testCreateAndUnregisterCreditCard() {
        LocalDateTime expirationDate = LocalDateTime.now().plusYears(3); // expires in 3 years
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedExpirationDate = expirationDate.format(formatter);

        // create credit card
        CreateCreditCardCriteria createCreditCardCriteria = CreateCreditCardCriteria.builder()
                .accountIdentifier(accountId)
                .customerIdentifier(customerId)
                .ipAddress("127.0.0.1")
                .billingAddress(BillingAddressCriteria.builder()
                        .firstName("Boaty")
                        .lastName("McBoatface")
                        .emailAddress("boaty.mcboatface@pacific.ocean")
                        // This needs to be a real address, it will be verified
                        .addressLine1("4700 42nd Ave SW #430a")
                        .city("Seattle")
                        .state("WA")
                        .country("US")
                        .postalCode("98216")
                        .build())
                .contactInformation(Arrays.asList(ContactInformationCriteria.builder()
                        .fullName("Boaty McBoatface")
                        .emailAddress("boaty.mcboatface@pacific.ocean")
                        .build()))
                .creditCard(CreditCardCriteria.builder()
                        // see https://developers.tangocard.com/docs/funding-operations#section-sandbox-registration
                        .number("5555555555554444")
                        .expiration(formattedExpirationDate)
                        .verificationNumber("123")
                        .build())
                .label("Test Card Label")
                .build();

        // Add a credit card
        CreditCardView creditCardView = null;
        try {
            creditCardView = fundApi.createCreditCard(createCreditCardCriteria)
                    .block(timeoutInSeconds(60L));
        } catch (BadRequestException bre) {
            // "Bad Request: (422.050) There are already 10 credit cards registered to this account" is an acceptable answer
            log.warn(bre.getLocalizedMessage());
        } catch (TooManyRequestsException tmre) {
            // Too many requests is also an acceptable answer from the call to `createCreditCard()`
            log.warn(tmre.getLocalizedMessage());
        }

        if (creditCardView != null) {
            // validate create credit card
            String creditCardToken = creditCardView.getToken();
            Assert.assertTrue("Empty token!", Strings.isNotBlank(creditCardToken));

            // setup a deposit
            CreateCreditCardDepositCriteria criteria = CreateCreditCardDepositCriteria.builder()
                    .creditCardToken(creditCardToken)
                    .customerIdentifier(accountId)
                    .accountIdentifier(customerId)
                    .amount(BigDecimal.valueOf(1.00))
                    .build();

            // deposit
            try {
                Mono<CreditCardDepositView> depositMono = fundApi.createCreditCardDeposit(criteria);
                CreditCardDepositView creditCardDepositView = depositMono.block(timeoutInSeconds(60L));
                Assert.assertNotNull("creditCardDepositView is null", creditCardDepositView);
            } catch (TooManyRequestsException tmre) {
                // `429 Too Many Requests` is an acceptable response
                //  This happens because of funding rate limits that are
                //  outside the client's control.
                log.warn(tmre.getLocalizedMessage());
            } catch (Exception e) {
                // continue to cleanup
            }

            // cleanup (and test of unregister)
            Mono<CreditCardUnregisterView> creditCardUnregisterViewMono = fundApi
                    .createCreditCardUnregister(CreateCreditCardUnregisterCriteria.builder()
                            .accountIdentifier(accountId)
                            .customerIdentifier(customerId)
                            .creditCardToken(creditCardToken)
                            .build());
            CreditCardUnregisterView creditCardUnregisterView = creditCardUnregisterViewMono.block(timeoutInSeconds(60L));

            // validate cleanup
            Assert.assertTrue("Empty token!", Strings.isNotBlank(creditCardUnregisterView.getToken()));

            Mono<CreditCardView> creditCardViewMono = fundApi.getCreditCard(creditCardToken);
            CreditCardView verifyCreditCardView = creditCardViewMono.block(timeoutInSeconds(60L));
            Assert.assertEquals(creditCardToken, verifyCreditCardView.getToken());
            Assert.assertEquals("DELETED", verifyCreditCardView.getStatus());
        }
    }

    // GET /creditCards/{token}
    @Test
    public void testGetCreditCard() {
        Mono<CreditCardView> creditCardViewMono = fundApi.getCreditCard(EXISTING_CREDIT_CARD_TOKEN);
        CreditCardView creditCardView = creditCardViewMono.block(timeoutInSeconds(60L));
        Assert.assertEquals(EXISTING_CREDIT_CARD_TOKEN, creditCardView.getToken());
    }

    // GET /creditCards?showInactive=false
    @Test
    public void testListCreditCards_ActiveOnly() {
        List<CreditCardView> creditCards = null;
        try {
            Flux<CreditCardView> creditCardViewFlux = fundApi.listCreditCards(Optional.of(false));
            creditCards = creditCardViewFlux.collectList().block();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }

        creditCards.stream().forEach(c -> log.info("CC token: " + c.getToken()));

        Optional<CreditCardView> optionalCreditCard = creditCards.stream()
                .filter(c -> EXISTING_CREDIT_CARD_TOKEN.equals(c.getToken()))
                .findAny();
        Assert.assertTrue("Credit Card not found", optionalCreditCard.isPresent());
    }

    // GET /creditCards?showInactive=true
    @Test
    public void testListCreditCards_ShowInactive() {
        CreditCardView creditCardView = null;
        try {
            Flux<CreditCardView> creditCardViewFlux = fundApi.listCreditCards(Optional.of(true));
            creditCardView = creditCardViewFlux
                    .filter(c -> EXISTING_CREDIT_CARD_TOKEN.equals(c.getToken()))
                    .blockLast(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }

        Assert.assertNotNull("Credit Card not found", creditCardView);
    }
}
