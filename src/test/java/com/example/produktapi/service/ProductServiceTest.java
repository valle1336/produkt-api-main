package com.example.produktapi.service;

import com.example.produktapi.exception.BadRequestException;
import com.example.produktapi.exception.EntityNotFoundException;
import com.example.produktapi.model.Product;
import com.example.produktapi.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository; // En fake ProductRepository inte den riktiga.

    @InjectMocks
    private ProductService underTest; // Här ska fake ProductRepository användas.

    @Captor
    ArgumentCaptor<Product> productCaptor; // Fångar argument
    @Captor
    ArgumentCaptor<String> stringCaptor; // Fångar argument

    Product testProduct;

    @BeforeEach
    void setup() {
        testProduct = new Product("Testprodukt", 200.00, "testKategori", "testBeskrivning", "testURL");
        testProduct.setId(1);
    }

    @Test
    void shouldInvokeFindAllMethod() {
        //when
        underTest.getAllProducts();

        //then
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void addProductShouldInvokeSaveMethod() {
        //given
        Product product = new Product("En ny title", 35.0, "category", "description", "enUrlSträngHär");

        //when
        underTest.addProduct(product);
    }


    @Test
    void getAllProducts() {
        //when
        underTest.getAllProducts();

        //then
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAllCategories() {
        //when
        underTest.getAllProducts();

        //then
        verify(repository, times(1)).findAllCategories();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getProductsByCategory() {
        underTest.addProduct(testProduct);

        underTest.getProductsByCategory("category");

        verify(repository, times(1)).findByCategory(stringCaptor.capture());
        verifyNoMoreInteractions(repository);
        assertEquals("my category", stringCaptor.getValue());
        assertFalse(testProduct.getCategory().isEmpty());
    }

    @Test
    void getProductById() {
        //given
        underTest.addProduct(testProduct);

        given(repository.findById(testProduct.getId())).willReturn(Optional.of(testProduct));

        Product testTwo = underTest.getProductById(testProduct.getId());

        assertEquals(testProduct.getId(), testTwo.getId());
        assertEquals(testProduct, testTwo);

    }

    @Test
    void givenNonExistingProductId_whenGetProductById_thenThrowsEntityNotFoundException(){
        // given
        given(repository.findById(1)).willReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> underTest.getProductById(1));

        // then
        assertEquals(String.format("Produkt med id %d hittades inte", 1), exception.getMessage()); // ändra om i syntax
    }

    @Test
    @DisplayName("add-normalflöde")
    void addProductShouldReturnProduct() {
        //given

        //when
        underTest.addProduct(testProduct);

        verify(repository).save(productCaptor.capture()); //Verifierar att vi använt en save funktion i våran addProduct metod.

        //then
        Assertions.assertEquals(testProduct, productCaptor.getValue()); //Jämför det uppfångade värdet (productCaptor.capture) med våran "testProduct" vi la till-
        // på rad 89.
    }

    @Test
    @DisplayName("Add-metod felflöde")
    void addProductShouldThrow() {
        //given
        underTest.addProduct(testProduct);

        //when


        //then - kolla att exception kastas
        Assertions.assertThrows(BadRequestException.class, () -> underTest.addProduct(testProduct));
    }



    @Test
    void updateProduct_success() {
        //given
        BDDMockito.given(repository.findById(1)).willReturn(Optional.of(testProduct));

        Product updatedProduct = new Product("UppdateradProdukt", 200.00, "testKategori", "testBeskrivning", "testURL");

        //when
        underTest.updateProduct(updatedProduct, 1);

        //then
        verify(repository).save(productCaptor.capture());

        Assertions.assertEquals(productCaptor.getValue(), updatedProduct);
    }

    @Test
    void updateProduct_failed() {
        given(repository.findById(1)).willReturn(Optional.empty()); //Kommer vara empty eftersom vi inte har lagt till något

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> underTest.updateProduct(any(), 1));

        assertEquals(String.format("Produkt med id %d hittades inte", 1), exception.getMessage());
    }

    @Test
    void deleteProduct_success() {

            // given
            Product product = new Product("title", 1.0, "aaa", "aaa", "aaa");
            product.setId(1);

            // when
            when(repository.findById(product.getId())).thenReturn(Optional.of(product));
            underTest.deleteProduct(product.getId());

            // then
            verify(repository, times(1)).deleteById(1);
            verifyNoMoreInteractions(repository);
        }
    @Test
    void deleteProduct_notSuccessfull(){

        given(repository.findById(1)).willReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> underTest.deleteProduct(1));

        verify(repository, never()).deleteById(any());
        assertEquals(String.format("Produkt med id %d hittades inte", 1), exception.getMessage()); // fail id 2
    }

}
