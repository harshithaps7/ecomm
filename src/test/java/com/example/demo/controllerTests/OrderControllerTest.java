package com.example.demo.controllerTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);


    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.InjectObjects(orderController, "userRepository", userRepository);
        TestUtils.InjectObjects(orderController, "orderRepository", orderRepository);

        User user = new User();
        user.setId(1L);
        user.setUsername("orderUser");
        user.setPassword("orderPassword");

        Item item = new Item();
        item.setId(1L);
        item.setName("T1");
        item.setPrice(BigDecimal.valueOf(1.23));
        item.setDescription("Item description T1");

        Item item2 = new Item();
        item.setId(2L);
        item.setName("T2");
        item.setPrice(BigDecimal.valueOf(2.23));
        item.setDescription("Item description T2");

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemList.add(item2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(itemList);
        cart.setUser(user);
        cart.setTotal(BigDecimal.valueOf(3.46));
        user.setCart(cart);
        when(userRepository.findByUsername("orderUser")).thenReturn(user);
        UserOrder userOrder = new UserOrder();
        userOrder.setItems(itemList);
        userOrder.setTotal(BigDecimal.valueOf(1.23));
        userOrder.setId(1L);
        userOrder.setUser(user);
        UserOrder userOrder2 = new UserOrder();
        userOrder2.setItems(itemList);
        userOrder2.setTotal(BigDecimal.valueOf(1.23));
        userOrder2.setId(2L);
        userOrder2.setUser(user);
        List<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(userOrder);
        userOrderList.add(userOrder2);
        when(orderRepository.findByUser(user)).thenReturn(userOrderList);

    }

    @Test
    public void testSubmitOrder(){
        ResponseEntity<UserOrder> responseEntity = orderController.submit("orderUser");
        assertEquals(200, responseEntity.getStatusCodeValue());

        UserOrder order = responseEntity.getBody();
        assertEquals(2, order.getItems().size());
        assertEquals(BigDecimal.valueOf(3.46), order.getTotal());
    }

    @Test
    public void testUserNotFound(){
        ResponseEntity<UserOrder> responseEntity = orderController.submit("nonExistentUser");
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testOrderHistory() {
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("orderUser");
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity);
        List<UserOrder> uo = responseEntity.getBody();
        assertEquals(2, uo.size());
        UserOrder uo1 = uo.get(0);
        assertEquals(uo1.getId().intValue(), 1);
        assertEquals(uo1.getUser().getUsername(), "orderUser");

    }

    @Test
    public void testOrderHistoryNoUser() {
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("orderUser1");
        assertEquals(404, responseEntity.getStatusCodeValue());

    }
}
