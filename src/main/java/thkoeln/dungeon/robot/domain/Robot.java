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
    private ROBOT_MODE mode = ROBOT_MODE.IDLE;

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
}
