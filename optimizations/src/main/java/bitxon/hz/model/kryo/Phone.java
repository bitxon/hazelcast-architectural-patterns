package bitxon.hz.model.kryo;

import lombok.Builder;

@Builder
public record Phone(
    PhoneType type,
    String number) {
}
