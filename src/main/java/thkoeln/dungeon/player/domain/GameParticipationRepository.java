package thkoeln.dungeon.player.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.Game;

import java.util.List;
import java.util.UUID;

public interface GameParticipationRepository extends CrudRepository<GameParticipation, UUID> {
    List<GameParticipation> findByGame ( Game game );
    List<GameParticipation> findByPlayer ( Player player );
    List<GameParticipation> findByRegistrationTransactionId ( UUID registrationTransactionId );

}
