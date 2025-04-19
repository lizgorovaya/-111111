package com.example.progsp1.controllers;

import com.example.progsp1.models.Book;
import com.example.progsp1.models.Order;
import com.example.progsp1.models.User;
import com.example.progsp1.repositories.BookRepository;
import com.example.progsp1.repositories.OrderRepository;
import com.example.progsp1.repositories.UserRepository;
import com.example.progsp1.servicies.AuthLogService;
import com.example.progsp1.servicies.BookService;
import com.example.progsp1.servicies.OrderService;
import com.example.progsp1.servicies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @PostMapping("/create")
    @CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
    public ResponseEntity<Order> createOrder(@RequestBody Order orderRequest) {
        // Получение последнего добавленного пользователя (например, из базы данных)
        User lastUser = userService.getLastUser(); // Добавьте этот метод в ваш UserService

        if (lastUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Если пользователя нет
        }

        // Проверка наличия книги
        if (orderRequest.getBook() == null || orderRequest.getBook().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Получение книги по ID
        Book book = bookService.getBookById(orderRequest.getBook().getId());
        if (book == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Если книга не найдена
        }

        // Создаем заказ
        Order order = new Order();
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerAddress(orderRequest.getCustomerAddress());
        order.setLatitude(orderRequest.getLatitude());
        order.setLongitude(orderRequest.getLongitude());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setBook(book); // Устанавливаем книгу
        order.setUser(lastUser); // Привязываем последнего пользователя

        // Сохраняем заказ
        Order savedOrder = orderService.saveOrder(order);

        // Создаем объект ответа
        Order response = new Order(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/user-orders")
    public ResponseEntity<?> getUserOrders(HttpSession session) {
        // Считываем ID последнего авторизованного пользователя из файла
        Long lastAuthenticatedUserId = getLastAuthenticatedUserFromFile();

        // Получаем пользователя из базы данных по ID
        Optional<User> userOptional = userRepository.findById(lastAuthenticatedUserId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден.");
        }

        User user = userOptional.get();

        // Получаем все заказы пользователя по ID
        List<Order> orders = orderRepository.findByUser(user);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();  // Если заказов нет, возвращаем пустой ответ
        }

        // Создаем список для хранения данных о заказах с названиями книг
        List<Map<String, Object>> enrichedOrders = new ArrayList<>();

        for (Order order : orders) {
            // Получаем ID книги из заказа
            Long bookId = order.getBook().getId();

            // Находим книгу по ID
            Optional<Book> bookOptional = bookRepository.findById(bookId);

            // Формируем карту для хранения информации о заказе
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", order.getId());
            orderData.put("deliveryDate", order.getDeliveryDate());
            orderData.put("customerName", order.getCustomerName());
            orderData.put("customerAddress", order.getCustomerAddress());
            orderData.put("paymentMethod", order.getPaymentMethod());


            // Добавляем название книги, если она найдена
            orderData.put("bookTitle", bookOptional.map(Book::getTitle).orElse("Название книги не найдено"));

            // Добавляем заказ в список
            enrichedOrders.add(orderData);
        }

        // Возвращаем обогащённые данные о заказах
        return ResponseEntity.ok(enrichedOrders);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/all-orders")
    public ResponseEntity<?> getUserOrders() {
        // Получаем все заказы пользователя (без проверки статуса)
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();  // Если заказов нет, возвращаем пустой ответ
        }
        // Создаем список для хранения данных о заказах с названиями книг
        List<Map<String, Object>> enrichedOrder = new ArrayList<>();

        for (Order order : orders) {
            // Получаем ID книги из заказа
            Long bookId = order.getBook().getId();

            // Находим книгу по ID
            Optional<Book> bookOptional = bookRepository.findById(bookId);

            // Формируем карту для хранения информации о заказе
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", order.getId());
            orderData.put("deliveryDate", order.getDeliveryDate());
            orderData.put("customerName", order.getCustomerName());
            orderData.put("customerAddress", order.getCustomerAddress());
            orderData.put("paymentMethod", order.getPaymentMethod());


            // Добавляем название книги, если она найдена
            orderData.put("bookTitle", bookOptional.map(Book::getTitle).orElse("Название книги не найдено"));

            // Добавляем заказ в список
            enrichedOrder.add(orderData);
        }


        // Возвращаем все заказы
        return ResponseEntity.ok(enrichedOrder);
    }



    public Long getLastAuthenticatedUserFromFile() {
        String filePath = "last_user.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String userIdString = reader.readLine();
            return Long.parseLong(userIdString);  // Преобразуем строку в Long
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка чтения файла.");
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        // Проверяем, существует ли заказ
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ не найден.");
        }

        // Удаляем заказ
        orderRepository.deleteById(id);

        return ResponseEntity.ok("Заказ успешно удален.");
    }

    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole(HttpSession session) {
        Long userId = getLastAuthenticatedUserFromFile(); // Логика получения ID пользователя

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден");
        }

        User user = userOptional.get();

        // Создаем Map с ролью
        Map<String, String> response = new HashMap<>();
        response.put("role", user.getRole());

        return ResponseEntity.ok(response); // Возвращаем Map с ролью
    }





}
