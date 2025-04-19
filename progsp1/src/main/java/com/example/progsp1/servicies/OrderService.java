package com.example.progsp1.servicies;

import com.example.progsp1.models.Book;
import com.example.progsp1.models.Order;
import com.example.progsp1.models.User;
import com.example.progsp1.repositories.OrderRepository;
import com.example.progsp1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AuthLogService authLogService;

    @Autowired
    private UserRepository userRepository;

    // Метод для оформления заказа
    public Order createOrder(String customerName, String customerAddress, Long bookId) {
        // Получаем книгу по ID
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Книга с ID " + bookId + " не найдена");
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerAddress(customerAddress);
        order.setBook(book); // Устанавливаем одну книгу для заказа

        // Сохраняем заказ в базе данных
        return orderRepository.save(order);
    }


    public Order saveOrder(Order order) {
        // Получаем ID последнего пользователя из файла
        Long userId = getLastUserIdFromFile(); // Изменено на получение ID

        // Если ID пользователя не найден, выбрасываем исключение
        if (userId == null) {
            throw new RuntimeException("Пользователь не найден.");
        }

        // Получаем пользователя из базы данных по ID
        User user = userRepository.findById(userId) // Передаем ID в метод findById
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        // Устанавливаем пользователя в заказ
        order.setUser(user);

        // Сохраняем заказ в базе данных
        return orderRepository.save(order);
    }


    private Long getLastUserIdFromFile() {
        Long id = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("last_user.txt"))) {
            String line = reader.readLine();  // Читаем первую строку, которая будет ID
            if (line != null && !line.isEmpty()) {
                id = Long.parseLong(line);  // Преобразуем строку в Long
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return id;
    }


    public Map<Book, Integer> getOrdersCount() {
        List<Object[]> results = orderRepository.findOrdersCountGroupedByBook();
        Map<Book, Integer> ordersCount = new HashMap<>();
        for (Object[] result : results) {
            Book book = (Book) result[0];
            Integer count = ((Number) result[1]).intValue();
            ordersCount.put(book, count);
        }
        return ordersCount;
    }



}
