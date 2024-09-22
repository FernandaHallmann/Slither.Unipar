package br.unipar.frameworksweb.slitherunipar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {
    @Autowired
    private ScoreRepository scoreRepository;

    public void saveScore(String player, int score) {
        Score playerScore = new Score(player, score);
        scoreRepository.save(playerScore);
    }
}

