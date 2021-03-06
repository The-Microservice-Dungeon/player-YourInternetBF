package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.*;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This game class encapsulates the game tactics for a simple autonomous controlling of a robot
 * swarm. It has the following structure:
 * - the "round started" event triggers the main round() method
 * - if there is enough money, new robots are bought (or, depending on configuration, existing robots are upgraded)
 * - for each robot, the proper command is chosen and issued (based on the configured tactics)
 * - each time an answer is received (with transaction id), the robots and the map are updated.
 */
@Service
public class PlayerApplicationService {
    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private ModelMapper modelMapper = new ModelMapper();

    private PlayerRepository playerRepository;
    private GameParticipationRepository gameParticipationRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private GameApplicationService gameApplicationService;

    @Value("${dungeon.singlePlayer.playerName}")
    private String singlePlayerName;

    @Value("${dungeon.singlePlayer.playerEmail}")
    private String singlePlayerEmail;

    @Value("${dungeon.mode}")
    private PlayerMode playerMode;

    @Value("${dungeon.multiPlayer.number}")
    private int numberOfMultiPlayers;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameParticipationRepository gameParticipationRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.playerRepository = playerRepository;
        this.gameParticipationRepository = gameParticipationRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
    }

    public PlayerMode currentMode() {
        return playerMode;
    }

    public int numberOfPlayers() {
        return currentMode().isSingle() ? 1 : numberOfMultiPlayers;
    }


    /**
     * Create player(s), if not there already
     */
    public void createPlayers() {
        List<Player> players = playerRepository.findAll();
        if (players.size() == 0) {
            for (int iPlayer = 0; iPlayer < numberOfPlayers(); iPlayer++) {
                Player player = new Player();
                if ( currentMode().isSingle() && (! "".equals( singlePlayerName ) ) && (! "".equals( singlePlayerEmail ) )  ) {
                    player.setName( singlePlayerName );
                    player.setEmail( singlePlayerEmail );
                }
                else {
                    player.assignRandomName();
                }
                playerRepository.save(player);
                logger.info("Created new player: " + player);
                players.add(player);
            }
        }
    }


    /**
     * Obtain the bearer token for all players defined in this service
     */
    public void obtainBearerTokensForMultiplePlayers() {
        List<Player> players = playerRepository.findAll();
        for (Player player : players) obtainBearerTokenForPlayer( player );
    }


    /**
     * Obtain the bearer token for one specific player
     * @param player
     * @return true if successful
     */
    public void obtainBearerTokenForPlayer(Player player ) {
        if ( player.getBearerToken() != null ) return;
        try {
            PlayerRegistryDto playerDto = modelMapper.map(player, PlayerRegistryDto.class);
            PlayerRegistryDto registeredPlayerDto = gameServiceRESTAdapter.getBearerTokenForPlayer(playerDto);
            if ( registeredPlayerDto != null ) {
                if ( registeredPlayerDto.getBearerToken() == null ) logger.error( "Received no bearer token for " + player + "!");
                else player.setBearerToken( registeredPlayerDto.getBearerToken() );
                playerRepository.save( player );
                logger.info("Bearer token received for " + player );
            }
            else {
                logger.error( "PlayerRegistryDto returned by REST service is null for player " + player );
            }
        }
        catch ( RESTRequestDeniedException e ) {
            // TODO - unclear what to do in this cases
            logger.error( "Name collision while getting bearer token for player " + player );
        }
        catch ( RESTConnectionFailureException | UnexpectedRESTException e ) {
            logger.error( "No connection or no valid response from GameService - no bearer token for player " + player );
        }
    }




    /**
     * We have received the event that a game has been created. So make sure that the game state is suitable,
     * and that our player(s) can join.
     * for the game.
     * @param gameId
     */
    public void joinPlayersInNewlyCreatedGame( UUID gameId ) {
        Game game = gameApplicationService.gameExternallyCreated( gameId );
        List<Player> players = playerRepository.findAll();
        for (Player player : players) registerOnePlayerForGame( player, game );
    }



    /**
     * Register one specific player for a game
     * @param player
     * @param game
     */
    public void registerOnePlayerForGame( Player player, Game game ) {
        try {
            if (player.getBearerToken() == null) {
                obtainBearerTokenForPlayer( player );
            }
            if (player.getBearerToken() == null) {
                logger.error("No bearer token for " + player + " also after another attempt - cannot register for game!");
                return;
            }
            UUID transactionId = gameServiceRESTAdapter.registerPlayerForGame( game.getGameId(), player.getBearerToken() );
            if ( transactionId != null ) {
                GameParticipation gameParticipation = new GameParticipation( player, game, transactionId );
                gameParticipationRepository.save( gameParticipation );
                logger.info("Player " + player + " successfully registered for game " + game +
                        " with transactionId " + transactionId );
            }
        } catch (RESTConnectionFailureException | RESTRequestDeniedException e) {
            // shouldn't happen - cannot do more than logging and retrying later
            // todo - err msg wrong
            logger.error( "Could not register " + player + " for " + game +
                    "\nOriginal Exception:\n" + e.getMessage() + "\n" + e.getStackTrace() );
        }
    }


    /**
     *
     */
    public void assignPlayerId( UUID registrationTransactionId, UUID playerId ) {
        if ( registrationTransactionId == null )
            throw new PlayerRegistryException( "registrationTransactionId cannot be null!" );
        if ( playerId == null )  throw new PlayerRegistryException( "PlayerId cannot be null!" );
        List<GameParticipation> foundParticipations =
                gameParticipationRepository.findByRegistrationTransactionId( registrationTransactionId );
        if ( foundParticipations.size() != 1 ) {
            throw new PlayerRegistryException( "Found not 1 participation for playerId " + playerId
                        + ", but " + foundParticipations.size() );
        }
        Player player = foundParticipations.get( 0 ).getPlayer();
        player.setPlayerId( playerId );
        playerRepository.save( player );
    }

}
