package com.kubaokleja.springbootangular.football.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private String id;
    private String name;
    private String shortName;
    private String tla;
    private String crest;
}
