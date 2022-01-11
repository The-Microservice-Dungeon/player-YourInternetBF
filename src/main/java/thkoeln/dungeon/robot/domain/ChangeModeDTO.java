package thkoeln.dungeon.robot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeModeDTO {
    private ROBOT_MODE mode;
}
