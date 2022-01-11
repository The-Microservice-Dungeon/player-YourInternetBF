package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

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
