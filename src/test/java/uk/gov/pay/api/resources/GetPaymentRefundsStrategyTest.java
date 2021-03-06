package uk.gov.pay.api.resources;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.pay.api.app.config.PublicApiConfig;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.model.TokenPaymentType;
import uk.gov.pay.api.service.GetPaymentRefundsService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class GetPaymentRefundsStrategyTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private PublicApiConfig configuration;

    private GetPaymentRefundsService mockGetPaymentRefundsService = mock(GetPaymentRefundsService.class);
    private GetPaymentRefundsStrategy getPaymentRefundsStrategy;

    private String paymentId = "payment-id";
    private Account account = new Account("account-id", TokenPaymentType.CARD);

    @Test
    @Parameters({"ledger-only", "future-behaviour"})
    public void validateAndExecuteShouldUseLedgerOnlyForListedStrategies(String strategy) {
        getPaymentRefundsStrategy = new GetPaymentRefundsStrategy(configuration, strategy, account, paymentId, mockGetPaymentRefundsService);
        getPaymentRefundsStrategy.validateAndExecute();

        verify(mockGetPaymentRefundsService).getLedgerTransactionTransactions(account, paymentId);
        verify(mockGetPaymentRefundsService, never()).getConnectorPaymentRefunds(account, paymentId);
    }

    @Test
    @Parameters({"", "unknown"})
    public void validateAndExecuteShouldUseConnectorOnlyForDefaultOrUnknownStrategy(String strategy) {
        getPaymentRefundsStrategy = new GetPaymentRefundsStrategy(configuration, strategy, account, paymentId, mockGetPaymentRefundsService);

        getPaymentRefundsStrategy.validateAndExecute();

        verify(mockGetPaymentRefundsService).getConnectorPaymentRefunds(account, paymentId);
        verify(mockGetPaymentRefundsService, never()).getLedgerTransactionTransactions(account, paymentId);
    }

    @Test
    public void whenSwitchingToFutureStrategyUsesFutureBehaviourStrategy() {
        when(configuration.getAlwaysUseFutureStrategy()).thenReturn(true);
        getPaymentRefundsStrategy = new GetPaymentRefundsStrategy(configuration, null, account, paymentId, mockGetPaymentRefundsService);
        getPaymentRefundsStrategy.validateAndExecute();

        verify(mockGetPaymentRefundsService).getLedgerTransactionTransactions(account, paymentId);
        verify(mockGetPaymentRefundsService, never()).getConnectorPaymentRefunds(account, paymentId);
    }
}
