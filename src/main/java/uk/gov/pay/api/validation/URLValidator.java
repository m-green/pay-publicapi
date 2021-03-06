package uk.gov.pay.api.validation;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.DomainValidator.ArrayType;
import org.apache.commons.validator.routines.UrlValidator;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_2_SLASHES;
import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

public enum URLValidator {
    SECURITY_ENABLED {
        private final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"https"}, ALLOW_LOCAL_URLS + ALLOW_2_SLASHES);

        @Override
        public boolean isValid(String value) {
            return URL_VALIDATOR.isValid(value);
        }

    }, 
    SECURITY_DISABLED {
        private final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"}, ALLOW_LOCAL_URLS + ALLOW_2_SLASHES);

        @Override
        public boolean isValid(String value) {
            return URL_VALIDATOR.isValid(value);
        }
    };

    static {
        String[] otherValidTlds = new String[]{"internal", "local"};
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, otherValidTlds);
    }

    public static URLValidator urlValidatorValueOf(boolean isDisabledSecureConnection) {
        return isDisabledSecureConnection ? SECURITY_DISABLED : SECURITY_ENABLED;
    }

    public abstract boolean isValid(String value);
}
