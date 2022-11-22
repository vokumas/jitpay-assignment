package io.vokumas.jitpayassignment.util.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {

    public final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public final String TIMESTAMP_DTO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public final String TIMESTAMP_URL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public final String TIMESTAMP_DEFAULT_REGION = "Europe/Berlin";

}
