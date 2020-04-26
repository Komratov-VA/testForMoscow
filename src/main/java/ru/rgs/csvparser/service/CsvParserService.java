package ru.rgs.csvparser.service;

import java.nio.file.Path;

public interface CsvParserService {
    /**
     * process all data in source csv file. It generates new request (for each line) to external system, and store result in another csv file.
     * @param source csv file with request data
     * @return csv file with response data
     */
    Path processCsv(Path source);
}
