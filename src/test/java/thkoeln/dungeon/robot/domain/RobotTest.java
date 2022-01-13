package thkoeln.dungeon.robot.domain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import thkoeln.dungeon.planet.domain.CompassDirection;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;
import thkoeln.dungeon.planet.domain.PlanetService;

import javax.transaction.Transactional;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RobotTest {
    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private RobotRepository robotRepository;

    private Planet[][] planetArray = new Planet[3][3];
    private Integer[][] numberOfNeighbours = new Integer[][]{{2, 3, 2}, {3, 4, 3}, {2, 3, 2}};
    private Robot robot = new Robot();
    private Planet startPlanet;

    @Autowired
    private RobotService robotService;
    private Planet planetStartingPosition;

    @BeforeEach
    @Transactional
    public void setup() {
        robotRepository.deleteAll();

        Robot newRobot = new Robot();
        newRobot.setMode(ROBOT_MODE.SERENDIPITY);
        robotRepository.save(newRobot);
        for (Robot robot1 : robotRepository.findAll()) {
            this.robot = robot1;
        }

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
                planetArray[i][j].setName("p" + String.valueOf(i) + String.valueOf(j));
            }
        }

        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                if ( i < 2 ) planetArray[i][j].defineNeighbour( planetArray[i+1][j], CompassDirection.east );
                if ( j < 2 ) planetArray[i][j].defineNeighbour( planetArray[i][j+1], CompassDirection.south );
            }
        }


        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetRepository.save( planetArray[i][j] );
            }
        }
        planetStartingPosition = planetArray[0][0];
        //System.out.println(planetArray[0][0]);
        robot.setCurrentPlanet(planetStartingPosition);
        startPlanet = planetStartingPosition;
        robotRepository.save(robot);
        //System.out.println(robot.getCurrentPlanet());
    }

    @Test
    public void testRobotIsOnStartingPosition() {
        //then
        assertEquals(planetArray[0][0], robot.getCurrentPlanet());
    }

    @Test
    public void testRobotLeftInitialPosition() {
        //given

        //when
        robotService.playRound();
        robotRepository.save(robot);

        System.out.println("Start planet:" + startPlanet);
        System.out.println("Current planet:" + robot.getCurrentPlanet());

        //then
        assertNotSame(startPlanet.getId(), robot.getCurrentPlanet().getId());
    }

    @Test
    public void testNumberOfVisitsCounter() {
        //given
        //when
        robotService.playRound();

        //then
        assertEquals(1, robot.getCurrentPlanet().getNumberOfVisits());
    }

    @Test
    public void testRobotMovesToLeastKnownPlanet(){
        robotService.playRound();
        robotService.playRound();
        robotService.playRound();

        for(int x = 0; x < planetRepository.findAll().size(); x++){
            assertTrue(planetRepository.findAll().get(x).getNumberOfVisits() < 2);
        }
    }

}
