package uk.gov.pay.api.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.api.app.config.PublicApiConfig;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.model.PaymentError;
import uk.gov.pay.api.model.directdebit.mandates.MandateError;
import uk.gov.pay.api.model.directdebit.mandates.CreateMandateRequest;
import uk.gov.pay.api.model.directdebit.mandates.MandateResponse;
import uk.gov.pay.api.model.search.directdebit.DirectDebitSearchMandatesParams;
import uk.gov.pay.api.model.search.directdebit.SearchMandateResponse;
import uk.gov.pay.api.resources.error.ApiErrorResponse;
import uk.gov.pay.api.service.MandatesService;
import uk.gov.pay.api.service.directdebit.DirectDebitMandateSearchService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@Api(tags = "Direct Debit", value = "/v1/directdebit/mandates")
@Produces({"application/json"})
public class MandatesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MandatesResource.class);

    private final String baseUrl;
    private final MandatesService mandateService;
    private final DirectDebitMandateSearchService mandateSearchService;

    @Inject
    public MandatesResource(PublicApiConfig configuration, MandatesService mandateService, DirectDebitMandateSearchService mandateSearchService) {
        this.baseUrl = configuration.getBaseUrl();
        this.mandateService = mandateService;
        this.mandateSearchService = mandateSearchService;
    }

    @GET
    @Timed
    @Path("/v1/directdebit/mandates/{mandateId}")
    @Produces(APPLICATION_JSON)
    @ApiOperation(
            nickname = "Get a mandate",
            value = "Find mandate by ID",
            notes = "Return information about the mandate. " +
                    "The Authorisation token needs to be specified in the 'Authorization' header " +
                    "as 'Authorization: Bearer YOUR_API_KEY_HERE'",
            authorizations = {@Authorization("Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MandateResponse.class),
            @ApiResponse(code = 401, message = "Credentials are required to access this resource"),
            @ApiResponse(code = 404, message = "Not found", response = MandateError.class),
            @ApiResponse(code = 429, message = "Too many requests", response = ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Downstream system error", response = MandateError.class)})
    public Response getMandate(@ApiParam(value = "accountId", hidden = true) @Auth Account account,
                               @PathParam("mandateId") String mandateId) {
        LOGGER.info("Mandate get request - [ {} ]", mandateId);
        MandateResponse getMandateResponse = mandateService.get(account, mandateId);
        LOGGER.info("Mandate returned: [ {} ]", getMandateResponse);
        return Response.ok().entity(getMandateResponse).build();
    }

    @GET
    @Timed
    @Path("/v1/directdebit/mandates/")
    @Produces(APPLICATION_JSON)
    @ApiOperation(
            nickname = "Search mandates",
            value = "Search mandates",
            notes = "Searches for mandates with the parameters provided. " +
                    "The Authorisation token needs to be specified in the 'Authorization' header " +
                    "as 'Authorization: Bearer YOUR_API_KEY_HERE'",
            authorizations = {@Authorization("Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SearchMandateResponse.class),
            @ApiResponse(code = 401, message = "Credentials are required to access this resource"),
            @ApiResponse(code = 404, message = "Not found", response = MandateError.class),
            @ApiResponse(code = 422, message = "Invalid parameters: from_date, to_date, state, page, display_size. See Public API documentation for the correct data formats", response = PaymentError.class),
            @ApiResponse(code = 429, message = "Too many requests", response = ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Downstream system error", response = MandateError.class)})
    public Response searchMandates(@ApiParam(value = "accountId", hidden = true) @Auth Account account,
                                   @Valid @BeanParam DirectDebitSearchMandatesParams mandateSearchParams) {
        LOGGER.info("Mandate search request - [ {} ]", mandateSearchParams.toString());
        SearchMandateResponse searchMandatesResponse = mandateSearchService.search(account, mandateSearchParams);
        
        LOGGER.info("Mandate search returned: [ {} ]", searchMandatesResponse);
        return Response.ok().entity(searchMandatesResponse).build();
    }
    
    @POST
    @Timed
    @Path("/v1/directdebit/mandates")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @ApiOperation(
            nickname = "Create a mandate",
            value = "Create a new mandate",
            notes = "Create a new mandate for the account associated to the Authorisation token. " +
                    "The Authorisation token needs to be specified in the 'Authorization' header " +
                    "as 'Authorization: Bearer YOUR_API_KEY_HERE'",
            code = 201,
            authorizations = {@Authorization("Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = MandateResponse.class),
            @ApiResponse(code = 400, message = "Bad request", response = PaymentError.class),
            @ApiResponse(code = 401, message = "Credentials are required to access this resource"),
            @ApiResponse(code = 429, message = "Too many requests", response = ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Downstream system error", response = PaymentError.class)})
    public Response createMandate(@ApiParam(value = "accountId", hidden = true) @Auth Account account,
                                  @ApiParam(value = "requestPayload", required = true) @Valid CreateMandateRequest createMandateRequest) {
        LOGGER.info("Mandate create request - [ {} ]", createMandateRequest);
        MandateResponse createMandateResponse = mandateService.create(account, createMandateRequest);
        URI mandateUri = UriBuilder.fromUri(baseUrl)
                .path("/v1/directdebit/mandates/{mandateId}")
                .build(createMandateResponse.getMandateId());
        LOGGER.info("Mandate returned (created): [ {} ]", createMandateResponse);
        return Response.created(mandateUri).entity(createMandateResponse).build();
    }
}
