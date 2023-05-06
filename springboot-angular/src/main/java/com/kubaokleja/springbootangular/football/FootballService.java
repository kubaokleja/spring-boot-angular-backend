package com.kubaokleja.springbootangular.football;

import com.google.gson.Gson;
import com.kubaokleja.springbootangular.exception.FootballApiException;
import com.kubaokleja.springbootangular.football.model.Scorers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
class FootballService {

    private static final String FOOTBALL_API_URL = "https://api.football-data.org/v4";

    private final ApiUtils apiUtils;

    Scorers findTopScorersByLeague(String leagueCode) throws FootballApiException {
        HttpResponse<String> response = apiUtils.getResponse(FOOTBALL_API_URL + "/competitions/" + leagueCode + "/scorers");
        Gson gson = new Gson();
        return gson.fromJson(response.body(), Scorers.class);
    }

}
