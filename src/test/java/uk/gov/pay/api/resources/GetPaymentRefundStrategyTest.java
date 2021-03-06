package uk.gov.pay.api.resources;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.pay.api.app.config.PublicApiConfig;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.exception.GetRefundException;
import uk.gov.pay.api.model.TokenPaymentType;
import uk.gov.pay.api.service.GetPaymentRefundService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class GetPaymentRefundStrategyTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private PublicApiConfig configuration;

    private GetPaymentRefundService mockGetPaymentRefundService = mock(GetPaymentRefundService.class);
    private GetPaymentRefundStrategy getPaymentRefundStrategy;

    private String paymentId = "payment-id";
    private String refundId = "refund-id";
    private Account account = new Account("account-id", TokenPaymentType.CARD);

    @Test
    public void validateAndExecuteUsesLedgerOnlyStrategy() {
        String strategy = "ledger-only";
        getPaymentRefundStrategy = new GetPaymentRefundStrategy(configuration, strategy, account, paymentId, refundId, mockGetPaymentRefundService);
        getPaymentRefundStrategy.validateAndExecute();

        verify(mockGetPaymentRefundService).getLedgerPaymentRefund(account, paymentId, refundId);
        verify(mockGetPaymentRefundService, never()).getConnectorPaymentRefund(account, paymentId, refundId);
        verify(mockGetPaymentRefundService, never()).getPaymentRefund(account, paymentId, refundId);
    }

    @Test
    public void validateAndExecuteUsesFutureStrategyOnly() {
        String strategy = "future-behaviour";
        getPaymentRefundStrategy = new GetPaymentRefundStrategy(configuration, strategy, account, paymentId, refundId, mockGetPaymentRefundService);
        getPaymentRefundStrategy.validateAndExecute();

        verify(mockGetPaymentRefundService).getPaymentRefund(account, paymentId, refundId);
    }

    @Test
    @Parameters({"", "unknown"})
    public void validateAndExecuteShouldUseConnectorOnlyStrategy(String strategy) {
        getPaymentRefundStrategy = new GetPaymentRefundStrategy(configuration, strategy, account, paymentId, refundId, mockGetPaymentRefundService);

        getPaymentRefundStrategy.validateAndExecute();

        verify(mockGetPaymentRefundService).getConnectorPaymentRefund(account, paymentId, refundId);
        verify(mockGetPaymentRefundService, never()).getLedgerPaymentRefund(account, paymentId, refundId);
        verify(mockGetPaymentRefundService, never()).getPaymentRefund(account, paymentId, refundId);
    }

    @Test
    public void whenSwitchingToFutureStrategyUsesFutureBehaviourStrategy() {
        when(configuration.getAlwaysUseFutureStrategy()).thenReturn(true);
        getPaymentRefundStrategy = new GetPaymentRefundStrategy(configuration, null, account, paymentId, refundId, mockGetPaymentRefundService);
        getPaymentRefundStrategy.validateAndExecute();

        verify(mockGetPaymentRefundService).getPaymentRefund(account, paymentId, refundId);
    }
}
