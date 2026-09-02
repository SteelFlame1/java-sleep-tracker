package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final List<Function<List<SleepSession>, SleepAnalysisResult>> analysisTasks;

    public SleepTrackerApp() {
        this.analysisTasks = new ArrayList<>();
        analysisTasks.add(new TotalSessionsCountAnalyzer());
        analysisTasks.add(new AverageDurationAnalyzer());
        analysisTasks.add(new GoodNightsCountAnalyzer());
        analysisTasks.add(new MaxDurationAnalyzer());
        analysisTasks.add(new BadQualitySessionsAnalyzer());
        analysisTasks.add(new MinDurationAnalyzer());
        analysisTasks.add(new SleeplessNightsAnalyzer());

    }

    public static void main(String[] args) {
        String resourceName = "sleep_log.txt";
        var inputStream = SleepTrackerApp.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            System.out.println("Файл не найден в ресурсах: " + resourceName);
            return;
        }
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream))) {
            List<String> rawLines = reader.lines().collect(Collectors.toList());
            System.out.println("Файл успешно прочитан! Строк: " + rawLines.size());
            List<SleepSession> sessions = rawLines.stream().map(SleepTrackerApp::parseLineToSession).collect(java.util.stream.Collectors.toList());
            System.out.println("--- НАЧАЛО АНАЛИЗА ---");
            SleepTrackerApp app = new SleepTrackerApp();
            app.analysisTasks.stream().map(task -> task.apply(sessions)).forEach(result -> System.out.println(result));
            ChronotypeAnalyzer chronotypeAnalyzer = new ChronotypeAnalyzer();
            Chronotype userType = chronotypeAnalyzer.apply(sessions);
            System.out.println("Хронотип пользователя: " + userType);
            System.out.println("--- АНАЛИЗ ЗАВЕРШЕН ---");
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл по пути");
            System.out.println("Детали ошибки: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка формата данных в файле: " + e.getMessage());
        }
    }

    private static SleepSession parseLineToSession(String line) {
        String trimmedLine = line.trim();

        if (trimmedLine.isEmpty()) {
            throw new IllegalArgumentException("Пустая строка в файле");
        }

        int lastSemicolonIndex = trimmedLine.lastIndexOf(';');

        String dataPart;
        if (lastSemicolonIndex != -1) {
            dataPart = trimmedLine.substring(0, lastSemicolonIndex);
        } else {
            dataPart = trimmedLine;
        }

        String[] parts = dataPart.split(";");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Не удалось разделить строку на две части времени. Строка: '" + line + "'. " +
                            "Ожидается формат: 'ДД.ММ.ГГ ЧЧ:ММ;ДД.ММ.ГГ ЧЧ:ММ;СТАТУС'"
            );
        }

        String startStr = parts[0].trim();
        String endStr = parts[1].trim();

        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        LocalDateTime start = LocalDateTime.parse(startStr, fileFormatter);
        LocalDateTime end = LocalDateTime.parse(endStr, fileFormatter);

        if (end.isBefore(start)) {
            end = end.plusDays(1);
        }

        return new SleepSession(start, end);
    }


}