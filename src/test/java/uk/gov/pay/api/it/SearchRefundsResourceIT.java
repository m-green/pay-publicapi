package uk.gov.pay.api.it;


import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import uk.gov.pay.api.it.fixtures.PaymentRefundSearchJsonFixture;
import uk.gov.pay.api.utils.PublicAuthMockClient;
import uk.gov.pay.api.utils.mocks.ConnectorMockClient;
import uk.gov.pay.commons.validation.DateTimeUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.core.Is.is;
import static uk.gov.pay.api.utils.Urls.paymentLocationFor;
import static uk.gov.pay.commons.model.ApiResponseDateTimeFormatter.ISO_INSTANT_MILLISECOND_PRECISION;

public class SearchRefundsResourceIT extends PaymentResourceITestBase {

    private static final String CHARGE_ID = "ch_ab2341da231434l";
    private static final ZonedDateTime TIMESTAMP = DateTimeUtils.toUTCZonedDateTime("2016-01-01T12:00:00Z").get();
    private static final String CREATED_DATE = ISO_INSTANT_MILLISECOND_PRECISION.format(TIMESTAMP);

    private ConnectorMockClient connectorMockClient = new ConnectorMockClient(connectorMock);
    private PublicAuthMockClient publicAuthMockClient = new PublicAuthMockClient(publicAuthMock);

    @Test
    public void searchRefunds_shouldReturnValidResponse() {

        publicAuthMockClient.mapBearerTokenToAccountId(API_KEY, GATEWAY_ACCOUNT_ID);

        PaymentRefundSearchJsonFixture refund_1 = new PaymentRefundSearchJsonFixture(100L, CREATED_DATE, "100", CHARGE_ID, "available", new ArrayList<>());
        PaymentRefundSearchJsonFixture refund_2 = new PaymentRefundSearchJsonFixture(300L, CREATED_DATE, "300", CHARGE_ID, "pending", new ArrayList<>());

        connectorMockClient.respondWithSearchRefunds(GATEWAY_ACCOUNT_ID, refund_1, refund_2);

        getRefundsSearchResponse(API_KEY)
                .statusCode(200)
                .contentType(JSON)
                .body("total", is(1))
                .body("count", is(1))
                .body("page", is(1))
                .body("results[0].refund_id", is("100"))
                .body("results[0].created_date", is("2016-01-01T12:00:00.000Z"))
                .body("results[0].payment_id", is("ch_ab2341da231434l"))
                .body("results[0].amount", is(100))
                .body("results[0].status", is("available"))
                .body("results[0]._links.self.href", is(paymentRefundLocationFor(CHARGE_ID, "100")))
                .body("results[0]._links.payment.href", is(paymentLocationFor(configuration.getBaseUrl(), CHARGE_ID)))
                .body("results[1].refund_id", is("300"))
                .body("results[1].created_date", is("2016-01-01T12:00:00.000Z"))
                .body("results[1].payment_id", is("ch_ab2341da231434l"))
                .body("results[1].amount", is(300))
                .body("results[1].status", is("pending"))
                .body("results[1]._links.self.href", is(paymentRefundLocationFor(CHARGE_ID, "300")))
                .body("results[1]._links.payment.href", is(paymentLocationFor(configuration.getBaseUrl(), CHARGE_ID)))
                .body("_links.first_page.href", is("http://publicapi.url/v1/refunds?page=1"))
                .body("_links.prev_page.href", is("http://publicapi.url/v1/refunds?page=2"))
                .body("_links.self.href", is("http://publicapi.url/v1/refunds?page=3"))
                .body("_links.next_page.href", is("http://publicapi.url/v1/refunds?page=4"))
                .body("_links.last_page.href", is("http://publicapi.url/v1/refunds?page=5"));
    }

    private ValidatableResponse getRefundsSearchResponse(String bearerToken) {
        return given().port(app.getLocalPort())
                .header(AUTHORIZATION, "Bearer " + bearerToken)
                .get("/v1/refunds")
                .then();
    }

}
