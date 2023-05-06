package com.kubaokleja.springbootangular.football.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scorers {
    private List<Scorer> scorers;
}
