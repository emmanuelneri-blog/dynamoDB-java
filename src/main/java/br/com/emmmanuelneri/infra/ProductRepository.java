package br.com.emmmanuelneri.infra;

import br.com.emmmanuelneri.model.Product;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ProductRepository {

    private static final String TABLE_NAME = "Product";

    private final DynamoDbAsyncClient client;

    public void save(final Product product) {
        final Map<String, AttributeValue> map = new HashMap<>();
        map.put("code", AttributeValue.builder().s(product.getCode()).build());
        map.put("name", AttributeValue.builder().s(product.getName()).build());

        if (product.getDescription() != null) {
            map.put("description", AttributeValue.builder().s(product.getDescription()).build());
        }

        final CompletableFuture<PutItemResponse> completableFuture = client.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(map)
                .build());

        completableFuture.join();
    }

    public void list(final CompletableFuture<List<Product>> productFuture) {
        final CompletableFuture<ScanResponse> scanFuture = client.scan(ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build());

        scanFuture.whenCompleteAsync((scanResponse, throwable) -> {
            if (throwable != null) {
                productFuture.completeExceptionally(throwable);
            }
            try {
                final List<Product> products = scanResponse.items().stream()
                        .map(this::toProduct)
                        .collect(Collectors.toList());

                productFuture.complete(products);
            } catch (Exception ex) {
                productFuture.completeExceptionally(ex);
            }
        });

        scanFuture.join();
    }

    private Product toProduct(final Map<String, AttributeValue> item) {
        final String code = item.get("code").s();
        final String name = item.get("name").s();

        final AttributeValue descriptionAttribute = item.get("description");
        final String description = descriptionAttribute != null ? descriptionAttribute.s() : null;
        return new Product(code, name, description);
    }

}
