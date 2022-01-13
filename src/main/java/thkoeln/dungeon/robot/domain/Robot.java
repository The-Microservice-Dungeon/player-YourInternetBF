package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;

import javax.persistence.*;
import java.util.List;
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
    private Planet currentPlanet;

    // SERENDIPITY is the default mode
    @Enumerated(EnumType.ORDINAL)
    private ROBOT_MODE mode = ROBOT_MODE.SERENDIPITY;

    @Column()
    private Integer energyPoints;

    @Column()
    private Integer level;

    @Column()
    private Integer hp;

    @Column()
    private Integer coal;

    @Column()
    private Integer iron;

    @Column()
    private Integer gem;

    @Column()
    private Integer gold;

    @Column()
    private Integer platin;

    // this method is being executed in the RobotService.playRound()
    public void playRound() {
        switch (mode) {
            case SERENDIPITY:
                // TODO: implement this
                //first step look at neighbouring planets
                //go to neighbouring Planet with lowest numberOfVisits
                System.out.println("_____________________________________________");
                System.out.println("_____________________________________________");
                System.out.println("_____________________________________________");
                System.out.println("_____________________________________________");
                System.out.println("**"+currentPlanet+"**");
                System.out.println(currentPlanet.randomNeighbourPlanet());
                if(currentPlanet.getNumberOfVisits() == 0){
                    currentPlanet.setNumberOfVisits(1);
                }
                currentPlanet = currentPlanet.randomNeighbourPlanet();
                currentPlanet.setNumberOfVisits(currentPlanet.getNumberOfVisits()+1);
                System.out.println("**"+currentPlanet+"**");
                System.out.println("Nachbarn-->"+currentPlanet.allNeighbours());
                break;

            case GO_HOME:
                // TODO: implement this
                break;

            case BUY_ROBOT:
                // TODO: implement this
                break;
        }
    }
}
