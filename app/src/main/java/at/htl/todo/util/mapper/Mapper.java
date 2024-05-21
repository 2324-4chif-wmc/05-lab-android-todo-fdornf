

package at.htl.todo.util.mapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/** A Mapper that maps types to their json representation and back.
 * ... plus a convenient deep-clone function
 * @param <T> the Class that is mapped
 */
public class Mapper<T> {
    private Class<? extends T> clazz;
    private ObjectMapper mapper;

    public Mapper(Class<? extends T> clazz) {
        this.clazz = clazz;
        mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY); // records
    }
    public String toResource(T model) {
        try {
            return mapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public T fromResource(String json) {
        T model = null;
        try {
            model = mapper.readValue(json.getBytes(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
    /** deep clone an object by converting it to its json representation and back.
     *
     * @param thing the thing to clone, unchanged
     * @return the deeply cloned thing
     */
    public T clone(final T thing) {
        return fromResource(toResource(thing));
    }
}

