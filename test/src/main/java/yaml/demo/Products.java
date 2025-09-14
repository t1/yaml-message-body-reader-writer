package yaml.demo;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;

@Path("/products")
@Slf4j
public class Products {
    private static final Product TABLE = Product.builder().id(1).name("Table").price(123).build();

    public @GET @Path("/{id}") Product get(@PathParam("id") long id) {
        if (id != 1)
            throw new BadRequestException("only id=1 supported, but got " + id);
        return TABLE;
    }

    public @PUT void get(Product product) {
        if (!TABLE.equals(product))
            throw new BadRequestException("expected " + TABLE + " but got " + product);
        log.info("got product: {}", product);
    }
}
