package bitxon.hz.model.objectmapper;

import lombok.Builder;

@Builder
public record Phone(
    PhoneType type,
    String number) {
}
