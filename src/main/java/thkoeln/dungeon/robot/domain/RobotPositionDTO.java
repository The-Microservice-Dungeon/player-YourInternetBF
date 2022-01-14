package thkoeln.dungeon.robot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.planet.domain.Planet;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotPositionDTO {
    private UUID robotID;
    private String currentPlanetName;
    private UUID currentPlanetID;
}
