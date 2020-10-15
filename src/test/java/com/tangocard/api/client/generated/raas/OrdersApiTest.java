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

import com.tangocard.api.client.config.JacksonConfiguration;
import com.tangocard.api.client.config.WebClientConfig;
import com.tangocard.api.client.exception.BadRequestException;
import com.tangocard.api.client.generated.raas.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.tangocard.api.client.util.DateTimeUtil.isoFormattedUtcZonedDateTime;
import static com.tangocard.api.client.util.DateTimeUtil.timeoutInSeconds;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.fail;

/**
 * This class is NOT code generated, it tests generated code.
 * That is why it's package name includes `generated`.
 */

@Slf4j
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/application.properties")
@ContextConfiguration(classes = {JacksonConfiguration.class, OrdersApi.class, WebClientConfig.class})
public class OrdersApiTest {

	public static final String EXISTING_ORDER_REFERENCE_ID = "RA200924-27116-40";

	@Value("${tangocard.raas.accountId}")
	private String accountId;
	@Value("${tangocard.raas.customerId}")
	private String customerId;
	@Value("${tangocard.raas.test.orders.utid}")
	private String testUtid;

	@Autowired
	private OrdersApi ordersApi;

	@Before
	public void setup() throws Exception {
		Assert.assertNotNull("raasOrderService unexpectedly null", ordersApi);
	}

	// GET /orders/{referenceOrderID}
	@Test
	public void getOrderByReferenceOrderId_HappyPath() {
		Optional<OrderViewVerbose> response = ordersApi.getOrder(EXISTING_ORDER_REFERENCE_ID)
				.blockOptional(timeoutInSeconds(60L));
		Assert.assertTrue("Unexpected empty response", response.isPresent());
	}

	// GET /orders
	@Test
	public void testListOrders_HappyPath() {
		Optional<String> accountIdentifier = Optional.of(accountId);
		Optional<String> customerIdentifier = Optional.of(customerId);
		Optional<String> externalRefID = Optional.empty();
		Optional<String> startDate = Optional.empty();
		Optional<String> endDate = Optional.empty();
		Optional<Integer> elementsPerBlock = Optional.empty();
		Optional<Integer> page = Optional.empty();
		Optional<Integer> id = Optional.empty();
		Optional<String> name = Optional.empty();
		Optional<String> identityHash = Optional.empty();

		Optional<OrderListView> pageOfOrders = ordersApi.listOrders(
				accountIdentifier, customerIdentifier, externalRefID, startDate, endDate, elementsPerBlock, page, id, name, identityHash)
				.blockOptional(timeoutInSeconds(60L));

		Assert.assertTrue("Unexpected empty response", pageOfOrders.isPresent());
		Assert.assertEquals("Unexpected page number", Integer.valueOf(0), pageOfOrders.get().getPage().getNumber());
		Assert.assertTrue("Unexpected number of orders", pageOfOrders.get().getPage().getResultCount() > 0);
	}

	@Test
	public void testListOrders_HappyPath_WithDateRange() {
		Optional<String> accountIdentifier = Optional.of(accountId);
		Optional<String> customerIdentifier = Optional.of(customerId);
		Optional<String> externalRefID = Optional.empty();
		// The `startDate` and `endDate` strings are expected to to be ISO 8601 formatted ZonedDateTime
		String utcZonedIsoFormattedStartDate = isoFormattedUtcZonedDateTime(ZonedDateTime.now().minus(5L, DAYS));
		Optional<String> startDate = Optional.of(utcZonedIsoFormattedStartDate);
		String utcZonedIsoFormattedEndDate = isoFormattedUtcZonedDateTime(ZonedDateTime.now());
		Optional<String> endDate = Optional.of(utcZonedIsoFormattedEndDate);
		Optional<Integer> elementsPerBlock = Optional.of(2);
		Optional<Integer> page = Optional.empty();
		Optional<Integer> id = Optional.empty();
		Optional<String> name = Optional.empty();
		Optional<String> identityHash = Optional.empty();

		Optional<OrderListView> pageOfOrders = ordersApi.listOrders(
				accountIdentifier, customerIdentifier, externalRefID, startDate, endDate, elementsPerBlock, page, id, name, identityHash)
				.blockOptional(timeoutInSeconds(60L));

		Assert.assertTrue("Unexpected empty response", pageOfOrders.isPresent());
		Assert.assertEquals("Unexpected page number", Integer.valueOf(0), pageOfOrders.get().getPage().getNumber());
		Assert.assertTrue("Unexpected number of orders: " + pageOfOrders.get().getPage().getResultCount(),
				pageOfOrders.get().getPage().getResultCount() > 0);
	}

	// POST /orders
	@Test
	public void testCreateOrder_HappyPath() throws UnknownHostException {
		// Create a unique identifier (This is your uniqe reference identifier for this order.)
		// The "create order" endpoint is idempotent. Meaning that if you supply an external (to RaaS)
		// reference id that has been used before, the API will return the order details the
		// identifier belongs to rather than createing a new order.
		// This endpoint is "retry safe"!
		String externalRefId = String.format("testCreateOrder-%s", UUID.randomUUID().toString());

		// The builder pattern is helpful for populating data transfer objects (DTO)
		CreateOrderCriteria createOrderCriteria = CreateOrderCriteria.builder()
				.accountIdentifier(accountId)
				.customerIdentifier(customerId)
				.amount(new BigDecimal(10))
				.campaign("campaign")
				.emailSubject("Test `createOrder()` email subject")
				.externalRefID(externalRefId)
				.message("Test `createOrder()` message")
				.notes("Test `createOrder()` notes")
				.recipient(RecipientInfoCriteria.builder()
						.email("noreply@tangocard.com")
						.firstName("Boaty")
						.lastName("McBoatface")
						.build())
				.sendEmail(false)	// don't send emails for tests
				.sender(SenderInfoCriteria.builder()
						.email("noreply@tangocard.com")
						.firstName("Egg")
						.lastName("McTangocard")
						.build())
				.utid(testUtid)
				.build();

		// Call the "create order" endpoint on the Tango Card RaaS API.
		// The REST request is made using Spring WebClient, part of the
		// Web Reactive Framework, which supports non-blocking web requests.
		Optional<OrderViewSummary> optionalOrderViewSummary = Optional.empty();
		try {
			Mono<OrderViewSummary> orderViewMono = ordersApi.createOrder(createOrderCriteria);
			optionalOrderViewSummary = orderViewMono.blockOptional(timeoutInSeconds(60L));
		} catch (WebClientResponseException e) {
			log.error(e.getResponseBodyAsString());

			// Error handling code here or just throw
			throw e;
		}

		// At this point, we can be confident we have an order response
		Assert.assertTrue(optionalOrderViewSummary.isPresent());
		// with a unique referenceOrderId
		Assert.assertNotNull(optionalOrderViewSummary.get().getReferenceOrderID());
		// and the supplied externalRefId is reflected back
		Assert.assertEquals(externalRefId, optionalOrderViewSummary.get().getExternalRefID());
	}

	@Test
	public void testCreateOrder_ValidationError() throws UnknownHostException {
		// setup
		String externalRefId = String.format("testCreateOrder-%s", UUID.randomUUID().toString());
		CreateOrderCriteria createOrderCriteria = CreateOrderCriteria.builder()
				.accountIdentifier(accountId)
				.customerIdentifier(customerId)
				.amount(new BigDecimal(10))
				.campaign("campaign")
				.emailSubject("Test `createOrder()` email subject")
				.externalRefID(externalRefId)
				.message("Test `createOrder()` message")
				.notes("Test `createOrder()` notes")
				.recipient(RecipientInfoCriteria.builder()
						.email("noreply@tangocard.com")
						.firstName("Boaty")
						.lastName("McBoatface")
						.build())
				.sendEmail(false)	// don't send emails for tests
				.sender(SenderInfoCriteria.builder()
						.email("INVALID_EMAIL_ADDRESS")
						.firstName("Egg")
						.lastName("McTangocard")
						.build())
				.utid(testUtid)
				.build();

		// execute
		Optional<OrderViewSummary> optionalOrderViewSummary = Optional.empty();
		try {
			Mono<OrderViewSummary> orderViewMono = ordersApi.createOrder(createOrderCriteria);
			optionalOrderViewSummary = orderViewMono.blockOptional(timeoutInSeconds(60L));
		} catch (BadRequestException e) {
			// EXPECTED Exception, test passes
		} catch (WebClientResponseException e) {
			log.error(e.getResponseBodyAsString());
			fail(e.getMessage());
		}
	}

	// POST /orders/{referenceOrderID}/resends
	@Test
	public void testGetOrderResends_HappyPath() {
		OrderResendRequestCriteria orderResendCriteria = OrderResendRequestCriteria.builder()
				.build();
		Mono<ResendView> resendViewMono = ordersApi.getOrderResends(EXISTING_ORDER_REFERENCE_ID, orderResendCriteria);
		Optional<ResendView> resendView = resendViewMono.blockOptional(timeoutInSeconds(60L));
		Assert.assertTrue("orderViewSummary not present", resendView.isPresent());
		log.debug(resendView.toString());
	}
}
