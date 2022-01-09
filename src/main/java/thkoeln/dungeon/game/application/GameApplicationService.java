package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.game.GameEventConsumerService;
import thkoeln.dungeon.eventconsumer.game.GameStatusEvent;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameException;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameApplicationService {
    private GameRepository gameRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  PlayerApplicationService playerApplicationService ) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }



    public Optional<Game> retrieveRunningGame() {
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals( GameStatus.GAME_RUNNING );
        if ( foundGames.size() > 1 ) throw new GameException( "More than one running game!" );
        if ( foundGames.size() == 1 ) {
            return Optional.of( foundGames.get( 0 ) );
        }
        else {
            return Optional.empty();
        }
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created
     */
    public void gameStatusExternallyChanged( UUID gameId, GameStatus gameStatus ) {
        switch ( gameStatus ) {
            case CREATED:
                gameExternallyCreated( gameId );
                break;
            case GAME_RUNNING:
                gameExternallyStarted( gameId );
                break;
            case GAME_FINISHED:
                gameExternallyEnded( gameId );
                break;
        }
    }


    /**
     * To be called by event consumer listening to GameService event
     * @param gameId ID of the new game
     */
    public void gameExternallyCreated ( UUID gameId ) {
        logger.info( "Processing external event that the game with id " + gameId + " has been created" );
        Game game = findAndIfNeededCreateGame( gameId );
        game.resetToNewlyCreated();
        gameRepository.save( game );
    }



    /**
     * To be called by event consumer listening to GameService event.
     * In that case, we simply assume that there is only ONE game currently running, and that it is THIS
     * game. All other games I might have here in the player will be set to GAME_FINISHED state.
     * @param gameId ID of the new game
     */
    public void gameExternallyStarted ( UUID gameId ) {
        logger.info( "Processing external event that the game with id " + gameId + " has started" );
        List<Game> allGames = gameRepository.findAll();
        for ( Game game: allGames ) {
            game.setGameStatus( GameStatus.GAME_FINISHED );
            gameRepository.save( game );
        }
        Game game = findAndIfNeededCreateGame( gameId );
        game.setGameStatus( GameStatus.GAME_RUNNING );
        gameRepository.save( game );
    }



    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void gameExternallyEnded( UUID gameId ) {
        logger.info( "Processing external event that the game with id " + gameId + " has ended" );
        Game game = findAndIfNeededCreateGame( gameId );
        game.setGameStatus( GameStatus.GAME_FINISHED );
        gameRepository.save( game );
    }




    private Game findAndIfNeededCreateGame( UUID gameId ) {
        List<Game> fittingGames = gameRepository.findByGameId( gameId );
        Game game = null;
        if ( fittingGames.size() == 0 ) {
            game = Game.newlyCreatedGame( gameId );
        }
        else {
            if ( fittingGames.size() > 1 ) throw new GameException( "More than one game with id " + gameId );
            game = fittingGames.get( 0 );
        }
        gameRepository.save( game );
        return game;
    }


    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void newRound( UUID gameId, Integer roundNumber ) {
        logger.info( "Processing 'new round' event for round no. " + roundNumber );
        // todo
    }


    /**
     * Makes sure that our own game state is consistent with what GameService says.
     * We take a very simple approach here. We, as a Player, don't manage any game
     * state - we just assume that GameService does a proper job. So we just store
     * the incoming games. Only in the case that a game should suddenly "disappear",
     * we keep it and mark it as ORPHANED - there may be local references to it.
     *
     * This method is currently not actively called; just kept in for safety reasons.
     * We currently assume that no "cleanup" will be necessary. If such a cleanup
     * (after a messed-up communication with GameService) is needed, this is the
     * method to call.
     */
    public void synchronizeGameState() {
        GameDto[] gameDtos = new GameDto[0];
        try {
            gameDtos = gameServiceRESTAdapter.fetchCurrentGameState();
        }
        catch ( UnexpectedRESTException | RESTConnectionFailureException e ) {
            logger.warn( "Problems with GameService while synchronizing game state - need to try again later.\n" +
                    e.getStackTrace() );
        }

        // We need to treat the new games (those we haven't stored yet) and those we
        // already have in a different way. Therefore let's split the list.
        List<GameDto> unknownGameDtos = new ArrayList<>();
        List<GameDto> knownGameDtos = new ArrayList<>();
        for ( GameDto gameDto: gameDtos ) {
            if ( gameRepository.existsByGameId( gameDto.getGameId() ) ) knownGameDtos.add( gameDto );
            else unknownGameDtos.add( gameDto );
        }

        List<Game> storedGames = gameRepository.findAll();
        for ( Game game: storedGames ) {
            Optional<GameDto> foundDtoOptional = knownGameDtos.stream()
                    .filter( dto -> game.getGameId().equals( dto.getGameId() )).findAny();
            if ( foundDtoOptional.isPresent() ) {
                modelMapper.map( foundDtoOptional.get(), game );
                gameRepository.save( game );
                logger.info( "Updated game " + game );
            }
            else {
                game.makeOrphan();
                gameRepository.save( game );
            }
        }
        for ( GameDto gameDto: unknownGameDtos ) {
            Game game = modelMapper.map( gameDto, Game.class );
            gameRepository.save( game );
            logger.info( "Received game " + game + " for the first time");
        }
        logger.info( "Retrieval of new game state finished" );
    }

}
