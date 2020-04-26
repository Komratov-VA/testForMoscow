package ru.rgs.csvparser.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EntityRq {

    private String clientName;
    private String contractDate;
}
