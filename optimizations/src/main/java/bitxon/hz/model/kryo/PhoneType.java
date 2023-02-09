package bitxon.hz.model.kryo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PhoneType {
    @JsonProperty("home")
    HOME,
    @JsonProperty("work")
    WORK
}
