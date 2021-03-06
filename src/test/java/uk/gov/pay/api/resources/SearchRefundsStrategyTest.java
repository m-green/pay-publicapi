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
import uk.gov.pay.api.service.RefundsParams;
import uk.gov.pay.api.service.SearchRefundsService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class SearchRefundsStrategyTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private PublicApiConfig configuration;

    private SearchRefundsService mockSearchRefundsService = mock(SearchRefundsService.class);
    private SearchRefundsStrategy searchRefundsStrategy;

    private RefundsParams refundsParams;
    private Account account = new Account("account-id", TokenPaymentType.CARD);

    @Test
    @Parameters({"ledger-only", "future-behaviour"})
    public void validateAndExecuteShouldUseLedgerOnlyForListedStrategies(String strategy) {
        searchRefundsStrategy = new SearchRefundsStrategy(configuration, strategy, account, refundsParams, mockSearchRefundsService);
        searchRefundsStrategy.validateAndExecute();

        verify(mockSearchRefundsService).searchLedgerRefunds(account, refundsParams);
        verify(mockSearchRefundsService, never()).searchConnectorRefunds(account, refundsParams);
    }

    @Test
    @Parameters({"", "unknown"})
    public void validateAndExecuteShouldUseConnectorOnlyForDefaultOrUnknownStrategy(String strategy) {
        searchRefundsStrategy = new SearchRefundsStrategy(configuration, strategy, account, refundsParams, mockSearchRefundsService);

        searchRefundsStrategy.validateAndExecute();

        verify(mockSearchRefundsService).searchConnectorRefunds(account, refundsParams);
        verify(mockSearchRefundsService, never()).searchLedgerRefunds(account, refundsParams);
    }

    @Test
    public void whenSwitchingToFutureStrategyUsesFutureBehaviourStrategy() {
        when(configuration.getAlwaysUseFutureStrategy()).thenReturn(true);
        searchRefundsStrategy = new SearchRefundsStrategy(configuration, null, account, refundsParams, mockSearchRefundsService);
        searchRefundsStrategy.validateAndExecute();

        verify(mockSearchRefundsService).searchLedgerRefunds(account, refundsParams);
        verify(mockSearchRefundsService, never()).searchConnectorRefunds(account, refundsParams);
    }
}
