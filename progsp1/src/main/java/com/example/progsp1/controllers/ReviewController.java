package com.example.progsp1.controllers;

import com.example.progsp1.models.Author;
import com.example.progsp1.models.Book;
import com.example.progsp1.models.Review;
import com.example.progsp1.models.User;
import com.example.progsp1.repositories.AuthorRepository;
import com.example.progsp1.repositories.BookRepository;
import com.example.progsp1.repositories.ReviewRepository;
import com.example.progsp1.servicies.BookService;
import com.example.progsp1.servicies.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookRepository bookRepository;  // Добавьте аннотацию @Autowired

    @Autowired
    public ReviewController(BookService bookService) {
        this.bookService = bookService;
    }

    // Метод для получения всех отзывов по книге
    @GetMapping("/book/{booksId}")
    public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getReviewsByBook(bookId);
        return ResponseEntity.ok(reviews);
    }

    // Метод для получения всех отзывов пользователя


    // Метод для создания нового отзыва
    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@RequestParam Long bookId,
                                               @RequestParam int rating,
                                               @RequestParam String comment) {
        try {
            // Создаем отзыв (без привязки к пользователю)
            Review review = reviewService.createReview(bookId, rating, comment);
            return ResponseEntity.ok(review); // Возвращаем успешный ответ с отзывом
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Ошибка сервера
        }
    }


    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookWithReviews(@PathVariable Long bookId) {
        // Получаем книгу по ID
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Книга не найдена");
        }

        // Получаем книгу
        Book book = bookOptional.get();

        // Преобразуем изображение в base64, если оно есть
        String base64Image = null;
        if (book.getPhoto() != null) {
            base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getPhoto());
        }

        // Получаем автора по author_id из книги
        Optional<Author> authorOptional = authorRepository.findById(book.getAuthor().getId());

        if (authorOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Автор не найден");
        }

        // Получаем список отзывов для этой книги
        List<Review> reviews = reviewRepository.findByBookId(bookId);

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("book", book);
        response.put("author", authorOptional.get());  // Добавляем информацию об авторе
        response.put("reviews", reviews);
        response.put("image", base64Image);  // Добавляем изображение в формате base64

        return ResponseEntity.ok(response);
    }



    @GetMapping("/books/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = bookService.getAllBooks(); // Предположим, что в bookService есть метод getAllBooks()
        return ResponseEntity.ok(books);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations() {
        // Получаем список рекомендаций из репозитория
        List<Book> recommendations = bookRepository.findTop10ByRatingAndRecentComments();

        // Создаём список для хранения результатов
        List<Map<String, Object>> response = new ArrayList<>();

        // Обрабатываем каждую книгу из рекомендаций
        for (Book book : recommendations) {
            Map<String, Object> bookData = new HashMap<>();
            bookData.put("id", book.getId());
            bookData.put("title", book.getTitle());
            bookData.put("description", book.getDescription());
            bookData.put("views", book.getViews());

            // Добавляем средний рейтинг книги
            Double averageRating = reviewRepository.findAverageRatingByBookId(book.getId());
            bookData.put("averageRating", averageRating != null ? averageRating : 0.0);

            // Добавляем дату последнего комментария
            LocalDate lastCommentDate = reviewRepository.findLastCommentDateByBookId(book.getId());
            bookData.put("lastCommentDate", lastCommentDate != null ? lastCommentDate : "No comments");

            // Добавляем картинку книги в формате Base64, если она есть
            if (book.getPhoto() != null) {
                String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getPhoto());
                bookData.put("image", base64Image);  // Добавляем изображение
            } else {
                bookData.put("image", null);  // Если изображения нет, передаем null
            }

            // Добавляем информацию о книге в результат
            response.add(bookData);
        }

        // Возвращаем ResponseEntity с данными
        return ResponseEntity.ok(response);
    }



}
