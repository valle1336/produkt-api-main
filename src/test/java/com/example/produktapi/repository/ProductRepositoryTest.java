package com.example.produktapi.repository;

import com.example.produktapi.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository underTest;


    @Test
    void whenSearchingForExistingTitle_thenReceiveThatProduct() {
        //given (Här skapar vi en produkt som vi sedan hämtar titeln ifrån!)
        Product product = new Product("Dator", 20000.0, "Elektronik", "Används för att koda i Java", "urlSträng");
        underTest.save(product);

        //when
        Optional<Product> result = underTest.findByTitle(product.getTitle());

        //then
        Assertions.assertAll(
                ()-> assertTrue(result.isPresent()),
                ()-> assertEquals(result.get().getTitle(), "Dator")
        );
    }

    @Test
    void whenSearchingForNonExistingTitle_thenReturnTrue() {
        //Denna gång skapar vi inget eftersom vi vill söka efter något som inte existerar.

        //when
        Optional<Product> result = underTest.findByTitle("Denna titel exsisterar inte!");

        //then
        Assertions.assertAll(
                ()-> assertFalse(result.isPresent()),
                ()-> assertTrue(result.isEmpty()),
                ()-> assertThrows(NoSuchElementException.class, ()-> result.get())
        );
    }


    @Test
    void findAllCategories() {
    }

    //ÄNDRA SYNTAX!!
    @Test
    void findByCategory_givenValidCategory_whenFindByCategory_thenCheckCategoryNotEmptyAndReturnAnSpecificProductInCategory() {
        // given
        String category = "Elektronik";
        Product product = new Product("En dator", 250000.0, category, "bra at ha", "urlTillBild");
        underTest.save(product);

        // when
        List<Product> listProduct = underTest.findByCategory("Elektronik");

        // then
        assertFalse(listProduct.isEmpty());
        assertEquals(category, listProduct.get(0).getCategory());
    }

    @Test
    void findByNonExistingCategory_givenDeleteAll_whenFindByCategory_thenCheckCategoryIsEmpty() {
        // given
        underTest.deleteAll();

        // when
        List<Product> listProduct = underTest.findByCategory("Elektronik");

        // then
        assertTrue(listProduct.isEmpty());
    }
    @Test
    void whenSearchingFindAllCategory_thenReturnAllFourCategories(){
        // when
        List<String> listProduct = underTest.findAllCategories();

        // then
        assertFalse(listProduct.isEmpty());
        assertEquals(listProduct.size(), 4); //
    }

    @Test
    void givenAnListOfCategoriesWithSameCategoriesAsTheRepository_whenFindAllCategories_thenCheckAllFourListCategoriesAndTheyAreSameAsTheRepositoryCategories(){
        List <String> categorys = new ArrayList<>(Arrays.asList("electronics", "jewelery", "men's clothing", "women's clothing")); // Fail electronic
        categorys.stream().distinct().collect(Collectors.toList());

        List<String> listProduct = underTest.findAllCategories();

        assertTrue(listProduct.size() == 4); // Fail annat än 4
        assertEquals(categorys, listProduct); // Jämför Listorna så det ej finns dubbel
    }
}