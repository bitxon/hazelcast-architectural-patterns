package bitxon.hz.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bitxon.hz.model.kryo.User;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class UserKryoStreamSerializer implements StreamSerializer<User> {

    private static final ThreadLocal<Kryo> KRYO = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public int getTypeId() {
        return 2;
    }

    public void write(ObjectDataOutput objectDataOutput, User object) throws IOException {
        var output = new Output((OutputStream) objectDataOutput);
        KRYO.get().writeObject(output, object);
        output.flush();
    }

    public User read(ObjectDataInput objectDataInput) throws IOException {
        var in = (InputStream) objectDataInput;
        var input = new Input(in);
        return KRYO.get().readObject(input, User.class);
    }
}
