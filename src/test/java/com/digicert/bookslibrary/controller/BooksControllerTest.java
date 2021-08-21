package com.digicert.bookslibrary.controller;

import com.digicert.bookslibrary.entity.BooksEntity;
import com.digicert.bookslibrary.repository.BooksRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@AutoConfigureMockMvc
public class BooksControllerTest {

    @MockBean
    BooksRepository booksRepository;

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAllBooksNoBooksFound() throws Exception {
        Mockito.when(booksRepository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(mockMvc.perform(get("/books")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "No Records Found");
    }

    @Test
    void testGetAllBooksSuccess() throws Exception {
        Mockito.when(booksRepository.findAll()).thenReturn(getTestData());
        List<BooksEntity> actualBooks = objectMapper.readValue(mockMvc.perform(get("/books")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                new TypeReference<List<BooksEntity>>() {});
        assertEquals(getTestData().size(), actualBooks.size());
    }

    @Test
    void testGetSingleBookNotFound() throws Exception{
        Mockito.when(booksRepository.findById(1)).thenReturn(Optional.empty());
        mockMvc.perform(get("/books/1")).andExpect(status().isNotFound());
    }

    @Test
    void testGetSingleBookSuccess() throws Exception{
        List<BooksEntity> books = getTestData();
        BooksEntity booksEntity = books.get(ThreadLocalRandom.current().nextInt(0, books.size()));
        booksEntity.setBookId(1);
        Mockito.when(booksRepository.findById(booksEntity.getBookId())).thenReturn(Optional.of(booksEntity));
        BooksEntity actual = objectMapper.readValue(mockMvc.perform(get("/books/1")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), BooksEntity.class);
        assertEquals(booksEntity.getBookId(), actual.getBookId());
        assertEquals(booksEntity.getBookGenre(), actual.getBookGenre());
    }

    @Test
    void testAddBookBadRequest() throws Exception {
        mockMvc.perform(post("/books")).andExpect(status().isBadRequest());
    }

    @Test
    void testAddBookSuccess() throws Exception{
        List<BooksEntity> testData = getTestData();
        BooksEntity expected = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size()));
        expected.setBookId(ThreadLocalRandom.current().nextInt());
        Mockito.when(booksRepository.save(expected)).thenReturn(expected);
        BooksEntity actual = objectMapper.readValue(mockMvc.perform(post("/books").content(objectMapper.writeValueAsString(expected)).contentType("application/json")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), BooksEntity.class);
        assertEquals(actual.getBookId(), expected.getBookId());
        assertEquals(actual.getBookAuthor(), expected.getBookAuthor());
    }

    @Test
    void testUpdateBookSuccess() throws Exception{
        List<BooksEntity> testData = getTestData();
        BooksEntity expected = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size()));
        expected.setBookId(ThreadLocalRandom.current().nextInt());
        expected.setBookName("ModifiedName");
        Mockito.when(booksRepository.findById(expected.getBookId())).thenReturn(Optional.of(expected));
        Mockito.when(booksRepository.save(expected)).thenReturn(expected);
        BooksEntity actual = objectMapper.readValue(mockMvc.perform(put("/books").content(objectMapper.writeValueAsString(expected)).contentType("application/json")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), BooksEntity.class);
        assertEquals(actual.getBookId(), expected.getBookId());
        assertEquals(actual.getBookAuthor(), expected.getBookAuthor());
    }

    @Test
    void testUpdateBookNotExists() throws Exception{
        BooksEntity testBook = getTestData().get(0);
        testBook.setBookId(1);
        mockMvc.perform(put("/books").content(objectMapper.writeValueAsString(testBook)).contentType("application/json")).andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBookNotExists() throws Exception{
        BooksEntity testBook = getTestData().get(0);
        testBook.setBookId(1);
        Mockito.when(booksRepository.findById(1)).thenReturn(Optional.empty());
        mockMvc.perform(put("/delete/1")).andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBookSuccess() throws Exception{
        BooksEntity testBook = getTestData().get(0);
        testBook.setBookId(1);
        Mockito.when(booksRepository.findById(1)).thenReturn(Optional.of(testBook));
        mockMvc.perform(delete("/books/1")).andExpect(status().isNoContent());

    }

    List<BooksEntity> getTestData(){
        BooksEntity booksEntity1 = new BooksEntity("Book1","Author1","Genre1");
        BooksEntity booksEntity2 = new BooksEntity("Book2","Author2","Genre2");
        BooksEntity booksEntity3 = new BooksEntity("Book3","Author3","Genre3");
        BooksEntity booksEntity4 = new BooksEntity("Book4","Author4","Genre4");
        BooksEntity booksEntity5 = new BooksEntity("Book5","Author5","Genre5");
        return Arrays.asList(booksEntity1, booksEntity2, booksEntity3, booksEntity4, booksEntity5);
    }
}
