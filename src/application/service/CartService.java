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

import application.model.CartDisplayItem;
import application.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;

    public CartService() {
        this(new CartRepository());
    }

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartDisplayItem> getCartItems(int userId) throws SQLException {
        return cartRepository.findCartItemsByUserId(userId);
    }
}
