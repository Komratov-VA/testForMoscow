package ru.rgs.csvparser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.rgs.csvparser.entity.EntityRq;
import ru.rgs.csvparser.entity.EntityRs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class CsvParserServiceImpl implements CsvParserService {

    private static final String URL = "http://localhost:8081/score";

    @Autowired
    RestTemplate restTemplate;

    @Override
    public Path processCsv(Path source) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        List<EntityRq> listForRest = null;
        try {
            listForRest = createAndFillEntity(source);
        } catch (FileNotFoundException e) {
            //loging
        } catch (IOException e) {
            //loging
        }
        List<ResponseEntity<EntityRs>> stringResponseEntity;
        File file = createFileOutput(source);
        List<EntityRq> finalListForRest = listForRest;
        List<Future<ResponseEntity<EntityRs>>> futures = new ArrayList<>();
        finalListForRest
                .stream()
                .map(x -> futures.add(CompletableFuture.supplyAsync(() -> restTemplate.postForEntity(URL, x, EntityRs.class), threadPool)))
                .collect(Collectors.toList());
        try {
            stringResponseEntity = getFutureList(futures);
        } catch (UnsupportedOperationException e) {
            //loging
            return fillFileOutputIfFailed(listForRest, file);
        }
        return fillFileOutput(stringResponseEntity, file, listForRest);
    }

    private List<EntityRq> createAndFillEntity(Path source) throws IOException {
        List<String> expected = Files.readAllLines(source);
        return expected.stream()
                .skip(1)
                .map(x -> {
                    String[] strings = x.split(",");
                    String fio = new StringBuilder(strings[0] + " ")
                            .append(strings[2] + " ")
                            .append(strings[1]).toString().toUpperCase();
                    String date = strings[3];
                    return new EntityRq(fio, date);
                })
                .collect(Collectors.toList());
    }

    private File createFileOutput(Path source) {
        String fileName = source.getFileName().toString().replaceFirst("input", "output");
        return new File("src/main/resources/generate/" + fileName);
    }

    private List<ResponseEntity<EntityRs>> getFutureList(List<Future<ResponseEntity<EntityRs>>> futures)
    {
        return futures.stream()
                .map((x) -> {
                    try {
                        return x.get();
                    } catch (InterruptedException e) {
                        throw new UnsupportedOperationException();
                    } catch (ExecutionException e) {
                        throw new UnsupportedOperationException();
                    }
                })
                .collect(Collectors.toList());
    }
    private Path fillFileOutput(List<ResponseEntity<EntityRs>> stringResponseEntity, File file, List<EntityRq> listForRest) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("CLIENT_NAME,CONTRACT_DATE,SCORING");
            writer.newLine();
            for (int i = 0; i < stringResponseEntity.size(); i++) {
                EntityRq entityRq = listForRest.get(i);
                EntityRs entityRs = stringResponseEntity.get(i).getBody();
                if (entityRs.getStatus().equals(EntityRs.Status.COMPLETED)) {
                    writer.write(entityRq.getClientName() + ',' + entityRq.getContractDate() + ',' + entityRs.getScoringValue());
                    writer.newLine();
                } else {
                    writer.write(entityRq.getClientName() + ',' + entityRq.getContractDate() + ',' + "не найден");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            //loging
        }
        return file.getAbsoluteFile().toPath();
    }

    private Path fillFileOutputIfFailed(List<EntityRq> listForRest, File file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("CLIENT_NAME,CONTRACT_DATE,SCORING");
            writer.newLine();
            for (int i = 0; i < listForRest.size(); i++) {
                EntityRq entityRq = listForRest.get(i);
                writer.write(entityRq.getClientName() + ',' + entityRq.getContractDate() + ',' + "ошибка обработки");
                writer.newLine();
            }
        } catch (IOException e) {
            //loging
        }
        return file.getAbsoluteFile().toPath();
    }
}

