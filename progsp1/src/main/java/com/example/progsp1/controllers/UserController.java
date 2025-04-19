package com.example.progsp1.controllers;

import com.example.progsp1.repositories.UserRepository;
import com.example.progsp1.models.User; // Импорт вашего Entity-класса
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Указываем источник вашего фронтенда
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("Users: " + users);  // Добавьте вывод в лог для проверки
        return ResponseEntity.ok()
                .body(users);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusUpdateRequest statusUpdate) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setActive(statusUpdate.isActive());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    public static class UserStatusUpdateRequest {
        private boolean active;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}

