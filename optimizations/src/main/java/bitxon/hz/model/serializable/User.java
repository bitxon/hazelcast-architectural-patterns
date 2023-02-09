package bitxon.hz.model.serializable;

import java.io.Serial;
import java.io.Serializable;
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
    Set<Phone> phones) implements Serializable {
    @Serial
    private static final long serialVersionUID = -379590726109814430L;

}
