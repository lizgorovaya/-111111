package com.example.progsp1.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;           // Оценка книги (например, от 1 до 5)

    @Lob
    private String comment;       // Текст отзыва

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;            // Книга, к которой оставляется отзыв



    private LocalDate createdDate;

    public Review() {
        this.createdDate = LocalDate.now(); // Устанавливается текущая дата
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}

