package com.example.demo.controllerTests;


import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.InjectObjects(cartController, "userRepository", userRepository);
        TestUtils.InjectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.InjectObjects(cartController, "itemRepository", itemRepository);

        Cart cart = new Cart();
        cart.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setUsername("cartTest");
        user.setPassword("cartpassword");
        user.setCart(cart);


        when(userRepository.findByUsername("cartTest")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description of Item1");
        item.setPrice(BigDecimal.valueOf(99.99));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void testAddToCart(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1L);
        cartRequest.setUsername("cartTest");
        cartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(cartRequest);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(BigDecimal.valueOf(199.98), cart.getTotal());
    }

    @Test
    public void testRemoveFromCart(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1L);
        cartRequest.setUsername("cartTest");
        cartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(cartRequest);
        assertEquals(200, response.getStatusCodeValue());

        ModifyCartRequest cartRequest1 = new ModifyCartRequest();
        cartRequest1.setItemId(1L);
        cartRequest1.setUsername("cartTest");
        cartRequest1.setQuantity(1);

        response = cartController.removeFromCart(cartRequest1);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(BigDecimal.valueOf(99.99), cart.getTotal());
        assertEquals(1, cart.getId().intValue());
    }

    @Test
    public void testInvalidCartUser(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1L);
        cartRequest.setUsername("userNotExist");
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addToCart(cartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testInvalidRemoveCartUser(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1L);
        cartRequest.setUsername("userNotExist");
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromCart(cartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testInvalidRemoveCartItem(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(9L);
        cartRequest.setUsername("cartTest");
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromCart(cartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testIRemoveCartNullItem(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        User noCartuser = new User();
        noCartuser.setUsername("noCartUser");
        Cart noItemCart = new Cart();
        noItemCart.setId(2L);
        noCartuser.setCart(noItemCart);
        when(userRepository.findByUsername("noCartUser")).thenReturn(noCartuser);
        cartRequest.setItemId(1L);
        cartRequest.setUsername("noCartUser");
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromCart(cartRequest);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testInvalidItem(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(3L);
        cartRequest.setUsername("cartTest");
        cartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(cartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }
}
