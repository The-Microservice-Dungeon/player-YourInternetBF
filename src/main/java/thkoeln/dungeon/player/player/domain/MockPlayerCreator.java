package thkoeln.dungeon.player.player.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile( "mock" )
public class MockPlayerCreator implements ApplicationListener<ContextRefreshedEvent> {

    private PlayerRepository playerRepository;
    Logger logger = LoggerFactory.getLogger(MockPlayerCreator.class);

    @Value("${dungeon.mock.numberOfPlayers}")
    private int numberOfPlayers;

    @Autowired
    public MockPlayerCreator( PlayerRepository playerRepository ) {
        this.playerRepository = playerRepository;
    }

    @Override
    /**
     * Creates a number of players to play concurrently
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for( int iPlayer = 0; iPlayer<numberOfPlayers; iPlayer++ ) {
            Player player = new Player();
            player.setName( "P" + iPlayer );
            playerRepository.save( player );
            logger.info( "Created player no. " + player.getName() );
        }
    }
}
