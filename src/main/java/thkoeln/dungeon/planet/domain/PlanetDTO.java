package thkoeln.dungeon.planet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.robot.domain.ROBOT_MODE;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetDTO {
    private UUID id;
    private String name;
}
