package bitxon.hz.model.kryo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Builder;

@Builder
public record User(
    Long id,
    String name,
    LocalDate dateOfBirth,
    LocalDateTime lastUpdated,
    String[] strings,
    List<Address> addresses,
    Set<Phone> phones) {
}
