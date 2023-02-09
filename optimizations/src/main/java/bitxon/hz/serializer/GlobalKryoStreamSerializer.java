package bitxon.hz.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class GlobalKryoStreamSerializer implements StreamSerializer<Object> {

    private static final ThreadLocal<Kryo> KRYO = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public int getTypeId() {
        return 1;
    }

    public void write(ObjectDataOutput objectDataOutput, Object object) throws IOException {
        var output = new Output((OutputStream) objectDataOutput);
        KRYO.get().writeClassAndObject(output, object);
        output.flush();
    }

    public Object read(ObjectDataInput objectDataInput) throws IOException {
        var in = (InputStream) objectDataInput;
        var input = new Input(in);
        return KRYO.get().readClassAndObject(input);
    }
}
