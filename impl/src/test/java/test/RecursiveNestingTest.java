package test;

import com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter.APPLICATION_YAML_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.BDDAssertions.then;

class RecursiveNestingTest {
    private static final String PRODUCT_YAML = """
            components:
            - name: Leg
              price: 100
            - name: Top
              price: 1000
            name: Table
            price: 1234
            """;
    private static final Product PRODUCT = Product.builder().name("Table").price(1234)
            .components(List.of(
                    Product.builder().name("Leg").price(100).build(),
                    Product.builder().name("Top").price(1000).build()))
            .build();

    @Data @NoArgsConstructor @SuperBuilder
    public static class Product {
        String name;
        int price;

        List<Product> components;
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T read(String yaml, Class<T> type) {
        var reader = new ByteArrayInputStream(yaml.getBytes(UTF_8));
        var productReaderWriter = new YamlMessageBodyReaderWriter<T>();
        return productReaderWriter.readFrom(type, type, null, APPLICATION_YAML_TYPE, null, reader);
    }

    @SuppressWarnings("SameParameterValue")
    private <T> String write(T o) {
        var productReaderWriter = new YamlMessageBodyReaderWriter<T>();
        var writer = new java.io.ByteArrayOutputStream();
        productReaderWriter.writeTo(o, o.getClass(), o.getClass(), null, APPLICATION_YAML_TYPE, null, writer);
        return writer.toString(UTF_8);
    }

    @Deprecated @Test void shouldReadProduct() {
        var product = read(PRODUCT_YAML, Product.class);

        then(product).isEqualTo(PRODUCT);
    }

    @Test void shouldWriteProduct() {
        var yaml = write(PRODUCT);

        then(yaml).isEqualTo(PRODUCT_YAML);
    }
}
