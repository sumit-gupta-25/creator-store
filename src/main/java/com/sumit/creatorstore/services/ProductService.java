package com.sumit.creatorstore.services;

import com.sumit.creatorstore.dto.ProductResponse;
import com.sumit.creatorstore.entities.Product;
import com.sumit.creatorstore.exceptions.ResourceNotFoundException;
import com.sumit.creatorstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(Product product) {
        return toProductResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, Product product) {
        Product existingProduct = getProductEntityById(id);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());

        return toProductResponse(productRepository.save(existingProduct));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProduct() {
        return productRepository.findAll()
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return toProductResponse(getProductEntityById(id));
    }

    public void deleteProduct(Long id) {
        Product product = getProductEntityById(id);
        productRepository.delete(product);
    }

    private Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id " + id
                ));
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}
