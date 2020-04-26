package ru.rgs.csvparse;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.rgs.csvparser.Starter;
import ru.rgs.csvparser.service.CsvParserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Starter.class)
@AutoConfigureWireMock(port = 8081)
public class MainTest {

    @Autowired
    private CsvParserService csvParserService;

    @Test
    void testInput1() {
        checkParser("/input/input_1.csv", "/output/output_1.csv");
    }

    @Test
    void testInput2() {
        checkParser("/input/input_2.csv", "/output/output_2.csv");
    }

    @Test
    void testInput3() {
        checkParser("/input/input_3.csv", "/output/output_3.csv");
    }

    @SneakyThrows
    private void checkParser(String sourcePath, String expectedFilePath) {
        Path input = getTestFile(sourcePath);
        Path output = Awaitility.await()
                               .atMost(Duration.TEN_SECONDS)
                               .until(() -> csvParserService.processCsv(input), Matchers.any(Path.class));

        List<String> expected = Files.readAllLines(getTestFile(expectedFilePath));
        List<String> actual = Files.readAllLines(output);
        Assertions.assertLinesMatch(expected, actual);
    }

    @SneakyThrows
    private Path getTestFile(String localPath) {
        return Paths.get(getClass().getResource(localPath).toURI());
    }

}
