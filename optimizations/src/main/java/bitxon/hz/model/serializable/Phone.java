package bitxon.hz.model.serializable;


import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;

@Builder
public record Phone(
    PhoneType type,
    String number) implements Serializable {
    @Serial
    private static final long serialVersionUID = -8631868631749772416L;
}
