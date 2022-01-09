package thkoeln.dungeon.eventconsumer.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.MessageHeaders;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.GameStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class GameStatusEvent extends AbstractEvent {
    private GameStatus gameStatus;
    private UUID gameId;

    public static final String TYPE_KEY = "type";
    public static final String GAME_ID_KEY = "gameId";

    public GameStatusEvent( MessageHeaders messageHeaders, GameStatusEventPayload gameStatusEventPayload ) {
        super( messageHeaders );
        setGameStatus( gameStatusEventPayload.gameStatus() );
        setGameId( gameStatusEventPayload.gameId() );
    }

    public boolean isValid() {
        return ( gameId != null && gameStatus != null );
    }
}
