package com.example.progsp1.repositories;


import com.example.progsp1.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Repository

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorId(Long authorId);

    @Transactional
    void deleteByAuthorId(Long authorId);

    List<Book> findAllByOrderByTitleAsc(); // Сортировка по алфавиту
    List<Book> findAllByOrderByCreatedDateAsc(); // Сортировка по дате

    @Query("""
    SELECT b FROM Book b
    LEFT JOIN Review r ON b.id = r.book.id
    GROUP BY b.id
    ORDER BY COALESCE(AVG(r.rating), 0) DESC, MAX(r.createdDate) DESC
""")
    List<Book> findTop10ByRatingAndRecentComments();

    List<Book> findByAuthor_FullNameIgnoreCase(String fullName);




}
