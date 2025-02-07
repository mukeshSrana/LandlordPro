package com.landlordpro.service.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ChartService {

    // Generate Chart Image as byte array
    public byte[] generateChartImage(String chartType, List<Integer> values, List<String> labels) throws IOException {
        JFreeChart chart = createChart(chartType, values, labels);
        BufferedImage bufferedImage = chart.createBufferedImage(800, 400);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        return outputStream.toByteArray();
    }

    // General method to create different chart types
    private JFreeChart createChart(String chartType, List<Integer> values, List<String> labels) {
        switch (chartType.toLowerCase()) {
        case "line":
            return createLineChart(values, labels);
        case "bar":
            return createBarChart(values, labels);
        case "pie":
            return createPieChart(values, labels);
        default:
            throw new IllegalArgumentException("Unsupported chart type: " + chartType);
        }
    }

    // Create Line Chart
    private JFreeChart createLineChart(List<Integer> values, List<String> labels) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < values.size(); i++) {
            dataset.addValue(values.get(i), "Expenses", labels.get(i));
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Monthly Expenses", "Month", "Amount",
            dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    // Create Bar Chart
    private JFreeChart createBarChart(List<Integer> values, List<String> labels) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < values.size(); i++) {
            dataset.addValue(values.get(i), "Expenses", labels.get(i));
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Expenses", "Month", "Amount",
            dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    // Create Pie Chart
    private JFreeChart createPieChart(List<Integer> values, List<String> labels) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < values.size(); i++) {
            dataset.setValue(labels.get(i), values.get(i));
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Expenses Distribution", dataset, true, true, false);

        chart.setBackgroundPaint(Color.white);
        return chart;
    }
}
