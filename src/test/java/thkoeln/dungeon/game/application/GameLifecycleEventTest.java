package thkoeln.dungeon.game.application;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.restadapter.GameDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameLifecycleEventTest {
    private static final UUID GAME_ID_0 = UUID.randomUUID();
    private static final UUID GAME_ID_1 = UUID.randomUUID();
    private static final UUID GAME_ID_2 = UUID.randomUUID();
    private Game game0, game1, game2;



    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameApplicationService gameApplicationService;


    @Before
    public void setUp() throws Exception {
        gameRepository.deleteAll();
        game0 = Game.newlyCreatedGame( GAME_ID_0 );
        game1 = Game.newlyCreatedGame( GAME_ID_1 );
        game2 = Game.newlyCreatedGame( GAME_ID_2 );
    }


    @Test
    public void testGameExternallyCreated_OnEmptyDatabase() {
        // given
        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, CREATED );

        // then
        assertTrue( gameApplicationService.retrieveRunningGame().isEmpty() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( CREATED ).size() );
        assertEquals( 1, gameRepository.findAll().size() );
        assertEquals( GAME_ID_0, gameRepository.findAllByGameStatusEquals( CREATED ).get( 0 ).getGameId() );
    }


    @Test
    public void testGameExternallyStarted_OnEmptyDatabase() {
        // given
        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, GAME_RUNNING );

        // then
        assertEquals( GAME_ID_0, gameApplicationService.retrieveRunningGame().get().getGameId() );
        assertEquals( GAME_RUNNING, gameApplicationService.retrieveRunningGame().get().getGameStatus() );
        assertEquals( 1, gameRepository.findAll().size() );
    }



    @Test
    public void testGameExternallyEnded_OnEmptyDatabase() {
        // given
        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, GAME_FINISHED );

        // then
        assertTrue( gameApplicationService.retrieveRunningGame().isEmpty() );
        assertEquals( 1, gameRepository.findAll().size() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( GAME_FINISHED ).size() );
    }


    @Test
    public void testGameExternallyCreated_OnFilledDatabase() {
        // given
        game1.setGameStatus( GAME_RUNNING );
        gameRepository.save( game1 );
        game2.setGameStatus( GAME_FINISHED );
        gameRepository.save( game2 );

        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, CREATED );

        // then
        assertEquals( game1, gameApplicationService.retrieveRunningGame().get() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( CREATED ).size() );
        assertEquals( 3, gameRepository.findAll().size() );
    }


    @Test
    public void testGameExternallyStarted_OnFilledDatabase() {
        // given
        game1.setGameStatus( GAME_RUNNING );
        gameRepository.save( game1 );
        game2.setGameStatus( GAME_FINISHED );
        gameRepository.save( game2 );

        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, GAME_RUNNING );

        // then
        assertEquals( GAME_ID_0, gameApplicationService.retrieveRunningGame().get().getGameId() );
        assertEquals( GAME_RUNNING, gameApplicationService.retrieveRunningGame().get().getGameStatus() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( GAME_RUNNING ).size() );
        assertEquals( 2, gameRepository.findAllByGameStatusEquals( GAME_FINISHED ).size() );
        assertEquals( 3, gameRepository.findAll().size() );
    }



    @Test
    public void testGameExternallyEnded_OnFilledDatabase() {
        // given
        game1.setGameStatus( GAME_RUNNING );
        gameRepository.save( game1 );
        game2.setGameStatus( GAME_FINISHED );
        gameRepository.save( game2 );

        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_0, GAME_FINISHED );

        // then
        assertEquals( game1, gameApplicationService.retrieveRunningGame().get() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( GAME_RUNNING ).size() );
        assertEquals( 2, gameRepository.findAllByGameStatusEquals( GAME_FINISHED ).size() );
        assertEquals( 3, gameRepository.findAll().size() );
    }


    @Test
    public void testGameExternallyStarted_withLocalStateCollision() {
        // given
        game0.setGameStatus( CREATED );
        gameRepository.save( game0 );
        game1.setGameStatus( GAME_RUNNING );
        gameRepository.save( game1 );
        game2.setGameStatus( GAME_FINISHED );
        gameRepository.save( game2 );

        // when
        gameApplicationService.gameStatusExternallyChanged( GAME_ID_2, GAME_RUNNING );

        // then
        assertEquals( GAME_ID_2, gameApplicationService.retrieveRunningGame().get().getGameId() );
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( GAME_RUNNING ).size() );
        assertEquals( 2, gameRepository.findAllByGameStatusEquals( GAME_FINISHED ).size() );
        assertEquals( 3, gameRepository.findAll().size() );
    }
}
