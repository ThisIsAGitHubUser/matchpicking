package com.jj.matchpicking;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

    @Test
    public void shouldNotChangeSmartOpponent() {
        //given
        Game game = new Game();

        //when
        game.setSmartOpponent(true);
        game.setSmartOpponent(false);

        //then
        assertThat(game.isSmartOpponent()).isTrue();
    }
}