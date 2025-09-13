package test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.github.t1.jaxrs.yaml.YamlMessageBodyReaderWriter.APPLICATION_YAML;
import static org.assertj.core.api.BDDAssertions.then;

class ProductsIT {
    @Path("/products")
    public interface ProductsApi {
        @Produces(APPLICATION_YAML)
        @GET @Path("/{id}") String get(@PathParam("id") long id);
    }

    URI baseUri = URI.create("http://localhost:8080");
    ProductsApi products = RestClientBuilder.newBuilder().baseUri(baseUri).build(ProductsApi.class);

    @Test void shouldGetProduct() {
        var product = products.get(1);

        then(product).isEqualTo("""
                id: 1
                name: Table
                price: 123
                """);
    }
}
