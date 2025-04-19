package com.example.progsp1.servicies;

import com.example.progsp1.models.Book;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class PdfReportService {

    private final ChartService chartService;

    public PdfReportService(ChartService chartService) {
        this.chartService = chartService;
    }

    // Генерация PDF с круговой диаграммой
    public byte[] generatePdfReport(Map<String, Integer> data) throws IOException {
        // Генерация круговой диаграммы
        byte[] chartImage = chartService.generatePieChart(data);

        // Создание PDF-документа
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Добавление заголовка
            document.add(new Paragraph("Artwork Views Report").setBold().setFontSize(18));

            // Добавление изображения диаграммы
            ImageData imageData = ImageDataFactory.create(chartImage);
            Image chart = new Image(imageData).setAutoScale(true);
            document.add(chart);

            // Закрытие документа
            document.close();
            return out.toByteArray();
        }
    }

    // Генерация PDF с столбчатой диаграммой (для заказов и отзывов)
    public byte[] generateBarChartPdfReport(Map<Book, Integer> orderCounts, Map<Book, Integer> reviewCounts) throws IOException {
        // Создание набора данных и диаграммы
        DefaultCategoryDataset dataset = createDataset(orderCounts, reviewCounts);
        JFreeChart chart = chartService.createChart(dataset);

        // Сохранение диаграммы в поток
        ByteArrayOutputStream chartStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(chartStream, chart, 800, 600);

        // Генерация PDF-документа
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Добавление текста
            document.add(new Paragraph("Artwork Orders and Reviews Report").setBold().setFontSize(18));

            // Добавление изображения диаграммы
            Image chartImage = new Image(ImageDataFactory.create(chartStream.toByteArray()));
            document.add(chartImage);

            // Закрытие документа
            document.close();
            return out.toByteArray();
        }
    }

    // Создание набора данных для столбчатой диаграммы
    private DefaultCategoryDataset createDataset(Map<Book, Integer> orderCounts, Map<Book, Integer> reviewCounts) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Добавление данных о заказах
        for (Map.Entry<Book, Integer> entry : orderCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Orders", entry.getKey().getTitle());
        }

        // Добавление данных о комментариях
        for (Map.Entry<Book, Integer> entry : reviewCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Reviews", entry.getKey().getTitle());
        }

        return dataset;
    }
}
