package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.SQLException;
import java.util.List;

import application.model.Product;
import application.repository.ProductRepository;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService() {
        this(new ProductRepository());
    }

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() throws SQLException {
        return productRepository.findAll();
    }

    public List<Product> searchByName(String productName) throws SQLException {
        return productRepository.findByName(productName);
    }

    public List<Product> searchByStatus(String status) throws SQLException {
        return productRepository.findByStatus(status);
    }
}
