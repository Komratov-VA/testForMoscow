package ru.rgs.csvparser.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityRs {

    private Status status;
    private Double scoringValue;
    private String description;

    public enum Status {
        COMPLETED,
        FAILED,
        NOT_FOUND
    }
}