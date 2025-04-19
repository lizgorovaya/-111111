package com.example.progsp1.servicies;

import com.example.progsp1.models.Book;
import com.example.progsp1.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }



    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }
    public List<Book> findBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public Book createBook(String title, String description, MultipartFile photo) throws IOException {
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);

        if (photo != null && !photo.isEmpty()) {
            book.setPhoto(photo.getBytes());
        }

        return bookRepository.save(book);
    }
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public String getBookCoverAsBase64(Book book) {
        if (book.getPhoto() != null) {
            byte[] photoBytes = book.getPhoto();  // Получаем BLOB-данные
            return Base64.getEncoder().encodeToString(photoBytes);  // Преобразуем в строку Base64
        }
        return null;  // Если фото нет, возвращаем null
    }
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getBooksSortedByTitle() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    public List<Book> getBooksSortedByCreatedDate() {
        return bookRepository.findAllByOrderByCreatedDateAsc();
    }
}
