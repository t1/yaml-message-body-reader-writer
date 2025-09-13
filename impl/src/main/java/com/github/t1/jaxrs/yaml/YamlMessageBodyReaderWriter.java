package com.github.t1.jaxrs.yaml;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter.APPLICATION_YAML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;

@Provider
@Produces(APPLICATION_YAML)
@Consumes(APPLICATION_YAML)
public class YamlMessageBodyReaderWriter<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {
    public static final String APPLICATION_YAML = "application/yaml";
    public static final MediaType APPLICATION_YAML_TYPE = MediaType.valueOf(APPLICATION_YAML);

    private final DumperOptions options = dumperOptions();
    private final CustomRepresenter representer = new CustomRepresenter(options);
    private final Yaml yaml = new Yaml(representer, options);

    private static DumperOptions dumperOptions() {
        var options = new DumperOptions();
        options.setDefaultFlowStyle(BLOCK);
        options.setAllowReadOnlyProperties(true);
        options.setPrettyFlow(true);
        return options;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        return yaml.loadAs(entityStream, type);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        representer.addClassTag(type, Tag.MAP);
        try {
            yaml.dumpAll(singleton(t).iterator(), new OutputStreamWriter(entityStream, UTF_8));
        } finally {
            representer.removeClassTag(type);
        }
    }

    private static class CustomRepresenter extends Representer {
        public CustomRepresenter(DumperOptions options) {super(options);}

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            if (propertyValue == null) return null; // skip nulls
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }

        public void removeClassTag(Class<?> type) {super.classTags.remove(type);}
    }
}
