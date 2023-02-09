package bitxon.hz.serializer;

import java.io.IOException;

import bitxon.hz.model.objectmapper.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class UserObjectMapperStreamSerializer implements StreamSerializer<User> {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = ThreadLocal
        .withInitial(() -> new ObjectMapper().findAndRegisterModules());


    @Override
    public void write(ObjectDataOutput out, User object) throws IOException {
        var bytes = OBJECT_MAPPER.get().writeValueAsBytes(object);
        out.write(bytes);
    }

    @Override
    public User read(ObjectDataInput in) throws IOException {
        return OBJECT_MAPPER.get().readValue(in, User.class);
    }

    @Override
    public int getTypeId() {
        return 3;
    }
}
