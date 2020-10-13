package com.example.demo.controllerTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void Setup() {
        itemController = new ItemController();
        TestUtils.InjectObjects(itemController, "itemRepository", itemRepository);
        Item item = new Item();
        item.setId(1L);
        item.setDescription("test Item 1");
        item.setName("T1");
        item.setPrice(BigDecimal.TEN);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("test Item 2");
        item2.setName("T2");
        item2.setPrice(BigDecimal.valueOf(9.98));
        List<Item> itemList = new ArrayList<>();
        itemList.add(item2);
        when(itemRepository.findByName("T2")).thenReturn(itemList);
        List<Item> allItemList = new ArrayList<>();
        allItemList.add(item);
        allItemList.add(item2);
        when(itemRepository.findAll()).thenReturn(allItemList);

    }

    @Test
    public void GetItemById() {
        final ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Item item = responseEntity.getBody();
        assertEquals(1, item.getId().intValue());
        assertEquals("T1", item.getName());
        assertEquals("test Item 1", item.getDescription());
        assertEquals(BigDecimal.TEN, item.getPrice());
    }

    @Test
    public void GetItemByIdNotExist() {
        final ResponseEntity<Item> responseEntity = itemController.getItemById(3L);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void GetItemByName() {
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("T2");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        List<Item> itemL = responseEntity.getBody();
        assertEquals(2, itemL.get(0).getId().intValue());
        assertEquals("T2", itemL.get(0).getName());
        assertEquals("test Item 2", itemL.get(0).getDescription());
        assertEquals(BigDecimal.valueOf(9.98), itemL.get(0).getPrice());
    }

    @Test
    public void GetItemByNameNotPresent() {
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("T5");
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void GetAllItems() {

        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        List<Item> itemL = responseEntity.getBody();
        assertEquals(2, itemL.size());
    }

}
