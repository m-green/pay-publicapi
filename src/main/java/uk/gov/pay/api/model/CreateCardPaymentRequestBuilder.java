package uk.gov.pay.api.model;

import uk.gov.pay.commons.model.SupportedLanguage;
import uk.gov.pay.commons.model.charge.ExternalMetadata;

public class CreateCardPaymentRequestBuilder {
    private ExternalMetadata metadata;
    private int amount;
    private String returnUrl;
    private String reference;
    private String description;
    private String agreementId;
    private SupportedLanguage language;
    private Boolean delayedCapture;
    private String email;
    private String cardholderName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postcode;
    private String country;
    private PrefilledCardholderDetails prefilledCardholderDetails;

    public CreateCardPaymentRequest build() {
        return new CreateCardPaymentRequest(this);
    }

    public static CreateCardPaymentRequestBuilder builder() {
        return new CreateCardPaymentRequestBuilder();
    }

    public CreateCardPaymentRequestBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public CreateCardPaymentRequestBuilder returnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    public CreateCardPaymentRequestBuilder reference(String reference) {
        this.reference = reference;
        return this;
    }

    public CreateCardPaymentRequestBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CreateCardPaymentRequestBuilder mandateId(String agreementId) {
        this.agreementId = agreementId;
        return this;
    }

    public CreateCardPaymentRequestBuilder language(SupportedLanguage language) {
        this.language = language;
        return this;
    }

    public CreateCardPaymentRequestBuilder delayedCapture(Boolean delayedCapture) {
        this.delayedCapture = delayedCapture;
        return this;
    }

    public CreateCardPaymentRequestBuilder metadata(ExternalMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public CreateCardPaymentRequestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public CreateCardPaymentRequestBuilder cardholderName(String cardHolderName) {
        this.cardholderName = cardHolderName;
        return this;
    }

    public CreateCardPaymentRequestBuilder addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public CreateCardPaymentRequestBuilder addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public CreateCardPaymentRequestBuilder city(String city) {
        this.city = city;
        return this;
    }

    public CreateCardPaymentRequestBuilder postcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public CreateCardPaymentRequestBuilder country(String country) {
        this.country = country;
        return this;
    }

    public PrefilledCardholderDetails getPrefilledCardholderDetails() {
        if (cardholderName != null) {
            this.prefilledCardholderDetails = new PrefilledCardholderDetails();
            this.prefilledCardholderDetails.setCardholderName(cardholderName);
        }
        if (addressLine1 != null || addressLine2 != null ||
                postcode != null || city != null || country != null) {
            if (this.prefilledCardholderDetails == null) {
                this.prefilledCardholderDetails = new PrefilledCardholderDetails();
            }
            this.prefilledCardholderDetails.setAddress(addressLine1, addressLine2, postcode, city, country);
        }
        return prefilledCardholderDetails;
    }

    public ExternalMetadata getMetadata() {
        return metadata;
    }

    public int getAmount() {
        return amount;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public String getMandateId() {
        return agreementId;
    }

    public SupportedLanguage getLanguage() {
        return language;
    }

    public Boolean getDelayedCapture() {
        return delayedCapture;
    }

    public String getEmail() {
        return email;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }
}
