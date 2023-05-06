package com.kubaokleja.springbootangular.football.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scorer {

    Player player;
    Team team;
    String goals;
    String assists;
    String penalties;
}
