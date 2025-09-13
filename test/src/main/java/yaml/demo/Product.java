package yaml.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder @NoArgsConstructor
public class Product {
    private long id;
    private String name;
    private int price;
}
