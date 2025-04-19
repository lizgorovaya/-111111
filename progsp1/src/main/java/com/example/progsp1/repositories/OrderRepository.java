package com.example.progsp1.repositories;

import com.example.progsp1.models.Order;
import com.example.progsp1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Дополнительные методы для поиска заказов можно добавить здесь, если нужно
    List<Order> findByUser(User user);


    @Query("SELECT o.book AS book, COUNT(o) AS count FROM Order o GROUP BY o.book")
    List<Object[]> findOrdersCountGroupedByBook();
}

