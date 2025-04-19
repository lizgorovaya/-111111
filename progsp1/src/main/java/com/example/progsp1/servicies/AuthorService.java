package com.example.progsp1.servicies;

import com.example.progsp1.models.Author;
import com.example.progsp1.repositories.AuthorRepository;
import com.example.progsp1.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    // Оставляем только один конструктор, который инициализирует оба репозитория
    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void deleteAuthorById(Long authorId) {
        // Удаляем книги, связанные с автором
        bookRepository.deleteByAuthorId(authorId);

        // Удаляем автора
        authorRepository.deleteById(authorId);
    }

    // Создание или обновление автора
    public Author saveAuthor(String fullName, String bio, MultipartFile photo) throws IOException {
        Author author = new Author();
        author.setFullName(fullName);
        author.setBio(bio);

        if (photo != null && !photo.isEmpty()) {
            author.setPhoto(photo.getBytes()); // Конвертируем файл в байтовый массив
        }

        return authorRepository.save(author);
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id); // Возвращает Optional, чтобы обработать случай отсутствия автора
    }

    // Получение всех авторов
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }
}
