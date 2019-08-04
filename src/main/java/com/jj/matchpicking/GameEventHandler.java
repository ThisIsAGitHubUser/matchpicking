package com.jj.matchpicking;

import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class GameEventHandler {

    @HandleBeforeSave
    public void beforeSave(Game game) {
        game.performTurn();
    }
}
