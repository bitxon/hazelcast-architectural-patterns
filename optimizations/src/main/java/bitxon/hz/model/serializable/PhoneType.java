package bitxon.hz.model.serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PhoneType {
    @JsonProperty("home")
    HOME,
    @JsonProperty("work")
    WORK
}
