package com.example.progsp1.controllers;

import com.example.progsp1.models.Book;
import com.example.progsp1.servicies.BookService;
import com.example.progsp1.servicies.OrderService;
import com.example.progsp1.servicies.PdfReportService;
import com.example.progsp1.servicies.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final BookService bookService;
    private final PdfReportService pdfReportService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    // Конструктор для внедрения зависимостей
    public ReportController(BookService bookService, PdfReportService pdfReportService,
                            OrderService orderService, ReviewService reviewService) {
        this.bookService = bookService;
        this.pdfReportService = pdfReportService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    // Генерация PDF-отчета с диаграммами для заказов и отзывов
    @GetMapping("/generate-report")
    public String generateReport() throws IOException {
        // Получаем данные для заказов и отзывов через сервисы
        Map<Book, Integer> orderCounts = orderService.getOrdersCount();
        Map<Book, Integer> reviewCounts = reviewService.getReviewsCount();

        // Генерируем отчет в формате PDF
        pdfReportService.generateBarChartPdfReport(orderCounts, reviewCounts);
        return "Report generated successfully";
    }

    @GetMapping("/bar-chart-pdf")
    public ResponseEntity<byte[]> getBarChartPdf() throws IOException {
        // Формируем данные для заказов и отзывов
        Map<Book, Integer> orderCounts = orderService.getOrdersCount();
        Map<Book, Integer> reviewCounts = reviewService.getReviewsCount();

        // Генерируем PDF с диаграммой
        byte[] pdf = pdfReportService.generateBarChartPdfReport(orderCounts, reviewCounts);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=book_orders_reviews_report.pdf")
                .body(pdf);
    }


    // Получение круговой диаграммы для просмотров книг
    @GetMapping("/chart")
    public ResponseEntity<byte[]> getBookViewsChart() throws IOException {
        // Формируем данные для круговой диаграммы: название книги и количество просмотров
        Map<String, Integer> bookViews = bookService.getAllBooks().stream()
                .collect(Collectors.toMap(Book::getTitle, Book::getViews));

        // Генерация диаграммы
        byte[] chart = pdfReportService.generatePdfReport(bookViews);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)  // Тип контента для изображения
                .body(chart);
    }

    // Получение PDF-отчета с диаграммой
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getBookViewsPdf() throws IOException {
        // Формируем данные для PDF: название книги и количество просмотров
        Map<String, Integer> bookViews = bookService.getAllBooks().stream()
                .collect(Collectors.toMap(Book::getTitle, Book::getViews));

        // Генерация PDF с диаграммой
        byte[] pdf = pdfReportService.generatePdfReport(bookViews);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)  // Тип контента для PDF
                .header("Content-Disposition", "attachment; filename=book_views_report.pdf")
                .body(pdf);
    }
}
