package thkoeln.dungeon.robot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class RobotMoveEvent {
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("remainingEnergy")
    private Integer remainingEnergy;

    @JsonProperty("planet")
    private PlanetMoveInformation planetMoveInformation;

    public static RobotMoveEvent fromJsonString(String jsonString ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue( jsonString, RobotMoveEvent.class );
    }
}
