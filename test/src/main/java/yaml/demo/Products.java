package yaml.demo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/products")
public class Products {
    public @GET @Path("/{id}") Product get(@PathParam("id") long id) {
        return Product.builder().id(id).name("Table").price(123).build();
    }
}
