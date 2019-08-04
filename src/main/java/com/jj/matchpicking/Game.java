package com.jj.matchpicking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Random;

@Entity
public class Game {

    private static final int MATCH_COUNT_START = 13;

    private static final int MIN_DRAW = 1;

    private static final int MAX_DRAW = 3;

    @Id
    @GeneratedValue
    private long id;

    private boolean smartOpponent = false;

    private int matchCount = MATCH_COUNT_START;

    private int playerDraw;

    private int lastPlayerDraw;

    private int lastOpponentDraw;

    private String hint = Hint.DEFAULT.getText();

    public boolean isSmartOpponent() {
        return smartOpponent;
    }

    public void setSmartOpponent(boolean smartOpponent) {
        this.smartOpponent |= smartOpponent;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
    }

    public int getPlayerDraw() {
        return playerDraw;
    }

    public void setPlayerDraw(int playerDraw) {
        this.playerDraw = playerDraw;
    }

    public int getLastPlayerDraw() {
        return lastPlayerDraw;
    }

    public void setLastPlayerDraw(int lastPlayerDraw) {
    }

    public int getLastOpponentDraw() {
        return lastOpponentDraw;
    }

    public void setLastOpponentDraw(int lastOpponentDraw) {
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
    }

    private boolean isGameOver() {
        return matchCount < 1;
    }

    private boolean validatePlayerDraw() {
        if (playerDraw < MIN_DRAW || playerDraw > MAX_DRAW) {
            hint = Hint.DRAW_RULES.getFormattedText(MIN_DRAW, MAX_DRAW, playerDraw);
            return false;
        }
        return true;
    }

    private boolean canPerformTurn() {
        return !isGameOver() && validatePlayerDraw();
    }

    private void performPlayerTurn() {
        matchCount -= playerDraw;
        lastPlayerDraw = playerDraw;
        playerDraw = 0;

        if (isGameOver()) {
            hint = Hint.DEFEAT.getText();
        } else {
            hint = Hint.NICE_TURN.getText();
        }
    }

    private void performOpponentTurn() {
        lastOpponentDraw = OpponentAI.getDraw(matchCount);
        matchCount -= lastOpponentDraw;

        if (isGameOver()) {
            hint = Hint.VICTORY.getText();
        }
    }

    void performTurn() {
        if (!canPerformTurn()) {
            return;
        }
        performPlayerTurn();
        if (!isGameOver()) {
            performOpponentTurn();
        }
    }

    private enum Hint {
        DEFAULT("Have fun!"),
        DRAW_RULES("You can draw at least %d match or a maximum of %d matches, but you have drawn %d matches."),
        NICE_TURN("Nice turn my friend! ^^"),
        VICTORY("CONGRATULATIONS! You have won the game! :)"),
        DEFEAT("You have lost the game... so sad dude... :/");

        private final String text;

        Hint(String text) {
            this.text = text;
        }

        private String getText() {
            return text;
        }

        private String getFormattedText(Object... params) {
            return String.format(text, params);
        }
    }

    private static class OpponentAI {
        private static final Random NUMBER_GENERATOR = new Random();

        private static int getDraw(int matchCount) {
            int randomDraw = NUMBER_GENERATOR.nextInt(MAX_DRAW) + MIN_DRAW;
            return Math.min(matchCount, randomDraw);
        }
    }
}
