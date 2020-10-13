package com.example.demo.controllerTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder pCrypt = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.InjectObjects(userController, "userRepository", userRepository);
        TestUtils.InjectObjects(userController, "cartRepository", cartRepository);
        TestUtils.InjectObjects(userController, "bCryptPasswordEncoder", pCrypt );

        User user = new User();
        user.setId(1);
        user.setUsername("TestUserName");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByUsername("TestUserName")).thenReturn(user);
    }

    @Test
    public void createUserHappyPath() {
        when(pCrypt.encode("matchingpassword")).thenReturn("hashedPassword");
        CreateUserRequest cr = new CreateUserRequest();
        cr.setUsername("TestCase1");
        cr.setPassword("matchingpassword");
        cr.setConfirmPassword("matchingpassword");
        final ResponseEntity<User> responseEntity = userController.createUser(cr);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertEquals(cr.getUsername(), user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void passwordNotMatching() {
        when(pCrypt.encode("ThePassword1")).thenReturn("hashedPassword");
        CreateUserRequest cr = new CreateUserRequest();
        cr.setUsername("TestCase1");
        cr.setPassword("matchingpassword");
        cr.setConfirmPassword("notmatchingpassword");
        final ResponseEntity<User> responseEntity = userController.createUser(cr);
        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void passwordNot7Bytes() {
        CreateUserRequest cr = new CreateUserRequest();
        cr.setUsername("TestCase1");
        cr.setPassword("123456");
        cr.setConfirmPassword("123456");
        final ResponseEntity<User> responseEntity = userController.createUser(cr);
        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getUserByID() {
        final ResponseEntity<User> responseEntity = userController.findById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        User user = responseEntity.getBody();
        assert user != null;
        assertEquals(1L, user.getId());
    }

    @Test
    public void getUserByName() {
        final ResponseEntity<User> responseEntity = userController.findByUserName("TestUserName");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        User user = responseEntity.getBody();
        assert user != null;
        assertEquals("TestUserName", user.getUsername());
    }
}
