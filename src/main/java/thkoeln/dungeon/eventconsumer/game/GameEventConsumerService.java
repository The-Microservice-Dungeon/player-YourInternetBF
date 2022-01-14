package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import java.util.UUID;

@Service
public class GameEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( GameEventConsumerService.class );
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private GameStatusEventRepository gameStatusEventRepository;
    private PlayerStatusEventRepository playerStatusEventRepository;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    @Autowired
    public GameEventConsumerService( GameApplicationService gameApplicationService,
                                     GameStatusEventRepository gameStatusEventRepository,
                                     PlayerStatusEventRepository playerStatusEventRepository,
                                     PlayerApplicationService playerApplicationService ) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.playerStatusEventRepository = playerStatusEventRepository;
        this.playerApplicationService = playerApplicationService;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener( topics = "status" )
    public void consumeGameStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload ) {
        GameStatusEvent gameStatusEvent = new GameStatusEvent( eventId, timestamp, transactionId, payload );
        // saving the event is not really mandatory, just to keep track / "just in case"
        gameStatusEventRepository.save( gameStatusEvent );
        if ( gameStatusEvent.isValid() ) {
            switch ( gameStatusEvent.getGameStatus() ) {
                case CREATED:
                    playerApplicationService.joinPlayersInNewlyCreatedGame( gameStatusEvent.getGameId() );
                    break;
                case RUNNING:
                    gameApplicationService.gameExternallyStarted( gameStatusEvent.getGameId() );
                    break;
                case FINISHED:
                    gameApplicationService.gameExternallyFinished( gameStatusEvent.getGameId() );
                    break;
            }
        }
        else {
            logger.warn( "Caught invalid GameStatusEvent " + gameStatusEvent );
        }
    }


    /**
     * Event published by GameService after registering a player. Needed to get the playerId ... <sigh>
     */
    @KafkaListener( topics = "playerStatus" )
    public void consumePlayerStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                          @Payload String payload ) {
        PlayerStatusEvent playerStatusEvent = new PlayerStatusEvent( eventId, timestamp, transactionId, payload );
        // saving the event is not really mandatory, just to keep track / "just in case"
        playerStatusEventRepository.save( playerStatusEvent );
        if ( playerStatusEvent.isValid() ) {
            playerApplicationService.assignPlayerId(
                    playerStatusEvent.getTransactionId(), playerStatusEvent.getPlayerId() );
        }
        else {
            logger.warn( "Caught invalid PlayerStatusEvent " + playerStatusEvent );
        }
    }


    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
