package com.kubaokleja.springbootangular.football.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String id;
    private String name;
    private String position;
    private String dateOfBirth;
    private String nationality;
    private String shirtNumber;
    private String role;
    private String lastUpdated;
}
