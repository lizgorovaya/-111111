package com.example.progsp1.servicies;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthLogService {
    private static final String LOG_FILE = "auth_attempts.log";

    public String getLastAuthenticatedUsername() {
        try {
            List<String> lines = Files.readAllLines(Path.of(LOG_FILE));
            if (lines.isEmpty()) {
                return null;  // Нет данных о пользователях
            }
            // Извлекаем имя последнего пользователя из последней строки лога
            String lastLog = lines.get(lines.size() - 1);
            // Пример строки: [2024-12-04T23:55:55] Username: user1, Success: YES
            String[] parts = lastLog.split(", ");
            if (parts.length > 0) {
                String usernamePart = parts[0];  // Пример: Username: user1
                System.out.println(usernamePart);
                return usernamePart.split(":")[1].trim();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения лог-файла", e);
        }
        return null;
    }

    // Метод для записи попытки авторизации в лог
    public void logAuthAttempt(String username, boolean success) {
        String logEntry = String.format(
                "[%s] Username: %s, Success: %s%n",
                LocalDateTime.now(),
                username,
                success ? "YES" : "NO"
        );

        try {
            Files.writeString(
                    Path.of(LOG_FILE),
                    logEntry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи в лог-файл", e);
        }
    }

}
