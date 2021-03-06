package uk.gov.pay.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.pay.api.model.ledger.RefundTransactionFromLedger;
import uk.gov.pay.api.model.links.RefundLinksForSearch;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static uk.gov.pay.api.resources.PaymentRefundsResource.PAYMENT_BY_ID_PATH;
import static uk.gov.pay.api.resources.PaymentRefundsResource.PAYMENT_REFUND_BY_ID_PATH;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Schema(name = "Refund")
public class RefundResponse {

    @Schema(example = "act4c33g40j3edfmi8jknab84x", accessMode = READ_ONLY)
    private String refundId;
    @Schema(example = "2017-01-10T16:52:07.855Z", accessMode = READ_ONLY)
    private String createdDate;
    @Schema(example = "120", accessMode = READ_ONLY)
    private Long amount;
    @JsonProperty("_links")
    private RefundLinksForSearch links;
    @Schema(example = "success", allowableValues = {"submitted", "success", "error"}, accessMode = READ_ONLY)
    private String status;

    private RefundResponse(RefundFromConnector refund, URI selfLink, URI paymentLink) {
        this.refundId = refund.getRefundId();
        this.amount = refund.getAmount();
        this.status = refund.getStatus();
        this.createdDate = refund.getCreatedDate();
        this.links = new RefundLinksForSearch();

        links.addSelf(selfLink.toString());
        links.addPayment(paymentLink.toString());
    }

    private RefundResponse(RefundTransactionFromLedger refund, URI selfLink, URI paymentLink) {
        this.refundId = refund.getTransactionId();
        this.amount = refund.getAmount();
        this.status = refund.getState().getStatus();
        this.createdDate = refund.getCreatedDate();
        this.links = new RefundLinksForSearch();

        links.addSelf(selfLink.toString());
        links.addPayment(paymentLink.toString());
    }

    private RefundResponse(String refundId, Long amount, String status,
                           String createdDate, URI selfLink, URI paymentLink) {
        this.refundId = refundId;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
        this.links = new RefundLinksForSearch();

        links.addSelf(selfLink.toString());
        links.addPayment(paymentLink.toString());
    }

    public static RefundResponse from(RefundFromConnector refund, URI selfLink, URI paymentLink) {
        return new RefundResponse(refund, selfLink, paymentLink);
    }

    public static RefundResponse from(RefundTransactionFromLedger refund, URI selfLink, URI paymentLink) {
        return new RefundResponse(refund, selfLink, paymentLink);
    }

    //todo: remove after full refactoring of PaymentRefundsResource (to use service layer) 
    public static RefundResponse valueOf(RefundFromConnector refundEntity, String paymentId, String baseUrl) {
        URI selfLink = UriBuilder.fromUri(baseUrl)
                .path(PAYMENT_REFUND_BY_ID_PATH)
                .build(paymentId, refundEntity.getRefundId());

        URI paymentLink = UriBuilder.fromUri(baseUrl)
                .path(PAYMENT_BY_ID_PATH)
                .build(paymentId);

        return new RefundResponse(
                refundEntity.getRefundId(),
                refundEntity.getAmount(),
                refundEntity.getStatus(),
                refundEntity.getCreatedDate(),
                selfLink,
                paymentLink);
    }

    public String getRefundId() {
        return refundId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public RefundLinksForSearch getLinks() {
        return links;
    }
}
