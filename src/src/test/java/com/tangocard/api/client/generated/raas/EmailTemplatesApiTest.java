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
import com.tangocard.api.client.exception.NotFoundException;
import com.tangocard.api.client.generated.raas.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tangocard.api.client.util.DateTimeUtil.timeoutInSeconds;
import static org.junit.Assert.fail;

/**
 * This class is NOT code generated, it tests generated code.
 * That is why it's package name includes `generated`.
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/application.properties")
@ContextConfiguration(classes = {EmailTemplatesApi.class, WebClientConfig.class})
public class EmailTemplatesApiTest {

    private static final String KNOWN_ETID = "E975182";

    @Value("${tangocard.raas.accountId}")
    private String accountId;
    @Value("${tangocard.raas.customerId}")
    private String customerId;

    @Autowired
    private EmailTemplatesApi emailTemplatesApi;

    // GET /emailTemplates
    @Test
    public void testListEmailTemplates() {
        Optional<Integer> elementsPerBlock = Optional.empty();
        Optional<Integer> page = Optional.empty();

        EmailTemplateListView emailTemplateListView = null;
        try {
            Mono<EmailTemplateListView> emailTemplateListViewMono = emailTemplatesApi.listEmailTemplates(elementsPerBlock, page);
            emailTemplateListView = emailTemplateListViewMono.block(timeoutInSeconds(60L));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }

        Assert.assertNotNull("Page of email templates not found", emailTemplateListView);
        Assert.assertTrue("No templates in the page",  emailTemplateListView.getEmailTemplates().size() > 0);
        emailTemplateListView.getEmailTemplates().forEach(t -> System.out.println(t.getEtid()));
    }


    // GET /emailTemplates/{etid}
    @Test
    public void testGetEmailTemplate() {
        String expectedTemplateName = "name-586581ca-01c7-4e10-8e97-a41604c87157";
        Mono<EmailTemplateViewVerbose> emailTemplateMono = emailTemplatesApi.getEmailTemplate(KNOWN_ETID);
        Optional<EmailTemplateViewVerbose> optionalEmailTemplate = emailTemplateMono.blockOptional(timeoutInSeconds(60L));
        EmailTemplateViewVerbose emailTemplate = optionalEmailTemplate.get();

        Assert.assertEquals(expectedTemplateName, emailTemplate.getName());
    }

    // POST /emailTemplates
    // PATCH /emailTemplates/{etid}
    // DELETE /emailTemplates/{etid}
    // doingZ all 3 in one test to make it self-cleaning
    @Test
    public void testCreatePatchAndDeleteEmailTemplate() throws IOException {
        // setup

        // accessControl - (Optional) Which Customers and/or Accounts should have access to this template.
        // accessControl - type - The type of access being specified: PLATFORM, CUSTOMER or ACCOUNT.
        // accessControl - identifier - If the type is PLATFORM, the platform name or can be left blank.
        //                 If the type is CUSTOMER OR ACCOUNT, the customerIdentifier or the accountIdentifier, respectively.
        List<StandardRewardEmailTemplateAccess> accessControl = null;

        // defaults - If you want this template to be used at order time for the given Platform, Customer or Account
        //            when the Email Template Identifier (etid) is not provided with the order.
        // defaults - type - The type of default being specified: PLATFORM, CUSTOMER or ACCOUNT.
        // defaults - identifier - If the type is PLATFORM, the platform name or can be left blank. If the type
        //            is CUSTOMER OR ACCOUNT, the customerIdentifier or the accountIdentifier, respectively.
        List<StandardRewardEmailTemplateDefault> defaults = null;

        // create
        CreateEmailTemplateCriteria createEmailTemplateCriteria = CreateEmailTemplateCriteria.builder()
                .accentColor("#FF5733") // use CSS hex color values
                .accessControl(accessControl)
                .closing("closing")
                .customerServiceMessage("customerServiceMessage")
                .defaults(defaults)
                .fromName("fromName")
                // needs to be base64 encoded image bytes
                .headerImage(emailTemplatesApi.encodeImageAsBase64EncodedUpload("tangocard-logo.jpeg"))
                .headerImageAltText("headerImageAltText")
                .messageBody("messageBody")
                .name("name-" + UUID.randomUUID().toString())
                .subject("subject")
                .build();
        Mono<EmailTemplateViewVerbose> templateMono = emailTemplatesApi.createEmailTemplate(createEmailTemplateCriteria);
        EmailTemplateViewVerbose template = templateMono.block(timeoutInSeconds(60L));
        Assert.assertNotNull(template.getEtid());

        // update
        String newName = "name-" + UUID.randomUUID().toString();
        UpdateEmailTemplateCriteria updateEmailTemplateCriteria = UpdateEmailTemplateCriteria.builder()
                .accentColor("#FF5733") // use CSS hex color values
                .accessControl(accessControl)
                .closing("closing")
                .customerServiceMessage("customerServiceMessage")
                .defaults(defaults)
                .fromName("fromName")
                // needs to be base64 encoded image bytes
                .headerImage(emailTemplatesApi.encodeImageAsBase64EncodedUpload("tangocard-logo.jpeg"))
                .headerImageAltText("headerImageAltText")
                .messageBody("messageBody")
                .name(newName)
                .subject("subject")
                .build();
        Mono<EmailTemplateViewVerbose> updateEmailTemplateMono = emailTemplatesApi.updateEmailTemplate(template.getEtid(), updateEmailTemplateCriteria);
        EmailTemplateViewVerbose updatedTemplate = updateEmailTemplateMono.block(timeoutInSeconds(60L));

        Assert.assertEquals(newName, updatedTemplate.getName());

        // delete
        Mono<Void> deleted = emailTemplatesApi.deleteEmailTemplate(updatedTemplate.getEtid());
        deleted.block(timeoutInSeconds(60L));

        // verify deleted
        try {
            Mono<EmailTemplateViewVerbose> emailTemplateMono = emailTemplatesApi.getEmailTemplate(updatedTemplate.getName());
            emailTemplateMono.block(timeoutInSeconds(60L));
            fail("Expected a not found Exception");
        } catch (NotFoundException nfe) {
            // expected because we just deleted it
        }
    }
}
