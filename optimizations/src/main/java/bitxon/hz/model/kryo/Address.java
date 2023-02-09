package bitxon.hz.model.kryo;

import lombok.Builder;

@Builder
public record Address(
    String line1,
    String city,
    String state,
    Integer zip) {
}
