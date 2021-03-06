package uk.gov.pay.api.resources;

import uk.gov.pay.api.app.config.PublicApiConfig;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.model.PaymentEventsResponse;
import uk.gov.pay.api.service.GetPaymentEventsService;

public class GetPaymentEventsStrategy extends LedgerOrConnectorStrategyTemplate<PaymentEventsResponse> {
    private final Account account;
    private final String paymentId;
    private final GetPaymentEventsService getPaymentEventsService;

    public GetPaymentEventsStrategy(PublicApiConfig configuration, String strategyName, Account account, String paymentId,
                                    GetPaymentEventsService getPaymentEventsService) {
        super(configuration, strategyName);
        this.account = account;
        this.paymentId = paymentId;
        this.getPaymentEventsService = getPaymentEventsService;
    }

    @Override
    protected PaymentEventsResponse executeLedgerOnlyStrategy() {
        return getPaymentEventsService.getPaymentEventsFromLedger(account, paymentId);
    }

    @Override
    protected PaymentEventsResponse executeFutureBehaviourStrategy() {
        return executeLedgerOnlyStrategy();
    }

    @Override
    protected PaymentEventsResponse executeDefaultStrategy() {
        return getPaymentEventsService.getPaymentEventsFromConnector(account, paymentId);
    }
}
