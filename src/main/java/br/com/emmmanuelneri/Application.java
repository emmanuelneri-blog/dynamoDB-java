package br.com.emmmanuelneri;

import br.com.emmmanuelneri.infra.ProductRepository;
import br.com.emmmanuelneri.model.Product;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Application {

    public static void main(final String[] args) {
        final DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();

        final ProductRepository productRepository = new ProductRepository(client);

        final Product product = new Product("111", "Product 999", null);
        product.setName("Product Updated");
        productRepository.save(product);

        product.setDescription("Product description");
        productRepository.save(product);

        productRepository.save(new Product("2222", "Product 2", "Product 2"));
        productRepository.save(new Product("3333", "Product 3", "Product 3"));

        listProducts(productRepository);

        client.close();
    }

    private static void listProducts(final ProductRepository productRepository) {
        final CompletableFuture<List<Product>> productFuture = new CompletableFuture<>();
        productRepository.list(productFuture);

        System.out.println("--------- All Products ---------");
        try {
            final List<Product> products = productFuture.get();
            products.forEach(System.out::println);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
