package bitxon.hz.model.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PhoneType {
    @JsonProperty("home")
    HOME,
    @JsonProperty("work")
    WORK
}
