package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.game.domain.Game;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameParticipation {
    @Id
    private final UUID id = UUID.randomUUID();
    private Integer money;
    private UUID registrationTransactionId;

    @ManyToOne
    private Game game;

    public GameParticipation( Game game, UUID registrationTransactionId ) {
        this.registrationTransactionId = registrationTransactionId;
        this.game = game;
    }

}
