package bitxon.hz.model.serializable;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;

@Builder
public record Address(
    String line1,
    String city,
    String state,
    Integer zip) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5919515130641758682L;
}
