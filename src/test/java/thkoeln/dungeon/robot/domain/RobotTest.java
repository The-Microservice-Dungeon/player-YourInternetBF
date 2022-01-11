package thkoeln.dungeon.robot.domain;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

@SpringBootTest
public class RobotTest {

    private Planet[][] planetArray = new Planet[3][3];
    private Integer[][] numberOfNeighbours = new Integer[][]{{2, 3, 2}, {3, 4, 3}, {2, 3, 2}};
    private Robot robot = new Robot();
    private Planet planetStartingPosition = planetArray[0][0];


    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    public void setup() {
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
                planetArray[i][j].setName("p" + String.valueOf(i) + String.valueOf(j));
            }
        }
        robot.setCurrentPlanet(planetStartingPosition.getId());
    }




}
