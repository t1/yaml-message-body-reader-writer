package test;

import com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import yaml.demo.Product;

import java.net.URI;

import static com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter.APPLICATION_YAML;
import static org.assertj.core.api.BDDAssertions.then;

class ProductsIT {
    public static final Product PRODUCT = Product.builder().id(1).name("Table").price(123).build();
    private static final String PRODUCT_YAML = """
            id: 1
            name: Table
            price: 123
            """;

    @Path("/products")
    public interface ProductsApi {
        @Produces(APPLICATION_YAML)
        @GET @Path("/{id}") String getString(@PathParam("id") long id);

        @Consumes(APPLICATION_YAML)
        @PUT void putString(String product);

        @Produces(APPLICATION_YAML)
        @GET @Path("/{id}") Product get(@PathParam("id") long id);

        @Consumes(APPLICATION_YAML)
        @PUT void put(Product product);
    }

    URI baseUri = URI.create("http://localhost:8080");
    ProductsApi products = api();

    private ProductsApi api() {
        var rw = new YamlMessageBodyReaderWriter<>();
        return RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .register(rw)
                .build(ProductsApi.class);
    }

    @Test void shouldGetProductString() {
        var product = products.getString(1);

        then(product).isEqualTo(PRODUCT_YAML);
    }

    @Test void shouldPutProductString() {
        products.putString(PRODUCT_YAML);
    }

    @Test void shouldGetProduct() {
        var product = products.get(1);

        then(product).isEqualTo(PRODUCT);
    }

    @Test void shouldPutProduct() {
        products.put(PRODUCT);
    }
}
