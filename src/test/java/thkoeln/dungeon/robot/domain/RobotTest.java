package thkoeln.dungeon.robot.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.planet.domain.CompassDirection;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.transaction.Transactional;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RobotTest {

    private Planet[][] planetArray = new Planet[3][3];
    private Integer[][] numberOfNeighbours = new Integer[][]{{2, 3, 2}, {3, 4, 3}, {2, 3, 2}};
    private Robot robot = new Robot();
    private Planet planetStartingPosition;


    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    @Transactional
    public void setup() {
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
        robot.playRound();

        //then
        assertNotSame(planetArray[0][0], robot.getCurrentPlanet());
    }

    @Test
    public void testNumberOfVisitsCounter() {
        //given
        //when
        robot.playRound();
        //then
        assertEquals(1, robot.getCurrentPlanet().getNumberOfVisits());
    }



}
