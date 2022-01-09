package thkoeln.dungeon.eventconsumer.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import java.util.UUID;

@Service
public class GameEventConsumerService {
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private GameStatusEventRepository gameStatusEventRepository;

    @Autowired
    public GameEventConsumerService( GameApplicationService gameApplicationService,
                                     GameStatusEventRepository gameStatusEventRepository,
                                     PlayerApplicationService playerApplicationService ) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.playerApplicationService = playerApplicationService;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeGameStatusEvent( @Payload GameStatusEventPayload gameStatusEventPayload, MessageHeaders headers ) {
        GameStatusEvent gameStatusEvent = new GameStatusEvent( headers, gameStatusEventPayload );
        gameStatusEventRepository.save( gameStatusEvent );
        if ( gameStatusEvent.isValid() ) {
            switch ( gameStatusEvent.getGameStatus() ) {
                case CREATED:
                    playerApplicationService.joinPlayersInNewlyCreatedGame( gameStatusEvent.getGameId() );
                    break;
                case GAME_RUNNING:
                    gameApplicationService.gameExternallyStarted( gameStatusEvent.getGameId() );
                    break;
                case GAME_FINISHED:
                    gameApplicationService.gameExternallyFinished( gameStatusEvent.getGameId() );
                    break;
            }
        }
    }


    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
