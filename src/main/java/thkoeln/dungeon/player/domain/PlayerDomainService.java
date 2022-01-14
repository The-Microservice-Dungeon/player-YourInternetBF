package thkoeln.dungeon.player.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Domain service that handles aspects that are still strictly domain-related (no REST, no events, ...),
 * but go beyond the boundaries of a single domain entity.
 */
@Service
public class PlayerDomainService {
    private GameParticipationRepository gameParticipationRepository;

    @Autowired
    public PlayerDomainService( GameParticipationRepository gameParticipationRepository ) {
        this.gameParticipationRepository = gameParticipationRepository;
    }

    public List<Player> findPlayersForGame( Game game ) {
        if ( game == null ) return new ArrayList<>();
        List<GameParticipation> foundParticipations = gameParticipationRepository.findByGame( game );
        List<Player> foundPlayers = foundParticipations.stream()
                .map( GameParticipation::getPlayer )
                .collect(Collectors.toList());
        return foundPlayers;
    }

    public Optional<Game> findCurrentRunningGameForPlayer( Player player ) {
        if ( player == null ) return Optional.empty();
        List<GameParticipation> foundParticipations = gameParticipationRepository.findByPlayer( player );
        List<Game> foundGames = foundParticipations.stream()
                .map( GameParticipation::getGame )
                .filter( game -> (game.getGameStatus() == GameStatus.RUNNING) )
                .collect(Collectors.toList());
        if ( foundGames.size() > 1 ) {
            throw new PlayerDomainException( "More than one running game for player!" );
        }
        return Optional.of( foundGames.get( 0 ) );
    }
}
