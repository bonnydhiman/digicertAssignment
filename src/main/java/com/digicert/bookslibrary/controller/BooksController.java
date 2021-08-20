package com.digicert.bookslibrary.controller;

import com.digicert.bookslibrary.entity.BooksEntity;
import com.digicert.bookslibrary.repository.BooksRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private BooksRepository booksRepository;

    BooksController(BooksRepository booksRepository){
        this.booksRepository = booksRepository;
    }

    @GetMapping
    public ResponseEntity getAllBooks(){
        List<BooksEntity> books = booksRepository.findAll();
        if(books.isEmpty()) return ResponseEntity.ok("No Records Found");
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity findBookById(@PathVariable("id") Integer bookId){
        BooksEntity book = booksRepository.findById(bookId).orElse(null);
        if(null == book) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity addNewBook(@RequestBody BooksEntity booksEntity){
        booksEntity = booksRepository.save(booksEntity);
        return ResponseEntity.created(URI.create("/books/"+booksEntity.getBookId())).body(booksEntity);
    }

    @PutMapping
    public ResponseEntity updateBook(@RequestBody BooksEntity booksEntity){
        BooksEntity book = booksRepository.findById(booksEntity.getBookId()).orElse(null);
        if(null == book) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(booksRepository.save(booksEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBook(@PathVariable("id") Integer bookId){
        BooksEntity book = booksRepository.findById(bookId).orElse(null);
        if(null == book) return ResponseEntity.notFound().build();
        else booksRepository.delete(book);
        return ResponseEntity.noContent().build();
    }


}
