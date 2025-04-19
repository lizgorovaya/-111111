package com.example.progsp1.servicies;

import com.example.progsp1.models.Book;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ChartService {

    // Метод для генерации круговой диаграммы в виде байтового массива
    public byte[] generatePieChart(Map<String, Integer> data) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Artwork Views",  // Заголовок диаграммы
                dataset,       // Данные
                true,          // Легенда
                true,          // Подсказки
                false          // URL
        );

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsJPEG(out, chart, 800, 600);  // Экспортируем в JPEG
            return out.toByteArray();
        }
    }

    // Метод для генерации столбчатой диаграммы и получения её как байтового массива
    public byte[] generateBarChart(Map<Book, Integer> orderCounts, Map<Book, Integer> reviewCounts) throws IOException {
        DefaultCategoryDataset dataset = createDataset(orderCounts, reviewCounts);

        JFreeChart chart = createChart(dataset);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsJPEG(out, chart, 800, 600);  // Экспортируем в JPEG
            return out.toByteArray();
        }
    }

    // Создание набора данных для столбчатой диаграммы
    private DefaultCategoryDataset createDataset(Map<Book, Integer> orderCounts, Map<Book, Integer> reviewCounts) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Book, Integer> entry : orderCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Orders", entry.getKey().getTitle());
        }

        for (Map.Entry<Book, Integer> entry : reviewCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Reviews", entry.getKey().getTitle());
        }

        return dataset;
    }

    // Создание диаграммы с использованием данных
    public JFreeChart createChart(DefaultCategoryDataset dataset) {
        return ChartFactory.createBarChart(
                "Artwork Orders and Reviews",  // Заголовок
                "Artwork",                     // Ось X
                "Count",                    // Ось Y
                dataset,                    // Данные
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,                       // Легенда
                true,                       // Подсказки
                false                       // URL
        );
    }
}
