package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.GameEngine;
import raaidsm.spring.test.models.forms.MoveForm;
import raaidsm.spring.test.models.utils.GameStatus;

import java.util.*;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);
    private final GameEngine gameEngine;

    public TerachessRestController() {
        gameEngine = new GameEngine();
    }

    @PostMapping(value="/ReadFirstPieceSelection", produces="application/json")
    public List<String> readFirstPieceSelection(@ModelAttribute MoveForm form) {
        //Receive the square name of a piece and return all legal moves for that piece
        logger.trace("readFirstPieceSelection() runs");

        return gameEngine.getLegalMovesForAPiece(form.getFirstSquare());
    }

    @PostMapping(value="/ReadMove")
    public String readMove(@ModelAttribute MoveForm form) {
        //Read move and calculate changes on the board
        logger.trace("readMove() runs");

        GameStatus gameStatus = gameEngine.makeMove(form.getFirstSquare(), form.getSecondSquare());

        //TODO: For now, throwing exception when game stops for any reason (Checkmate, Stalemate, etc.)
        if (gameStatus == GameStatus.CHECKMATE) return "Checkmate";
        else if (gameStatus == GameStatus.STALEMATE) return "Stalemate";
        else return "Live";
    }
}