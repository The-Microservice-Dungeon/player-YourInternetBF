package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    @Getter
    private int test ;

    @ManyToOne
    Planet planet;
    private UUID currentPlanet  = planet.getId();


    // SERENDIPITY is the default mode
    @Enumerated(EnumType.ORDINAL)
    private ROBOT_MODE mode = ROBOT_MODE.SERENDIPITY;

    // this method is being executed in the RobotService.playRound()
    public void playRound() {
        switch (mode) {
            case SERENDIPITY:
                // TODO: implement this
                break;

            case GO_HOME:
                // TODO: implement this
                break;
        }
    }
}
