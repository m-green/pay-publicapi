package uk.gov.pay.api.resources;


import uk.gov.pay.api.app.config.PublicApiConfig;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.model.RefundResponse;
import uk.gov.pay.api.service.GetPaymentRefundService;

public class GetPaymentRefundStrategy extends LedgerOrConnectorStrategyTemplate<RefundResponse> {

    private final Account account;
    private final String paymentId;
    private final String refundId;
    private final GetPaymentRefundService getPaymentRefundsService;

    public GetPaymentRefundStrategy(PublicApiConfig configuration, String strategy, Account account, String paymentId, String refundId,
                                    GetPaymentRefundService getPaymentRefundsService) {
        super(configuration, strategy);
        this.account = account;
        this.paymentId = paymentId;
        this.refundId = refundId;
        this.getPaymentRefundsService = getPaymentRefundsService;
    }

    @Override
    protected RefundResponse executeLedgerOnlyStrategy() {
        return getPaymentRefundsService.getLedgerPaymentRefund(account, paymentId, refundId);
    }

    @Override
    protected RefundResponse executeFutureBehaviourStrategy() {
        return getPaymentRefundsService.getPaymentRefund(account, paymentId, refundId);
    }

    @Override
    protected RefundResponse executeDefaultStrategy() {
        return getPaymentRefundsService.getConnectorPaymentRefund(account, paymentId, refundId);
    }
}
