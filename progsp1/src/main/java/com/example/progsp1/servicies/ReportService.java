package com.example.progsp1.servicies;

import com.example.progsp1.models.Book;
import com.example.progsp1.models.Order;
import com.example.progsp1.models.Review;
import com.example.progsp1.repositories.OrderRepository;
import com.example.progsp1.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Метод для получения количества заказов и комментариев для каждой книги
    public Map<Book, Integer> getOrderCountForBooks() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getBook, Collectors.summingInt(order -> 1)));
    }

    public Map<Book, Integer> getReviewCountForBooks() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .collect(Collectors.groupingBy(Review::getBook, Collectors.summingInt(review -> 1)));
    }

    // Метод для получения самой популярной книги (с максимальным количеством заказов)
    public Book getMostOrderedBook() {
        Map<Book, Integer> orderCounts = getOrderCountForBooks();
        return orderCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Метод для получения самой обсуждаемой книги (с максимальным количеством отзывов)
    public Book getMostReviewedBook() {
        Map<Book, Integer> reviewCounts = getReviewCountForBooks();
        return reviewCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
