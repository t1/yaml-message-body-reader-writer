package test;

import com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.github.t1.ThreadLocalConfigSource;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter.APPLICATION_YAML_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.BDDAssertions.then;

class RecursiveNestingTest {
    private static final String PRODUCT_YAML = """
            components:
            - components:
              - name: Foot
                price: 60
              - name: Knee
                price: 40
              name: Leg
              price: 100
            - name: Top
              price: 1000
            name: Table
            price: 1234
            """;
    private static final Product PRODUCT = Product.builder().name("Table").price(1234)
            .components(List.of(
                    Product.builder().name("Leg").price(100).components(List.of(
                            Product.builder().name("Foot").price(60).build(),
                            Product.builder().name("Knee").price(40).build())).build(),
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
        var productReaderWriter = readerWriter(type);
        return productReaderWriter.readFrom(type, type, null, APPLICATION_YAML_TYPE, null, reader);
    }

    private static <T> YamlMessageBodyReaderWriter<T> readerWriter(@SuppressWarnings("unused") Class<T> type) {
        return new YamlMessageBodyReaderWriter<>();
    }

    @SuppressWarnings("SameParameterValue")
    private <T> String write(T o) {
        var productReaderWriter = readerWriter(null);
        var writer = new java.io.ByteArrayOutputStream();
        productReaderWriter.writeTo(o, o.getClass(), o.getClass(), null, APPLICATION_YAML_TYPE, null, writer);
        return writer.toString(UTF_8);
    }

    @Test void shouldNotBeUnsafeReadableByDefault() {
        var readable = readerWriter(Product.class)
                .isReadable(Product.class, null, null, APPLICATION_YAML_TYPE);

        then(readable).isFalse();
    }

    @Test void shouldConfigureUnsafeReadable() {
        ThreadLocalConfigSource.with("yaml.unsafe-read.enabled", "true", () -> {
            var readerWriter = readerWriter(Product.class);

            var readable = readerWriter.isReadable(Product.class, null, null, APPLICATION_YAML_TYPE);

            then(readable).isTrue();
        });
    }

    @Test void shouldReadProduct() {
        var product = read(PRODUCT_YAML, Product.class);

        then(product).isEqualTo(PRODUCT);
    }

    @Test void shouldWriteProduct() {
        var yaml = write(PRODUCT);

        then(yaml).isEqualTo(PRODUCT_YAML);
    }
}
