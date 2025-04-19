package com.example.progsp1.repositories;

import com.example.progsp1.models.Book;
import com.example.progsp1.models.Review;
import com.example.progsp1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Метод для получения всех отзывов по книге
    List<Review> findByBookId(Long bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(Long bookId);

    // Получаем дату последнего комментария для книги
    @Query("SELECT MAX(r.createdDate) FROM Review r WHERE r.book.id = :bookId")
    LocalDate findLastCommentDateByBookId(Long bookId);

    @Query("SELECT r.book AS book, COUNT(r) AS count FROM Review r GROUP BY r.book")
    List<Object[]> findReviewsCountGroupedByBook();



}
