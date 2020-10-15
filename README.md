Tango Card's Rewards-as-a-Service API Client Spring&reg; Starter Project
========================================================================

# Summary

This project is an API connectivity accelerator for making calls to the Tango Card RaaS API. 
It is NOT a Software Development Kit (SDK).

This is also an opinionated demonstration of using code generation techniques to build the connection 
layer to our API. As such, it is intended to accelerate the on-boarding process by providing a concrete 
example of our suggested method of building API client code (with code generation).
 
This code is NOT production ready as it must be integrated into your system with proper 
security around the handling of Rewards and other sensitive information like Personally Identifiable Information (PII). 
Always test your code thoroughly.


# Getting Started with Tango Card RaaS
- RaaS v2 API Documentation - [Getting Started](https://developers.tangocard.com/docs)
- The source of truth is the RaaS API itself, not this accelerator project
  - supplemented by [Tango Card RaaS API Test Console](https://integration-www.tangocard.com/raas_api_console/v2/)
    - which exposes its [Swagger / OpenAPI definition](https://integration-www.tangocard.com/raas_api_console/v2/api-docs/swagger.json) 
- **You need to obtain a platform and credentials.**
  - To get sandbox environment credentials or to ask general questions about the API, please contact us at devsupport@tangocard.com
  - Production credentials will be given only after completing a business agreement.
- Once credentials are obtained, configure your [`application.properties`](src/main/resources/application.properties) or your 
[test `application.properties`](src/test/resources/application.properties) as follows:
  - Use the credentials given to you by Tango Card.
    ```
    tangocard.raas.platformKey=s0mEr@nd0MSeToFcHaRict3Rs
    tangocard.raas.platformName=your-platform-name
    ```
  - Create your first `Customer` and `Account` via the API, either by executing methods from [AccountsApiTest](src/test/java/com/tangocard/api/client/generated/raas/AccountsApiTest.java) 
  or via the [RaaS Test Console](https://integration-www.tangocard.com/raas_api_console/v2/), substituting your Platform Name and Platform Key.
    ```
    tangocard.raas.accountId=your-account-id
    tangocard.raas.customerId=your-customer-id
    ```
  - Pick a product, identified by `utid`, from your catalog to use in test orders. Use the RaaS Test Console or [CatalogsApiTest](src/test/java/com/tangocard/api/client/generated/raas/CatalogsApiTest.java).
    ```
    # Obtain utids from the API. See `CatalogsApiTest` for examples
    tangocard.raas.test.orders.utid=U974333
    
    ```
    
# Code Generation
We suggest that you use [OpenAPI Generator](https://openapi-generator.tech/) to generate your API client code.
In fact, this project also uses OpenAPI Generator.
- [An Introduction to OpenAPI Generator](https://nordicapis.com/introduction-to-openapi-generator/)
  - Most popular languages are supported
  - C++
  - C#
  - Java
  - Javascript
  - Python
  - [And more](https://openapi-generator.tech/docs/generators)

# How to consume this code

## Cut and paste this code right into your application.
- Make sure you port the Maven plugin `openapi-generator-maven-plugin` or equivalent into your build or you won't get model updates.
 
## Consume as a Maven or Gradle dependency
1. Clone this project
2. Build the project locally and consume the SNAPSHOT dependency in development
3. Add it as a project in your CI/CD system and consume a shared versioned release (optional)
4. Fork the code and manage change yourself
    ```
    <dependency>
        <groupId>com.tangocard.raas</groupId>
        <artifactId>tangocard-raas-client-starter</artifactId>
        <version>0.0.0-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
    ```
5. This project uses [Spring Boot Auto-configure](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration)
so merely including it as a Maven dependency enough to wire the Spring dependencies.

## Configuration
Consider using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/) to secure your configuration values.
At the very least, you will need to implement an encryption-at-rest approach to secure your Platform Key.

## Logging Levels
RaaS API Client request and response logging is managed with the `logging.level.reactor.netty.http.client` property.
The following log levels will behave as follows for request and response logging:
- **ERROR/WARN/INFO**  - Logs failed response messages with headers as plain strings
- **DEBUG** - Logs request and response messages as JSON Strings, including headers
- **TRACE** - Logs request and response messages as bytes, including headers

# Code Examples
Examples of invoking the API client methods can be found in the project's [`tests/` folder](src/test/java/com/tangocard/api/client/generated/raas/).

To create an order that orders a give card:
```
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
```

Additional test code examples cover the following topics
1. [Customers](src/test/java/com/tangocard/api/client/generated/raas/CustomersApiTest.java)
2. [Accounts](src/test/java/com/tangocard/api/client/generated/raas/AccountsApiTest.java)
3. [Funds](src/test/java/com/tangocard/api/client/generated/raas/FundApiTest.java)
4. [Catalogs](src/test/java/com/tangocard/api/client/generated/raas/CatalogsApiTest.java)
5. [Orders](src/test/java/com/tangocard/api/client/generated/raas/OrdersApiTest.java)
6. [Email Templates](src/test/java/com/tangocard/api/client/generated/raas/EmailTemplatesApiTest.java)
7. [Exchange Rates](src/test/java/com/tangocard/api/client/generated/raas/ExchangeRatesApiTest.java)
 
## Error Handling and i81n
When there is an error, the RaaS API will return an [ApiError](src/main/java/com/tangocard/api/client/error/ApiError.java).
This error response object contains fields that contain an internationalization (i18n) message key and a set of token replacement 
values that can be used to look up and complete translated phrases. This library only supplies [English translations](src/main/resources/messages.properties) 
as a reference. Support for additional languages will require translation work and additional coding. 

### HTTP Error Specific Exception Handling
When this API client layer encounters an error response from Tango Card, an Exception will be thrown that corresponds to the returned HTTP code.
When an Exception is created, it will use the i18n key and token replacement values to build the Exception message using
the supplied translation file.

Possible Exceptions are
- [BadRequestException](src/main/java/com/tangocard/api/client/exception/BadRequestException.java) (400)
- [ConflictException](src/main/java/com/tangocard/api/client/exception/ConflictException.java) (409)
- [ForbiddenException](src/main/java/com/tangocard/api/client/exception/ForbiddenException.java) (403)
- [InsufficientFundsException](src/main/java/com/tangocard/api/client/exception/InsufficientFundsException.java) (402)
- [NotFoundException](src/main/java/com/tangocard/api/client/exception/NotFoundException.java) (404)
- [ServerErrorException](src/main/java/com/tangocard/api/client/exception/ServerErrorException.java) (500)
- [ServiceUnavailableException](src/main/java/com/tangocard/api/client/exception/ServiceUnavailableException.java) (503)
- [UnauthorizedException](src/main/java/com/tangocard/api/client/exception/UnauthorizedException.java) (401)
- [UnprocessableEntityException](src/main/java/com/tangocard/api/client/exception/UnprocessableEntityException.java) (422)

## Considerations 
- Be kind, please DO NOT load test our API
- If you need to do load testing, you should mock the calls somehow or set up a stub server that returns "canned" responses
- Also, don't depend on our sandbox being "up" as part of your deploy pipeline. Our sandbox has no SLA for response time or up time. 
- You MUST secure your Platform Key so that it is encrypted at rest.
- You MUST secure reward credentials obtained from Tango Card in transit and at rest (if you choose to store them). This is $$!
- You MUST NOT log your Platform Key or any reward credentials without redaction.
- You SHOULD also redact any [personally identifiable information](https://en.wikipedia.org/wiki/Personal_data) (PII) you may log.

## Use of OpenAPI Generator
The model and API client code in this project is generated from the [RaaS API Swagger / OpenAPI definition](https://integration-www.tangocard.com/raas_api_console/v2/api-docs/swagger.json)
using custom defined [OpenAPI Generator](https://openapi-generator.tech/) templates. The custom templates are
based on the Java Spring WebClient and are specific to this Auto-configure Spring Starter implementation.

### Build Integration
Maven is the build tool for this project. The OpenAPI Code generation step is implemented with the 
[OpenAPI Generator Plugin](https://openapi-generator.tech/docs/plugins/) and is bound to the `generate-sources` phase.
Plugin configurations are specific to this project with special consideration to code generation templates 
and the scope of what will be generated and where.
```
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <id>raas-api-client</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <generatorName>java</generatorName>
                <inputSpec>src/main/resources/api/swagger.json</inputSpec>
                <output>${project.build.directory}/generated-sources</output>
                <templateDirectory>${project.basedir}/src/main/openapi-template/java</templateDirectory>
                <invokerPackage>${openapi-generator.invokerPackage}</invokerPackage>
                <apiPackage>${openapi-generator.apiPackage}</apiPackage>
                <modelPackage>${openapi-generator.modelPackage}</modelPackage>
                <generateAliasAsModel>false</generateAliasAsModel>
                <generateApis>true</generateApis>
                <generateApiTests>false</generateApiTests>
                <generateApiDocumentation>false</generateApiDocumentation>
                <generateModels>true</generateModels>
                <generateModelTests>false</generateModelTests>
                <generateSupportingFiles>false</generateSupportingFiles>
                <skipValidateSpec>true</skipValidateSpec>

                <!-- pass any necessary config options -->
                <configOptions>
                    <library>webclient</library>
                    <booleanGetterPrefix>is</booleanGetterPrefix>
                    <dateLibrary>java8</dateLibrary>
                    <generateApiDocumentation>true</generateApiDocumentation>
                    <hideGenerationTimestamp>true</hideGenerationTimestamp>
                    <interfaceOnly>true</interfaceOnly>
                    <serializableModel>true</serializableModel>
                    <snapshotVersion>true</snapshotVersion>
                    <useBeanValidation>false</useBeanValidation>
                    <useOptional>true</useOptional>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
### Custom Templates
The [Custom OpenAPI Generator templates](src/main/openapi-template/java) define a Spring Boot centric
implementation of Spring WebClient, leveraging the common error handling and internationalization that
is wrapped around the Reactive REST calls.

### Update the Swagger Definition
The Maven POM also defines a custom profile that controls download of the Swagger definition used 
in code generation. This profile is invoke in one of 2 ways: by explicitly including it in the Maven
command line or by the fact that the [local cache of the Swagger file](src/main/resources/api/swagger.json) does not exist.

Forcing the `update` profile to be active and re-download the RaaS Swagger definition:
```
mvn clean test-compile -P update
```

# Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/maven-plugin/)
* [Stackoverflow: a Netty issue](https://stackoverflow.com/questions/57885828/netty-cannot-access-class-jdk-internal-misc-unsafe)
* [An example of Spring Boot and Lombok code generation](https://github.com/deviantlycan/openapi-generator-templates/tree/master/generator-templates/JavaSpring/spring-boot-lombok-actuator)
* [Spring WebClient Requests with Parameters](https://www.baeldung.com/webflux-webclient-parameters)
* [5 common mistakes of Webflux novices](https://medium.com/@nikeshshetty/5-common-mistakes-of-webflux-novices-f8eda0cd6291)

# License
This accelerator project is licensed under the [Gnu Public License (GPL) v.3](LICENSE).

# DISCLAIMER OF WARRANTIES
YOU EXPRESSLY UNDERSTAND AND AGREE THAT: 
(i) THE SOFTWARE IS PROVIDED ON AN “AS IS” AND “AS AVAILABLE” BASIS, (ii) TANGO CARD AND ITS AFFILIATES, OFFICERS, EMPLOYEES, AGENTS, PARTNERS AND LICENSORS EXPRESSLY DISCLAIM ALL WARRANTIES OF ANY KIND, WHETHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. TANGO CARD AND ITS SUBSIDIARIES, AFFILIATES, OFFICERS, EMPLOYEES, AGENTS, PARTNERS AND LICENSORS MAKE NO WARRANTY THAT: (i) THE SOFTWARE WILL MEET YOUR EXPECTATIONS OR REQUIREMENTS, (ii) THE SOFTWARE WILL BE SECURE OR ERROR-FREE, (iii) THE RESULTS THAT MAY BE OBTAINED FROM THE USE OF THE SOFTWARE WILL BE ACCURATE OR RELIABLE, (iv) THE QUALITY OF ANY PRODUCTS OBTAINED BY YOU THROUGH THE SOFTWARE WILL MEET YOUR EXPECTATIONS, (v) THAT THE SOFTWARE IS FREE OF VIRUSES OR OTHER HARMFUL COMPONENTS; AND (vi) ANY ERRORS IN THE SOFTWARE WILL BE CORRECTED. NO ADVICE OR INFORMATION, WHETHER ORAL OR WRITTEN, OBTAINED BY YOU FROM TANGO CARD OR THROUGH OR FROM THE SOFTWARE SHALL CREATE ANY WARRANTY NOT EXPRESSLY STATED IN THE TOS https://www.tangocard.com/terms-of-service/.
