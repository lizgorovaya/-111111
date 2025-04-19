package com.example.progsp1.servicies;

import com.example.progsp1.models.Review;
import com.example.progsp1.models.User;
import com.example.progsp1.repositories.ReviewRepository;
import com.example.progsp1.models.Book;
import com.example.progsp1.repositories.BookRepository;
import com.example.progsp1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    public Review createReview(Long bookId, int rating, String comment) {
        // Получаем книгу по её ID
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Создаем новый отзыв
        Review review = new Review();
        review.setBook(book);  // Присваиваем книгу
        review.setRating(rating);
        review.setComment(comment);

        // Сохраняем отзыв в базе данных
        return reviewRepository.save(review);
    }



    // Метод для получения всех отзывов для книги
    public List<Review> getReviewsByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }



    public Map<Book, Integer> getReviewsCount() {
        List<Object[]> results = reviewRepository.findReviewsCountGroupedByBook();
        Map<Book, Integer> reviewsCount = new HashMap<>();
        for (Object[] result : results) {
            Book book = (Book) result[0];
            Integer count = ((Number) result[1]).intValue();
            reviewsCount.put(book, count);
        }
        return reviewsCount;
    }

}
