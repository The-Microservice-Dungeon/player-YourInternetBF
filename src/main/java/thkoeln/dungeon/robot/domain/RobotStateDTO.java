package thkoeln.dungeon.robot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotStateDTO {
    private UUID robotId;
    private Integer energyPoints;
    private Integer level;
    private Integer hp;
    private Integer coal;
    private Integer iron;
    private Integer gem;
    private Integer gold;
    private Integer platin;
}
