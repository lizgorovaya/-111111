package com.example.progsp1.controllers;

import com.example.progsp1.models.User; // Импорт вашего Entity-класса
import com.example.progsp1.models.enums.Role;
import com.example.progsp1.repositories.UserRepository;
import com.example.progsp1.servicies.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.progsp1.servicies.AuthLogService;

import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthLogService authLogService;
    private final UserService userService;

    private static final String FILE_PATH = "last_user.txt";


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким логином уже существует");
        }
        user.getRoles().add(Role.ROLE_USER);
        user.setActive(true);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }


    // Метод для обновления информации о последнем авторизованном пользователе
    public void updateLastAuthenticatedUser(Long userId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Записываем ID пользователя в файл, перезаписывая его каждый раз
            writer.write(userId.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка записи в файл.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest) {
        // Проверяем, существует ли пользователь с таким именем
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            // Логируем отсутствие пользователя
            System.out.println("Попытка авторизации: Пользователь " + loginRequest.getUsername() + " не найден.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Пользователь не найден"));
        }

        User user = userOptional.get();

        // Проверяем, активен ли пользователь
        if (!user.isActive()) {
            // Логируем попытку авторизации заблокированного пользователя
            System.out.println("Попытка авторизации: Пользователь " + user.getUsername() + " заблокирован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Доступ к системе заблокирован"));
        }

        // Проверяем пароль
        boolean passwordMatches = new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword());
        if (!passwordMatches) {
            // Логируем неверный пароль
            System.out.println("Попытка авторизации: Неверный пароль для пользователя " + user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Неверный пароль"));
        }

        // Записываем ID пользователя в файл (для трекинга последнего авторизованного пользователя)
        updateLastAuthenticatedUser(user.getId());

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Авторизация успешна");
        response.put("role", user.getRoles()); // Если несколько ролей, возвращаем их список
        response.put("username", user.getUsername()); // Возвращаем имя пользователя
        response.put("id", user.getId()); // Возвращаем ID пользователя для удобства работы с фронтом

        // Логируем успешную авторизацию
        System.out.println("Пользователь " + user.getUsername() + " успешно авторизован.");

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            clearLastAuthenticatedUser(); // Очистка данных в файле
            return ResponseEntity.ok("Данные о пользователе очищены");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка очистки данных о пользователе");
        }
    }

    // Метод для очистки файла
    public void clearLastAuthenticatedUser() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(""); // Очищаем файл, записывая пустую строку
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка очистки файла.");
        }
    }


}
