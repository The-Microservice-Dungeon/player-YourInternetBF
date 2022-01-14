package thkoeln.dungeon.robot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlanetMoveInformation {
    @JsonProperty("planetId")
    private UUID planetId;

    @JsonProperty("planetType")
    private UUID planetType;

    public static PlanetMoveInformation fromJsonString(String jsonString ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue( jsonString, PlanetMoveInformation.class );
    }
}
