package com.digicert.bookslibrary.repository;


import com.digicert.bookslibrary.entity.BooksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<BooksEntity, Integer> {
}
