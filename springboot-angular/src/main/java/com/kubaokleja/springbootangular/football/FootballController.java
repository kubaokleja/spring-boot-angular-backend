package com.kubaokleja.springbootangular.football;

import com.kubaokleja.springbootangular.exception.FootballApiException;
import com.kubaokleja.springbootangular.football.model.Scorers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/football")
@RequiredArgsConstructor
class FootballController {

    private final FootballService footballService;

    @GetMapping("/scorers/{leagueCode}")
    ResponseEntity<Scorers> findTopScorersByLeague(@PathVariable("leagueCode") String leagueCode) throws FootballApiException {
        return new ResponseEntity<>(footballService.findTopScorersByLeague(leagueCode), HttpStatus.OK);
    }
}
