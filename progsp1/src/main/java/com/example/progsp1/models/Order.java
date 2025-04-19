package com.example.progsp1.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    public Order(Order other) {
        this.id = other.getId();
        this.customerName = other.getCustomerName();
        this.customerAddress = other.getCustomerAddress();
        this.latitude = other.getLatitude();
        this.longitude = other.getLongitude();
        this.deliveryDate = other.getDeliveryDate();
        this.paymentMethod = other.getPaymentMethod();
        this.book = other.getBook();
        this.user = other.getUser();
    }


    private String customerName;
    private String customerAddress;

    @ManyToOne
    @JoinColumn(name = "book_id") // внешний ключ для книги
    private Book book; // Здесь только один объект книги

    private LocalDateTime orderDateTime;

    private LocalDate deliveryDate;
    private String paymentMethod;

    // Новые поля для координат
    private Double latitude; // Широта
    private Double longitude; // Долгота

    @ManyToOne
    @JoinColumn(name = "user_name") // внешний ключ для пользователя
    private User user; // Связь с пользователем



    public Order() {
        this.orderDateTime = LocalDateTime.now(); // Устанавливается текущее время
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Геттеры и сеттеры для координат
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
