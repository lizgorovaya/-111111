package com.example.progsp1.controllers;

import com.example.progsp1.models.Author;
import com.example.progsp1.models.Book;
import com.example.progsp1.repositories.AuthorRepository;
import com.example.progsp1.repositories.BookRepository;
import com.example.progsp1.servicies.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/images")
    public ResponseEntity<List<Object>> getBookImages() {
        List<Object> response = bookService.getAllBooks().stream()
                .map(book -> {
                    return new Object() {
                        public final Long id = book.getId();
                        public final String image = book.getPhoto() != null
                                ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getPhoto())
                                : null;
                        public final String title = book.getTitle(); // Добавляем название книги
                    };
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        List<Book> books = bookRepository.findByAuthorId(authorId);
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getBookCover(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);

        if (book == null || book.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(book.getPhoto());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
//        Book book = bookService.getBookById(id);
//        return ResponseEntity.ok(book);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);

        // Если книга не найдена, возвращаем 404
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Увеличиваем количество просмотров
        book.setViews(book.getViews() + 1);
        bookService.saveBook(book); // Сохраняем обновлённое значение просмотров

        // Создаем карту для ответа
        Map<String, Object> response = new HashMap<>();
        response.put("id", book.getId());
        response.put("title", book.getTitle());
        response.put("description", book.getDescription());
        response.put("views", book.getViews()); // Добавляем количество просмотров в ответ

        // Преобразуем фото в base64
        if (book.getPhoto() != null) {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getPhoto());
            response.put("image", base64Image);
        }

        return ResponseEntity.ok(response);
    }



    @PostMapping
    public ResponseEntity<String> createBook(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "authorId", required = false) Long authorId) {

        try {
            // Проверяем, указан ли автор
            Author author = null;
            if (authorId != null) {
                author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Автор с ID " + authorId + " не найден"));
            }

            // Создаем объект книги
            Book book = new Book();
            book.setTitle(title);
            book.setDescription(description);

            // Сохраняем фото, если оно было загружено
            if (photo != null && !photo.isEmpty()) {
                book.setPhoto(photo.getBytes());
            }

            // Устанавливаем автора (если указан)
            book.setAuthor(author);

            // Сохраняем книгу
            bookService.saveBook(book);

            return ResponseEntity.ok("Книга успешно создана!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании книги: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sort/title")
    public List<Book> getBooksSortedByTitle() {
        return bookService.getBooksSortedByTitle();
    }

    // Эндпоинт для сортировки по дате создания
    @GetMapping("/sort/date")
    public List<Book> getBooksSortedByCreatedDate() {
        return bookService.getBooksSortedByCreatedDate();
    }

    @GetMapping("/{id}/image")
    public Map<String, String> getBookImage(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        Map<String, String> response = new HashMap<>();

        if (book.getPhoto() != null) {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getPhoto());
            response.put("image", base64Image);
        }

        return response;
    }

    @GetMapping("/filter")
    public List<Book> filterByAuthorName(@RequestParam String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            // Если автор не указан или пуст, возвращаем все книги
            return bookRepository.findAll();
        }

        // Фильтруем книги по имени автора (игнорируя регистр)
        return bookRepository.findByAuthor_FullNameIgnoreCase(authorName);
    }




}
