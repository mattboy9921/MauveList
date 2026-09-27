package net.mattlabs.mauvelist.plugin.communication;

import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueSerializer implements TypeSerializer<ConcurrentLinkedQueue<?>> {

    public static final ConcurrentLinkedQueueSerializer INSTANCE = new ConcurrentLinkedQueueSerializer();

    @Override
    public ConcurrentLinkedQueue<?> deserialize(@NonNull Type type, @NonNull ConfigurationNode source) throws SerializationException {
        // Get queue parameterized type
        if (!(type instanceof ParameterizedType parameterizedType)) {
            throw new SerializationException("ConcurrentLinkedQueue must have an element type");
        }

        Type elementType = parameterizedType.getActualTypeArguments()[0];

        // Deserialize list and convert to queue
        List<?> list = source.getList(TypeToken.get(elementType));

        if (list == null) {
            return new ConcurrentLinkedQueue<>();
        }

        return new ConcurrentLinkedQueue<>(list);
    }

    @Override
    public void serialize(@NonNull Type type, ConcurrentLinkedQueue<?> queue, @NonNull ConfigurationNode target) throws SerializationException {
        // Get queue parameterized type
        if (!(type instanceof ParameterizedType parameterizedType)) {
            throw new SerializationException("ConcurrentLinkedQueue must have an element type");
        }

        Type elementType = parameterizedType.getActualTypeArguments()[0];

        // Convert queue to list and serialize
        serializeQueue(target, elementType, queue);
    }

    @SuppressWarnings("unchecked")
    private <T> void serializeQueue(ConfigurationNode target, Type elementType, Collection<?> queue) throws SerializationException {
        // Makes sure both type token and collection share the same T
        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(elementType);

        Collection<T> typedQueue = (Collection<T>) queue;

        List<T> list = new ArrayList<>(typedQueue);

        target.setList(typeToken, list);
    }
}
