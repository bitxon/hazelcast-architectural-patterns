package bitxon.hz.model.objectmapper;

import lombok.Builder;

@Builder
public record Address(
    String line1,
    String city,
    String state,
    Integer zip) {
}
